package com.example.expense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.expense.data.TransactionHelper;
import com.example.expense.helpers.DateHelper;
import com.example.expense.models.SummaryListItem;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class SummaryActivity extends ListActivity {

    private SummaryArrayAdapter mSummaryArrayAdapter;
    private int mPosition;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent intent = new Intent(this, EntryActivity.class);
			startActivityForResult(intent, 0);
			return true;

	      case R.id.action_summary_two:
	            startActivity(new Intent(this, SummaryTwoActivity.class));
	            return true;
          case R.id.action_summary_three:
              startActivity(new Intent(this, SummaryThreeActivity.class));
              return true;
          case R.id.action_summary_bbb:
              startActivity(new Intent(this, SummaryBbbActivity.class));
              return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mPosition = position;
        
	    SummaryListItem listItem = (SummaryListItem) getListView().getItemAtPosition(position);
	    
	    Intent intent = new Intent(this, EntryActivity.class);
	    intent.putExtra(EntryActivity.EXTRA_TRANSACTION_GROUP_ID, listItem.getId());
	    startActivityForResult(intent, 0);
    }
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String action = data.getStringExtra(EntryActivity.EXTRA_ACTION);
            
            if (action.contentEquals(EntryActivity.ACTION_DELETE)) {
                mSummaryArrayAdapter.remove(mSummaryArrayAdapter.getItem(mPosition));
            } else {
                loadListView(); // TODO remove
            }
        }
    }
    
	private void loadListView() {
	    List<Transaction> transactions = TransactionHelper.getTransactions(this);
	    List<SummaryListItem> list = getSummaryListItems(transactions);
	    mSummaryArrayAdapter = new SummaryArrayAdapter(this, list);
		setListAdapter(mSummaryArrayAdapter);
	}
	
	private List<SummaryListItem> getSummaryListItems(List<Transaction> transactions) {
	    List<SummaryListItem> list = new ArrayList<SummaryListItem>();
        
        for (Transaction transaction : transactions) {
            long id = transaction.getTransactionGroup().getId();
            String label = getLabel(transaction);
            BigDecimal amount = transaction.getAmount();
            SummaryListItem summaryListItem = new SummaryListItem(id, label, amount, 0);
            list.add(summaryListItem);
        }
        
        return list;
	}
	
	private static String getLabel(Transaction transaction) {
	    TransactionGroup transactionGroup = transaction.getTransactionGroup();
        Calendar date = transactionGroup.getDate();
        int transactionGroupSequence = transactionGroup.getSequence();
        int transactionSequence = transaction.getSequence();
        String description = transaction.getDescription();
        
        return DateHelper.getDateWithDayOfWeekString(date) + " " + 
                transactionGroupSequence + "." + transactionSequence + " " +
                description; 
	}

}
