package com.example.learning_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LookBackActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> falseExercise;
    private int exerciseLen;

    private TextView tvFalseLate;
    private ListView lvFalseExercise;

    private static final String[] from = {"question", "answer"};
    private static final int[] to = {R.id.tvQuestionRow, R.id.tvAnswerRow};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_back);

        Intent intent = getIntent();
        falseExercise = (ArrayList) intent.getSerializableExtra("false_exercise_info");
        exerciseLen = intent.getIntExtra("exercise_len", 0);

        tvFalseLate = findViewById(R.id.tv_false_rate);
        tvFalseLate.setText("間違えた数： " + falseExercise.size() + " / " + exerciseLen);

        lvFalseExercise = findViewById(R.id.lv_false_exercise);
        SimpleAdapter adapter = new SimpleAdapter(LookBackActivity.this,
                falseExercise, R.layout.row, from, to);
        lvFalseExercise.setAdapter(adapter);

        Button btExitLookBack = findViewById(R.id.btExitLookBack);
        onExitLookBackButtonClick btListener = new onExitLookBackButtonClick();
        btExitLookBack.setOnClickListener(btListener);

    }

    private class onExitLookBackButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

}