package com.example.expense;

import java.math.BigDecimal;
import java.util.Calendar;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.example.common.helpers.DateHelper;
import com.example.common.helpers.FormatHelper;
import com.example.common.helpers.SqlHelper;
import com.example.expense.data.ExpenseContract;
import com.zacwhy.expense.content.SummaryProvider;

public class SummariesActivity extends ListActivity implements LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        String[] from = { SummaryProvider.COLUMN_DESCRIPTION, SummaryProvider.COLUMN_AMOUNT };
        int[] to = { R.id.label, R.id.amount };
        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_summary, null, from, to, 0);

        mAdapter.setViewBinder(new ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if (columnIndex == 2) {
                    BigDecimal totalAmount = new BigDecimal(cursor.getString(columnIndex));
                    String totalAmountString = FormatHelper.formatMoney(totalAmount);

                    TextView textView = (TextView) view;
                    textView.setText(totalAmountString);
                    return true;
                }

                return false;
            }

        });

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.accounts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        //        case R.id.action_add:
        //            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = SqlHelper.format(
                "(datetime(%s.%s/1000, 'unixepoch') BETWEEN ? AND ?)" +
                " AND (%s.%s = 'Expenditure')",
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.TransactionGroup.COLUMN_NAME_DATE,
                SummaryProvider.TABLE_TO_ACCOUNT,
                ExpenseContract.Account.COLUMN_NAME_NAME);

        Calendar calendar = DateHelper.getFirstDayOfCurrentMonth();
        String startDateString = SqlHelper.getDateTimeString(calendar);
        calendar.add(Calendar.MONTH, 1);
        String endDateString = SqlHelper.getDateTimeString(calendar);
        String[] selectionArgs = { startDateString, endDateString };
        String sortOrder = ExpenseContract.ExpenseCategory.COLUMN_NAME_TITLE;

        return new CursorLoader(this, SummaryProvider.CONTENT_URI, null, selection, selectionArgs, sortOrder);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
    }

}
