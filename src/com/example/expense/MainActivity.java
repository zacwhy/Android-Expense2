package com.example.expense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //InitialDataLoader.initialize(this);
        
        // SummaryActivity SummaryTwoActivity SummaryThreeActivity SummaryBbbActivity
        Intent intent = new Intent(this, SummaryActivity.class);
        startActivity(intent);
        
        finish();
        
        //setContentView(R.layout.activity_main);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
    
}
