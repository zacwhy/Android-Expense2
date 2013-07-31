package com.example.expense;

import java.util.ArrayList;
import java.util.List;

import com.example.expense.models.SummaryListItem;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.app.Activity;

public class EntryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		loadSpinner(R.id.paymentMethod, Values.PaymentMethods);
		loadSpinner(R.id.category, Values.Categories);
		loadListView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}
	
	private void loadSpinner(int id, String[] values) {
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, values);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) findViewById(id);
		spinner.setAdapter(spinnerArrayAdapter);
	}
	
	private void loadListView() {
		List<SummaryListItem> list = new ArrayList<SummaryListItem>();
		
	    list.add(new SummaryListItem("Z", 1.23, 0));
	    list.add(new SummaryListItem("I", 2.23, 0));
	    list.add(new SummaryListItem("M", 3.23, 0));
	    list.add(new SummaryListItem("P", 4.23, 0));
	    list.add(new SummaryListItem("R", 5.23, 0));
	    
	    ListView listView = (ListView) findViewById(R.id.paidFor);
	    listView.setAdapter(new SummaryArrayAdapter(this, list));
	}

}
