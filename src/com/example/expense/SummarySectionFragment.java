package com.example.expense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.common.helpers.SqlHelper;
import com.example.expense.data.ExpenseContract;
import com.example.expense.data.ExpenseDatabaseHelper;
import com.example.expense.data.TransactionGroupsDataSource;
import com.example.expense.models.SummaryListItem;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class SummarySectionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_YEAR = "year";
    public static final String ARG_MONTH = "month";

    public SummarySectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        int year = arguments.getInt(ARG_YEAR);
        int month = arguments.getInt(ARG_MONTH);

        int sectionNumber = arguments.getInt(ARG_SECTION_NUMBER);
        String sectionNumberString = Integer.toString(sectionNumber);

        View rootView = inflater.inflate(R.layout.fragment_summary_section, container, false);
        TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
        dummyTextView.setText(sectionNumberString + " " + year + " " + month);

        List<SummaryListItem> mListItems = new ArrayList<SummaryListItem>();
        ExpenseDatabaseHelper dbHelper = new ExpenseDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryGroupByCategory(db, year, month);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String description = cursor.getString(1);
            String amount = cursor.getString(2);
            SummaryListItem listItem = new SummaryListItem(null, description, new BigDecimal(amount));
            mListItems.add(listItem);
            cursor.moveToNext();
        }

        cursor.close();
        dbHelper.close();

        SummaryArrayAdapter mSummaryArrayAdapter = new SummaryArrayAdapter(getActivity(), mListItems);
        ListView listView1 = (ListView) rootView.findViewById(R.id.listView1);
        listView1.setAdapter(mSummaryArrayAdapter);

        return rootView;
    }

    private Cursor queryGroupByCategory(SQLiteDatabase db, int year, int month) {
        String selection = SqlHelper.format(
                "(datetime(%s.%s/1000, 'unixepoch') BETWEEN ? AND ?)" +
                        " AND (%s.%s = 'Expenditure')",
                        ExpenseContract.TransactionGroup.TABLE_NAME,
                        ExpenseContract.TransactionGroup.COLUMN_NAME_DATE,
                        TransactionGroupsDataSource.TABLE_TO_ACCOUNT,
                        ExpenseContract.Account.COLUMN_NAME_NAME);

        Calendar calendar = new GregorianCalendar(year, month, 1);
        String startDateString = SqlHelper.getDateTimeString(calendar);
        calendar.add(Calendar.MONTH, 1);
        String endDateString = SqlHelper.getDateTimeString(calendar);
        String[] selectionArgs = { startDateString, endDateString };
        String sortOrder = SqlHelper.qualifiedColumn(ExpenseContract.ExpenseCategory.TABLE_NAME, ExpenseContract.ExpenseCategory.COLUMN_NAME_TITLE);

        TransactionGroupsDataSource transactionGroupsDataSource = new TransactionGroupsDataSource(db);
        return transactionGroupsDataSource.queryGroupByCategory(selection, selectionArgs, sortOrder);
    }
}