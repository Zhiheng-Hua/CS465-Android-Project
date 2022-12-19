package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {
  private RelativeLayout rootView;
  private TextView groupNameTextView;
  private Button addQuestionBtn;

  private RelativeLayout editQuestionContainer;
  private LinearLayout questionsContainer;
  private PopupWindow addQuestionPopupWindow;  // add question pop up window
  private PopupWindow setStatusPopupWindow;
  private PopupWindow emojiPickerPopup;

  private final int EMOJI_PICKER_COL_COUNT = 9;
  private final int EMOJI_PICKER_ROW_COUNT = 5;

  private HashSet<LinearLayout> selectedTags;

  private GroupDataStore dataStore;  // data store used to store all group data involved

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group);

    rootView = findViewById(R.id.group_page_root);

    // group name
    groupNameTextView = findViewById(R.id.groupName);

    String groupName = getIntent().getStringExtra("groupname_value");
    groupNameTextView.setText(groupName);
    dataStore = MainActivityDataStore.getInstance().getGroupDataStore(groupName);

    // container to hold delete and edit button
    initEditQuestionBar();

    // container to hold all question tags
    questionsContainer = findViewById(R.id.questionsContainer);

    // add button
    initAddQuestionPopupView();

    ((TextView) findViewById(R.id.showJoinCode)).setText("JOIN CODE: " + dataStore.getJoinCode());

    selectedTags = new HashSet<>();

    // fetch data from globalStore and render them on the screen
    HashMap<Integer, HashMap<String, Pair<Integer, Boolean>>> questionData = dataStore.getQuestionEmojiSetMap();
    for (Map.Entry<Integer, HashMap<String, Pair<Integer, Boolean>>> p : questionData.entrySet()) {
      Integer questionId = p.getKey();
      TagStatus stat = dataStore.getQuestionStatus(questionId);
      String title = dataStore.getQuestionTitle(questionId);
      addNewQuestionTag(title, stat, p.getValue(), questionId);
    }
  }

  private void addNewQuestionTag(String text, TagStatus status, HashMap<String, Pair<Integer, Boolean>> emojiSet, Integer questionId) {
    // inflate the layout of the popup window
    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    LinearLayout questionTag = (LinearLayout) inflater.inflate(R.layout.question_tags, questionsContainer, false);
    if (questionId == -1) {
      questionId = ViewCompat.generateViewId();
    }
    questionTag.setId(questionId);

    dataStore.initNewQuestionActivity(questionId, emojiSet);

    // single click
    setQuestionTagReadMode(questionTag);

    // long press
    questionTag.setOnLongClickListener(view -> {
      // set status select options
      ListView statusOptionList = new ListView(this);
      statusOptionList.setBackgroundColor(Color.WHITE);       // TODO: change color and style
      ArrayAdapter<TagStatus> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TagStatus.values());
      statusOptionList.setAdapter(adapter);
      int width = LinearLayout.LayoutParams.MATCH_PARENT;
      int height = LinearLayout.LayoutParams.WRAP_CONTENT;
      setStatusPopupWindow = new PopupWindow(statusOptionList, width, height, false);  // prevent tap outside to dismiss
      setStatusPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
      statusOptionList.setOnItemClickListener((parent, v, position, id) -> {
        TagStatus selectedStatus = (TagStatus) statusOptionList.getAdapter().getItem(position);
        for (LinearLayout tag : selectedTags) {
          tag.getChildAt(1).setBackgroundColor(Color.parseColor(selectedStatus.getColor()));
          dataStore.setQuestionStatus(tag.getId(), selectedStatus);
        }
        exitEditMode();
      });

      // select current question, unselect all other questions
      for (int i = 0; i < questionsContainer.getChildCount(); i++) {
        LinearLayout currentTag = (LinearLayout) questionsContainer.getChildAt(i);
        setQuestionTagEditMode(currentTag);
        CheckBox box = (CheckBox) currentTag.getChildAt(0);
        box.setVisibility(View.VISIBLE);
        box.setChecked(false);
      }
      ((CheckBox) questionTag.getChildAt(0)).setChecked(true);
      selectedTags.clear();
      selectedTags.add(questionTag);

      // show edit buttons and status options
      editQuestionContainer.setVisibility(View.VISIBLE);
      groupNameTextView.setVisibility(View.GONE);
      addQuestionBtn.setVisibility(View.GONE);
      return true;
    });

    // status color
    questionTag.getChildAt(1).setBackgroundColor(Color.parseColor(status.getColor()));
    // question content
    ((TextView) questionTag.getChildAt(2)).setText(text);
    dataStore.setQuestionStatus(questionId, status);
    dataStore.setQuestionTitle(questionId, text);

    // add this new tag to the question container
    questionsContainer.addView(questionTag);
  }

  /**
   * Edit Mode: click to toggle tag selection
   * @param currentTag LinearLayout of the current question tag
   */
  private void setQuestionTagEditMode(LinearLayout currentTag) {
    currentTag.setOnClickListener(v -> {
      CheckBox checkbox = (CheckBox) currentTag.getChildAt(0);
      checkbox.toggle();
      if (checkbox.isChecked()) {
        selectedTags.add(currentTag);
      } else {
        selectedTags.remove(currentTag);
      }
    });
  }

  /**
   * Read Mode: click question tags to go to the questions
   * @param currentTag LinearLayout of the current question tag
   */
  private void setQuestionTagReadMode(LinearLayout currentTag) {
    currentTag.setOnClickListener(v -> {
      Intent intent = new Intent(this, QuestionActivity.class);
      String questionTitle = ((TextView) currentTag.getChildAt(2)).getText().toString();
      intent.putExtra("questionTitle", questionTitle);
      intent.putExtra("questionId", currentTag.getId());
      intent.putExtra("groupName", dataStore.getGroupName());
      startActivity(intent);
    });
  }

  private void initEditQuestionBar() {
    editQuestionContainer = findViewById(R.id.editQuestionContainer);

    // delete button handler
    editQuestionContainer.getChildAt(0).setOnClickListener(v -> {
      for (LinearLayout tag : selectedTags) {
        questionsContainer.removeView(tag);
        dataStore.deleteQuestionActivity(tag.getId());
      }
      exitEditMode();
    });

    // cancel button handler
    editQuestionContainer.getChildAt(1).setOnClickListener(v -> exitEditMode());
  }

  /**
   * hide all edit buttons, clear selected tag set
   */
  private void exitEditMode() {
    for (int i = 0; i < questionsContainer.getChildCount(); i++) {
      LinearLayout currentTag = (LinearLayout) questionsContainer.getChildAt(i);
      CheckBox box = (CheckBox) currentTag.getChildAt(0);
      box.setVisibility(View.GONE);
      box.setChecked(false);
      setQuestionTagReadMode(currentTag);
    }
    selectedTags.clear();
    editQuestionContainer.setVisibility(View.GONE);
    groupNameTextView.setVisibility(View.VISIBLE);
    addQuestionBtn.setVisibility(View.VISIBLE);
    setStatusPopupWindow.dismiss();
  }

  private void initAddQuestionPopupView() {
    // inflate the layout of the popup window
    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    LinearLayout addQuestionPopupView = (LinearLayout) inflater.inflate(R.layout.add_question_popup, null);

    EditText editTitle = addQuestionPopupView.findViewById(R.id.questionPopupEditTitle);
    Spinner statusSpinner = addQuestionPopupView.findViewById(R.id.questionPopupStatusSpinner);
    LinearLayout defaultEmojiList = addQuestionPopupView.findViewById(R.id.questionPopupEmojiList);
    ImageView addEmojiIcon = addQuestionPopupView.findViewById(R.id.questionPopupAddEmojiIcon);
    Button addNewQuestionButton = addQuestionPopupView.findViewById(R.id.questionPopupAddBtn);

    HashMap<String, Pair<Integer, Boolean>> defaultEmojis = new HashMap<>();

    addEmojiIcon.setOnClickListener(v -> {
      // show emojiSelector, update defaultEmojis, add to screen view
      emojiSelectorHandler(defaultEmojiList, defaultEmojis, addQuestionPopupView);
    });

    // status
    ArrayAdapter<TagStatus> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TagStatus.values());
    statusSpinner.setAdapter(adapter);

    // popup add button
    addNewQuestionButton.setOnClickListener(v -> {
      String questionText = editTitle.getText().toString();
      TagStatus selectedStatus = (TagStatus) statusSpinner.getSelectedItem();
      addNewQuestionTag(questionText, selectedStatus, defaultEmojis, -1);
      addQuestionPopupWindow.dismiss();
    });

    // + icon add button
    addQuestionBtn = findViewById(R.id.addQuestionBtn);
    addQuestionBtn.setOnClickListener(v -> {
      // create the popup window
      int width = LinearLayout.LayoutParams.MATCH_PARENT;
      int height = LinearLayout.LayoutParams.WRAP_CONTENT;
      addQuestionPopupWindow = new PopupWindow(addQuestionPopupView, width, height, true);  // tap outside to dismiss
      // show the popup window
      // which view you pass in doesn't matter, it is only used for the window token
      addQuestionPopupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    });
  }

  private void emojiSelectorHandler(LinearLayout defaultEmojiList, HashMap<String, Pair<Integer, Boolean>> defaultEmojis, LinearLayout addQuestionPopupView) {
    List<Emoji> allEmojis = new ArrayList<>(EmojiManager.getAll()).subList(0, EMOJI_PICKER_ROW_COUNT * EMOJI_PICKER_COL_COUNT);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    int screenWidth = displayMetrics.widthPixels;

    // inflate the layout of the popup window
    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    GridLayout emojiPickerPopupView = (GridLayout) inflater.inflate(R.layout.emoji_picker, null);

    for (Emoji e : allEmojis) {
      String emojiUnicode = e.getUnicode();

      TextView curr_emoji = new TextView(this);
      curr_emoji.setText(emojiUnicode);
      curr_emoji.setTextSize(28);
      curr_emoji.setWidth(screenWidth / EMOJI_PICKER_COL_COUNT);
      curr_emoji.setGravity(Gravity.CENTER_HORIZONTAL);
      curr_emoji.setOnClickListener(view -> {
        if (!defaultEmojis.containsKey(emojiUnicode)) {
          LinearLayout emojiEntry = (LinearLayout) inflater.inflate(R.layout.emoji_entry, defaultEmojiList, false);
          emojiEntry.setOnClickListener(v -> {
            defaultEmojiList.removeView(emojiEntry);
            defaultEmojis.remove(emojiUnicode);
          });
          ((TextView) emojiEntry.getChildAt(0)).setText(emojiUnicode);
          defaultEmojiList.addView(emojiEntry);
          defaultEmojis.put(emojiUnicode, new Pair<>(0, false));
        }
        emojiPickerPopup.dismiss();
      });
      emojiPickerPopupView.addView(curr_emoji);
    }

    int width = LinearLayout.LayoutParams.MATCH_PARENT;
    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
    emojiPickerPopup = new PopupWindow(emojiPickerPopupView, width, height, true);  // tap outside to dismiss
    emojiPickerPopup.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
  }
}