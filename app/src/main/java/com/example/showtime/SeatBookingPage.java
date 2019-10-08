package com.example.showtime;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.showtime.Database.Movies_database;
import com.example.showtime.entity.MovieItemClass;
import com.google.android.material.chip.ChipGroup;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;

public class SeatBookingPage extends AppCompatActivity{

    ChipGroup timeChipGroup;
    CalendarView calendarView;
    Spinner amount_of_tickets;
    Spinner available_theaters;
    Button confirm;
    Button cancel;
    Button pick_a_date;
    Button reserve_a_seat;
    TextView txt_total_amount;
    TextView txt_total_reservations;

    MovieItemClass movieItemClass;
    String[] mTicketsArray;
    double rangeMin = .50;
    double rangeMax = 2.59;
    DecimalFormat numberFormat;
    Calendar_Dialog_Fragment calendarDialogFragment;
    SeatPicker_Dialog_Fragment seatPicker_dialog_fragment;

    String alerttitle = null;
    String ok = null;
    String warning = null;
    String cancel_string;
    String confirm_payment;
    String selected_date;
    String selectdatestring;
    String totalamount_string;
    String yourtotalString;

    String[] addresses;
    String[] cinema_names;
    String not_available;
    String selected_date_not_avaiblabe;
    String field_empty;
    String payment_recived_message;
    String payment_received;

    String seats = null;
    int spinnerposition;
    int chip_pos = 500;
    long cal_date = 0;

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
        setContentView(R.layout.available_seats);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        reserve_a_seat = findViewById(R.id.reserve_a_seat);
        pick_a_date = findViewById(R.id.pick_a_date);
        cancel = findViewById(R.id.cancel_booking);
        confirm = findViewById(R.id.confirm_reservation);
        txt_total_amount = findViewById(R.id.total_payment_amount);
        txt_total_reservations = findViewById(R.id.txt_total_reservations);
        available_theaters = findViewById(R.id.available_cinemas);

        alerttitle = getString(R.string.booking);
        ok = getString(R.string.ok);
        warning = getString(R.string.warning);
        cancel_string = getString(R.string.cancel);
        confirm_payment = getString(R.string.confirm_payment);
        yourtotalString = getString(R.string.your_total_amount);
        field_empty = getString(R.string.field_empty);

        selectdatestring = getString(R.string.selected_date);
        not_available = getString(R.string.not_available);
        selected_date_not_avaiblabe = getString(R.string.selected_date_not_available);

        payment_received = getString(R.string.payment_recieved);
        payment_recived_message = getString(R.string.payment_recieved_message);

        Movies_database movies_database = new Movies_database(SeatBookingPage.this);

        movie_times = getResources().getStringArray(R.array.movie_times);
        cinema_locations = getResources().getStringArray(R.array.theater_names);
        cinema_addresses = getResources().getStringArray(R.array.theater_addresses);

        movieItemClass = getIntent().getParcelableExtra("movie_detail");
        setTitle(movieItemClass.getTitle());

        mTicketsArray = getResources().getStringArray(R.array.ticket_amount);
        cinema_names = getResources().getStringArray(R.array.theater_names);
        addresses = getResources().getStringArray(R.array.theater_addresses);

        spinnerposition = available_theaters.getSelectedItemPosition();

        //Get similiar movie preference
        notification_movie_item = movies_database.SelectPreference(movieItemClass.getGenreId(), 0, movieItemClass.getId());

        if(notification_movie_item != null) {

            //Create Random theater location
            GenerateRandomMovietheater();

            //Set the text in each field below and then display the movie to the user.
            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(cinema_name + System.lineSeparator() + cinema_address + System.lineSeparator() + moviedate + " " + movie_time);
            bigText.setSummaryText("Movie Recommendation");


            NotificationCompat.Builder mBuilder = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mBuilder = new NotificationCompat.Builder(SeatBookingPage.this, "myChannelId")
                        .setSmallIcon(R.drawable.ic_info_blue_24dp)
                        .setContentTitle(notification_movie_item.getTitle())
                        //.setLargeIcon(test)
                        .setStyle(bigText);
            } else {
                mBuilder = new NotificationCompat.Builder(SeatBookingPage.this)
                        .setSmallIcon(R.drawable.ic_info_blue_24dp)
                        .setContentTitle(notification_movie_item.getTitle())
                        //.setLargeIcon(null)
                        .setStyle(bigText);
            }

            int mNotificationId = 001;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // It will display the notification in notification bar
            notificationManager.notify(mNotificationId, mBuilder.build());
        }

        //available_theaters.setOnItemSelectedListener();
        available_theaters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //If the movie theater is not the same then reset all fields
                //Reason being they booked seats in a completely different location
                if(spinnerposition != position){
                    ResetFields();
                    spinnerposition = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_adapter customAdapter = new spinner_adapter(getApplicationContext(),cinema_names,addresses);
        available_theaters.setAdapter(customAdapter);


        available_theaters.setAdapter(customAdapter);


        //Open the seat picker dialog fragment
        reserve_a_seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                seatPicker_dialog_fragment = SeatPicker_Dialog_Fragment.newInstance(false, 0, seats);
                seatPicker_dialog_fragment.show(fm, "fragment_edit_name");
            }
        });

        //Open the calendar dialog fraqment
        pick_a_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                calendarDialogFragment = Calendar_Dialog_Fragment.newInstance(cal_date, chip_pos);
                calendarDialogFragment.show(fm, "fragment_edit_name");
            }
        });


        //Go back to main or search page
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalamount_string != null &&  selected_date != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SeatBookingPage.this);
                    builder.setTitle(alerttitle);
                    builder.setMessage(yourtotalString + " " + totalamount_string)
                            .setCancelable(false)
                            .setPositiveButton(confirm_payment, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SeatBookingPage.this);
                                    builder2.setTitle(payment_received);
                                    builder2.setMessage(payment_recived_message)
                                            .setCancelable(false)
                                            .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    onBackPressed();
                                                }
                                            });
                                    AlertDialog alert2 = builder2.create();
                                    alert2.show();
                                }
                            })
                            .setNegativeButton(cancel_string, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }

                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(SeatBookingPage.this);
                    builder.setTitle(warning);
                    builder.setMessage(field_empty)
                            .setCancelable(false)
                            .setPositiveButton(ok, new DialogInterface.OnClickListener() {
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setButtonText(String date, long cal_date, int chip_pos) {
        pick_a_date.setText(selectdatestring + " " + date);
        selected_date = date;
        this.chip_pos = chip_pos;
        this.cal_date = cal_date;
    }

    public void setReservationsAndTotal(String total_amount, int total_reservations, String seats){
        txt_total_amount.setText(total_amount);
        txt_total_reservations.setText(String.valueOf(total_reservations));
        this.seats = seats;
        totalamount_string = total_amount;

    }

    //Reset all fields to their default values
    private void ResetFields(){
        pick_a_date.setText(selected_date_not_avaiblabe);
        txt_total_reservations.setText(not_available);
        txt_total_amount.setText(not_available);
        totalamount_string = null;
        seats = null;
        selected_date = null;
    }

        //Generate random movie theater
        //The date will be set two days after today and the time is a random choices out of listed movie times
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

        }
}
