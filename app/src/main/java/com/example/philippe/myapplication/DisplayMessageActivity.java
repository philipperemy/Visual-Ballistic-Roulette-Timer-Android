package com.example.philippe.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class DisplayMessageActivity extends AppCompatActivity {

    HttpClient httpClient;
    List<String> logs = new LinkedList<>(); //Efficient to pop/push elements to HEAD

    private void clearLogs() {
        logs = new ArrayList<>();
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.removeAllViews();
    }

    private void log(TextView textView, String newLog) {
        logs.add(newLog);
        if(logs.size() > 10) {
            logs.remove(0);
        }
        StringBuilder sb = new StringBuilder();
        for(String log : logs) {
            sb.append(log + "\n");
        }
        textView.setText(sb.toString());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearLogs();
                Snackbar.make(view, getResources().getString(R.string.logs_cleared), Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });

        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);

        log(textView,  getResources().getString(R.string.application_started));
        textView.setMovementMethod(new ScrollingMovementMethod());
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.addView(textView);
        setTitle("Device : " + message);

        String target = intent.getStringExtra(MainActivity.API_TARGET_URL_MESSAGE);

        if(target == null ||target.isEmpty()) {
            target = getResources().getString(R.string.edit_message);
        }

        log(textView, "Initiated HTTP Client with target: " + target);

        httpClient = new HttpClient(message, target);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long ms = System.currentTimeMillis();
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            TextView textView = new TextView(this);
            //textView.setTextSize(40);
            //textView.setText("key pressed (" + keyCode +") : " + System.currentTimeMillis() + " ms");
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
            layout.removeAllViews();

            String ret = "";
            try {
                ret = httpClient.sendTimeStamp(ms);
            } catch (Exception e) {
                ret = e.getMessage();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200); //100ms vibration
            }

            log(textView, ret);
            textView.setMovementMethod(new ScrollingMovementMethod());
            layout.addView(textView);
        }
        return true;
    }
}
