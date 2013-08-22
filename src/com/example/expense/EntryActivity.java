package com.example.expense;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.expense.data.ExpenseDbHelper;
import com.example.expense.data.Helper;
import com.example.expense.data.TransactionHelper;
import com.example.expense.helpers.DateHelper;
import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class EntryActivity extends FragmentActivity implements OnDateSetListener {
    
    public final static String EXTRA_TRANSACTION_ID = "com.example.expense.TRANSACTION_ID";
    
    private Calendar mDate;
    
    private ArrayAdapter<Account> fromAccountArrayAdapter;
    private ArrayAdapter<Account> toAccountArrayAdapter;
    private ArrayAdapter<ExpenseCategory> expenseCategoryArrayAdapter;

    private AutoCompleteTextView mAutoCompleteTextViewDescription;
    private EditText mEditTextAmount;
    private Spinner mSpinnerExpenseCategory;
    private Spinner mSpinnerFromAccount;
    private Spinner mSpinnerToAccount;
    private Button mButtonDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_entry);
	    
	    setFinishOnTouchOutside(false);

	    populateSpinners();
        addListenerOnButtonDate();
	    
	    Intent intent = getIntent();
	    long transactionId = intent.getLongExtra(EXTRA_TRANSACTION_ID, -1);
	    
	    if (transactionId == -1) {
	        setCurrentDate();
	    } else {
	        Transaction transaction = TransactionHelper.getTransaction(this, transactionId);
	        populateFields(transaction);
	    }
	    
	    refreshDateOnView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}
	
	@Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
	    mDate.set(year, month, day);
	    refreshDateOnView();
    }
    
    public void addPayment(View view) {
        if (validateInputs()) {
            saveTransaction();
            finish();
        }
    }

    private void setCurrentDate() {
        mDate = Calendar.getInstance();
        mDate.set(Calendar.HOUR_OF_DAY, 0);
        mDate.set(Calendar.MINUTE, 0);
        mDate.set(Calendar.SECOND, 0);
        mDate.set(Calendar.MILLISECOND, 0);
    }
    
    private void addListenerOnButtonDate() {
        getButtonDate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
    }
    
    private void refreshDateOnView() {
        String dateString = DateHelper.getDateWithDayOfWeekString(mDate);
        getButtonDate().setText(dateString);
    }
	
	private void populateSpinners() {
		ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		List<Account> accounts = Helper.getAccounts(db);
		List<ExpenseCategory> expenseCategories = Helper.getExpenseCategories(db);
		
		dbHelper.close();
		
		populateToAccountSpinner(accounts);
		populateFromAccountSpinner(accounts);
		populateExpenseCategorySpinner(expenseCategories);
	}
    
    private void populateFromAccountSpinner(List<Account> accounts) {
        fromAccountArrayAdapter = new ArrayAdapter<Account>(this,
                android.R.layout.simple_spinner_item, accounts);
        fromAccountArrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        getSpinnerFromAccount().setAdapter(fromAccountArrayAdapter);
    }
	
    private void populateToAccountSpinner(List<Account> accounts) {
        toAccountArrayAdapter = new ArrayAdapter<Account>(this,
                android.R.layout.simple_spinner_item, accounts);
        toAccountArrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        getSpinnerToAccount().setAdapter(toAccountArrayAdapter);
    }
	
	private void populateExpenseCategorySpinner(List<ExpenseCategory> expenseCategories) {
	    expenseCategoryArrayAdapter= new ArrayAdapter<ExpenseCategory>(this,
				android.R.layout.simple_spinner_item, expenseCategories);
	    expenseCategoryArrayAdapter.setDropDownViewResource(
	            android.R.layout.simple_spinner_dropdown_item);
		
		getSpinnerExpenseCategory().setAdapter(expenseCategoryArrayAdapter);
	}

    private void populateFields(Transaction transaction) {
        mDate = transaction.getTransactionGroup().getDate();
        
        getAutoCompleteTextViewDescription().setText(transaction.getDescription());
        getEditTextAmount().setText(transaction.getAmount().toPlainString());
        
        setSpinnerFromAccountSelection(transaction.getFromAccount());
        setSpinnerToAccountSelection(transaction.getToAccount());
        setSpinnerExpenseCategorySelection(transaction.getTransactionGroup().getExpenseCategory());
    }
    
    private void setSpinnerFromAccountSelection(Account fromAccount) {
        int position = getAccountPosition(fromAccount, fromAccountArrayAdapter);
        getSpinnerFromAccount().setSelection(position);
    }
    
    private void setSpinnerToAccountSelection(Account toAccount) {
        int position = getAccountPosition(toAccount, toAccountArrayAdapter);
        getSpinnerToAccount().setSelection(position);
    }
    
    private void setSpinnerExpenseCategorySelection(ExpenseCategory expenseCategory) {
        int position = getExpenseCategoryPosition(expenseCategory, expenseCategoryArrayAdapter);
        getSpinnerExpenseCategory().setSelection(position);
    }
	
	private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        
        int year = mDate.get(Calendar.YEAR);
        int month = mDate.get(Calendar.MONTH);
        int day = mDate.get(Calendar.DAY_OF_MONTH);
        
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        newFragment.setArguments(args);
        
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    
    private boolean validateInputs() {
        if (getEditTextAmount().getText().length() == 0) {
            getEditTextAmount().setError(getString(R.string.error_required_amount));
            return false;
        }
        
        return true;
    }

    private void saveTransaction() {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        
        TransactionHelper transactionHelper = new TransactionHelper(database);
        transactionHelper.insert(getTransaction());
        
        dbHelper.close();
    }
    
    private Transaction getTransaction() {
        Transaction transaction = new Transaction();
        
        transaction.setTransactionGroup(getTransactionGroup());
        
        int sequence = 1; // TODO
        transaction.setSequence(sequence);
        
        Account fromAccount = (Account) getSpinnerFromAccount().getSelectedItem();
        transaction.setFromAccount(fromAccount);
        
        Account toAccount = (Account) getSpinnerToAccount().getSelectedItem();
        transaction.setToAccount(toAccount);
        
        // amount
        BigDecimal amount = new BigDecimal(getEditTextAmount().getText().toString());
        transaction.setAmount(amount);
        
        // description
        String description = getAutoCompleteTextViewDescription().getText().toString();
        transaction.setDescription(description);
        
        return transaction;
    }
    
    private TransactionGroup getTransactionGroup() {
        TransactionGroup transactionGroup = new TransactionGroup();
        
        transactionGroup.setDate(mDate);
        
        ExpenseCategory expenseCategory = 
                (ExpenseCategory) getSpinnerExpenseCategory().getSelectedItem();
        transactionGroup.setExpenseCategory(expenseCategory);
        
        return transactionGroup;
    }
    
    private AutoCompleteTextView getAutoCompleteTextViewDescription() {
        if (mAutoCompleteTextViewDescription == null) {
            mAutoCompleteTextViewDescription = 
                    (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewDescription);
        }
        return mAutoCompleteTextViewDescription;
    }
    
    private EditText getEditTextAmount() {
        if (mEditTextAmount == null) {
            mEditTextAmount = (EditText) findViewById(R.id.amount);
        }
        return mEditTextAmount;
    }
    
    private Spinner getSpinnerExpenseCategory() {
        if (mSpinnerExpenseCategory == null) {
            mSpinnerExpenseCategory = (Spinner) findViewById(R.id.spinnerCategory);
        }
        return mSpinnerExpenseCategory;
    }
    
    private Spinner getSpinnerFromAccount() {
        if (mSpinnerFromAccount == null) {
            mSpinnerFromAccount = (Spinner) findViewById(R.id.paymentMethod);
        }
        return mSpinnerFromAccount;
    }
    
    private Spinner getSpinnerToAccount() {
        if (mSpinnerToAccount == null) {
            mSpinnerToAccount = (Spinner) findViewById(R.id.spinnerToAccount);
        }
        return mSpinnerToAccount;
    }
    
    private Button getButtonDate() {
        if (mButtonDate == null) {
            mButtonDate = (Button) findViewById(R.id.buttonDate);
        }
        return mButtonDate;
    }
    
    private static int getAccountPosition(Account target, ArrayAdapter<Account> arrayAdapter) {
        for (int position = 0; position < arrayAdapter.getCount(); position++)  {
            Account account = arrayAdapter.getItem(position);
            if (account.getId() == target.getId()) {
                return position;
            }
        }
        return -1;
    }

    private static int getExpenseCategoryPosition(ExpenseCategory target, 
            ArrayAdapter<ExpenseCategory> arrayAdapter) {
        for (int position = 0; position < arrayAdapter.getCount(); position++)  {
            ExpenseCategory expenseCategory = arrayAdapter.getItem(position);
            if (expenseCategory.getId() == target.getId()) {
                return position;
            }
        }
        return -1;
    }

}
