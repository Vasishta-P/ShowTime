package com.example.showtime;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



public class credit_card_page extends AppCompatActivity {

    EditText creditcardnumber;
    EditText cvv;
    EditText expirationdate;
    EditText zip_code;
    EditText card_holdername;
    Button save;
    Button cancel;

    String fieldempty;

    private TextInputLayout textInput_card_number;
    private TextInputLayout textInput_CVV;
    private TextInputLayout textInput_expiration_date;
    private TextInputLayout textInput_zipcode;
    private TextInputLayout textInput_card_name;
    boolean newUser = true;
    private User_Details creditcard_details;

    private CreditCard creditCard;
    SqlUserDetails sqlUserDetails;
    String email;

    String warning;
    String save_success;
    String save_failure;
    String ok;
    String title;

    String invalid_card_number;
    String invalid_cvv;
    String invalid_expiration_date;
    String invalid_zipcode;

    boolean exists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //isTablet = getResources().getBoolean(R.bool.isTablet);
        setContentView(R.layout.credit_card);

        fieldempty = getResources().getString(R.string.field_empty);
        warning = getString(R.string.warning);
        save_success = getString(R.string.credit_card_saved_success);
        save_failure = getString(R.string.credit_card_save_failure);
        ok = getString(R.string.ok);
        title = getString(R.string.payment_page_title);

        invalid_card_number = getString(R.string.invaild_cardnumber);
        invalid_cvv = getString(R.string.invaild_cvv);
        invalid_expiration_date = getString(R.string.invaild_exp_date);
        invalid_zipcode = getString(R.string.invaild_zip_code);

        creditcardnumber = (EditText) findViewById(R.id.card_number);
        cvv = (EditText) findViewById(R.id.cvv);
        expirationdate = (EditText) findViewById(R.id.expiration_number);
        zip_code = (EditText) findViewById(R.id.postal_code);
        card_holdername = (EditText) findViewById(R.id.card_name);

        textInput_card_number = (TextInputLayout) findViewById(R.id.txtInputCardNum);
        textInput_CVV = (TextInputLayout) findViewById(R.id.txtInputCVV);
        textInput_expiration_date = (TextInputLayout)  findViewById(R.id.txtInputExpiration);
        textInput_zipcode = (TextInputLayout)  findViewById(R.id.txtInputZipCode);
        textInput_card_name = (TextInputLayout) findViewById(R.id.txtInputCardName);
        save = (Button) findViewById(R.id.btnSaveCredit);
        cancel = (Button)findViewById(R.id.btnCancelCredit);

        sqlUserDetails = new SqlUserDetails(credit_card_page.this);


        //Try to get credit card details from database
        try {
            creditcard_details = sqlUserDetails.getAllUserData().get(0);
            exists = true;
        }
        catch (Exception np){
            np.printStackTrace();
            exists = false;
        }

        //If credit card information is not null
        //Then it has already been setup so fill in the edit text with existing data
        if(creditcard_details != null){
            card_holdername.setText(creditCard.getCard_name());
            creditcardnumber.setText(creditCard.getCardnumber());
            cvv.setText(creditCard.getCvv());
            expirationdate.setText(creditCard.getExpirationdate());
            zip_code.setText(creditCard.getZipcode());
        }

