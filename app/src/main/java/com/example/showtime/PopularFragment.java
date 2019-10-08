package com.example.showtime;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showtime.Database.Movies_database;



//Populate the fragment with data before sending it the view Pager in it's main page and it's adapter
public class PopularFragment extends Fragment {


    RecyclerView recyclerView;
    ProgressBar progressBar;
    MovieAdapter movieAdapter;
    Movies_database moivies_database;



    public PopularFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_upcoming_movies, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_movie_upcoming);
        progressBar = rootView.findViewById(R.id.progress_bar_upcoming);
        initView();
        getMovies();

        return rootView;
    }

    void initView() {
        moivies_database = new Movies_database(getActivity());
        movieAdapter = new MovieAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void getMovies() {

        showProgressBar();

        //Populate adapter with movies under the popular movies list
        movieAdapter.setMovieResult(moivies_database.listMoviesUnderType(3));
        recyclerView.setAdapter(movieAdapter);

        hideProgressBar();

    }

    //Show loading circle
    void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    //Hide loading circle
    void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        }
    }
}