package com.example.kristoffer.graphimaging;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (getSupportActionBar() != null) {
            assert mActionBar != null;
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
