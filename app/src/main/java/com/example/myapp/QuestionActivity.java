package com.example.myapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class QuestionActivity extends AppCompatActivity {
  private int questionId;

  private LinearLayout proposalsContainer;
  private LinearLayout confirmCancelContainer;
  private Button addProposalBtn;
  private Button confirmProposalBtn;
  private Button cancelProposalBtn;

  private RelativeLayout editProposalContainer;
  private LinearLayout selectedProp;

  private GroupDataStore dataStore;   // data Store to store all data involved (related to a group)

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_question);

    dataStore = MainActivityDataStore.getInstance().getGroupDataStore(getIntent().getStringExtra("groupName"));

    // edit question bar
    initEditQuestionBar();

    questionId = getIntent().getIntExtra("questionId", -1);

    if (questionId == -1) {
      new AlertDialog.Builder(this)
        .setTitle("Question does not exist")
        .setPositiveButton(android.R.string.yes, null)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();
      return;
    }

    ((TextView) findViewById(R.id.questionTitle)).setText(getIntent().getStringExtra("questionTitle"));

    proposalsContainer = findViewById(R.id.proposalsContainer);
    confirmCancelContainer = findViewById(R.id.confirmCancelContainer);
    addProposalBtn = findViewById(R.id.addProposalBtn);
    confirmProposalBtn = findViewById(R.id.confirmProposalBtn);
    cancelProposalBtn = findViewById(R.id.cancelProposalBtn);

    ArrayList<ProposalTagInfo> proposalsList = dataStore.getProposalsList(questionId);
    for (ProposalTagInfo info : proposalsList) {
      LinearLayout newTag = createProposalTag(info, false);
      proposalsContainer.addView(newTag);
      newTag.setOnLongClickListener(subview -> {
        // select current proposal
        selectedProp = newTag;
        for(int i = 0; i < proposalsContainer.getChildCount(); i++) {
          proposalsContainer.getChildAt(i).setBackgroundColor(0xffffffff);
        }
        newTag.setBackgroundColor(0xffffdddd);
        // show delete button
        editProposalContainer.setVisibility(View.VISIBLE);
        return true;
      });
    }

    // add new proposal button
    addProposalBtn.setOnClickListener(view -> {
      addProposalBtn.setVisibility(View.GONE);
      confirmCancelContainer.setVisibility(View.VISIBLE);

      HashMap<String, Pair<Integer, Boolean>> defaultEmojis = dataStore.getDefaultEmojiSet(questionId);
      ProposalTagInfo info = new ProposalTagInfo(ViewCompat.generateViewId(), "", defaultEmojis);
      LinearLayout proposalTag = createProposalTag(info, true);
      proposalsContainer.addView(proposalTag);

      // single click
      setProposalReadMode(proposalTag);

      // long press
      proposalTag.setOnLongClickListener(subview -> {
        // select current proposal
        selectedProp = proposalTag;
        for(int i = 0; i < proposalsContainer.getChildCount(); i++) {
          proposalsContainer.getChildAt(i).setBackgroundColor(0xffffffff);
        }
        proposalTag.setBackgroundColor(0xffffdddd);
        // show delete button
        editProposalContainer.setVisibility(View.VISIBLE);
        return true;
      });
    });
  }

  void initEditQuestionBar() {
    editProposalContainer = findViewById(R.id.editProposalContainer);

    // delete button handler
    editProposalContainer.getChildAt(0).setOnClickListener(v -> {
      proposalsContainer.removeView(selectedProp);
      //TODO: anything else to delete here??
      exitEditMode();
    });

    // cancel button handler
    editProposalContainer.getChildAt(1).setOnClickListener(v -> exitEditMode());
  }

  private void exitEditMode() {
    selectedProp.setBackgroundColor(0xffffffff);
    selectedProp = null;
    editProposalContainer.setVisibility(View.GONE);
    addProposalBtn.setVisibility(View.VISIBLE);
  }

  /**
   * Edit Mode: click to toggle tag selection
   * @param currentTag LinearLayout of the current question tag
   */
  private void setProposalEditMode(LinearLayout currentTag) {
  }

  /**
   * Read Mode: click question tags to go to the questions
   * @param currentTag LinearLayout of the current question tag
   */
  private void setProposalReadMode(LinearLayout currentTag) {

  }

  /**
   * create a proposal tag and update QuestionActivityDataStore
   * @param info ProposalTag info to init tag
   * @param isEditMode true if tag is edit mode, else false
   * @return LinearLayout of the proposal tag
   */
  private LinearLayout createProposalTag(ProposalTagInfo info, Boolean isEditMode) {
    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    LinearLayout proposalTag = (LinearLayout) inflater.inflate(R.layout.proposal_tag, proposalsContainer, false);
    LinearLayout emojiEntryContainer = (LinearLayout) proposalTag.getChildAt(2);
    proposalTag.setId(info.getId());
    ((TextView) proposalTag.getChildAt(1)).setText(info.getTitle());

    for (Entry<String, Pair<Integer, Boolean>> entry : info.getEmojiEntries().entrySet()) {
      String emojiCode = entry.getKey();
      String countText = entry.getValue().first.toString();
      Boolean isClicked = entry.getValue().second;

      LinearLayout emojiEntry = (LinearLayout) inflater.inflate(R.layout.emoji_entry, proposalTag, false);
      TextView emojiView = (TextView) emojiEntry.getChildAt(0);
      TextView countView = (TextView) emojiEntry.getChildAt(1);
      emojiView.setText(emojiCode);
      countView.setText(countText);

      if (isClicked) {
        emojiEntry.setBackgroundColor(Color.YELLOW);
        emojiEntry.setOnClickListener(v -> unClickEmojiEntry(emojiEntry, info));
      } else {
        emojiEntry.setBackgroundColor(Color.TRANSPARENT);
        emojiEntry.setOnClickListener(v -> clickEmojiEntry(emojiEntry, info));
      }
      emojiEntryContainer.addView(emojiEntry);
    }

    EditText titleInput = (EditText) proposalTag.getChildAt(0);
    TextView proposalTitle = (TextView) proposalTag.getChildAt(1);

    if (isEditMode) {
      confirmProposalBtn.setOnClickListener(v -> {
        // update screen component
        proposalTitle.setText(titleInput.getText().toString());
        titleInput.setVisibility(View.GONE);
        proposalTitle.setVisibility(View.VISIBLE);
        // update QuestionActivityDataStore
        info.setTitle(titleInput.getText().toString());
        dataStore.putProposal(questionId, info);
        // update screen buttons
        addProposalBtn.setVisibility(View.VISIBLE);
        confirmCancelContainer.setVisibility(View.GONE);
      });

      cancelProposalBtn.setOnClickListener(v -> {
        addProposalBtn.setVisibility(View.VISIBLE);
        confirmCancelContainer.setVisibility(View.GONE);
        proposalsContainer.removeView(proposalTag);
      });
    } else {
      titleInput.setVisibility(View.GONE);
      proposalTitle.setVisibility(View.VISIBLE);
    }

    return proposalTag;
  }

  @SuppressLint("SetTextI18n")
  private void clickEmojiEntry(LinearLayout emojiEntry, ProposalTagInfo info) {
    String emojiCode = ((TextView) emojiEntry.getChildAt(0)).getText().toString();
    TextView countView = (TextView) emojiEntry.getChildAt(1);
    emojiEntry.setBackgroundColor(Color.YELLOW);
    // upVoteEmoji, and change emojiEntry LinearLayout content
    countView.setText(info.upVoteEmoji(emojiCode).toString());
    // change on click method
    emojiEntry.setOnClickListener(view -> unClickEmojiEntry(emojiEntry, info));
  }

  @SuppressLint("SetTextI18n")
  private void unClickEmojiEntry(LinearLayout emojiEntry, ProposalTagInfo info) {
    String emojiCode = ((TextView) emojiEntry.getChildAt(0)).getText().toString();
    TextView countView = (TextView) emojiEntry.getChildAt(1);
    emojiEntry.setBackgroundColor(Color.TRANSPARENT);
    // downVoteEmoji, and change emojiEntry LinearLayout content
    countView.setText(info.downVoteEmoji(emojiCode).toString());
    // change on click method
    emojiEntry.setOnClickListener(view -> clickEmojiEntry(emojiEntry, info));
  }
}
