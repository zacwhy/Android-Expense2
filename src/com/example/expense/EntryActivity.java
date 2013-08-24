package com.example.expense;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
import com.example.expense.data.TransactionGroupsDataSource;
import com.example.expense.data.TransactionHelper;
import com.example.expense.helpers.DateHelper;
import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class EntryActivity extends FragmentActivity implements OnDateSetListener {
    
    public final static String EXTRA_TRANSACTION_GROUP_ID = "com.example.expense.TRANSACTION_ID";
    public final static String EXTRA_ACTION = "com.example.expense.ACTION";
    public final static String ACTION_DELETE = "delete";
    
    private long mTransactionGroupId;
    private TransactionGroup mTransactionGroup;
    
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
	    
        mTransactionGroupId = getIntent().getLongExtra(EXTRA_TRANSACTION_GROUP_ID, 0);
        
        
        // database access
        
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        
        List<Account> accountsList = Helper.getAccounts(readableDatabase);
        List<ExpenseCategory> expenseCategoriesList = Helper.getExpenseCategories(readableDatabase);
        
        if (!isNew()) {
            TransactionGroupsDataSource dataSource = 
                    new TransactionGroupsDataSource(readableDatabase);
            
            mTransactionGroup = 
                    dataSource.getTransactionGroupWithTransactionsById(mTransactionGroupId);
        }
        
        dbHelper.close();

	    
	    Map<Long, Account> mAccountsMap = Helper.convertToAccountsMap(accountsList);
	    Map<Long, ExpenseCategory> mExpenseCategoriesMap = 
	            Helper.convertToExpenseCategoriesMap(expenseCategoriesList);
	    
	    populateSpinners(accountsList, expenseCategoriesList);
	    
        if (isNew()) {
            mTransactionGroup = new TransactionGroup();
            mTransactionGroup.setDate(DateHelper.getCurrentDateOnly());
            getButtonDelete().setVisibility(View.INVISIBLE);
        } else {
            TransactionHelper.populateTransactionGroupChildren(mTransactionGroup, 
                    mAccountsMap, mExpenseCategoriesMap);
            
	        populateFields(mTransactionGroup);
	    }
	    
	    refreshDateOnView();
        addListeners();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}
	
	@Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
	    mTransactionGroup.getDate().set(year, month, day);
	    refreshDateOnView();
    }
    
    private boolean isNew() {
        return mTransactionGroupId == 0;
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
        String dateString = DateHelper.getDateWithDayOfWeekString(mTransactionGroup.getDate());
        getButtonDate().setText(dateString);
    }
	
	private void populateSpinners(List<Account> accounts, List<ExpenseCategory> expenseCategories) {
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

    private void populateFields(TransactionGroup transactionGroup) {
        Transaction transaction = transactionGroup.getTransactions().get(0);
        
        getAutoCompleteTextViewDescription().setText(transaction.getDescription());
        getEditTextAmount().setText(transaction.getAmount().toPlainString());
        
        setSpinnerFromAccountSelection(transaction.getFromAccount());
        setSpinnerToAccountSelection(transaction.getToAccount());
        setSpinnerExpenseCategorySelection(transactionGroup.getExpenseCategory());
    }
    
    private void setSpinnerFromAccountSelection(Account fromAccount) {
        int position = mFromAccountArrayAdapter.getPosition(fromAccount);
        getSpinnerFromAccount().setSelection(position);
    }
    
    private void setSpinnerToAccountSelection(Account toAccount) {
        int position = mToAccountArrayAdapter.getPosition(toAccount);
        getSpinnerToAccount().setSelection(position);
    }
    
    private void setSpinnerExpenseCategorySelection(ExpenseCategory expenseCategory) {
        int position = mExpenseCategoryArrayAdapter.getPosition(expenseCategory);
        getSpinnerExpenseCategory().setSelection(position);
    }
	
	private void showDatePickerDialog(View v) {
        Calendar calendar = mTransactionGroup.getDate();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    
    private void onClickButtonOk() {
        if (validateInputs()) {
            if (isNew()) {
                TransactionHelper.insertTransactionGroup(this, getInputTransactionGroup());
            } else {
                // update
            }
            finish();
        }
    }
    
    private void onClickButtonDelete() {
        TransactionHelper.deleteTransactionGroup(this, mTransactionGroup); // TODO by Id
        
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

}
