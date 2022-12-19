package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private GridLayout groupTagContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupTagContainer = findViewById(R.id.groupTagContainer);

        renderPageData();

        findViewById(R.id.button_add_group).setOnClickListener(v -> {
            Intent intent = new Intent(this, JoinGroupActivity.class);
            startActivity(intent);
        });
    }

    private void renderPageData() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        Set<String> allGroupNames = MainActivityDataStore.getInstance().getAllGroupNames();

        // not efficient but correct, clear the screen first, then re-add all groups
        groupTagContainer.removeAllViews();

        for (String groupName : allGroupNames) {
            LinearLayout groupTag = (LinearLayout) inflater.inflate(R.layout.group_tag, groupTagContainer, false);
            ((TextView) groupTag.getChildAt(0)).setText(groupName);
            groupTag.setOnClickListener(v -> {
                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("groupname_value", groupName);
                startActivity(intent);
            });
            groupTagContainer.addView(groupTag);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        // When BACK BUTTON is pressed, the activity on the stack is restarted
        renderPageData();
    }
}