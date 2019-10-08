package com.example.showtime.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.showtime.R;
import com.example.showtime.entity.Genre;
import com.example.showtime.entity.MovieItemClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


    /*
    5 total
    List Types:
    Now Playing = 0
    Upcoming = 1
    Top Rated = 2
    Popular = 3

    Search Results = 5
    */

//This is the SQL Lite Database for the Genre information and all movie information for all types
public class Movies_database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MovieDB.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_NAME = "movie";
    public static final String GENRE_TABLE_NAME = "genre_table";
    public static final String COLUMN_NAME_GENRE_ID = "genre_id";
    public static final String COLUMN_GENRE_NAME = "genre_name";

    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
    public static final String COLUMN_LIST_TYPE = "list_type";
    public static final String COLUMN_NAME_BACKDROP_URL = "backdrop_url";
    public static final String COLUMN_NAME_POSTER_URL = "poster_url";
    public static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
    public static final String COLUMN_NAME_VOTE_COUNT = "vote_count";
    public static final String COLUMN_NAME_VOTE_OVERVIEW = "overview";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_NAME_GENRE_IDS = "genre_ids";
    private static final String GENRE_ID_DIVISOR = ";";

    //List of columns in movie table
    public static final String[] COLUMNS = new String [] {COLUMN_NAME_ID, COLUMN_NAME_TITLE,
            COLUMN_NAME_RELEASE_DATE, COLUMN_LIST_TYPE, COLUMN_NAME_BACKDROP_URL, COLUMN_NAME_POSTER_URL,
            COLUMN_NAME_VOTE_AVERAGE, COLUMN_NAME_VOTE_COUNT, COLUMN_NAME_VOTE_OVERVIEW, COLUMN_POPULARITY,
            COLUMN_NAME_GENRE_IDS};

    //List of columns in genre table
    public static final String[] GENRE_COLUMNS = new String []{COLUMN_NAME_GENRE_ID, COLUMN_GENRE_NAME};


    //The string command to Create genre table
    private static final String CREATE_TABLE_GENRE = "CREATE TABLE IF NOT EXISTS "
            + GENRE_TABLE_NAME+ "(" + COLUMN_NAME_GENRE_ID + " INTEGER PRIMARY KEY,"+ COLUMN_GENRE_NAME + " TEXT );";


    //The string command to Create movie table
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (\n" +
            COLUMN_NAME_ID + " INTEGER PRIMARY KEY, \n" +
            COLUMN_NAME_TITLE + " TEXT, \n" +
            COLUMN_NAME_RELEASE_DATE + " INTEGER, \n" +
            COLUMN_LIST_TYPE + " INTEGER, \n" +
            COLUMN_NAME_BACKDROP_URL + " TEXT, \n" +
            COLUMN_NAME_POSTER_URL + " TEXT, \n" +
            COLUMN_NAME_VOTE_AVERAGE + " REAL, \n" +
            COLUMN_NAME_VOTE_COUNT + " INTEGER, \n" +
            COLUMN_NAME_VOTE_OVERVIEW + " TEXT, \n" +
            COLUMN_POPULARITY + " TEXT, \n" +
            COLUMN_NAME_GENRE_IDS + " TEXT \n" + ")";

    public Movies_database(Context context)
    {
        //context.getString(R.s)
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Insert or update a single movie list
    public void insertMovieList(ArrayList<MovieItemClass> movieItemClasses, int list_type) {
        SQLiteDatabase movieDatabase = this.getWritableDatabase();
        // movieDatabase.beginTransaction();

        //Remove previous search results if the list type is under search
        if(list_type == 5) {
            if(previousResultsExists()) {
                movieDatabase.delete(TABLE_NAME, COLUMN_LIST_TYPE + " = " + 5, null);
            }
        }

        //If the movies don't exist insert, otherwise update information.
            for (int i = 0; i < movieItemClasses.size(); i++) {
                if (!movieExists(movieItemClasses.get(i).getId())) {
                    movieDatabase.insert(TABLE_NAME, null, toContentValues(movieItemClasses.get(i), list_type));
                } else {
                    movieDatabase.update(TABLE_NAME, toContentValues(movieItemClasses.get(i), list_type), COLUMN_NAME_ID + " = ?", new String[]{Long.toString(movieItemClasses.get(i).getId())});
                }
                System.out.println("insert: " + i + " List Type: " + list_type);
            }

       // movieDatabase.close();
    }

    //Check if movie exists in database
    public boolean movieExists(Long id){
        SQLiteDatabase sqldb = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_NAME_ID + " = " + id;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }



    //Check if genre Exists
    public boolean genreExists(Long id) {
        SQLiteDatabase sqldb = this.getReadableDatabase();

        try {
            String Query = "Select * from " + GENRE_TABLE_NAME + " where " + COLUMN_NAME_GENRE_ID + " = " + id;
            Cursor cursor = sqldb.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();

            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    //Check if there are previous search results
    public boolean previousResultsExists(){
        SQLiteDatabase sqldb = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_LIST_TYPE + " ='" + 5 + "'";
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

    //Insert or update the genre list
    public void insertGenreList(ArrayList<Genre> genreList) {
        SQLiteDatabase movieDatabase = this.getWritableDatabase();


        //If genre list does not exists then create otherwise update.
        for(int i=0; i < genreList.size(); i++) {
            ContentValues contentValues = new ContentValues();


            contentValues.put(COLUMN_NAME_GENRE_ID, genreList.get(i).getId());
            contentValues.put(COLUMN_GENRE_NAME, genreList.get(i).getName());

            if(!genreExists(genreList.get(i).getId())) {
                movieDatabase.insert(GENRE_TABLE_NAME, null, contentValues);
            }
            else{
                movieDatabase.update(GENRE_TABLE_NAME, contentValues, COLUMN_NAME_GENRE_ID + " = ?", new String[]{Long.toString(genreList.get(i).getId())});
            }

            contentValues = null;
        }
    }

    //Find a movie based on the user search preference
    public MovieItemClass SelectPreference(List<Long> genreId_list, int size, long movie_id) {

        MovieItemClass movieItemClass = null;
        try {
            List<MovieItemClass> movieList = new ArrayList<>();

            float highest_vote_average = 10f;
            String genre_query = "";

            //The genres are by default added to the database as a string and each value is separated by a semicolon
            //So in here we get all the genre ids and combine them with the separators so they can be put into the query
            for (int i = 0; i < genreId_list.size(); i++) {
                genre_query = genre_query + genreId_list.get(i) + ";";
            }


            //The search query below gets all movies that match the genre list
            //Furthermore they are ordered by descending order based on the voting average
            SQLiteDatabase locationDatabase = this.getReadableDatabase();
            Cursor cursor = locationDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_GENRE_IDS + " ='" + genre_query + "'" + " ORDER BY " + COLUMN_NAME_VOTE_AVERAGE + " DESC", null);
            Cursor testcursor = cursor;

            if(!testcursor.isFirst()) {
                testcursor.moveToFirst();
            }

            //If the search results are only one and the movie is the same as before
            //Then remove one of the genre ids if the genre list is not equal to only one and activate the function again
            if (cursor.getCount() == 1 && testcursor.getLong(0) == movie_id) {
                if (genreId_list.size() != 1) {
                    genreId_list.remove(genreId_list.size() - 1);
                    return SelectPreference(genreId_list, genreId_list.size(), movie_id);
                }
            }
                //If the search results are zero and the genre list is not equal to one
                //Then remove one of the genre ids and activate the function again
                else if (cursor.getCount() == 0){
                    if (genreId_list.size() != 1) {
                        genreId_list.remove(genreId_list.size() - 1);
                       return SelectPreference(genreId_list, genreId_list.size(), movie_id);
                    }
                }

                //
             else {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    //move to the next row
                    if (cursor.getLong(0) != movie_id) {
                        MovieItemClass movie = new MovieItemClass();
                        movie.setId(cursor.getLong(0));
                        movie.setTitle(cursor.getString(1));
                        movie.setReleaseDate(new Date(cursor.getLong(2)));
                        movie.setBackdropUrl(cursor.getString(4));
                        movie.setPosterUrl(cursor.getString(5));
                        movie.setVoteAverage(cursor.getFloat(6));
                        movie.setVoteCount(cursor.getLong(7));
                        movie.setOverview(cursor.getString(8));
                        movie.setPopularity(cursor.getDouble(9));
                        ArrayList<Long> genreIdList = new ArrayList<>();
                        String[] genreIdArray = cursor.getString(10).split(GENRE_ID_DIVISOR);
                        for (String genreId : genreIdArray) {
                            if (!genreId.isEmpty()) {
                                genreIdList.add(Long.parseLong(genreId));
                            }
                        }

                        movie.setGenreId(genreIdList);
                        movieList.add(movie);
                    }
                    cursor.moveToNext();
                }
             }

             //if there is only one movie send it as the recommendation otherwise pick a random one out of the ones selected
            if(movieList.size() == 1){
                movieItemClass = movieList.get(0);
            }

            else if (movieList.size() != 0) {
                Random random = new Random();
                movieItemClass = movieList.get(random.nextInt(movieList.size()));
                random = null;
            }

            return movieItemClass;
        }
        catch (Exception e){
            e.printStackTrace();
            return movieItemClass;
        }
    }

    //List all movies that are now playing
    public List<MovieItemClass> listAllNowPlaying() {

        SQLiteDatabase locationDatabase = this.getReadableDatabase();
        Cursor cursor = locationDatabase.rawQuery("SELECT * FROM "  + TABLE_NAME + " WHERE " + COLUMN_LIST_TYPE  + " = 0" , null);
        return fromCursor(cursor);
    }



    //List all upcoming movies
    public List<MovieItemClass> listAllUpcoming() {

        SQLiteDatabase locationDatabase = this.getReadableDatabase();
        Cursor cursor = locationDatabase.rawQuery("SELECT * FROM "  + TABLE_NAME + " WHERE " + COLUMN_LIST_TYPE  + " = 1" , null);
        return fromCursor(cursor);
    }

    //List all movies under the specified list type
    //The legend is at the top of the class
    public List<MovieItemClass> listMoviesUnderType(int listtype) {

        SQLiteDatabase locationDatabase = this.getReadableDatabase();
        Cursor cursor = locationDatabase.rawQuery("SELECT * FROM "  + TABLE_NAME + " WHERE " + COLUMN_LIST_TYPE  + " = " + listtype , null);
        System.out.println("under Cursor count " + cursor.getCount());
        return fromCursor(cursor);
    }


    //Get the listed movie values from the database and return them as a List
    public List<MovieItemClass> fromCursor(Cursor cursor) {
        List<MovieItemClass> movieList = new ArrayList<>();
        while (cursor.moveToNext()) {
            MovieItemClass movie = new MovieItemClass();
            movie.setId(cursor.getLong(0));
            movie.setTitle(cursor.getString(1));
            movie.setReleaseDate(new Date(cursor.getLong(2)));
            movie.setBackdropUrl(cursor.getString(4));
            movie.setPosterUrl(cursor.getString(5));
            movie.setVoteAverage(cursor.getFloat(6));
            movie.setVoteCount(cursor.getLong(7));
            movie.setOverview(cursor.getString(8));
            movie.setPopularity(cursor.getDouble(9));
            ArrayList<Long> genreIdList = new ArrayList<>();
            String[] genreIdArray = cursor.getString(10).split(GENRE_ID_DIVISOR);

            for(String genreId : genreIdArray) {
                if(!genreId.isEmpty()) {
                    genreIdList.add(Long.parseLong(genreId));
                }
            }
            movie.setGenreId(genreIdList);

            movieList.add(movie);
        }

        return movieList;
    }

    //Assign each movie field to content values
    public ContentValues toContentValues(MovieItemClass movie, int list_type) {
        ContentValues contentValues = new ContentValues();

        try {

            contentValues.put(COLUMN_NAME_ID, movie.getId());
            contentValues.put(COLUMN_NAME_TITLE, movie.getTitle());
            contentValues.put(COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate().getTime());
            contentValues.put(COLUMN_NAME_BACKDROP_URL, movie.getBackdropUrl());
            contentValues.put(COLUMN_NAME_POSTER_URL, movie.getPosterUrl());
            contentValues.put(COLUMN_NAME_VOTE_AVERAGE, movie.getVoteAverage());
            contentValues.put(COLUMN_NAME_VOTE_COUNT, movie.getVoteCount());
            contentValues.put(COLUMN_POPULARITY, String.valueOf(movie.getPopularity()));
            contentValues.put(COLUMN_LIST_TYPE, list_type);
            contentValues.put(COLUMN_NAME_VOTE_OVERVIEW, movie.getOverview());
            StringBuilder stringBuilder = new StringBuilder();
            for (Long genreId : movie.getGenreId()) {
                stringBuilder.append(genreId);
                stringBuilder.append(GENRE_ID_DIVISOR);
            }
            contentValues.put(COLUMN_NAME_GENRE_IDS, stringBuilder.toString());

            return contentValues;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception Alert");

            return null;
    }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_GENRE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GENRE_TABLE_NAME);
    }

    //Do not remove this.
    // Used to clear databasehelper when removing corrupt data in the whole sql databasehelper table
    //Developer Testing purposes ONLY
    public void removeAll()
    {
        //databasehelper = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        SQLiteDatabase locationDatabase = this.getWritableDatabase();
        locationDatabase.delete(Movies_database.TABLE_NAME, null, null);
        locationDatabase.delete(Movies_database.GENRE_TABLE_NAME, null, null);
        //locationDatabase.close();
    }

    //Find the Genre name using the genre id from the movie class
    public Genre findGenreNamebyMovieID(Long id) {
        SQLiteDatabase locationDatabase = this.getReadableDatabase();
        Cursor cursor = locationDatabase.query(GENRE_TABLE_NAME, GENRE_COLUMNS, COLUMN_NAME_GENRE_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        Genre genre = new Genre();

        System.out.println("Cursor count " + cursor.getCount());

        if(cursor.getCount() == 1) {

            cursor.moveToFirst();

            genre.setId(cursor.getLong(0));
            genre.setName(cursor.getString(1));

            System.out.println("Name " + genre.getName());
        }
        return genre;
    }
}
