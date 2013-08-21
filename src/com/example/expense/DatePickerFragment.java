package com.example.expense;

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

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    int year = getArguments().getInt("year");
	    int month = getArguments().getInt("month");
	    int day = getArguments().getInt("day");
	    
		// Use the current date as the default date in the picker
//		final Calendar c = Calendar.getInstance();
//		int year = c.get(Calendar.YEAR);
//		int month = c.get(Calendar.MONTH);
//		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
	    OnDateSetListener callBack = (OnDateSetListener) getActivity();
		return new DatePickerDialog(getActivity(), callBack, year, month, day);
	}

}