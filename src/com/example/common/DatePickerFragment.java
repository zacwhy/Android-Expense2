package com.example.common;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

//
// http://developer.android.com/guide/topics/ui/controls/pickers.html
// http://stackoverflow.com/questions/11527051/get-date-from-datepicker-using-dialogfragment
//

public class DatePickerFragment extends DialogFragment {
    
    public final static String EXTRA_YEAR = "com.example.expense.year";
    public final static String EXTRA_MONTH = "com.example.expense.month";
    public final static String EXTRA_DAY = "com.example.expense.day";
    
    private final static int DEFAULT_VALUE = -1;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    Bundle arguments = getArguments();
	    
	    int year = arguments.getInt(EXTRA_YEAR, DEFAULT_VALUE);
	    int month = arguments.getInt(EXTRA_MONTH, DEFAULT_VALUE);
	    int day = arguments.getInt(EXTRA_DAY, DEFAULT_VALUE);
	    
	    if (year == DEFAULT_VALUE || month == DEFAULT_VALUE || day == DEFAULT_VALUE) {
	        // Use the current date as the default date in the picker
	        final Calendar calendar = Calendar.getInstance();
	        
	        if (year == DEFAULT_VALUE) {
	            year = calendar.get(Calendar.YEAR);
	        }
	        
	        if (month == DEFAULT_VALUE) {
	            month = calendar.get(Calendar.MONTH);
	        }
	        
	        if (day == DEFAULT_VALUE) {
	            day = calendar.get(Calendar.DAY_OF_MONTH);
	        }
	    }

	    Activity activity = getActivity();
	    
	    OnDateSetListener callBack = (OnDateSetListener) activity;
	    
		return new DatePickerDialog(activity, callBack, year, month, day);
	}

}