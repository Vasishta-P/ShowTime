package com.example.showtime;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class new_user extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText phonenumber;
    AutoCompleteTextView email;
    EditText password;
    EditText re_password;

    TextInputLayout txtInputfirstName;
    TextInputLayout txtInputlastName;
    TextInputLayout txtInputphonenumber;
    TextInputLayout txtInputEmail;
    TextInputLayout txtInputPassword;
    TextInputLayout txtInput_re_Password;

    Button cancel;
    Button createUser;

    String fieldempty;
    String account_succesfullycreated;
    String account_welcome;
    String ok;
    String passwords_noMatch;
    String invalid_format;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);

        fieldempty = getResources().getString(R.string.field_empty);
        passwords_noMatch = getString(R.string.password_no_match);
        invalid_format = getString(R.string.invalid_format);

        ok = getString(R.string.ok);
        account_welcome = getString(R.string.welcome);
        account_succesfullycreated = getString(R.string.account_successfully_created);

        firstName = findViewById(R.id.firstnameTxt);
        lastName = findViewById(R.id.lastnameTxt);
        phonenumber = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_password);

        txtInputfirstName = findViewById(R.id.txtInputFirstName);
        txtInputlastName = findViewById(R.id.txtInputLastName);
        txtInputphonenumber = findViewById(R.id.txtInputPhoneNum);
        txtInputEmail = findViewById(R.id.txtInputEmail);
        txtInputPassword = findViewById(R.id.txtInputPassword);
        txtInput_re_Password = findViewById(R.id.txtInput_Re_Password);

        createUser = findViewById(R.id.btnCreate);
        cancel = findViewById(R.id.btnCancelUser);

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClearErrors();

                //Check to see if any fields are empty
                if (!CheckAnyIfEmpty()) {
                    //Check to see if email is valid
                    if (isEmailValid(email.getText().toString())) {
                        //Check to see if phone number is valid
                        if (isValidMobile(phonenumber.getText().toString())) {
                            //Check to sse if passwords match
                            if (validPassword(password.getText().toString(), re_password.getText().toString())) {

                                AlertDialog.Builder builder2 = new AlertDialog.Builder(new_user.this);
                                builder2.setTitle(account_welcome);
                                builder2.setMessage(account_succesfullycreated)
                                        .setCancelable(false)
                                        .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent i = new Intent(new_user.this, main_activity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                            }
                                        });
                                AlertDialog alert2 = builder2.create();
                                alert2.show();

                            }

                            else {
                                //Set a message saying passwords don't match
                                txtInputPassword.setError(passwords_noMatch);
                                txtInput_re_Password.setError(passwords_noMatch);
                            }
                        }

                        else{
                            //Set a message saying the phone number is in invalid format
                            txtInputphonenumber.setError(invalid_format);
                        }
                    }
                    else{
                        //Set a message saying the emial is in invalid format
                        txtInputEmail.setError(invalid_format);
                    }
                }
            }
        });

        //Go back to login page
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //Check if phone is in valid format
    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private boolean validPassword(String password1, String password2){
        return password1.equals(password2);
    }


    //Clear any previous error messages from last attempt at creating a new account
    private void ClearErrors(){
        txtInputfirstName.setError(null);
        txtInputlastName.setError(null);
        txtInputEmail.setError(null);
        txtInputPassword.setError(null);
        txtInput_re_Password.setError(null);
    }

    /**
     *Check email is in valid format or not
     */
    public static boolean isEmailValid(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    //Sets the IsEmpty error messages in the text Input layout for all fields
    private boolean CheckAnyIfEmpty() {
        boolean isAnyEmpty = false;

        if (isEmpty(firstName)) {
            txtInputfirstName.setError(fieldempty);
            isAnyEmpty = true;
        }
        else {
            txtInputfirstName.setError(null);
        }

        if (isEmpty(lastName)) {
            txtInputlastName.setError(fieldempty);
            isAnyEmpty = true;
        }

        else{
            txtInputlastName.setError(null);
        }

        if (isEmpty(phonenumber)) {
            txtInputphonenumber.setError(fieldempty);
            isAnyEmpty = true;
        }
        else {
            txtInputphonenumber.setError(null);
        }

        if (isEmpty(email)) {
            txtInputEmail.setError(fieldempty);
            isAnyEmpty = true;
        }
        else{
            txtInputEmail.setError(null);
        }

        if (isEmpty(password)) {
            txtInputPassword.setError(fieldempty);
            isAnyEmpty = true;
        }
        else{
            txtInputPassword.setError(null);
        }

        if (isEmpty(re_password)) {
            txtInput_re_Password.setError(fieldempty);
            isAnyEmpty = true;
        }
        else{
            txtInput_re_Password.setError(null);
        }


        return isAnyEmpty;
    }


    //Check if edit text is empty
    private boolean isEmpty(EditText myedittext7) {
        return myedittext7.getText().toString().trim().length() == 0;
    }
}
