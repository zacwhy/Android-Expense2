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
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.SummaryListItem;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class SummaryActivity extends ListActivity {

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
	    SummaryListItem listItem = (SummaryListItem) getListView().getItemAtPosition(position);
	    
	    Intent intent = new Intent(this, EntryActivity.class);
	    intent.putExtra(EntryActivity.EXTRA_TRANSACTION_ID, listItem.getId());
	    startActivityForResult(intent, 0);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadListView();
    }
	
	private void loadListView() {
	    List<Transaction> transactions = TransactionHelper.getTransactions(this);
	    List<SummaryListItem> list = getSummaryListItems(transactions);
	    SummaryArrayAdapter summaryArrayAdapter = new SummaryArrayAdapter(this, list);
		setListAdapter(summaryArrayAdapter);
	}
	
	private List<SummaryListItem> getSummaryListItems(List<Transaction> transactions) {
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
