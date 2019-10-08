package com.example.showtime;

import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeatPicker_Dialog_Fragment extends DialogFragment implements View.OnClickListener  {

    Button confirm;
    Button cancel;
    double rangeMin = .50;
    double rangeMax = 6.00;
    DecimalFormat numberFormat;
    int count = 0;
    int white;
    int black;
    int transparentcolor;

    ViewGroup layout;

    String seats;

    List<TextView> seatViewList = new ArrayList<>();

    int STATUS_AVAILABLE = 1;
    int STATUS_BOOKED = 2;
    int STATUS_RESERVED = 3;
    String selectedIds = "";
    ArrayList<Character> chars;

    String getSeats;


    public SeatPicker_Dialog_Fragment() {
        // Required empty public constructor
    }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment Calendar_Dialog_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        public static SeatPicker_Dialog_Fragment newInstance (boolean modecheck, int selectedid, String seats){
        SeatPicker_Dialog_Fragment fragment = new SeatPicker_Dialog_Fragment();
        Bundle args = new Bundle();
        args.putString("Seats", seats);
        //args.putBoolean(dialogmodeparam, modecheck);
        //args.putInt(location_id_param, selectedid);
        fragment.setArguments(args);
        return fragment;
    }

        @Override
        public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getSeats = getArguments().getString("Seats" ,null);
            //  isEditMode = getArguments().getBoolean(dialogmodeparam);
            //selectedid = getArguments().getInt(location_id_param);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
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

        View dialogView = inflater.inflate(R.layout.seat_booking, container, false);


            confirm = dialogView.findViewById(R.id.btn_confirm_seats);
            cancel = dialogView.findViewById(R.id.btn_cancel_seats);
            layout = dialogView.findViewById(R.id.layoutSeat);
            confirm.setEnabled(false);

            //If the seats were already booked in the past, then repopulate the seat picker from the
            // passed in string, otherwise used the default setup
            if(getSeats != null){
                seats = getSeats;
                confirm.setEnabled(true);
            }
            else {
                seats = getString(R.string.default_seatlisting);
            }


            chars = new ArrayList<Character>();
            for (char c : seats.toCharArray()) {
                chars.add(c);
            }


            white = ContextCompat.getColor(getContext(), R.color.white);
            black = ContextCompat.getColor(getContext(), R.color.black);
            transparentcolor = ContextCompat.getColor(getContext(), R.color.transparent);

            setupSeats();


            //Close dialog fragment
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    double val = count * 8.00f;
                    val = val + randomTax_Generator();
                    String total;
                    total = String.format("%.2f", val);
                    total= "$ " + total;

                    seats = covert_CharsTo_String(chars);
                    System.out.println(seats);


                    ((SeatBookingPage) getContext()).setReservationsAndTotal(total, count, seats);
                        getDialog().dismiss();

                }
            });


            return dialogView;
    }

    //Set-up movie seat picker
    //Reserved seats are in black, open seats are in white and user-booked seats are in gray
    private void setupSeats(){

        LinearLayout layoutSeat = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        layoutSeat.setPadding(80, 80, 80, 80);
        layout.addView(layoutSeat);

        LinearLayout layout = null;

        int count = 0;

        for (int index = 0; index < chars.size(); index++) {
            if (chars.get(index) == '/') {
                layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layoutSeat.addView(layout);
            } else if (chars.get(index) == 'U') {
                count++;
                TextView book_seat = new TextView(getContext());
                LinearLayout.LayoutParams booked_seat_layoutParams = new LinearLayout.LayoutParams(100, 100);
                booked_seat_layoutParams.setMargins(10, 10, 10, 10);
                book_seat.setLayoutParams(booked_seat_layoutParams);
                book_seat.setPadding(0, 0, 0, 20);
                book_seat.setId(count);
                book_seat.setGravity(Gravity.CENTER);
                book_seat.setBackgroundResource(R.drawable.ic_seats_b);
                book_seat.setTextColor(white);
                book_seat.setContentDescription(String.valueOf(index));
                book_seat.setTag(STATUS_BOOKED);
                book_seat.setText(count + "");
                book_seat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                layout.addView(book_seat);
                seatViewList.add(book_seat);
                book_seat.setOnClickListener(this);
            } else if (chars.get(index) == 'A') {
                count++;
                TextView available_seat = new TextView(getContext());
                LinearLayout.LayoutParams available_seat_layoutParams = new LinearLayout.LayoutParams(100, 100);
                available_seat_layoutParams.setMargins(10, 10, 10, 10);
                available_seat.setLayoutParams(available_seat_layoutParams);
                available_seat.setPadding(0, 0, 0, 20);
                available_seat.setId(count);
                available_seat.setGravity(Gravity.CENTER);
                available_seat.setBackgroundResource(R.drawable.ic_seats_book);
                available_seat.setText(count + "");
                available_seat.setContentDescription(String.valueOf(index));
                available_seat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                available_seat.setTextColor(black);
                available_seat.setTag(STATUS_AVAILABLE);
                layout.addView(available_seat);
                seatViewList.add(available_seat);
                available_seat.setOnClickListener(this);
            } else if (chars.get(index) == 'R') {
                count++;
                TextView reserved_seat = new TextView(getContext());
                LinearLayout.LayoutParams reserved_seat_layoutParams = new LinearLayout.LayoutParams(100, 100);
                reserved_seat_layoutParams.setMargins(10, 10, 10, 10);
                reserved_seat.setLayoutParams(reserved_seat_layoutParams);
                reserved_seat.setPadding(0, 0, 0, 20);
                reserved_seat.setId(count);
                reserved_seat.setGravity(Gravity.CENTER);
                reserved_seat.setBackgroundResource(R.drawable.ic_seats_reserved);
                reserved_seat.setText(count + "");
                reserved_seat.setContentDescription(String.valueOf(index));
                reserved_seat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                reserved_seat.setTextColor(white);
                reserved_seat.setTag(STATUS_RESERVED);
                layout.addView(reserved_seat);
                seatViewList.add(reserved_seat);
                reserved_seat.setOnClickListener(this);
            } else if (chars.get(index) == '_') {
                TextView empty_space = new TextView(getContext());
                LinearLayout.LayoutParams empty_space_layoutParams = new LinearLayout.LayoutParams(100, 100);
                empty_space_layoutParams.setMargins(10, 10, 10, 10);
                empty_space.setLayoutParams(empty_space_layoutParams);
                empty_space.setContentDescription(String.valueOf(index));
                empty_space.setBackgroundColor(transparentcolor);
                empty_space.setText("");
                layout.addView(empty_space);
            }
        }
    }

    //On click it changes the seats status from either available to booked or the exact opposite
    //The change replace the char value at the position and changes the mini-icon for the control where the user clicked
    //Nothing happens if user clicks on a seat that was already reserved by someone else
    @Override
    public void onClick(View view) {
        if ((int) view.getTag() == STATUS_AVAILABLE) {
            if (selectedIds.contains(view.getId() + ",")) {
                selectedIds = selectedIds.replace(+view.getId() + ",", "");
                view.setBackgroundResource(R.drawable.ic_seats_book);
                chars.set(Integer.parseInt(view.getContentDescription().toString()), 'A');
                count--;

                if(count == 0){
                    confirm.setEnabled(false);
                }

            } else {
                selectedIds = selectedIds + view.getId() + ",";
                view.setBackgroundResource(R.drawable.ic_seats_b);
                chars.set(Integer.parseInt(view.getContentDescription().toString()), 'U');
                count++;
                confirm.setEnabled(true);
            }
        }
    }


    //Taxes vary now and today so I made it random between $0.50 and $6.00
    private double randomTax_Generator(){
        Random r = new Random();
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        numberFormat = new DecimalFormat(".00");
        randomValue = Double.valueOf(numberFormat.format(randomValue));
        return randomValue;
    }

    //Converts char arraylist to string so it can easily sent back to the seat booking page
    String covert_CharsTo_String(ArrayList<Character> seat_list)
    {
        StringBuilder builder = new StringBuilder(seat_list.size());
        for(int i = 0; i< seat_list.size(); i++)
        {
            builder.append(seat_list.get(i));
        }
        return builder.toString();
    }
}
