package com.example.passworld;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class PasswordActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the password data loader */
    private static final int PASSWORD_LOADER = 0;

    /** Adapter for the ListView */
    PasswordCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_word);

        // Setup FAB to open EditorActivity

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordActivity.this,EditorActivity.class );
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the password data
        ListView passwordListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        passwordListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of password data in the Cursor.
        // There is no password data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new PasswordCursorAdapter(this,null);
        passwordListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        // Setup the item click listener
        passwordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(PasswordActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific password that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PasswordEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.password/password/2"
                // if the pet with ID 2 was clicked on.
                Uri currentPasswordUri = ContentUris.withAppendedId(PasswordContract.PasswordEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPasswordUri);

                // Launch the {@link EditorActivity} to display the data for the current password.
                startActivity(intent);
            }
        });
        // Kick off the loader
        getSupportLoaderManager().initLoader(PASSWORD_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String [] projection = {
                PasswordContract.PasswordEntry._ID,
                PasswordContract.PasswordEntry.COLUMN_NAME_SITE,
                PasswordContract.PasswordEntry.COLUMN_NAME_PASSWORD
        };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, PasswordContract.PasswordEntry.CONTENT_URI,projection,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated password data
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
