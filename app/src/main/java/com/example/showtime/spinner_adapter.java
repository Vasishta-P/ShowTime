package com.example.showtime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

//Standard spinners are similiar to drop-down menus that display a list of choices to the user
//Problem is they only show one line of text per entry
//The solution to this problem was to create a custom spinner adapter so I can customize it my choosing
//In this case I made each entry carry two lines of text one for cinema names and one for addresses
public class spinner_adapter extends BaseAdapter {

    private LayoutInflater inflater;
    String[]cinema_names2;
    String[] addresses2;

    public spinner_adapter(Context context, String[]cinema_names, String[] addresses) {
        inflater = LayoutInflater.from(context);
        cinema_names2 = cinema_names;
        addresses2 = addresses;
    }

    @Override
    public int getCount() {
        return cinema_names2.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.cinema_spinner_item, null);
        TextView cinema_name = (TextView) view.findViewById(R.id.cinema_textview);
        TextView address = (TextView) view.findViewById(R.id.address_textview);
        cinema_name.setText(cinema_names2[i]);
        address.setText(addresses2[i]);
        return view;
    }


}