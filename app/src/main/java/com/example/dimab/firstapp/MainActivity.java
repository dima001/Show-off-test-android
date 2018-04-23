package com.example.dimab.firstapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private SQLiteDatabaseHandler db;
    private String TAG = MainActivity.class.getSimpleName();

    // URL to get contacts JSON
    private static String url = "https://api.androidhive.info/json/movies.json";

    ArrayList<HashMap<String, String>> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize DataBase
        db = new SQLiteDatabaseHandler(this);

        //rest Move List
        movieList = new ArrayList<>();

        //get movies from the "server" and save them in DB
        new GetMoviesIntoDB().execute();
    }


    private class GetMoviesIntoDB extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {

                    JSONArray jsonArry = new JSONArray(jsonStr);

                    // looping through All movies
                    for (int i = 0; i < jsonArry.length(); i++) {
                        JSONObject c = jsonArry.getJSONObject(i);
                        String title = c.getString("title");
                        String image = c.getString("image");
                        double rating = c.getDouble("rating");
                        int releaseYear = c.getInt("releaseYear");

                        //getting movies genre list
                        JSONArray genreArray = c.getJSONArray("genre");
                        String genre = genreArray.toString();

                        //construct movie object
                        Movie movie = new Movie(title, image, rating, releaseYear, genre);
                        //save the movie to Data Base
                        db.addMovie(movie);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //move to Movies activity
            Intent intent = new Intent(MainActivity.this, MoviesActivty.class);
            startActivity(intent);
            finish();
        }

    }
}
