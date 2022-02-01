package com.example.learning_app;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FieldSelectActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "Async";
    private static final String FIELDINFO_URL =
            "URL(ここを書き換える)/get_json_from_csv.php?file=./";
    private List<HashMap<String, String>> fields;
    private String folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_select);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        fields = (List) intent.getSerializableExtra("fields_info");
        folder = intent.getStringExtra("folder");

        ListView lvFieldList = findViewById(R.id.lvFieldList);
        String[] from = {"name"};
        int[] to = {android.R.id.text1};
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
                fields, android.R.layout.simple_list_item_1, from, to);
        lvFieldList.setAdapter(adapter);
        lvFieldList.setOnItemClickListener(new ListItemClickListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean returnVal = true;
        int itemId = item.getItemId();

        if(itemId == android.R.id.home) {
            finish();
        }else {
            returnVal = super.onOptionsItemSelected(item);
        }

        return returnVal;
    }

    @UiThread
    public void receiveExerciseInfo(final String urlFull) {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);
        ExerciseInfoBackgroundReceiver backgroundReceiver =
                new ExerciseInfoBackgroundReceiver(handler, urlFull);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(backgroundReceiver);
    }

    private class ExerciseInfoBackgroundReceiver implements Runnable {

        private final Handler _handler;
        private final String _urlFull;

        public ExerciseInfoBackgroundReceiver(Handler handler, String urlFull) {
            _handler = handler;
            _urlFull = urlFull;
        }

        @WorkerThread
        @Override
        public void run() {

            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try {
                URL url = new URL(_urlFull);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(1000);
                con.setReadTimeout(1000);
                con.setRequestMethod("GET");
                con.connect();
                is = con.getInputStream();
                result = is2String(is);
            }
            catch(MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            }
            catch(SocketTimeoutException ex) {
                Log.w(DEBUG_TAG, "通信タイムアウト", ex);
            }
            catch(IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            }
            finally {
                if (con != null) {
                    con.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream変換失敗", ex);
                    }
                }
            }

            ExerciseInfoPostExecutor postExecutor = new ExerciseInfoPostExecutor(result);
            _handler.post(postExecutor);
        }

        private String is2String(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            char[] b = new char[1024];
            int line;
            while(0 <= (line = reader.read(b))) {
                sb.append(b, 0, line);
            }
            return sb.toString();
        }
    }

    private class ExerciseInfoPostExecutor implements Runnable {

        private final String _result;

        public ExerciseInfoPostExecutor(String result) {
            _result = result;
        }

        @UiThread
        @Override
        public void run() {
            ArrayList<Map<String, String>> exercise = new ArrayList<>();
            try {
                JSONArray rootJSON = new JSONArray(_result);
                for(int i=0; i<rootJSON.length(); i++) {
                    JSONObject field_jo = rootJSON.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("question", field_jo.getString("question"));
                    map.put("answer1", field_jo.getString("answer1"));
                    map.put("answer2", field_jo.getString("answer2"));
                    exercise.add(map);
                }
            }
            catch(JSONException ex) {
                Log.e(DEBUG_TAG, "JSON解析失敗", ex);
            }


            Intent intent = new Intent(FieldSelectActivity.this, ExerciseActivity.class);
            intent.putExtra("exercise_info", exercise);
            startActivity(intent);
        }
    }

    private class ListItemClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String, String> item = fields.get(position);
            String file = item.get("file");
            String urlFull = FIELDINFO_URL + folder + "/" + file;

            receiveExerciseInfo(urlFull);
        }
    }
}