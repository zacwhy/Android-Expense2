package com.example.expense;

import java.math.BigDecimal;
import java.util.ArrayList;
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
                
            } else if (action.contentEquals(EntryActivity.ACTION_UPDATE)) {
                
                loadListView();
                //refreshUpdate();
                
            } else if (action.contentEquals(EntryActivity.ACTION_INSERT)) {
                
                loadListView();
                
            }
        }
    }
    
//    private void refreshUpdate() {
//        SummaryListItem listItem = mSummaryArrayAdapter.getItem(mPosition);
//        
//        ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
//        
//        SQLiteDatabase database = dbHelper.getReadableDatabase();
//        
//        long transactionGroupId = listItem.getId();
//        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
//        TransactionGroup transactionGroup = 
//                dataSource.getTransactionGroupWithTransactionsById(transactionGroupId);
//        
//        dbHelper.close();
//        
//        Transaction transaction = transactionGroup.getTransactions().get(0);
//        String label = getLabel(transactionGroup, transaction);
//        
//        listItem.setLabel(label);
//        listItem.setAmount(transaction.getAmount());
//        
//        mSummaryArrayAdapter.notifyDataSetChanged();
//    }
    
	private void loadListView() {
        List<TransactionGroup> list = TransactionHelper.getTransactionGroups(this);
	    List<SummaryListItem> listItems = getListItems(list);
	    
	    mSummaryArrayAdapter = new SummaryArrayAdapter(this, listItems);
		setListAdapter(mSummaryArrayAdapter);
	}
	
	private List<SummaryListItem> getListItems(List<TransactionGroup> transactionGroups) {
	    List<SummaryListItem> items = new ArrayList<SummaryListItem>();
	    
	    for (TransactionGroup transactionGroup : transactionGroups) {
	        long id = transactionGroup.getId();
	        
	        for (Transaction transaction : transactionGroup.getTransactions()) {
	            String label = getLabel(transactionGroup, transaction);
	            BigDecimal amount = transaction.getAmount();
	            SummaryListItem item = new SummaryListItem(id, label, amount, 0);
	            items.add(item);
	        }
	    }
	    
	    return items;
	}
	
	private static String getLabel(TransactionGroup transactionGroup, Transaction transaction) {
	    return DateHelper.getDateWithDayOfWeekString(transactionGroup.getDate()) + " " + 
        	    transactionGroup.getSequence() + "." + transaction.getSequence() + " " +
        	    transaction.getDescription(); 
	}

}
