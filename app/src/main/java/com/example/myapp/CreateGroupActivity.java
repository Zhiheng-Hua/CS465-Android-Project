package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CreateGroupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_layout);

        findViewById(R.id.create_button).setOnClickListener(v -> {
            // create a new group
            String groupName = ((EditText) findViewById(R.id.groupname_response)).getText().toString();
            MainActivityDataStore.getInstance().createNewGroupActivity(groupName);

            Intent intent = new Intent(this, GroupActivity.class);
            intent.putExtra("groupname_value", groupName);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.join_option).setOnClickListener(v -> {
            // navigate to join group page
            Intent intent = new Intent(this, JoinGroupActivity.class);
            startActivity(intent);
            finish();
        });
    }
}