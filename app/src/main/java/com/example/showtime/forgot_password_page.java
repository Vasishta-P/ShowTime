package com.example.showtime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;

public class forgot_password_page extends AppCompatActivity {


    Button cancel;
    Button send;
    AutoCompleteTextView email;
    TextInputLayout txtInputemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        email = findViewById(R.id.email);
        txtInputemail = findViewById(R.id.txtInputEmail);
        send = findViewById(R.id.btnSend);
        cancel = findViewById(R.id.btnCancelUser);


        //Send Password Retrival method
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //Fi
            }
        });

        //Go Back to login page
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             onBackPressed();
            }
        });
    }

}
