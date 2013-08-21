package com.example.expense;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;

import com.example.expense.models.SummaryListItem;

public class SummaryTwoActivity extends FragmentActivity {
    TabHost tHost;  

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        loadActionBar();
        
        tHost = (TabHost) findViewById(android.R.id.tabhost);
        tHost.setup();
        
        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
            
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equalsIgnoreCase("android")){
                    loadListView(1);
                } else {
                    loadListView(2);
                }
                
//                android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
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
//                }else{  /** If current tab is apple */
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
        
        
        /** Setting tabchangelistener for the tab */
        tHost.setOnTabChangedListener(tabChangeListener);
                
        /** Defining tab builder for Andriod tab */
        TabHost.TabSpec tSpecAndroid = tHost.newTabSpec("android");
        tSpecAndroid.setIndicator("All");//,getResources().getDrawable(R.drawable.android));        
        tSpecAndroid.setContent(new DummyTabContent(getBaseContext()));        
        tHost.addTab(tSpecAndroid);
        
        
        /** Defining tab builder for Apple tab */
        TabHost.TabSpec tSpecApple = tHost.newTabSpec("apple");
        tSpecApple.setIndicator("Category");//,getResources().getDrawable(R.drawable.apple));        
        tSpecApple.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecApple);
        
        TabHost.TabSpec tSpecMonth = tHost.newTabSpec("month");
        tSpecMonth.setIndicator("Account");
        tSpecMonth.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecMonth);
        
//        TabHost.TabSpec tSpecYear = tHost.newTabSpec("year");
//        tSpecYear.setIndicator("Year");
//        tSpecYear.setContent(new DummyTabContent(getBaseContext()));
//        tHost.addTab(tSpecYear);
        
        
        loadListView(1);
     }
    
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
    
    private void loadListView(int i) {
        List<SummaryListItem> list = new ArrayList<SummaryListItem>();
        
//        if (i == 1) {
//            list.add(new SummaryListItem("a", new BigDecimal(1), 4.56));
//            list.add(new SummaryListItem("b", new BigDecimal(2), 4.56));
//            list.add(new SummaryListItem("c", new BigDecimal(3), 4.56));
//        } else {
//            list.add(new SummaryListItem("d", new BigDecimal(1), 4.56));
//            list.add(new SummaryListItem("e", new BigDecimal(2), 4.56));
//            list.add(new SummaryListItem("f", new BigDecimal(3), 4.56));
//        }
        
        SummaryArrayAdapter listAdapter = new SummaryArrayAdapter(this, list);
        //listAdapter.clear();
        
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(listAdapter);
    }
    
}
