package com.example.showtime;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.example.showtime.Database.Movies_database;
import com.example.showtime.entity.MovieItemClass;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



//Populate the fragment with data before sending it the view Pager in it's main page and it's adapter
public class NowPlayingFragment extends Fragment {


    public NowPlayingFragment() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Movies_database moivies_database;

    public List<MovieItemClass> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<MovieItemClass> movieList) {
        this.movieList = movieList;
    }

    List<MovieItemClass> movieList;
    MovieAdapter movieAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_now_playing_movies, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view_now_movies);
        progressBar = rootView.findViewById(R.id.now_loading_bar);

        initView();
        getMovies();

        return rootView;
    }

    void initView() {
        moivies_database = new Movies_database(getActivity());
        movieAdapter = new MovieAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void getMovies() {

        showProgressBar();

        //Populate adapter with movies under the now playing list
        movieAdapter.setMovieResult(moivies_database.listAllNowPlaying());
        recyclerView.setAdapter(movieAdapter);

        hideProgressBar();

    }

    //Show loading circle
    void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    //Hide Loading circle
    void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<MovieItemClass> list;

        }
    }

}
