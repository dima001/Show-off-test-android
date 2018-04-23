package com.example.dimab.firstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class movieDetailActivty extends AppCompatActivity {

    private SQLiteDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail_activty);

        db = new SQLiteDatabaseHandler(this);

        //receiving movie title from previous activity
        Intent intent = getIntent();
        String movieString = intent.getStringExtra("com.example.dimab.firstapp.movie");

        //get movie details from the DB by it name(title)
        Movie movie = db.getMovie(movieString);

        //finding views elements
        ImageView imageView = (ImageView) findViewById(R.id.ImageView);
        TextView textViewTitle = findViewById(R.id.title);
        TextView textViewRating = findViewById(R.id.rating);
        TextView textViewYear = findViewById(R.id.year);
        TextView textViewGenre = findViewById(R.id.genre);

        //"cleaning" genre list from unnecessary characters
        String genreWithoutComma = movie.getGenre().replace("\",\""," ,");
        String genreWithoutOpenParentheses = genreWithoutComma.replace("[\"","");
        String genre = genreWithoutOpenParentheses.replace("\"]","");

        //fill movie info into view elements
        textViewTitle.setText(movie.getTitle());
        textViewRating.setText(Double.toString(movie.getRating()));
        textViewYear.setText(Integer.toString(movie.getReleaseYear()));
        textViewGenre.setText(genre);

        //uses glide to download image of the movie from the internet and insert it into imageView
        Glide.with(movieDetailActivty.this).load(movie.getImage()).into(imageView);
    }
}
