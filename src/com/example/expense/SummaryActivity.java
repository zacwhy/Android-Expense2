package com.example.expense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.expense.data.ExpenseDatabaseHelper;
import com.example.expense.data.TransactionGroupsDataSource;
import com.example.expense.data.TransactionHelper;
import com.example.expense.helpers.DateHelper;
import com.example.expense.helpers.Helper;
import com.example.expense.models.Account;
import com.example.expense.models.SummaryListItem;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;
import com.example.expense.models.TransactionGroupListComparator;

public class SummaryActivity extends ListActivity {

    private List<SummaryListItem> listItems;
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

	    case R.id.action_accounts:
	        startActivity(new Intent(this, AccountsActivity.class));
	        return true;
	    }
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mPosition = position;
        
	    SummaryListItem listItem = (SummaryListItem) getListView().getItemAtPosition(position);
	    Transaction transaction = (Transaction) listItem.getUnderlyingObject();
	    long transactionGroupId = transaction.getTransactionGroup().getId();
	    
	    Intent intent = new Intent(this, EntryActivity.class);
	    intent.putExtra(EntryActivity.EXTRA_TRANSACTION_GROUP_ID, transactionGroupId);
	    startActivityForResult(intent, 0);
    }
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String action = data.getStringExtra(EntryActivity.EXTRA_ACTION);
            
            if (action.contentEquals(EntryActivity.ACTION_DELETE)) {
                
                mSummaryArrayAdapter.remove(mSummaryArrayAdapter.getItem(mPosition));
                
            } else if (action.contentEquals(EntryActivity.ACTION_UPDATE)) {
                
                refreshUpdate();
                
            } else if (action.contentEquals(EntryActivity.ACTION_INSERT)) {
                
                loadListView();
                
            }
        }
    }
    
    private void refreshUpdate() {
        SummaryListItem listItem = mSummaryArrayAdapter.getItem(mPosition);
        
        Transaction transaction2 = (Transaction) listItem.getUnderlyingObject();
        long transactionGroupId = transaction2.getTransactionGroup().getId();
        
        
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(this);
        
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        TransactionGroup transactionGroup = dataSource.getTransactionGroupWithTransactionsById(transactionGroupId);
        
        Map<Long, Account> accountsMap = Helper.getAccountsMap(db);
        
        databaseHelper.close();
        
        
        long fromAccountId = transactionGroup.getFromAccount().getId();
        Account fromAccount = accountsMap.get(fromAccountId);
        transactionGroup.setFromAccount(fromAccount);
        
        
        Transaction transaction = transactionGroup.getTransactions().get(0);
        
        long toAccountId = transaction.getToAccount().getId();
        Account toAccount = accountsMap.get(toAccountId);
        transaction.setToAccount(toAccount);
        
        String label = getLabel(transactionGroup, transaction);        
        
        SummaryListItem newListItem = new SummaryListItem(transaction, label, transaction.getAmount());
        
        listItems.remove(mPosition);
        listItems.add(newListItem);
        
        sortListItems();
        
        mSummaryArrayAdapter.notifyDataSetChanged();
    }
    
    private void sortListItems() {
        final Comparator<TransactionGroup> comparator = new TransactionGroupListComparator(true);
        
        Collections.sort(listItems, new Comparator<SummaryListItem>() {

            @Override
            public int compare(SummaryListItem lhs, SummaryListItem rhs) {
                
                Transaction t1 = (Transaction) lhs.getUnderlyingObject();
                Transaction t2 = (Transaction) rhs.getUnderlyingObject();
                
                TransactionGroup g1 = t1.getTransactionGroup();
                TransactionGroup g2 = t2.getTransactionGroup();
                
                return comparator.compare(g1, g2);
            }
            
        });
    }
    
	private void loadListView() {
        List<TransactionGroup> list = TransactionHelper.getTransactionGroups(this);
        listItems = getListItems(list);
	    mSummaryArrayAdapter = new SummaryArrayAdapter(this, listItems);
		setListAdapter(mSummaryArrayAdapter);
	}
	
	private List<SummaryListItem> getListItems(List<TransactionGroup> transactionGroups) {
	    List<SummaryListItem> items = new ArrayList<SummaryListItem>();
	    
	    for (TransactionGroup transactionGroup : transactionGroups) {
	        for (Transaction transaction : transactionGroup.getTransactions()) {
	            String label = getLabel(transactionGroup, transaction);
	            BigDecimal amount = transaction.getAmount();
	            SummaryListItem item = new SummaryListItem(transaction, label, amount);
	            items.add(item);
	        }
	    }
	    
	    return items;
	}
	
	private static String getLabel(TransactionGroup transactionGroup, Transaction transaction) {
	    return DateHelper.getDateWithDayOfWeekString(transactionGroup.getDate()) + " " + 
	            transactionGroup.getSequence() + "." + transaction.getSequence() + " " +
	            getDescription(transactionGroup, transaction);
	}
	
	private static String getDescription(TransactionGroup transactionGroup, 
	        Transaction transaction) {
	    
	    if (transaction.getDescription().isEmpty()) {
	        String fromAccount = transactionGroup.getFromAccount().getName();
	        String toAccount = transaction.getToAccount().getName();
            return fromAccount + " to " + toAccount;
        }
	    
	    return transaction.getDescription();
	}

}
