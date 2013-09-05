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

import com.example.expense.data.AccountHelper;
import com.example.expense.data.ExpenseDatabaseHelper;
import com.example.expense.data.TransactionGroupHelper;
import com.example.expense.data.TransactionGroupsDataSource;
import com.example.expense.helpers.DateHelper;
import com.example.expense.models.Account;
import com.example.expense.models.SummaryListItem;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;
import com.example.expense.models.TransactionGroupListComparator;

public class TransactionGroupsActivity extends ListActivity {

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
	        Intent intent = new Intent(this, TransactionGroupActivity.class);
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
	    TransactionGroup transactionGroup = (TransactionGroup) listItem.getUnderlyingObject();
	    long transactionGroupId = transactionGroup.getId();
	    
	    Intent intent = new Intent(this, TransactionGroupActivity.class);
	    intent.putExtra(TransactionGroupActivity.EXTRA_TRANSACTION_GROUP_ID, transactionGroupId);
	    startActivityForResult(intent, 0);
    }
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            int action = data.getIntExtra(TransactionGroupActivity.EXTRA_ACTION, 0);
            
            switch (action) {
            
            case TransactionGroupActivity.ACTION_INSERT:
                loadListView();
                break;
                
            case TransactionGroupActivity.ACTION_UPDATE:
                loadListView();
                //refreshUpdate();
                break;
                
            case TransactionGroupActivity.ACTION_DELETE:
                mSummaryArrayAdapter.remove(mSummaryArrayAdapter.getItem(mPosition));
                break;
                
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
        TransactionGroup transactionGroup = dataSource.getWithTransactionsById(transactionGroupId);
        
        Map<Long, Account> accountsMap = AccountHelper.getMap(db);
        
        databaseHelper.close();
        
        
        long fromAccountId = transactionGroup.getFromAccount().getId();
        Account fromAccount = accountsMap.get(fromAccountId);
        transactionGroup.setFromAccount(fromAccount);
        
        
        Transaction transaction = transactionGroup.getTransactions().get(0);
        
        long toAccountId = transaction.getToAccount().getId();
        Account toAccount = accountsMap.get(toAccountId);
        transaction.setToAccount(toAccount);
        
        String label = getLabel(transactionGroup);
        
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
        List<TransactionGroup> list = TransactionGroupHelper.getSortedList(this);
        listItems = getListItems(list);
	    mSummaryArrayAdapter = new SummaryArrayAdapter(this, listItems);
		setListAdapter(mSummaryArrayAdapter);
	}
	
	private List<SummaryListItem> getListItems(List<TransactionGroup> transactionGroups) {
	    List<SummaryListItem> items = new ArrayList<SummaryListItem>();
	    
	    for (TransactionGroup transactionGroup : transactionGroups) {
	        BigDecimal totalAmount = BigDecimal.ZERO;
	        
	        for (Transaction transaction : transactionGroup.getTransactions()) {
	            totalAmount = totalAmount.add(transaction.getAmount());
	        }
	        
	        String label = getLabel(transactionGroup);
	        SummaryListItem item = new SummaryListItem(transactionGroup, label, totalAmount);
            items.add(item);
	    }
	    
	    return items;
	}
	
	private static String getLabel(TransactionGroup transactionGroup) {
        return DateHelper.getDateWithDayOfWeekString(transactionGroup.getDate()) + " " + 
                transactionGroup.getSequence() + " " + 
                getDescription(transactionGroup);
    }
	
	private static String getDescription(TransactionGroup transactionGroup) {
	    if (transactionGroup.getTransactions().size() == 1) {
	        Transaction transaction = transactionGroup.getTransactions().get(0);
	        
	        if (transaction.getDescription().isEmpty()) {
	            String fromAccount = transactionGroup.getFromAccount().getName();
	            String toAccount = transaction.getToAccount().getName();
	            return fromAccount + " to " + toAccount;
	        }
	        
	        return transaction.getDescription();
	    }
	    
	    if (transactionGroup.getTransactions().size() > 1) {
	        String expenseCategory = transactionGroup.getExpenseCategory().getTitle();
	        String fromAccount = transactionGroup.getFromAccount().getName();
	        int transactionCount = transactionGroup.getTransactions().size();
	        return String.format("%s by %s (%s)", expenseCategory, fromAccount, transactionCount);
	    }
	    
	    return "NO TRANSACTIONS?";
	}

}
