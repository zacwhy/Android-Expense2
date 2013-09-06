package com.example.expense;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.expense.data.TransactionGroupHelper;
import com.example.expense.helpers.SummaryListItemHelper;
import com.example.expense.models.SummaryListItem;
import com.example.expense.models.TransactionGroup;

public class TransactionGroupsActivity extends ListActivity {

    private List<SummaryListItem> mListItems;
    private SummaryArrayAdapter mSummaryArrayAdapter;
    private int mPosition;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        List<TransactionGroup> transactionGroupList = TransactionGroupHelper.getList(this);
        mListItems = SummaryListItemHelper.getListItems(transactionGroupList);
        SummaryListItemHelper.sort(mListItems);
        mSummaryArrayAdapter = new SummaryArrayAdapter(this, mListItems);
        setListAdapter(mSummaryArrayAdapter);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transaction_groups, menu);
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
                long id = data.getLongExtra(TransactionGroupActivity.EXTRA_TRANSACTION_GROUP_ID, 0);
                refreshUpdate(id);
                break;
                
            case TransactionGroupActivity.ACTION_UPDATE:
                refreshUpdate(0);
                break;
                
            case TransactionGroupActivity.ACTION_DELETE:
                mListItems.remove(mPosition);
                break;
                
            }
            
            mSummaryArrayAdapter.notifyDataSetChanged();
        }
    }
    
    private void refreshUpdate(long id) {
        if (id == 0) {
            SummaryListItem oldListItem = mListItems.get(mPosition);
            TransactionGroup oldTransactionGroup = (TransactionGroup) oldListItem.getUnderlyingObject();
            id = oldTransactionGroup.getId();
            mListItems.remove(oldListItem);
        }
        
        TransactionGroup newTransactionGroup = TransactionGroupHelper.getById(this, id, true, true, false);
        SummaryListItem newListItem = SummaryListItemHelper.getListItem(newTransactionGroup);
        mListItems.add(newListItem);
        
        SummaryListItemHelper.sort(mListItems);
    }

}
