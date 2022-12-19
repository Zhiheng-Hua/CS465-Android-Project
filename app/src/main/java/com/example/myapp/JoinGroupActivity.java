package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class JoinGroupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_group_layout);

        findViewById(R.id.join_button).setOnClickListener(v -> {
            // join group (group must exist)
            String join_code = getInputJoinCode();
            String groupName = MainActivityDataStore.getInstance().findGroupByCode(join_code);
            if (groupName!= null) {
                // this will add group in record, and when main page re-render, new group will be shown
                MainActivityDataStore.getInstance().joinNewGroupActivity(groupName, join_code);
                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("groupname_value", groupName);
                startActivity(intent);
                finish();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("GROUP NOT FIND, PLEASE MAKE SURE JOIN CODE IS VALID")
                        .setPositiveButton(android.R.string.yes, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                clearJoinCodeInput();
            }
        });

        findViewById(R.id.create_option).setOnClickListener(v -> {
            // navigate to create group page
            Intent intent = new Intent(this, CreateGroupActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * get join code from the edit text views
     * @return join code the user input
     */
    private String getInputJoinCode() {
        LinearLayout container = findViewById(R.id.join_code_input_container);
        StringBuilder joinCode = new StringBuilder();
        for (int i = 0; i < container.getChildCount(); i++) {
            joinCode.append(((EditText) container.getChildAt(i)).getText().toString());
        }
        return joinCode.toString();
    }

    /**
     * clear all join code input, used in error handling
     */
    private void clearJoinCodeInput() {
        LinearLayout container = findViewById(R.id.join_code_input_container);
        for (int i = 0; i < container.getChildCount(); i++) {
            ((EditText) container.getChildAt(i)).setText("");
        }
    }
}
