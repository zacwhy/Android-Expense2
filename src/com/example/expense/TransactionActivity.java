package com.example.expense;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.expense.data.AccountHelper;
import com.example.expense.data.ExpenseDatabaseHelper;
import com.example.expense.models.Account;

public class TransactionActivity extends Activity {

    public static final String EXTRA_ACTION = "com.example.expense.EXTRA_ACTION";
    public static final String EXTRA_TO_ACCOUNT_ID = "com.example.expense.TO_ACCOUNT_ID";
    public static final String EXTRA_AMOUNT = "com.example.expense.AMOUNT";
    public static final String EXTRA_DESCRIPTION = "com.example.expense.DESCRIPTION";
    
    public static final int ACTION_INSERT = 1;
    public static final int ACTION_UPDATE = 2;
    public static final int ACTION_DELETE = 3;

    private int mAction;
    
    private Spinner mSpinnerToAccount;
    private EditText mEditTextAmount;
    private AutoCompleteTextView mAutoCompleteTextViewDescription;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        
        Intent intent = getIntent();
        mAction = intent.getIntExtra(EXTRA_ACTION, 0);
        
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Account> accountsList = AccountHelper.getList(db);
        databaseHelper.close();
        
        ArrayAdapter<Account> arrayAdapter = new ArrayAdapter<Account>(this, android.R.layout.simple_spinner_item, accountsList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        getSpinnerToAccount().setAdapter(arrayAdapter);
        
        if (mAction == ACTION_UPDATE) {
            long toAccountId = intent.getLongExtra(EXTRA_TO_ACCOUNT_ID, 0);
            Map<Long, Account> accountsMap = AccountHelper.convertListToMap(accountsList);
            Account toAccount = accountsMap.get(toAccountId);
            int position = arrayAdapter.getPosition(toAccount);
            getSpinnerToAccount().setSelection(position);
            
            BigDecimal amount = (BigDecimal) intent.getSerializableExtra(EXTRA_AMOUNT);
            getEditTextAmount().setText(amount.toPlainString());
            
            String description = intent.getStringExtra(EXTRA_DESCRIPTION);
            getAutoCompleteTextViewDescription().setText(description);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.transaction, menu);
        
        if (mAction == ACTION_INSERT) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_ok:
            Account toAccount = (Account) getSpinnerToAccount().getSelectedItem();
            BigDecimal amount = new BigDecimal(getEditTextAmount().getText().toString());
            String description = getAutoCompleteTextViewDescription().getText().toString();
            
            Intent intent = new Intent();
            intent.putExtra(EXTRA_ACTION, mAction);
            intent.putExtra(EXTRA_TO_ACCOUNT_ID, toAccount.getId());
            intent.putExtra(EXTRA_AMOUNT, amount);
            intent.putExtra(EXTRA_DESCRIPTION, description);
            
            setResult(RESULT_OK, intent);
            finish();
            return true;
            
        case R.id.action_delete:
            Intent intent2 = new Intent();
            intent2.putExtra(EXTRA_ACTION, ACTION_DELETE);
            setResult(RESULT_OK, intent2);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private Spinner getSpinnerToAccount() {
        if (mSpinnerToAccount == null) {
            mSpinnerToAccount = (Spinner) findViewById(R.id.spinnerToAccount);
        }
        return mSpinnerToAccount;
    }
    
    private EditText getEditTextAmount() {
        if (mEditTextAmount == null) {
            mEditTextAmount = (EditText) findViewById(R.id.editTextAmount);
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
