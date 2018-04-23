package com.example.dimab.firstapp;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dimab.firstapp.Movie;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    //Data Base information: DB version, DB table and columns
    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "MoviesDB";
    private static final String KEY_ID = "id";
    private static final String TABLE_NAME = "Movies";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_RATING= "rating";
    private static final String KEY_RELEASEYEAR = "releaseYear";
    private static final String KEY_GENRE = "genre";
    private static final String[] COLUMNS = { KEY_ID, KEY_TITLE, KEY_IMAGE, KEY_RATING,
            KEY_RELEASEYEAR, KEY_GENRE };

    //constructor
    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //create table Movies
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Movies ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "title TEXT, " + "image TEXT, "
                + "rating REAL, " + "releaseYear INTEGER, " + "genre TEXT )";

        db.execSQL(CREATION_TABLE);
    }

    //update Data Base if we have new version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    //delete movie from the table by it's title
    public void deleteOne(Movie movie) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "title = ?", new String[] { String.valueOf(movie.getTitle()) });
        db.close();
    }

    //get one move from the database by it's title
    // if we have more then one movie it will return the first the query found
    public Movie getMovie(String title) {

        //query
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // table
                COLUMNS, // column names
                " title = ?", // condition
                new String[] { String.valueOf(title) }, // d. selections args
                null, null,null,null);

        if (cursor != null) {
            cursor.moveToFirst();
            //create movie object
            Movie movie = new Movie();
            movie.setTitle(cursor.getString(1));
            movie.setImage(cursor.getString(2));
            movie.setRating(cursor.getInt(3));
            movie.setReleaseYear(cursor.getInt(4));
            movie.setGenre(cursor.getString(5));
            return movie;
        }
        return null;
    }

    //get all movies from the Data Base from newest to oldest(by year)
    public List<Movie> allMovies() {

        List<Movie> movies = new LinkedList<Movie>();
        //query
        String query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY releaseYear DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Movie movie = null;

        if (cursor.moveToFirst()) {
            //build list of movies
            do {
                movie = new Movie();
                movie.setTitle(cursor.getString(1));
                movie.setImage(cursor.getString(2));
                movie.setRating(cursor.getInt(3));
                movie.setReleaseYear(cursor.getInt(4));
                movie.setGenre(cursor.getString(5));
                movies.add(movie);
            } while (cursor.moveToNext());
        }

        return movies;
    }

    //add movie to the Data Base if it not exist
    public void addMovie(Movie movie) {
        //check if this move already exist by it's title
        if(!getExist(movie.getTitle())) {

            //insert to data base with following values
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, movie.getTitle());
            values.put(KEY_IMAGE, movie.getImage());
            values.put(KEY_RATING, movie.getRating());
            values.put(KEY_RELEASEYEAR, movie.getReleaseYear());
            values.put(KEY_GENRE, movie.getGenre());

            // insert
            db.insert(TABLE_NAME, null, values);
            db.close();
        }
    }

    //check if movie exist in the Data Base
    public boolean getExist(String title) {

        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            //query
            cursor = db.query(TABLE_NAME, COLUMNS, " title = ?",
                    new String[] { String.valueOf(title) },
                    null, null, null, null);
            //return true if we found this move in the DB
            if (cursor.moveToFirst()) {
                return true;
            }
            return false;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                this.close();
            }
        }
    }
}
