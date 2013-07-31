package com.example.expense;

import java.util.ArrayList;
import java.util.List;

import com.example.expense.data.ExpenseCategoriesDataSource;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.SummaryListItem;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class SummaryActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadActionBar();
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
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("NewApi")
	private void loadActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayShowTitleEnabled(false);

		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list,
		          android.R.layout.simple_spinner_dropdown_item);
		
		ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				//lanuchItemSelected(itemPosition);
				return true;
			}
		};

		actionBar.setListNavigationCallbacks(mSpinnerAdapter, navigationListener);
	}
	
	private void loadListView() {
		List<SummaryListItem> list = new ArrayList<SummaryListItem>();
		
		List<ExpenseCategory> expenseCategories = getExpenseCategories();
		
		for (ExpenseCategory expenseCategory : expenseCategories) {
			list.add(new SummaryListItem(expenseCategory.getTitle(), expenseCategory.getId(), 2.22));
		}
	    
//	    for (String category : Values.Categories) {
//	    	list.add(new SummaryListItem(category, 1.23, 4.56));
//	    }
		
		setListAdapter(new SummaryArrayAdapter(this, list));
	}
	
	private List<ExpenseCategory> getExpenseCategories() {
		ExpenseCategoriesDataSource datasource = new ExpenseCategoriesDataSource(this);
		datasource.open();
		List<ExpenseCategory> expenseCategories = datasource.getAllExpenseCategories();
		datasource.close();
		return expenseCategories;
	}

}
