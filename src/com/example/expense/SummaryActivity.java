package com.example.expense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.expense.data.ExpenseDbHelper;
import com.example.expense.data.TransactionHelper;
import com.example.expense.helpers.DateHelper;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.SummaryListItem;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class SummaryActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//loadActionBar();
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
	    Context context = getApplicationContext();
	    CharSequence text = "position: " + position + "; id:" + id;
	    int duration = Toast.LENGTH_SHORT;

	    Toast.makeText(context, text, duration).show();
	    
	    SummaryListItem summaryListItem = (SummaryListItem) getListView().getItemAtPosition(position);
	    
	    Intent intent = new Intent(this, EntryActivity.class);
	    intent.putExtra(EntryActivity.EXTRA_TRANSACTION_ID, summaryListItem.getId());
        startActivity(intent);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadListView();
    }
	
//	@SuppressLint("NewApi")
//	private void loadActionBar() {
//		ActionBar actionBar = getActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//		actionBar.setDisplayShowTitleEnabled(false);
//
//		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list,
//		          android.R.layout.simple_spinner_dropdown_item);
//		
//		ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
//			@Override
//			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
//				//lanuchItemSelected(itemPosition);
//				return true;
//			}
//		};
//
//		actionBar.setListNavigationCallbacks(mSpinnerAdapter, navigationListener);
//	}
	
	private void loadListView() {
	    List<Transaction> transactions = GetTransactions();
	    List<SummaryListItem> list = getSummaryListItem(transactions);
	    SummaryArrayAdapter summaryArrayAdapter = new SummaryArrayAdapter(this, list);
		setListAdapter(summaryArrayAdapter);
	}
	
	private List<Transaction> GetTransactions() {
	    ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
	    
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        
        TransactionHelper transactionHelper = new TransactionHelper(database);
        List<Transaction> transactions = transactionHelper.getTransactions();
        
        dbHelper.close();
        
        return transactions;
	}
	
	private List<SummaryListItem> getSummaryListItem(List<Transaction> transactions) {
	    List<SummaryListItem> list = new ArrayList<SummaryListItem>();
        
        for (Transaction transaction : transactions) {
            long id = transaction.getId();
            
            TransactionGroup transactionGroup = transaction.getTransactionGroup();
            Calendar date = transactionGroup.getDate();
            int transactionGroupSequence = transactionGroup.getSequence();
            int transactionSequence = transaction.getSequence();
            
            ExpenseCategory expenseCategory = transactionGroup.getExpenseCategory();
            
            String description = transaction.getDescription();
            BigDecimal amount = transaction.getAmount();
            String fromAccountName = transaction.getFromAccount().getName();
            String toAccountName = transaction.getToAccount().getName();
            
            String s = DateHelper.getDateTimeString(date) + " " + 
                    transactionGroupSequence + "." + transactionSequence + " " +
                    description + 
                    " [" + fromAccountName + " to " + toAccountName + "]" +
                    " (" + expenseCategory.getTitle() + ")";
            
            SummaryListItem summaryListItem = new SummaryListItem(id, s, amount, 0);
            list.add(summaryListItem);
        }
        
        return list;
	}

}
