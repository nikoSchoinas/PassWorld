package com.example.passworld;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetPasswordActivity extends AppCompatActivity {

    private Uri mCurrentPasswordUri;
    private EditText mPasswordEditText;
    String passwordToSave;
    private static final String APP_SITE = "AppPassword";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);



        Button button = (Button) findViewById(R.id.set_password_button);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mPasswordEditText = (EditText) findViewById(R.id.set_password_field);
                    savePassword();
                Intent intent = new Intent(SetPasswordActivity.this,PasswordActivity.class);
                startActivity(intent);
            }
        });



    }

    private void savePassword(){
        // Read from input fields
        // Use trim to eliminate leading or trailing white space

        String siteString = APP_SITE;
        passwordToSave = mPasswordEditText.getText().toString().trim();

        // Check if this is supposed to be a new password
        // and check if all the fields in the editor are blank
        if (mCurrentPasswordUri == null && TextUtils.isEmpty(passwordToSave)) {
            // Since no fields were modified, we can return early without creating a new password.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and password attributes from the editor are the values.

        ContentValues values = new ContentValues();
        values.put(PasswordContract.PasswordEntry.COLUMN_NAME_SITE, siteString);
        values.put(PasswordContract.PasswordEntry.COLUMN_NAME_PASSWORD, passwordToSave);

        // Determine if this is a new or existing password by checking if mCurrentpasswordUri is null or not
        if(mCurrentPasswordUri == null){
            // This is a NEW password, so insert a new password into the provider,
            // returning the content URI for the new password.

            Uri newUri = getContentResolver().insert(PasswordContract.PasswordEntry.CONTENT_URI,values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_password_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_password_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // Otherwise this is an EXISTING password, so update the password with content URI: mCurrentPasswordUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentpasswordUri will already identify the correct row in the database that
            // we want to modify.

            int rowsAffected = getContentResolver().update(mCurrentPasswordUri,values,null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_password_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_password_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
