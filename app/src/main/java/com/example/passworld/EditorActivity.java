package com.example.passworld;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by New on 28/10/2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>   {

    /** Identifier for the password data loader */
    private static final int EXISTING_PASSWORD_LOADER = 0;

    /** Content URI for the existing password (null if it's a new password) */
    private Uri mCurrentPasswordUri;

    /** EditText field to enter site's name*/
    private EditText mSiteEditText;

    /*EditText field to enter password*/
    private EditText mPasswordEditText;

    /** Boolean flag that keeps track of whether the password has been edited (true) or not (false) */
    private boolean mPasswordHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPasswordHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPasswordHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle saveInstantState){
        super.onCreate(saveInstantState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new password or editing an existing one.
        Intent intent = getIntent();
        mCurrentPasswordUri = intent.getData();

        // Find all relevant views that we will need to read user input from
        mSiteEditText = (EditText) findViewById(R.id.site_field);
        mPasswordEditText = (EditText) findViewById(R.id.password_field);

        if(mCurrentPasswordUri!=null){
            // Initialize a loader to read the data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_PASSWORD_LOADER, null, this);
        }

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mSiteEditText.setOnTouchListener(mTouchListener);
        mPasswordEditText.setOnTouchListener(mTouchListener);

    }

    private void savePassword(){
        // Read from input fields
        // Use trim to eliminate leading or trailing white space

        String siteString = mSiteEditText.getText().toString().trim();
        String passwordString = mPasswordEditText.getText().toString().trim();

        // Check if this is supposed to be a new password
        // and check if all the fields in the editor are blank
        if (mCurrentPasswordUri == null &&
                TextUtils.isEmpty(siteString) && TextUtils.isEmpty(passwordString)) {
            // Since no fields were modified, we can return early without creating a new password.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and password attributes from the editor are the values.

        ContentValues values = new ContentValues();
        values.put(PasswordContract.PasswordEntry.COLUMN_NAME_SITE, siteString);
        values.put(PasswordContract.PasswordEntry.COLUMN_NAME_PASSWORD, passwordString);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new password, hide the "Delete" menu item.
        if (mCurrentPasswordUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            case R.id.action_save:
                //save password to the database
                savePassword();
                //Exit activity
                finish();
                return true;
            //Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the password hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPasswordHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the password hasn't changed, continue with handling back button press
        if (!mPasswordHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the password.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the password.
                deletePassword();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the password.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePassword() {
        // Only perform the delete if this is an existing password.
        if (mCurrentPasswordUri != null) {
            // Call the ContentResolver to delete the password at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPasswordUri
            // content URI already identifies the password that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPasswordUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_password_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_password_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all password attributes, define a projection that contains
        // all columns from the password table

        String[] projection = {
                PasswordContract.PasswordEntry._ID,
                PasswordContract.PasswordEntry.COLUMN_NAME_SITE,
                PasswordContract.PasswordEntry.COLUMN_NAME_PASSWORD
        };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, mCurrentPasswordUri,projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if(data.moveToFirst()){
            // Find the columns of passwords attributes that we're interested in
            int siteColumnIndex = data.getColumnIndex(PasswordContract.PasswordEntry.COLUMN_NAME_SITE);
            int passwordColumnIndex = data.getColumnIndex(PasswordContract.PasswordEntry.COLUMN_NAME_PASSWORD);

            // Extract out the value from the Cursor for the given column index
             String site = data.getString(siteColumnIndex);
            String password = data.getString(passwordColumnIndex);

            // Update the views on the screen with the values from the database
            mSiteEditText.setText(site);
            mPasswordEditText.setText(password);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mSiteEditText.setText("");
        mPasswordEditText.setText("");

    }
}
