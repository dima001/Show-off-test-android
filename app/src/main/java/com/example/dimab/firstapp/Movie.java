package com.example.dimab.firstapp;

public class Movie {
    private String title;
    private String image;
    private double rating;
    private int releaseYear;
    private String genre;

    //empty c-tor
    public Movie() {
    }

    //constructor
    public Movie(String title, String image, double rating, int releaseYear, String genre) {
        this.title = title;
        this.image = image;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.genre = genre;
    }

    //get and set of movie class fields
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    @Override
    public String toString() {
        return "title: " + title + " ,rating: " + rating + " ,releaseYear: " + releaseYear + " ,genre: " + genre;
    }

}
