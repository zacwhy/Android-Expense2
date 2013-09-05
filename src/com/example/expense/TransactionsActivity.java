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

import com.example.expense.models.Account;
import com.example.expense.models.SummaryListItem;
import com.example.expense.models.Transaction;

public class TransactionsActivity extends ListActivity {

    public static final String EXTRA_TRANSACTIONS = "com.example.expense.TRANSACTIONS";
    
    private List<SummaryListItem> mListItems;
    private SummaryArrayAdapter mSummaryArrayAdapter;
    private int mPosition;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        List<Transaction> transactions = intent.getParcelableArrayListExtra(EXTRA_TRANSACTIONS);
        mListItems = new ArrayList<SummaryListItem>();
        
        for (Transaction transaction : transactions) {
            SummaryListItem listItem = new SummaryListItem(transaction, transaction.getDescription(), transaction.getAmount());
            mListItems.add(listItem);
        }
        
        mSummaryArrayAdapter = new SummaryArrayAdapter(this, mListItems);
        setListAdapter(mSummaryArrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.transactions, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_ok:
            onActionOk();
            return true;
        case R.id.action_add:
            mPosition = -1;
            Intent intent = new Intent(this, TransactionActivity.class);
            intent.putExtra(TransactionActivity.EXTRA_ACTION, TransactionActivity.ACTION_INSERT);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void onActionOk() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        
        for (SummaryListItem listItem : mListItems) {
            Transaction transaction = (Transaction) listItem.getUnderlyingObject();
            transactions.add(transaction);
        }
        
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_TRANSACTIONS, (ArrayList<Transaction>) transactions);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
        mPosition = position;
        
        SummaryListItem listItem = (SummaryListItem) getListView().getItemAtPosition(position);
        Transaction transaction = (Transaction) listItem.getUnderlyingObject();
        
        Intent intent = new Intent(this, TransactionActivity.class);
        intent.putExtra(TransactionActivity.EXTRA_ACTION, TransactionActivity.ACTION_UPDATE);
        intent.putExtra(TransactionActivity.EXTRA_TO_ACCOUNT_ID, transaction.getToAccount().getId());
        intent.putExtra(TransactionActivity.EXTRA_AMOUNT, transaction.getAmount());
        intent.putExtra(TransactionActivity.EXTRA_DESCRIPTION, transaction.getDescription());
        
        startActivityForResult(intent, 0);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            onActivityResultPositive(data);
        }
    }

    private void onActivityResultPositive(Intent data) {
        int action = data.getIntExtra(TransactionActivity.EXTRA_ACTION, 0);

        if (action == TransactionActivity.ACTION_DELETE) {
            SummaryListItem listItem = mListItems.get(mPosition);
            mListItems.remove(listItem);
            mSummaryArrayAdapter.notifyDataSetChanged();
            return;
        }

        long toAccountId = data.getLongExtra(TransactionActivity.EXTRA_TO_ACCOUNT_ID, -1);
        Account toAccount = new Account(toAccountId);
        BigDecimal amount = (BigDecimal) data.getSerializableExtra(TransactionActivity.EXTRA_AMOUNT);
        String description = data.getStringExtra(TransactionActivity.EXTRA_DESCRIPTION);

        if (action == TransactionActivity.ACTION_INSERT) {
            Transaction transaction = new Transaction();
            transaction.setToAccount(toAccount);
            transaction.setAmount(amount);
            transaction.setDescription(description);

            SummaryListItem listItem = new SummaryListItem(transaction, description, amount);
            mListItems.add(listItem);
        } else if (action == TransactionActivity.ACTION_UPDATE) {
            SummaryListItem listItem = mListItems.get(mPosition);

            Transaction transaction = (Transaction) listItem.getUnderlyingObject();
            transaction.setToAccount(toAccount);
            transaction.setAmount(amount);
            transaction.setDescription(description);

            listItem.setUnderlyingObject(transaction);
            listItem.setLabel(description);
            listItem.setAmount(amount);
        }

        mSummaryArrayAdapter.notifyDataSetChanged();
    }

}
