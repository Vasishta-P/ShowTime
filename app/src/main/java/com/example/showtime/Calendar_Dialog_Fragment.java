package com.example.showtime;

import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Calendar_Dialog_Fragment extends DialogFragment {

    CalendarView calendarView;
    Button confirm;
    Button cancel;
    ChipGroup chipGroup;
    String[] mTicketsArray;
    String[] mMovieTimes;
    String choosenTime;
    Chip chip;
    String final_date;
    Date calendarviewdate;
    long cal_time = 0;
    int chipgrouppos = 0;

   public Calendar_Dialog_Fragment() {
        // Required empty public constructor
    }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment Calendar_Dialog_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        public static Calendar_Dialog_Fragment newInstance (long cal_date, int chip_pos){
        Calendar_Dialog_Fragment fragment = new Calendar_Dialog_Fragment();
        Bundle args = new Bundle();
        args.putLong("cal_date", cal_date);
        args.putInt("chip_pos", chip_pos);
        fragment.setArguments(args);
        return fragment;
    }

        @Override
        public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //If the values aren't null or 0, assign them to these variables
        //After the value are assigned in the code below
        if (getArguments() != null) {
            cal_time = getArguments().getLong("cal_date");
            chipgrouppos = getArguments().getInt("chip_pos");
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        //We need to make the dialog fragment as big as possible on start up.
        //If we don't the page would either look squished or have many cut-offs
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){

            View dialogView = inflater.inflate(R.layout.activity_calendar, container, false);
            calendarView = dialogView.findViewById(R.id.calendar_view);
            confirm = dialogView.findViewById(R.id.btn_calendar_confirm);
            cancel = dialogView.findViewById(R.id.btn_cancel_calendar);
            chipGroup = dialogView.findViewById(R.id.available_time_chips);
            confirm.setEnabled(false);


            mMovieTimes = getResources().getStringArray(R.array.movie_times);

            //Assign individual chips to chip group
            //Chips are essentially buttons
            for (int k =0; k <mMovieTimes.length; k++){
                chip = new Chip(getContext());
                chip.setText(mMovieTimes[k]);
                chip.setClickable(true);
                chip.setCheckable(true);
                chipGroup.addView(chip);
            }

            //This change listener does two things
            //One is getting the date in milliseconds
            //The Second thing is making sure the user does NOT pick a date before today
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    Date current_date = Calendar.getInstance().getTime();
                    //Date calendarviewdate = new Date(calendarView.getDate());
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth);
                    calendarviewdate = new Date(c.getTimeInMillis());
                   cal_time = calendarviewdate.getTime();

                   //Prevent the user from picking a date before today for obvious reasons
                    if(calendarviewdate.before(current_date)){
                        chipGroup.setEnabled(false);
                        chipGroup.setClickable(false);
                        confirm.setEnabled(false);
                    }

                    //If the day is after today it's valid and is allowed to be booked
                    else if(calendarviewdate.after(current_date)){
                        chipGroup.setEnabled(true);
                        chipGroup.setClickable(true);
                        if(choosenTime != null) {
                            confirm.setEnabled(true);
                        }
                    }

                    //
                    else {
                        chipGroup.setEnabled(true);
                        chipGroup.setClickable(true);
                        if(choosenTime != null) {
                            confirm.setEnabled(true);
                        }
                    }
                }
            });
                chipGroup.setSingleSelection(true);

            //Close the dialog fragment with no changes to the seat booking page
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                }
            });

            //Confirm selection and send details to seat booking page.
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        String pattern = "M/d/yyyy";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                       Calendar c = Calendar.getInstance();
                        calendarviewdate = new Date(c.getTimeInMillis());
                        final_date = simpleDateFormat.format(cal_time) + " " + choosenTime;

                        //Send the values to the activity page
                        ((SeatBookingPage) getContext()).setButtonText(final_date, cal_time, chipgrouppos);
                        getDialog().dismiss();

                }
            });

            //Obtains the time(Not the date) on whatever the user selects out of the choices in the chip group
            chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup chipGroup, int i) {
                    choosenTime = mMovieTimes[i - 1];
                    chipgrouppos = i - 1;
                    confirm.setEnabled(true);
                  //  chipGroup.
                }
            });

            if(cal_time != 0){
                System.out.println("caldate "  +cal_time);
                System.out.println("chip_pos " + chipgrouppos);

                confirm.setEnabled(true);
                calendarView.setDate(cal_time);
               // chipGroup.get
               // chipGroup.setSingleSelection(chipgrouppos);
            }


            return dialogView;
    }
}
