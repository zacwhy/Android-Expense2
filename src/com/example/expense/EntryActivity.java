package com.example.expense;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.expense.data.ExpenseDatabaseHelper;
import com.example.expense.data.TransactionGroupHelper;
import com.example.expense.data.TransactionGroupsDataSource;
import com.example.expense.helpers.DateHelper;
import com.example.expense.helpers.Helper;
import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class EntryActivity extends FragmentActivity implements OnDateSetListener {
    
    public final static String EXTRA_TRANSACTION_GROUP_ID = "com.example.expense.TRANSACTION_GROUP_ID";
    public final static String EXTRA_ACTION = "com.example.expense.ACTION";
    
    public final static int ACTION_INSERT = 1;
    public final static int ACTION_UPDATE = 2;
    public final static int ACTION_DELETE = 3;
    
    private long mTransactionGroupId;
    private TransactionGroup mTransactionGroup;
    
    private Map<Long, Account> mAccountsMap;
    
    private ArrayAdapter<Account> mToAccountArrayAdapter;

    private Calendar mCalendar;
    private Button mButtonDate;
    private Spinner mSpinnerExpenseCategory;
    private Spinner mSpinnerFromAccount;

    private AutoCompleteTextView mAutoCompleteTextViewDescription;
    private EditText mEditTextAmount;
    private Spinner mSpinnerToAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_entry);
	    
	    Intent intent = getIntent();
	    
        mTransactionGroupId = intent.getLongExtra(EXTRA_TRANSACTION_GROUP_ID, 0);
        
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        List<Account> accountsList = Helper.getAccounts(db);
        List<ExpenseCategory> expenseCategoriesList = Helper.getExpenseCategories(db);
        
        if (isInsertMode()) {
            mTransactionGroup = new TransactionGroup();
            mTransactionGroup.setDate(DateHelper.getCurrentDateOnly());
        } else {
            TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
            mTransactionGroup = dataSource.getWithTransactionsById(mTransactionGroupId);
        }
        
        databaseHelper.close();
        
        mAccountsMap = Helper.convertToAccountsMap(accountsList);

        ArrayAdapter<ExpenseCategory> expenseCategoryArrayAdapter= new ArrayAdapter<ExpenseCategory>(this, android.R.layout.simple_spinner_item, expenseCategoriesList);
        expenseCategoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSpinnerExpenseCategory().setAdapter(expenseCategoryArrayAdapter);

        ArrayAdapter<Account> fromAccountArrayAdapter = new ArrayAdapter<Account>(this, android.R.layout.simple_spinner_item, accountsList);
        fromAccountArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSpinnerFromAccount().setAdapter(fromAccountArrayAdapter);

        mToAccountArrayAdapter = new ArrayAdapter<Account>(this, android.R.layout.simple_spinner_item, accountsList);
        mToAccountArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSpinnerToAccount().setAdapter(mToAccountArrayAdapter);
	    
        if (!isInsertMode()) {
            TransactionGroupHelper.populateChildren(mTransactionGroup, accountsList, expenseCategoriesList);
            
            int position1 = expenseCategoryArrayAdapter.getPosition(mTransactionGroup.getExpenseCategory());
            getSpinnerExpenseCategory().setSelection(position1);
            
            int position2 = fromAccountArrayAdapter.getPosition(mTransactionGroup.getFromAccount());
            getSpinnerFromAccount().setSelection(position2);
            
            if (isTransactionFieldsVisible()) {
                Transaction transaction = mTransactionGroup.getTransactions().get(0);
                populateTransactionFields(transaction);
            } else if (mTransactionGroup.getTransactions().size() > 1) {
                setTransactionFieldsVisibility(View.GONE);
            }
	    }
	    
        mCalendar = (Calendar) mTransactionGroup.getDate().clone();
	    setDateOnView(mCalendar);
        
        getButtonDate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.entry, menu);
		
		if (isInsertMode()) {
		    MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
		}
		
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_multiple_transactions:
            if (mTransactionGroup.getTransactions().size() == 0) {
                Transaction transaction = getInputTransaction();
                mTransactionGroup.getTransactions().add(transaction);
            } else if (isTransactionFieldsVisible()) {
                Transaction transaction = mTransactionGroup.getTransactions().get(0);
                fillTransaction(transaction);
            }
            
            List<Transaction> transactions = mTransactionGroup.getTransactions();
            Intent intent = new Intent(this, TransactionsActivity.class);
            intent.putParcelableArrayListExtra(TransactionsActivity.EXTRA_TRANSACTIONS, (ArrayList<Transaction>) transactions);
            startActivityForResult(intent, 0);
            return true;
            
        case R.id.action_ok:
            onClickOk();
            return true;
            
        case R.id.action_delete:
            onClickDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
	    mCalendar.set(year, month, day);
	    setDateOnView(mCalendar);
    }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            onActivityResultPositive(data);
        }
	}
	
	private void onActivityResultPositive(Intent data) {
        List<Transaction> transactions = data.getParcelableArrayListExtra(TransactionsActivity.EXTRA_TRANSACTIONS);
        
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            transaction.setTransactionGroup(mTransactionGroup);
            transaction.setSequence(i + 1);
        }
        
        mTransactionGroup.setTransactions(transactions);
        
        int visibility = transactions.size() > 1 ? View.GONE : View.VISIBLE;
        setTransactionFieldsVisibility(visibility);
        
        if (transactions.size() == 1) {
            Transaction transaction = transactions.get(0);
            populateTransactionFields(transaction);
        }
	}
    
    private boolean isInsertMode() {
        return mTransactionGroupId == 0;
    }
    
    private boolean isTransactionFieldsVisible() {
        return mTransactionGroup.getTransactions().size() <= 1;
    }
	
	private void setTransactionFieldsVisibility(int visibility) {
	    getSpinnerToAccount().setVisibility(visibility);
        getEditTextAmount().setVisibility(visibility);
        getAutoCompleteTextViewDescription().setVisibility(visibility);
	}
	
	private void populateTransactionFields(Transaction transaction) {
	    Account toAccount = mAccountsMap.get(transaction.getToAccount().getId());
        int position = mToAccountArrayAdapter.getPosition(toAccount);
        getSpinnerToAccount().setSelection(position);
        
        getEditTextAmount().setText(transaction.getAmount().toPlainString());
        getAutoCompleteTextViewDescription().setText(transaction.getDescription());
	}
    
    private void setDateOnView(Calendar calendar) {
        String s = DateHelper.getDateWithDayOfWeekString(calendar);
        getButtonDate().setText(s);
    }
	
	private void showDatePickerDialog(View v) {
        Calendar calendar = mCalendar;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        Bundle args = new Bundle();
        args.putInt(DatePickerFragment.EXTRA_YEAR, year);
        args.putInt(DatePickerFragment.EXTRA_MONTH, month);
        args.putInt(DatePickerFragment.EXTRA_DAY, day);
        
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    
	private void onClickOk() {
	    if (isInsertMode()) {
	        if (isTransactionFieldsVisible() && !isInputTransactionValid()) {
	            return;
	        }
	        
	        insertTransactionGroup();
	        finishWithAction(ACTION_INSERT);
	    } else {
	        if (isTransactionFieldsVisible()) {
	            if (!isInputTransactionValid()) {
	                return;
	            }
	            
                Transaction transaction = mTransactionGroup.getTransactions().get(0);
                fillTransaction(transaction);
            }
	        
	        TransactionGroupHelper.update(this, mTransactionGroup);
	        finishWithAction(ACTION_UPDATE);
	    }
	}
    
    private void insertTransactionGroup() {
        ExpenseCategory expenseCategory = (ExpenseCategory) getSpinnerExpenseCategory().getSelectedItem();
        Account fromAccount = (Account) getSpinnerFromAccount().getSelectedItem();
        
        mTransactionGroup.setDate(mCalendar);
        mTransactionGroup.setExpenseCategory(expenseCategory);
        mTransactionGroup.setFromAccount(fromAccount);
        
        if (mTransactionGroup.getTransactions().size() == 0) {
            Transaction transaction = getInputTransaction();
            mTransactionGroup.getTransactions().add(transaction);
        }
        
        TransactionGroupHelper.insert(this, mTransactionGroup);
    }

    private void onClickDelete() {
        TransactionGroupHelper.deleteById(this, mTransactionGroup.getId());
        finishWithAction(ACTION_DELETE);
    }
    
    private void finishWithAction(int action) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ACTION, action);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean isInputTransactionValid() {
        if (getEditTextAmount().getText().length() == 0) {
            getEditTextAmount().setError(getString(R.string.error_required_amount));
            return false;
        }
        return true;
    }
    
    private Transaction getInputTransaction() {
        Transaction transaction = new Transaction();
        fillTransaction(transaction);
        return transaction;
    }
    
    private void fillTransaction(Transaction transaction) {
        Account toAccount = (Account) getSpinnerToAccount().getSelectedItem();
        transaction.setToAccount(toAccount);

        BigDecimal amount;
        if (getEditTextAmount().getText().length() > 0) {
            amount = new BigDecimal(getEditTextAmount().getText().toString());
        } else {
            amount = BigDecimal.ZERO;
        }
        transaction.setAmount(amount);

        String description = getAutoCompleteTextViewDescription().getText().toString();
        transaction.setDescription(description);
    }
    
    private Button getButtonDate() {
        if (mButtonDate == null) {
            mButtonDate = (Button) findViewById(R.id.buttonDate);
        }
        return mButtonDate;
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
    
    private EditText getEditTextAmount() {
        if (mEditTextAmount == null) {
            mEditTextAmount = (EditText) findViewById(R.id.amount);
        }
        return mEditTextAmount;
    }
    
    private AutoCompleteTextView getAutoCompleteTextViewDescription() {
        if (mAutoCompleteTextViewDescription == null) {
            mAutoCompleteTextViewDescription = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewDescription);
        }
        return mAutoCompleteTextViewDescription;
    }

}
