package com.example.expense;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.expense.models.SummaryListItem;

public class SummaryThreeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_three);
        initializeActionBar();
        initializeTabHost();
        loadListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.summary_three, menu);
        return true;
    }
    
    private void initializeActionBar() {
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

    private void initializeTabHost() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                //String a = tabId;
                //String b = a;
                //android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
//                AndroidFragment androidFragment = (AndroidFragment) fm.findFragmentByTag("android");
//                AppleFragment appleFragment = (AppleFragment) fm.findFragmentByTag("apple");
//                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
//
//                /** Detaches the androidfragment if exists */
//                if(androidFragment!=null)
//                    ft.detach(androidFragment);
//
//                /** Detaches the applefragment if exists */
//                if(appleFragment!=null)
//                    ft.detach(appleFragment);
//
//                /** If current tab is android */
//                if(tabId.equalsIgnoreCase("android")){
//
//                    if(androidFragment==null){
//                        /** Create AndroidFragment and adding to fragmenttransaction */
//                        ft.add(R.id.realtabcontent,new AndroidFragment(), "android");
//                    }else{
//                        /** Bring to the front, if already exists in the fragmenttransaction */
//                        ft.attach(androidFragment);
//                    }
//
//                }else{    /** If current tab is apple */
//                    if(appleFragment==null){
//                        /** Create AppleFragment and adding to fragmenttransaction */
//                        ft.add(R.id.realtabcontent,new AppleFragment(), "apple");
//                    }else{
//                        /** Bring to the front, if already exists in the fragmenttransaction */
//                        ft.attach(appleFragment);
//                    }
//                }
//                ft.commit();
            }
        };
        
        tabHost.setOnTabChangedListener(tabChangeListener);
        
        TabSpec spec1 = tabHost.newTabSpec("Day");
        spec1.setIndicator("Day");
        spec1.setContent(R.id.tab1);
        tabHost.addTab(spec1);
        
        TabSpec spec2 = tabHost.newTabSpec("Week");
        spec2.setIndicator("Week");
        spec2.setContent(R.id.tab2);
        tabHost.addTab(spec2);
      
        TabSpec spec3 = tabHost.newTabSpec("Month");
        spec3.setIndicator("Month");
        spec3.setContent(R.id.tab3);
        tabHost.addTab(spec3);
        
        TabSpec spec4 = tabHost.newTabSpec("Year");
        spec4.setIndicator("Year");
        spec4.setContent(R.id.tab1);
        tabHost.addTab(spec4);
    }
    
    
    private void loadListView() {
        //ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
        //SQLiteDatabase database = dbHelper.getReadableDatabase();
        //PaymentsDataSource dataSource = new PaymentsDataSource(database);
        //List<Payment> payments = dataSource.getAll();
        //dbHelper.close();
        
        List<SummaryListItem> list = new ArrayList<SummaryListItem>();
        
        //for (Payment payment : payments) {
        //    list.add(new SummaryListItem(payment.toString(), payment.getAmount(), 4.56));
        //}
        
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new SummaryArrayAdapter(this, list));
    }


}
