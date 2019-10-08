package com.example.showtime;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.showtime.Async.GetMovieDetailsAsync;
import com.example.showtime.Database.Movies_database;
import com.example.showtime.entity.Genre;
import com.example.showtime.entity.MovieItemClass;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;

public class Show_Movie_Details extends AppCompatActivity {

    ImageButton cancel;
    TextView moviesummary;
    TextView movietitle;
    ImageView poster;
    MovieDetailControls movieDetailControls;
    GetMovieDetailsAsync getMovieDetailsAsync;
    Long movie_id;
    MovieItemClass movieItemClass;
    String basePosterUrl;
    RatingBar ratingBar;
    TextView votecount;
    SetRating RatingDialogFragment;
    TextView releaseDate;
    ChipGroup genre_chipGroup;
    private String share_intent_title;
    private String basePosterurl;
    private String watch_this;
    MovieItemClass notification_movie_item;

    private String[] movie_times;
    private String[] cinema_locations;
    private String[] cinema_addresses;


    private String cinema_name;
    private String movie_time;
    private String moviedate;
    private String cinema_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //isTablet = getResources().getBoolean(R.bool.isTablet);
        setContentView(R.layout.movie_details);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        moviesummary = (TextView) findViewById(R.id.movie_full_summary);
        poster = (ImageView) findViewById(R.id.detailsPoster);
        ratingBar = (RatingBar)findViewById(R.id.average_rating);
        votecount = (TextView)findViewById(R.id.vote_count);
        releaseDate = (TextView)findViewById(R.id.release_date_details);
        genre_chipGroup = (ChipGroup)findViewById(R.id.genre_tags);

        share_intent_title = getResources().getString(R.string.intent_share_title);
        basePosterurl = getString(R.string.base_poster_url);
        watch_this = getString(R.string.watch_this);

        movie_times = getResources().getStringArray(R.array.movie_times);
        cinema_locations = getResources().getStringArray(R.array.theater_names);
        cinema_addresses = getResources().getStringArray(R.array.theater_addresses);

        movieItemClass = getIntent().getParcelableExtra("movie_detail");
        setTitle(movieItemClass.getTitle());
        //movietitle.setText(movieItemClass.getTitle());

        moviesummary.setText(movieItemClass.getOverview());
        String pattern = "M/d/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(movieItemClass.getReleaseDate());
        releaseDate.setText(date);
        ratingBar.setNumStars(10);
        ratingBar.setRating(movieItemClass.getVoteAverage());
        ratingBar.setClickable(false);

        Movies_database movies_database = new Movies_database(Show_Movie_Details.this);
        for(int i = 0; i < movieItemClass.getGenreId().size(); i++) {
            Chip chip = new Chip(this);
            chip.setPadding(10, 5, 10, 5);
            Genre genre = movies_database.findGenreNamebyMovieID(movieItemClass.getGenreId().get(i));
            chip.setText(genre.getName());
            genre = null;
            genre_chipGroup.addView(chip);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("myChannelId", "My Channel", importance);
            channel.setDescription("Reminders");
// Register the channel with the notifications manager
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
        }

        votecount.setText(Long.toString(movieItemClass.getVoteCount()) + " (" +  Float.toString(movieItemClass.getVoteAverage()) + ")");

        basePosterUrl = getString(R.string.base_poster_url);

        //System.out.println(basePosterUrl  + movieItemClass.getPosterUrl());
        Picasso.get()
                .load(basePosterUrl + movieItemClass.getPosterUrl())
                .placeholder(R.drawable.ic_movie_purple_black_24dp)
                .error(R.drawable.ic_movie_purple_black_24dp)
                .into(poster);

        notification_movie_item = movies_database.SelectPreference(movieItemClass.getGenreId(), 0, movieItemClass.getId());

        if(notification_movie_item != null){

            GenerateRandomMovietheater();

            //Bitmap licon = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);
            //Assign BigText style notification
           NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(cinema_name + System.lineSeparator() + cinema_address  + System.lineSeparator() + moviedate + " " + movie_time);
            bigText.setSummaryText("Movie Recommendation");


            NotificationCompat.Builder mBuilder = null;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                 mBuilder = new NotificationCompat.Builder(Show_Movie_Details.this, "myChannelId")
                        .setSmallIcon(R.drawable.ic_info_blue_24dp)
                        .setContentTitle(notification_movie_item.getTitle())
                         //.setLargeIcon(test)
                        .setStyle(bigText);
            }
            else {
                mBuilder = new NotificationCompat.Builder(Show_Movie_Details.this)
                        .setSmallIcon(R.drawable.ic_info_blue_24dp)
                        .setContentTitle(notification_movie_item.getTitle())
                        //.setLargeIcon(null)
                        .setStyle(bigText);
            }

            int mNotificationId = 001;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // It will display the notification in notification bar
            notificationManager.notify(mNotificationId, mBuilder.build());


           /* NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_info_blue_24dp)
                            .setContentTitle("Reccomendation " + movieItemClass.getTitle())
                            .setContentText(cinema_name + System.lineSeparator() + cinema_address + System.lineSeparator() + movie_time); */

           /* Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);*/

            // Add as notification
            //NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //manager.notify(0, builder.build());
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.details_menu, menu);
            return true;
        }

        catch (Exception u){
            u.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent i = null;

        switch (item.getItemId()) {


            case R.id.give_a_rating: {
                FragmentManager fm = getSupportFragmentManager();
                RatingDialogFragment = SetRating.newInstance(movieItemClass.getTitle(), movieItemClass.getId(), movieItemClass.getPosterUrl());
                RatingDialogFragment.show(fm, "fragment_edit_name");
                break;
            }
            case R.id.share_choice:{
                ShareMovieDetailsLink(movieItemClass);
                break;
            }

            case R.id.item_close:
            {
                this.closeOptionsMenu();
                break;
            }
        }

        return true;
    }

    private void ShareMovieDetailsLink(MovieItemClass movieItemClass){
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        share.putExtra(Intent.EXTRA_SUBJECT,  watch_this + " " + movieItemClass.getTitle());
        share.putExtra(Intent.EXTRA_TEXT, System.lineSeparator() + basePosterurl + movieItemClass.getPosterUrl() + System.lineSeparator() + System.lineSeparator() + movieItemClass.getOverview());
        startActivity(Intent.createChooser(share, share_intent_title));
    }

    private void GenerateRandomMovietheater(){
        cinema_name =  cinema_locations[new Random().nextInt(cinema_locations.length)];
        cinema_address = cinema_addresses[new Random().nextInt(cinema_addresses.length)];
        movie_time = movie_times[new Random().nextInt(movie_times.length)];

        //Date c = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance(); //current date and time
        cal.add(Calendar.DAY_OF_MONTH, 2); //add a day
        long millis = cal.getTimeInMillis();

        SimpleDateFormat df = new SimpleDateFormat("M/d/yy");
        moviedate = df.format(cal.getTime());

        //moviedate =
    }

  /*  @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }*/

}
