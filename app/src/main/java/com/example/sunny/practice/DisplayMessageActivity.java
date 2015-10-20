package com.example.sunny.practice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);
        setContentView(R.layout.activity_display_message);

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        TextView textView2 = (TextView)findViewById(R.id.display_question);
        textView2.setMovementMethod(new ScrollingMovementMethod());
        textView2.setTextSize(20);
        textView2.setText(TalkWithWatson.sQuestion);

        TextView textView = (TextView)findViewById(R.id.display_answer);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setTextSize(20);
        textView.setText(message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ask)
        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        else if(id== R.id.action_compose)
        {
            Intent intent = new Intent(this, ComposeMusic.class);
            // intent.putExtra(EXTRA_MESSAGE, msg);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
