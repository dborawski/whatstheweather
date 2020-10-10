package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView textView;
    TextView description;
    TextView pressure;
    TextView humidity;
    TextView wind;
    TextView temperature;
    EditText textField;
    String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.main);
        textField = findViewById(R.id.editText);
        description = findViewById(R.id.details);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humdity);
        wind = findViewById(R.id.wind);
        temperature = findViewById(R.id.temperature);

    }
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL (urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray array = new JSONArray(weatherInfo);
                for (int i = 0; i<array.length(); i++){
                    JSONObject jsonPart = array.getJSONObject(i);
                    textView.setText(jsonPart.getString("main"));
                    description.setText(jsonPart.getString("description"));
                }
            }catch(Exception error){
                error.printStackTrace();
            }
            try{
                JSONObject jsonObject2 = new JSONObject(s);
                String tempInfo = jsonObject2.getString("main");
                JSONObject temp = new JSONObject(tempInfo);
                String press = temp.getString("pressure") + "hPa";
                String hum = temp.getString("humidity") + "%" ;
                String windInfo = jsonObject2.getString("wind");
                JSONObject isWind = new JSONObject(windInfo);
                float temperatureValue = Float.parseFloat(temp.getString("temp"));
                float windSpeed = Float.parseFloat(isWind.getString("speed"));
                int temperatureRounded = Math.round(temperatureValue);
                int windRounded = Math.round(windSpeed);
                String tem = temperatureRounded + "°C";
                wind.setText(windRounded + "m/s");
                temperature.setText(tem);
                pressure.setText(press);
                humidity.setText(hum);
            }catch(Exception badMove){
                badMove.printStackTrace();
            }


        }
    }
    public void onClick(View view){
        city = textField.getText().toString();
        DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=cc290739860832afa7a8937b50133b89&units=metric");
    }
}