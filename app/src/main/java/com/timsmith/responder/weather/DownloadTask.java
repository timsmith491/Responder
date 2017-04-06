package com.timsmith.responder.weather;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadTask extends AsyncTask<String, Void, String>{




    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {

            url = new URL(urls[0]);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            JSONObject jsonObject = new JSONObject(result);


            JSONObject weatherData = new JSONObject(jsonObject.getString("main"));

            double temperature = Double.parseDouble(weatherData.getString("temp"));
            int tempInteger = (int) (temperature - 273.15);

//            JSONObject weatherConditions = new JSONObject(jsonObject.getString("weather"));
//            String conditions = weatherConditions.getString("description");

            String placeName = jsonObject.getString("name");
            final String DEGREE  = "\u00b0";

            WeatherActivity.temperatureTextView.setText(String.valueOf(tempInteger) + (DEGREE) + "C");
            WeatherActivity.placeTextView.setText(placeName);
//            WeatherActivity.conditionsTextView.setText(conditions);


//            JSONArray jsonArray = new JSONArray(weatherInfo);
//
//            for (int i = 0; i < jsonArray.length(); i++){
//                JSONObject jsonPart = jsonArray.getJSONObject(i);
////                JSONObject weatherData =
//
//            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