        //Saves the credit card information to database table if all is valid
        //If a field not valid in any way nothing gets saved and a error message appears in the text input layout
        //Also faild to save message box appears if saving failed.
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!CheckAnyIfEmpty()){
                    if(validateCreditCardInfo()){
                        creditCard = null;
                        creditCard = new CreditCard();
                        boolean saveresult = false;

                        creditCard.setCardnumber(creditcardnumber.getText().toString().trim());
                        creditCard.setCard_name(card_holdername.getText().toString().trim());
                         creditCard.setCvv(Integer.valueOf(cvv.getText().toString().trim()));
                        creditCard.setExpirationdate(expirationdate.getText().toString().trim());
                        creditCard.setZipcode(zip_code.getText().toString().trim());


                        if(exists){
                           saveresult = sqlUserDetails.updateCreditCardData("123", creditCard);
                        }
                        else {
                            creditcard_details = new User_Details();
                            creditcard_details.setCreditCard(creditCard);
                            saveresult = sqlUserDetails.insertData(creditcard_details);
                        }

                       if(saveresult){
                           AlertDialog.Builder builder = new AlertDialog.Builder(credit_card_page.this);
                           builder.setTitle(title);
                           builder.setMessage(save_success).setCancelable(false).setPositiveButton(ok, new DialogInterface.OnClickListener(){
                                       public void onClick(DialogInterface dialog, int id) {
                                           dialog.cancel();

                                           Intent i = new Intent(credit_card_page.this, main_activity.class);
                                           startActivity(i);

                                       }
                                   });

                           AlertDialog alert = builder.create();
                           alert.show();
                       }

                       else {
                           AlertDialog.Builder builder = new AlertDialog.Builder(credit_card_page.this);
                           builder.setTitle(warning);
                           builder.setMessage(save_failure).setCancelable(false).setPositiveButton(ok, new DialogInterface.OnClickListener(){
                               public void onClick(DialogInterface dialog, int id) {
                                   dialog.cancel();
                               }
                           });

                           AlertDialog alert = builder.create();
                           alert.show();
                       }


                    }
                }

            }
        });

        //Go back to main page
         cancel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                onBackPressed();
             }
         });

    }

    //Sets the IsEmpty error messages in the text Input layout for all fields
    private boolean CheckAnyIfEmpty() {
        boolean isAnyEmpty = false;

        if (isEmpty(creditcardnumber)) {
            textInput_card_number.setError(fieldempty);
            isAnyEmpty = true;
        }
        else {
            textInput_card_number.setError(null);
        }

        if(isEmpty(card_holdername)){
            textInput_card_name.setError(fieldempty);
            isAnyEmpty = true;
        }

        else{
            textInput_card_name.setError(null);
        }

        if (isEmpty(cvv)) {
            textInput_CVV.setError(fieldempty);
            isAnyEmpty = true;
        }
        else {
            textInput_CVV.setError(null);
        }

        if (isEmpty(expirationdate)) {
            textInput_expiration_date.setError(fieldempty);
            isAnyEmpty = true;
        }

        else{
            textInput_expiration_date.setError(null);
        }

        if (isEmpty(zip_code)) {
            textInput_zipcode.setError(fieldempty);
            isAnyEmpty = true;
        }

        else{
            textInput_zipcode.setError(null);
        }

        return isAnyEmpty;
    }



    //Validates the ZIP code using a regex pattern
    //Lastly this zip code only goes for U.S. areas due to the fact that other countries use postal code
    // which is completely different
    private boolean ValidateZipCode(String zipcode){
        String regex = "^[0-9]{5}(?:-[0-9]{4})?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(zipcode);
        return matcher.find();
    }

    //Validates the expiration date to see if it is valid or not
    //It's invalid if it's expired or in a invalid format
    private boolean ValidateExpirationDate(String date){
       // String input = "11/12"; // for example
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
            simpleDateFormat.setLenient(false);
            Date expiry = simpleDateFormat.parse(date);
            boolean expired = expiry.before(new Date());

            return expired;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //Validate credit card number to see if matches any knwon regex patterns
    //This list below show what types of credit cards the regex below is search for
    /*
    Visa
    Discover
    American Express
    Diners
    JCB
     */
    private boolean ValidateCardNumber(String cardNumber){
        String credit_card_regex = "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)|" +
                "(?<mastercard>5[1-5][0-9]{14})|" +
                "(?<discover>6(?:011|5[0-9]{2})[0-9]{12})|" +
                "(?<amex>3[47][0-9]{13})|" +
                "(?<diners>3(?:0[0-5]|[68][0-9])?[0-9]{11})|" +
                "(?<jcb>(?:2131|1800|35[0-9]{3})[0-9]{11}))$";

        Pattern pattern = Pattern.compile(credit_card_regex);
        Matcher matcher = pattern.matcher(cardNumber);
        return matcher.find();
    }

    //Validates the CVV
    //The edit text used to get the CVV only accepts numbers
    private boolean ValidateCVV(String cvv){
        if(cvv.length() == 3 || cvv.length() == 4){
            return true;
        }
        else {
            return false;
        }
    }

    //Validates the Credit Card Info for all fields
    private boolean validateCreditCardInfo(){

       boolean valid = true;
        if(!CheckAnyIfEmpty()){

            if(ValidateExpirationDate(expirationdate.getText().toString())){
                valid = false;
                textInput_expiration_date.setError(invalid_expiration_date);
            }
            else{
                textInput_expiration_date.setError(null);
            }

            if(!ValidateCardNumber(creditcardnumber.getText().toString())){
                valid = false;
                textInput_card_number.setError(invalid_card_number);
            }
            else{
                textInput_card_number.setError(null);
            }


            if(!ValidateCVV(cvv.getText().toString())){
                valid = false;
                textInput_CVV.setError(invalid_cvv);
            }
            else {
                textInput_CVV.setError(null);
            }

            if(!ValidateZipCode(zip_code.getText().toString())){
                valid = false;
                textInput_zipcode.setError(invalid_zipcode);
            }

            else {
                textInput_zipcode.setError(null);
            }

            return valid;
        }
        else{
            return valid;
        }

    }


    //Check to see if edit text is empty. This goes for a edit text only composed of whitespaces as well
    private boolean isEmpty(EditText myedittext7) {
        return myedittext7.getText().toString().trim().length() == 0;
    }

}

