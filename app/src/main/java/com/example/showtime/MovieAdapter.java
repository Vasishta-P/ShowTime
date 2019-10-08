package com.example.showtime;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.showtime.Database.Movies_database;
import com.example.showtime.entity.MovieItemClass;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


//This is an adapter for the recycler-view and it is used as a container to store details
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<MovieItemClass> movieResultList = new ArrayList<>();
    private AppCompatActivity context;
    private String share_intent_title;
    private String basePosterurl;
    private Show_Movie_Details show_detailsDialogFragment;
    private String releasedatestring;

    private String yes;
    private String no;
    private String warning;
    private String creditcard__not_setup;
    boolean isCardSetup = false;

    public MovieAdapter(Context context) {
        this.context =  (AppCompatActivity) context;
        share_intent_title = context.getResources().getString(R.string.intent_share_title);
        basePosterurl = context.getString(R.string.base_poster_url);
        releasedatestring = context.getString(R.string.release_date);

        yes = context.getString(R.string.yes);
        no = context.getString(R.string.no);
        warning = context.getString(R.string.warning);
        creditcard__not_setup = context.getString(R.string.credit_info_not_setup);

        //Check to see credit card information is setup or not
        try {
            User_Details user_details;
            SqlUserDetails sqlUserDetails = new SqlUserDetails(context);
            user_details = sqlUserDetails.checkifCardsetup().get(0);

            if(user_details != null) {
                if(user_details.getCreditCard() != null) {
                    isCardSetup = true;
                }
            }
        }
        catch (Exception np){
            np.printStackTrace();
            isCardSetup = false;
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card,
                        parent, false)
        );
    }

    public void setMovieResult(List<MovieItemClass> movieResult) {
        this.movieResultList = movieResult;
    }

    public List<MovieItemClass> getList(){
        return movieResultList;
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {

        //This block of code below sets the text, click listeners and other data for the movie card below

        holder.item_title.setText(movieResultList.get(position).getTitle());
        String pattern = "M/d/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(movieResultList.get(position).getReleaseDate());
        holder.item_date.setText(date);
        holder.item_overview.setText(movieResultList.get(position).getOverview());

        //This sets the average movie rating value
        String ratingtext =  Float.toString(movieResultList.get(position).getVoteAverage()) + "/10.0";

        holder.ratingTextView.setText(ratingtext);

        //Get the movie poster image using picasso
            Picasso.get()
                    .load(basePosterurl + movieResultList.get(position).getPosterUrl())
                    .placeholder(R.drawable.ic_movie_purple_black_24dp)
                    .error(R.drawable.ic_movie_purple_black_24dp)
                    .into(holder.item_poster);

        //Go to movie details page
        holder.item_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Show_Movie_Details.class);
                intent.putExtra("movie_detail", movieResultList.get(position));
                context.startActivity(intent);
            }
        });


        //If the credit card information is setup go to seat booking page, otherwise prompt user with
        //a message box to either close the messagebox itself or go to credit card page and setup information
        holder.item_seatbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCardSetup) {
                    Intent intent = new Intent(context, SeatBookingPage.class);
                    intent.putExtra("movie_detail", movieResultList.get(position));
                    context.startActivity(intent);
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(warning);
                    builder.setMessage(creditcard__not_setup)
                            .setCancelable(false)
                            .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(context, credit_card_page.class);
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton(no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieResultList.size();
    }

    //View Holder
    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView item_poster;
        Button item_seatbooking;
        TextView item_title;
        Button item_detail;
        TextView item_date;
        TextView item_overview;
        TextView ratingTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            item_poster = itemView.findViewById(R.id.img_item_photo);
            item_seatbooking = itemView.findViewById(R.id.btn_seat_booking);
            item_title = itemView.findViewById(R.id.tv_item_title);
            item_detail = itemView.findViewById(R.id.btn_show_details);
            item_date = itemView.findViewById(R.id.tv_item_date);
            item_overview = itemView.findViewById(R.id.tv_short_summary);
            ratingTextView = itemView.findViewById(R.id.movie_card_ratings);

        }
    }
}
