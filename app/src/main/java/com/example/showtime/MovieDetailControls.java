package com.example.showtime;

import android.widget.ImageView;
import android.widget.TextView;

public class MovieDetailControls {

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    TextView title;

    public TextView getSummary() {
        return summary;
    }

    public void setSummary(TextView summary) {
        this.summary = summary;
    }

    TextView summary;

    public ImageView getPoster() {
        return poster;
    }

    public void setPoster(ImageView poster) {
        this.poster = poster;
    }

    ImageView poster;



}
