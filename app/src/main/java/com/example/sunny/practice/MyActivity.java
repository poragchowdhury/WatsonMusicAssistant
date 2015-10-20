package com.example.sunny.practice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MyActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.sunny.practice.MESSAGE";

    // An object that manages Messages in a Thread
    public static Handler mHandler;
    private Runnable mCurrentThead;
    private ProgressBar mProgress;
    public static boolean gotreply = false;
    public static int mProgressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i("Myactivity", "Oncreate");

        MyActivity.gotreply = false;
        MyActivity.mProgressStatus = 0;
        mProgress=(ProgressBar) findViewById(R.id.progressId);
        mProgress.setVisibility(View.INVISIBLE);


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "LIKEY!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage) {


                String aResponse = inputMessage.getData().getString("message");
                Log.i("Myactivity", "Handling  :" + aResponse);


                handleAnswer(aResponse);

            }

        };



    }

    private void handleAnswer(String msg) {

        Log.i("Myactivity", "handleAnswer  :" + msg);


        if(!msg.matches("")) {

            MyActivity.gotreply = true;

            Log.i("Myactivity", "Starting intent  :" + msg);
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, msg);
        startActivity(intent);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("No Answer !")
                    .setMessage("Watson has no answer for the question")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MyActivity.gotreply = true;


                        }
                    })
                    .show();

        }
    }


    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {

        Log.i("Myactivity", "sendMessage");





        EditText editText = (EditText) findViewById(R.id.edit_message);
        if(!editText.getText().toString().matches("")) {
            startProgressBar();
            String message = editText.getText().toString();
            TalkWithWatson.sQuestion = message;
            Thread thread = new Thread(new TalkWithWatson(this));
            thread.start();

        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("No Question !")
                    .setMessage("Please enter a valid question")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mProgress = (ProgressBar) findViewById(R.id.progressId);
                            mProgress.setVisibility(View.INVISIBLE);
                            // Write your code here to execute after dialog closed
                            //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .show();
        }
    }

    private void startProgressBar() {

        mProgress = (ProgressBar) findViewById(R.id.progressId);
        mProgress.setVisibility(View.VISIBLE);


        new Thread(new Runnable() {
            public void run() {
                while (MyActivity.gotreply ==false) {
                    mProgressStatus += 5;

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                        }
                    });
                }
            }
        }).start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ask) {
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

    public void setCurrentThread(Thread thread) {

            this.mCurrentThead = thread;


    }
}
