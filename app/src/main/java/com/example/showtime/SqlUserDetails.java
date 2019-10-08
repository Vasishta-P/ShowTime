package com.example.showtime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


//This is the SQL Lite database
public class SqlUserDetails extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "UserID.db";
    public static final String TABLE_NAME = "user_table";
   public static final String id_col = "ID";
    public static final String firstNameCol = "firstname";
    public static final String lastNameCol = "lastname";
    public static final String email_col = "email";
    public static final String password_col = "password";

    public static final String card_holder_name_col = "cardnameholder";
    public static final String card_number_col = "cardnumber";
    public static final String cvv_col = "cvv_col";
    public static final String expirationdate_col = "expirationdate_col";
    public static final String zip_code = "zipcode";


    public SqlUserDetails(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,firstname TEXT,lastname TEXT,email TEXT,password TEXT, " +
                "cardnumber TEXT,cardnameholder TEXT, cvv_col TEXT, expirationdate_col TEXT, zipcode TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    //Insert new user data
    public boolean insertData(User_Details user_details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        /*contentValues.put(firstNameCol, user_details.getFirstname());
        contentValues.put(lastNameCol, user_details.getLastname());
        contentValues.put(email_col,user_details.getEmail());
        contentValues.put(password_col, user_details.getPassword());*/
        contentValues.put(card_holder_name_col, user_details.getCreditCard().getCard_name());
        contentValues.put(card_number_col, user_details.getCreditCard().getCardnumber());
        contentValues.put(cvv_col, user_details.getCreditCard().getCvv());
        contentValues.put(expirationdate_col, user_details.getCreditCard().getExpirationdate());
        contentValues.put(zip_code, user_details.getCreditCard().getZipcode());

        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }


   //Get all user data from the database
    public ArrayList<User_Details> getAllUserData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        ArrayList<User_Details> userinfotags = new ArrayList<User_Details>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {

                User_Details tags = new User_Details();

                tags.setDb_id(c.getInt(c.getColumnIndex(id_col)));
                /*tags.setFirstname(c.getString(c.getColumnIndex(firstNameCol)));
                tags.setLastname(c.getString(c.getColumnIndex(lastNameCol)));
                tags.setEmail(c.getString(c.getColumnIndex(email_col)));
                tags.setPassword(c.getString(c.getColumnIndex(password_col)));*/
                System.out.println("Length of card name " + c.getString(c.getColumnIndex(card_holder_name_col)).length());

                CreditCard creditCard = new CreditCard();
                creditCard.setCard_name(Encryptor.decrypt(c.getString(c.getColumnIndex(card_holder_name_col))));
                creditCard.setCardnumber(Encryptor.decrypt(c.getString(c.getColumnIndex(card_number_col))));
                creditCard.setCvv(Integer.valueOf(Encryptor.decrypt(c.getString(c.getColumnIndex(cvv_col)))));
                creditCard.setExpirationdate(Encryptor.decrypt(c.getString(c.getColumnIndex(expirationdate_col))));
                creditCard.setZipcode(Encryptor.decrypt(c.getString(c.getColumnIndex(zip_code))));

                tags.setCreditCard(creditCard);
                creditCard = null;

                userinfotags.add(tags);
            }
        }

        return userinfotags;

    }

    //We are trying to see if the credit card has been setup or not
    //We do NOT need to decrypt it just check to see if it is null or not
    public ArrayList<User_Details> checkifCardsetup() {
        String query = "SELECT * FROM " + TABLE_NAME;
        ArrayList<User_Details> userinfotags = new ArrayList<User_Details>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {

                User_Details tags = new User_Details();

                tags.setDb_id(c.getInt(c.getColumnIndex(id_col)));
                /*tags.setFirstname(c.getString(c.getColumnIndex(firstNameCol)));
                tags.setLastname(c.getString(c.getColumnIndex(lastNameCol)));
                tags.setEmail(c.getString(c.getColumnIndex(email_col)));
                tags.setPassword(c.getString(c.getColumnIndex(password_col)));*/
                System.out.println("Length of card name " + c.getString(c.getColumnIndex(card_holder_name_col)).length());

                CreditCard creditCard = new CreditCard();
                creditCard.setCard_name(c.getString(c.getColumnIndex(card_holder_name_col)));
                creditCard.setCardnumber(c.getString(c.getColumnIndex(card_number_col)));
               // creditCard.setCvv(Integer.valueOfc.getString(c.getColumnIndex(cvv_col)))));
                creditCard.setExpirationdate(c.getString(c.getColumnIndex(expirationdate_col)));
                creditCard.setZipcode(c.getString(c.getColumnIndex(zip_code)));

                tags.setCreditCard(creditCard);
                creditCard = null;

                userinfotags.add(tags);
            }
        }

        return userinfotags;

    }

    protected User_Details getUserDetailsByID(String id){

        int matchedlocation5 = 0;
        User_Details user_match = new User_Details();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM" + " " + TABLE_NAME + " " + "WHERE " +  id_col + " ='"+id+"'", null );
        while (res.moveToNext()){

            user_match.setDb_id(res.getInt(res.getColumnIndex(id_col)));
           /* user_match.setFirstname(res.getString(res.getColumnIndex(firstNameCol)));
            user_match.setLastname(res.getString(res.getColumnIndex(lastNameCol)));
            user_match.setEmail(res.getString(res.getColumnIndex(email_col)));
            user_match.setPassword(Encryptor.decrypt(res.getString(res.getColumnIndex(password_col))));*/


            CreditCard creditCard = new CreditCard();
            creditCard.setCard_name(Encryptor.decrypt(res.getString(res.getColumnIndex(card_holder_name_col))));
            creditCard.setCardnumber(Encryptor.decrypt(res.getString(res.getColumnIndex(card_number_col))));
            creditCard.setCvv(Integer.valueOf(Encryptor.decrypt(res.getString(res.getColumnIndex(cvv_col)))));
            creditCard.setExpirationdate(Encryptor.decrypt(res.getString(res.getColumnIndex(expirationdate_col))));
            creditCard.setZipcode(Encryptor.decrypt(res.getString(res.getColumnIndex(zip_code))));

            user_match.setCreditCard(creditCard);
        }

        return user_match;
    }


    //Update credit card information as well as encrypt each field before saving
    public boolean updateCreditCardData(String id, CreditCard creditCard) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(card_holder_name_col,Encryptor.encrypt(creditCard.getCard_name()));
        contentValues.put(card_number_col, Encryptor.encrypt(creditCard.getCardnumber()));
        contentValues.put(cvv_col, Encryptor.encrypt(Integer.toString(creditCard.getCvv())));
        contentValues.put(expirationdate_col, Encryptor.encrypt(creditCard.getExpirationdate()));
        contentValues.put(zip_code, Encryptor.encrypt(creditCard.getZipcode()));
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

    //Do not remove this.
    // Used to to clear database when removing corrupt data
    //Testing purposes ONLY
    protected void removeAll()
    {
        // If whereClause is null, it will delete all rows.
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(SqlUserDetails.DATABASE_NAME, null, null);
    }
}