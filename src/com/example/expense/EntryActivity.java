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
    public final static String EXTRA_ACTION = "com.example.expense.ACTION";
    
    public final static String ACTION_DELETE = "delete";
    
    private TransactionGroup mTransactionGroup;
    private long mTransactionId;
    private Transaction mTransaction;
    private Calendar mDate; // TODO: remove
    
    private ArrayAdapter<Account> mFromAccountArrayAdapter;
    private ArrayAdapter<Account> mToAccountArrayAdapter;
    private ArrayAdapter<ExpenseCategory> mExpenseCategoryArrayAdapter;

    private AutoCompleteTextView mAutoCompleteTextViewDescription;
    private EditText mEditTextAmount;
    private Spinner mSpinnerExpenseCategory;
    private Spinner mSpinnerFromAccount;
    private Spinner mSpinnerToAccount;
    private Button mButtonDate;
    private Button mButtonDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_entry);
	    setFinishOnTouchOutside(false);
	    populateSpinners();
        addListeners();
	    
	    mTransactionId = getIntent().getLongExtra(EXTRA_TRANSACTION_ID, 0);
	    
	    if (isNewTransaction()) {
	        mTransactionGroup = new TransactionGroup();
	        mTransactionGroup.setDate(DateHelper.getCurrentDateOnly());
	        
	        setCurrentDate();
	        getButtonDelete().setVisibility(View.INVISIBLE);
	    } else {
	        mTransaction = TransactionHelper.getTransaction(this, mTransactionId);
	        populateFields(mTransaction);
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
    
    private boolean isNewTransaction() {
        return mTransactionId == 0;
    }

    private void setCurrentDate() {
        mDate = Calendar.getInstance();
        mDate.set(Calendar.HOUR_OF_DAY, 0);
        mDate.set(Calendar.MINUTE, 0);
        mDate.set(Calendar.SECOND, 0);
        mDate.set(Calendar.MILLISECOND, 0);
    }
    
    private void addListeners() {
        getButtonDate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        
        getButtonDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonDelete();
            }
        });
        
        Button buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonOk();
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
        mFromAccountArrayAdapter = new ArrayAdapter<Account>(this,
                android.R.layout.simple_spinner_item, accounts);
        mFromAccountArrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        getSpinnerFromAccount().setAdapter(mFromAccountArrayAdapter);
    }
	
    private void populateToAccountSpinner(List<Account> accounts) {
        mToAccountArrayAdapter = new ArrayAdapter<Account>(this,
                android.R.layout.simple_spinner_item, accounts);
        mToAccountArrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        getSpinnerToAccount().setAdapter(mToAccountArrayAdapter);
    }
	
	private void populateExpenseCategorySpinner(List<ExpenseCategory> expenseCategories) {
	    mExpenseCategoryArrayAdapter= new ArrayAdapter<ExpenseCategory>(this,
				android.R.layout.simple_spinner_item, expenseCategories);
	    mExpenseCategoryArrayAdapter.setDropDownViewResource(
	            android.R.layout.simple_spinner_dropdown_item);
		
		getSpinnerExpenseCategory().setAdapter(mExpenseCategoryArrayAdapter);
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
        int position = getAccountPosition(fromAccount, mFromAccountArrayAdapter);
        getSpinnerFromAccount().setSelection(position);
    }
    
    private void setSpinnerToAccountSelection(Account toAccount) {
        int position = getAccountPosition(toAccount, mToAccountArrayAdapter);
        getSpinnerToAccount().setSelection(position);
    }
    
    private void setSpinnerExpenseCategorySelection(ExpenseCategory expenseCategory) {
        int position = getExpenseCategoryPosition(expenseCategory, mExpenseCategoryArrayAdapter);
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
    
    private void onClickButtonOk() {
        if (validateInputs()) {
            if (isNewTransaction()) {
                TransactionHelper.insertTransactionGroup(this, getInputTransactionGroup());
            } else {
                // update
            }
            finish();
        }
    }
    
    private void onClickButtonDelete() {
        TransactionHelper.deleteTransactionGroup(this, mTransaction.getTransactionGroup());
        
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ACTION, ACTION_DELETE);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateInputs() {
        if (getEditTextAmount().getText().length() == 0) {
            getEditTextAmount().setError(getString(R.string.error_required_amount));
            return false;
        }
        return true;
    }
    
    private TransactionGroup getInputTransactionGroup() {
        ExpenseCategory expenseCategory = 
                (ExpenseCategory) getSpinnerExpenseCategory().getSelectedItem();
        Account fromAccount = (Account) getSpinnerFromAccount().getSelectedItem();
        Account toAccount = (Account) getSpinnerToAccount().getSelectedItem();
        BigDecimal amount = new BigDecimal(getEditTextAmount().getText().toString());
        String description = getAutoCompleteTextViewDescription().getText().toString();
        
        Transaction transaction = new Transaction();
        transaction.setSequence(1);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        
        mTransactionGroup.setExpenseCategory(expenseCategory);
        mTransactionGroup.getTransactions().add(transaction);
        
        return mTransactionGroup;
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
    
    private Button getButtonDelete() {
        if (mButtonDelete == null) {
            mButtonDelete = (Button) findViewById(R.id.buttonDelete);
        }
        return mButtonDelete;
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
