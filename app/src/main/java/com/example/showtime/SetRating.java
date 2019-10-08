package com.example.showtime;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


//Set movie rating and send it to the changes to the movie db website
public class SetRating extends DialogFragment {

    RatingBar ratingBar;
    Button cancel;
    Button submit;
    TextView movietitle;
    ImageView ratingposter;

    String posterUrl;
    String title;
    Long id;

    String intent_movie_id  = "movie_id";
    String intent_title = "intent_title";
    String intent_poster_url = "intent_poster_url";
    String basePosterUrl;
    String ok;
    String successful_message;
    
    public SetRating() {

        // Empty constructor is required for DialogFragment

        // Make sure not to add arguments to the constructor

        // Use `newInstance` instead as shown below

    }




    public static SetRating newInstance(String title, Long id, String posterUrl) {

        SetRating frag = new SetRating();

        Bundle args = new Bundle();

        args.putString("intent_title", title);
        args.putLong("movie_id", id);
        args.putString("intent_poster_url", posterUrl);

        frag.setArguments(args);

        return frag;

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            posterUrl = getArguments().getString(intent_poster_url);
            title = getArguments().getString(intent_title);
            id = getArguments().getLong(intent_movie_id);
        }

    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.set_ratings, container);

    }



    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Get field from view

         ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
         cancel = (Button) view.findViewById(R.id.cancel_rating);
         submit = (Button) view.findViewById(R.id.submit);
         movietitle = (TextView) view.findViewById(R.id.movie_rating_title);
         ratingposter = (ImageView) view.findViewById(R.id.ratingPoster);

        basePosterUrl = getResources().getString(R.string.base_poster_url);
        successful_message = getString(R.string.rating_posted_successful);
        ok = getString(R.string.ok);

         movietitle.setText(title);


         //Get the movie poser using picasso
              Picasso.get()
                .load(basePosterUrl + posterUrl)
                .placeholder(R.drawable.ic_movie_purple_black_24dp)
                .error(R.drawable.ic_movie_purple_black_24dp)
                .into(ratingposter);


        //Close dialog fragment
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Submit changes
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title);
                builder.setMessage(successful_message)
                        .setCancelable(false)
                        .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        //Make the dialog fragment as big it can possibly be
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
