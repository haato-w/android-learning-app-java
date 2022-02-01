package com.example.learning_app;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    //ログに記載するタグ用の文字列
    private static final String DEBUG_TAG = "Async";
    //分野の項目と対応するファイル名を取得するURL
    private static final String FIELDINFO_URL =
            "URL(ここを書き換える)/get_json_name_table.php?folder=";
    //科目とそのフォルダ名を保持するHashMap
    private static final HashMap<String, String> subject_folder = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subject_folder.put("中1 地理", "jhs1_geo");
        subject_folder.put("中1 歴史", "jhs1_histo");
        subject_folder.put("中2 地理", "jhs2_geo");
        subject_folder.put("中2 歴史", "jhs2_histo");

        ListView lvSubject = findViewById(R.id.lvSubject);
        lvSubject.setOnItemClickListener(new ListItemClickListener());
    }

    //分野データの取得処理を行うメソッド
    @UiThread
    private void receiveFieldInfo(final String urlFull, final String folder) {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);
        FieldInfoBackgroundReceiver backgroundReceiver =
                new FieldInfoBackgroundReceiver(handler, urlFull, folder);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(backgroundReceiver);
    }

    //非同期で分野データAPIにアクセスするためのクラス。
    private class FieldInfoBackgroundReceiver implements Runnable {
        private final Handler _handler;

        private final String _urlFull;

        private final String _folder;

        public FieldInfoBackgroundReceiver(Handler handler, String urlFull, String folder) {
            _handler = handler;
            _urlFull = urlFull;
            _folder = folder;
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
                con.setConnectTimeout(2000);
                con.setReadTimeout(2000);
                con.setRequestMethod("GET");
                con.connect();
                final int responseCode = con.getResponseCode();
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
            catch(Exception ex) {
                String ex_str = ex.getClass().getName() + ": " + ex.getMessage();
                Log.i("check", ex_str);
            }
            finally {
                if(con != null) {
                    con.disconnect();
                }
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch(IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream開放失敗", ex);
                    }
                }
            }

            FieldInfoIntentExecutor postExecutor = new FieldInfoIntentExecutor(result, _folder);
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

    private class FieldInfoIntentExecutor implements Runnable {

        private final String _result;

        private final String _folder;

        public FieldInfoIntentExecutor(String result, String folder) {
            _result = result;
            _folder = folder;
        }

        @UiThread
        @Override
        public void run() {

            ArrayList<Map<String, String>> fields = new ArrayList<>();
            try {
                JSONArray rootJSON = new JSONArray(_result);
                for(int i=0; i<rootJSON.length(); i++) {
                    JSONObject field_jo = rootJSON.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", field_jo.getString("name"));
                    map.put("file", field_jo.getString("file"));
                    fields.add(map);
                }
            }
            catch(JSONException ex) {
                Log.e(DEBUG_TAG, "JSON解析失敗", ex);
            }


            Intent intent = new Intent(MainActivity.this, FieldSelectActivity.class);
            intent.putExtra("fields_info", fields);
            intent.putExtra("folder", _folder);
            startActivity(intent);
        }
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
            String item = (String) parent.getItemAtPosition(position);

            String urlFull = FIELDINFO_URL + subject_folder.get(item);

            receiveFieldInfo(urlFull, subject_folder.get(item));
        }
    }
}