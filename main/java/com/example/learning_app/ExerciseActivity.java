package com.example.learning_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> exercise;
    private ArrayList<HashMap<String, String>> falseExercise = new ArrayList<>();
    private int questionNum = 0;
    private int buttonMode = 0;
    private int exerciseLen = 0;

    private EditText input;
    private TextView question;
    private TextView answer;
    private TextView judgement;
    private InputMethodManager mInputMethodManager;
    private Button button;
;
    private LinearLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Intent intent = getIntent();
        exercise = (ArrayList) intent.getSerializableExtra("exercise_info");

        exerciseLen  = exercise.size();

        question = findViewById(R.id.tvQuestion);
        judgement = findViewById(R.id.tvJudgement);
        answer = findViewById(R.id.tvAnswer);
        input = findViewById(R.id.etInput);
        button = findViewById(R.id.btProgress);

        mMainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        onProgressButtonClick btListener = new onProgressButtonClick();
        button.setOnClickListener(btListener);

        refreshQuestion();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //キーボードを隠す
        mInputMethodManager.hideSoftInputFromWindow(mMainLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //背景にフォーカスを移す
        //mMainLayout.requestFocus();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.interruption_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean returnVal = true;
        int itemId = item.getItemId();

        if(itemId == R.id.interruptionOption) {
            finish();
        }else {
            returnVal = super.onOptionsItemSelected(item);
        }

        return returnVal;
    }

    private void refreshQuestion() {
        judgement.setText("");
        answer.setText("");
        input.setText("");
        input.setFocusable(true);
        input.setFocusableInTouchMode(true);

        button.setText("回答");
        question.setText(exercise.get(questionNum).get("question"));
    }

    private class onProgressButtonClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if(buttonMode == 1) {
                buttonMode = 0;
                Log.i("check_index",
                        "questionNum: " + questionNum +
                                "  exerciseLen: " + exerciseLen);
                if(questionNum == exerciseLen - 1) {
                    Intent intent = new Intent(ExerciseActivity.this, LookBackActivity.class);
                    intent.putExtra("false_exercise_info", falseExercise);
                    intent.putExtra("exercise_len", exerciseLen);
                    startActivity(intent);

                    finish();
                }
                //以下の処理をelseに入れないとエラーが起こる。
                //finish()後も以下の処理は続くのでは。
                else {
                    questionNum += 1;
                    refreshQuestion();
                }
            }
            else {
                buttonMode = 1;
                String inputStr = input.getText().toString();
                String questionStr = exercise.get(questionNum).get("question");
                String answer1 = exercise.get(questionNum).get("answer1");
                String answer2 = exercise.get(questionNum).get("answer2");

                input.setFocusable(false);

                if(!(inputStr.equals("")) &&
                        ((inputStr.equals(answer1)) || (inputStr.equals(answer2)))) {
                    judgement.setText("正解！");
                }
                else {
                    judgement.setText("不正解");

                    HashMap<String, String> map = new HashMap<>();
                    map.put("question", questionStr);
                    map.put("answer", answer1);
                    falseExercise.add(map);

                }
                answer.setText("解答：\n" + answer1);
                if(questionNum == exerciseLen - 1) {
                    button.setText("終了");
                }
                else {
                    button.setText("次の問題へ");
                }

            }
        }
    }
}