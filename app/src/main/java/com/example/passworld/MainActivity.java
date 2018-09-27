package com.example.passworld;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.okButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText passField = (EditText) findViewById(R.id.passfield);
                String givenPassword = passField.getText().toString();
                boolean passwordAfterCheck = checkPassword(givenPassword);
                if (passwordAfterCheck) {
                    Intent intent = new Intent(MainActivity.this, PasswordActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, R.string.wrong_password, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    public boolean checkPassword(String userPassword){
        String[] projection = {PasswordContract.PasswordEntry.COLUMN_NAME_SITE, PasswordContract.PasswordEntry.COLUMN_NAME_PASSWORD};
        String selection = PasswordContract.PasswordEntry._ID + "=?";
        String[] selectionArgs = {"1"};
        Cursor data = getContentResolver().query(PasswordContract.PasswordEntry.CONTENT_URI,projection,selection,selectionArgs,null);

        if(data == null || data.getCount()<1){
            return false;
        }
        if(data.moveToFirst()){
            // Find the columns of passwords attributes that we're interested in
            int passwordColumnIndex = data.getColumnIndex(PasswordContract.PasswordEntry.COLUMN_NAME_PASSWORD);

            // Extract out the value from the Cursor for the given column index
            String dataBasePassword = data.getString(passwordColumnIndex);

            if(userPassword.equals(dataBasePassword)){
                data.close();
                return true;
            }
        }


        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_set_change_password, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.option){
            Intent intent = new Intent(MainActivity.this, SetPasswordActivity.class);
            startActivity(intent);
            return true;
        }
        else{
            Toast toast = Toast.makeText(MainActivity.this, "Oops, something went wrong!", Toast.LENGTH_SHORT);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
