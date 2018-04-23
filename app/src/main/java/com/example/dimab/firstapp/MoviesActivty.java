package com.example.dimab.firstapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MoviesActivty extends AppCompatActivity  implements ZXingScannerView.ResultHandler{

        //definition for permission request
        private final int REQUEST_CODE=123;

        private String TAG = MainActivity.class.getSimpleName();

        //for QR code scanner
        private ZXingScannerView zXingScannerView;

        private ListView lv;
        private SQLiteDatabaseHandler db;

        //declaration of Movie list that we are going to display
        ArrayList<HashMap<String, String>> moviesList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.movies_activty);

            db = new SQLiteDatabaseHandler(this);


            List<Movie> movies;

            //get movies from the DB
            movies = db.allMovies();

            moviesList = new ArrayList<>();

            lv = (ListView) findViewById(R.id.list);

            //if we have movies so fill the movie list(moviesList)
            if(movies != null){
                //run on all movies that we received from DB and fill into moviesList
                for(int index = 0; index < movies.size(); index++) {
                    HashMap<String,String> movie = new HashMap<>();

                    movie.put("title",movies.get(index).getTitle());
                    movie.put("image",movies.get(index).getImage());
                    movie.put("rating",Double.toString(movies.get(index).getRating()));
                    movie.put("year",Integer.toString(movies.get(index).getReleaseYear()));

                    moviesList.add(movie);
                }

                //add the movies to out layout
                ListAdapter adapter = new SimpleAdapter(
                        this, moviesList,
                        R.layout.list_item, new String[]{"title", "image", "rating",
                        "year"}, new int[]{R.id.title,
                        R.id.image, R.id.rating, R.id.year});

                lv.setAdapter(adapter);
            }

            //define click listener for movie list, so when we click on movie in the list we will move to MovieDetailActivity
            final ListView listView = findViewById(R.id.list);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                    Intent intent = new Intent(MoviesActivty.this, movieDetailActivty.class);
                    //pass movie name(title) to next activty
                    String movieString = moviesList.get(position).get("title");
                    intent.putExtra("com.example.dimab.firstapp.movie", movieString);
                    //move to next activty but don't kill current one.
                    startActivity(intent);
                }
            });
        }

    public void scan(View view){
        //check if we have permissions to camera if not ask for them
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // request Permissions for camera
            ActivityCompat.requestPermissions(MoviesActivty.this,
                    new String[] {Manifest.permission.CAMERA}, REQUEST_CODE);
            return;
        }

        //scan for QR code
        doScan();

    }

    //scan QR Code
    protected void doScan(){
        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        //check for permissions
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//           return;
//        }
        if(zXingScannerView != null)
            zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        //get text from the QR Code scan
        String movie = result.getText();
        //try to convert it to Json object and save to the Data Base
        try {
            //convert result string to json
            JSONObject jsonObj = new JSONObject(movie);
            //create movie object
            Movie newMovie = new Movie(jsonObj.getString("title"), jsonObj.getString("image"),
                    jsonObj.getDouble("rating"),jsonObj.getInt("releaseYear"),jsonObj.getJSONArray("genre").toString() );
            //add to DB
            db.addMovie(newMovie);
            //display success message
            Toast.makeText(getApplicationContext(),"Succeeded",Toast.LENGTH_SHORT).show();
        }catch(final JSONException e){
            //handel error
            Toast.makeText(getApplicationContext(),"Bad QR Code.",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }

        //after the scan return to our movies list activity
        Intent returnActivty = new Intent(MoviesActivty.this, MoviesActivty.class);
        startActivity(returnActivty);
        finish();
    }

    //handel the premission request answer from the user
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Clima", "onRequestPermissionResult() Permission granted!");
                //if yes so do scan
                doScan();
            } else {
                Log.d("Clima", "Permission denied =( ");
            }
        }
    }

    //over ride back button pressed code so we can return from camera to our app and not exit from the app.
    @Override
    public void onBackPressed()
    {
        if(zXingScannerView != null) {
            startActivity(new Intent(MoviesActivty.this,MoviesActivty.class));
            this.finish();
        }
        else
            super.onBackPressed();
        return;
    }
}
