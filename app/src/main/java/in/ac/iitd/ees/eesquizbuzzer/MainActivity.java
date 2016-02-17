package in.ac.iitd.ees.eesquizbuzzer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    TextView indicatorText;
    Button answerButton;
    Firebase firebase;
    Firebase isQuestionActiveRef, isQuestionAnsweredRef;
    boolean isQuestionActive, isQuestionAnswered, isAnsweredByMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        isQuestionActive = false;
        isQuestionAnswered = false;
        isAnsweredByMe = false;
        firebase = new Firebase("https://quiz-buzzer.firebaseio.com/");
        isQuestionActiveRef = firebase.child("isQuestionActive");
        isQuestionAnsweredRef = firebase.child("isQuestionAnswered");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAnswerButtonClick(View view) {
        Log.d(TAG,"-->button clicked");
        if (!isQuestionAnswered) {
            isAnsweredByMe = true;
            isQuestionAnsweredRef.setValue(true);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        indicatorText = (TextView) findViewById(R.id.indicatorTextView);
        answerButton = (Button) findViewById(R.id.answerButton);
//        answerButton.setClickable(false);

        isQuestionActiveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    return;
                Log.d(TAG, "dataSnapshot=" + dataSnapshot);
                isQuestionActive = dataSnapshot.getValue(boolean.class);
                if (!isQuestionActive) {
                    isAnsweredByMe = false;
                    indicatorText.setText("Wait For Question");
                    answerButton.setText("Wait!");
                    answerButton.setEnabled(false);
                    answerButton.setBackgroundColor(Color.parseColor("#cccccc"));
                } else {
                    indicatorText.setText("Press Button to Answer!!");
                    isAnsweredByMe = false;
                    answerButton.setText("Answer");
                    answerButton.setEnabled(true);
                    answerButton.setBackgroundColor(Color.parseColor("#00ffff"));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        isQuestionAnsweredRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    return;
                Log.d(TAG,"dataSnapshot=" + dataSnapshot);
                isQuestionAnswered = dataSnapshot.getValue(boolean.class);
                if (!isQuestionActive)
                    return;
                if (isQuestionAnswered) {
                    if (isAnsweredByMe) {
                        indicatorText.setText("You pressed buzzer first!!");
                        answerButton.setText("Fast :)");
                        answerButton.setEnabled(false);
                        answerButton.setBackgroundColor(Color.parseColor("#00ff00"));
                    } else {
                        indicatorText.setText("Somebody else already pressed the buzzer!!");
                        answerButton.setText("Slow :(");
                        answerButton.setEnabled(false);
                        answerButton.setBackgroundColor(Color.parseColor("#ff0000"));
                    }
                } else {
                    //reset again and make question active??
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
