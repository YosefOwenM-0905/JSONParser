package com.androidrion.jsonparse;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private ListView listView;
    ArrayList<HashMap<String, String>> colorJsonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorJsonList = new ArrayList<>();
        listView = findViewById(R.id.listview);
        new GetColors().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetColors extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler httpHandler = new HttpHandler();

            // JSON data url
            String jsonurl = "https://api.jsonserve.com/vJmKnH";
            String jsonString = httpHandler.makeServiceCall(jsonurl);
            Log.e(TAG, "Response from url: " + jsonString);
            if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    // Getting JSON Array node
                    JSONArray colors = jsonObject.getJSONArray("colors");

                    for (int i = 0; i < colors.length(); i++) {
                        JSONObject c = colors.getJSONObject(i);
                        String id = c.getString("id");
                        String color = c.getString("color");
                        String type = c.getString("type");

                        JSONObject code = c.getJSONObject("code");
                        String hex = code.getString("hex");

                        HashMap<String, String> colorx = new HashMap<>();

                        colorx.put("id", id);
                        colorx.put("color", color);
                        colorx.put("type", type);
                        colorx.put("hex", hex);

                        colorJsonList.add(colorx);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Could not get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Could not get json from server.",
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing()) progressDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, colorJsonList, R.layout.list_item,
                    new String[]{"color", "type", "hex"},
                    new int[]{R.id.colorName, R.id.colorType, R.id.colorHex});

            listView.setAdapter(adapter);
        }

    }
}
