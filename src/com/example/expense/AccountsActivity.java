package com.example.expense;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.expense.data.ExpenseContract;
import com.zacwhy.expense.content.AccountProvider;

//
// http://developer.android.com/guide/topics/ui/layout/listview.html
//

public class AccountsActivity extends ListActivity implements LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data
    private SimpleCursorAdapter mAdapter;

    // These are the Contacts rows that we will retrieve
    private static final String[] PROJECTION = new String[] {
        ExpenseContract.Account._ID,
        ExpenseContract.Account.COLUMN_NAME_NAME
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT/*, Gravity.CENTER*/));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = { ExpenseContract.Account.COLUMN_NAME_NAME };
        int[] toViews = { android.R.id.text1 }; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

        //registerForContextMenu(getListView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.accounts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_add:
            showDialog(0, (String) null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, AccountProvider.CONTENT_URI, PROJECTION, null, null, null);
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        
        Cursor cursor = (Cursor) getListView().getItemAtPosition(position);
        long accountId = cursor.getLong(0);
        String name = cursor.getString(1);
        showDialog(accountId, name);
    }

    private void showDialog(final long id, final String name) {
        String hint;
        String text;
        
        final String hintAccountName = getString(R.string.hint_account_name);
        final String actionOk = getString(R.string.action_ok);
        final String actionCancel = getString(R.string.action_cancel);
        
        if (id > 0) {
            text = name;
            hint = name;
        } else {
            text = "";
            hint = hintAccountName;
        }
        
        final EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setText(text);

        new AlertDialog.Builder(this)
            //.setTitle("Edit Account Name")
            .setMessage(hintAccountName)
            .setView(editText)
            .setPositiveButton(actionOk, new DialogInterface.OnClickListener() {
    
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newName = editText.getText().toString();
                    String text;
                    
                    if (id > 0) {
                        updateAccount(id, newName);
                        text = "Changed " + name + " to " + newName;
                    } else {
                        insertAccount(newName);
                        text = "Added " + newName;
                    }
                    
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton(actionCancel, new DialogInterface.OnClickListener() {
    
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                }
            })
            .show();

    }

    private Uri insertAccount(String name) {
        ContentValues values = new ContentValues();
        values.put(ExpenseContract.Account.COLUMN_NAME_NAME, name);
        return getContentResolver().insert(AccountProvider.CONTENT_URI, values);
    }

    private int updateAccount(long id, String name) {
        ContentValues values = new ContentValues();
        values.put(ExpenseContract.Account.COLUMN_NAME_NAME, name);
        Uri uri = ContentUris.withAppendedId(AccountProvider.CONTENT_URI, id); 
        return getContentResolver().update(uri, values, null, null);
    }

}
