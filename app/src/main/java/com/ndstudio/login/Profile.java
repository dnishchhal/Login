package com.ndstudio.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Nishchhal on 18-Jun-16.
 */
public class Profile extends AppCompatActivity implements View.OnClickListener{

    String username;
    int Bal;

    Button credit,debit,transfer,delete,logout;
    TextView edtHeading,edtbalance;
    DBHandler db;
    Intent intentMain;
    AlertDialog.Builder builder;
    EditText edtAmount,edtName;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        credit = (Button)findViewById(R.id.credit);
        debit = (Button)findViewById(R.id.debit);
        transfer = (Button)findViewById(R.id.transfer);
        delete = (Button)findViewById(R.id.delete);
        logout = (Button)findViewById(R.id.logout);
        edtbalance = (TextView) findViewById(R.id.balance);

        db = new DBHandler(this);
        Bal = db.bal(username);
        edtHeading = (TextView) findViewById(R.id.heading);
        edtHeading.setText("Welcome, "+username);
        edtbalance.setText("Current Balance : "+Bal+" ₹");

        logout.setOnClickListener(this);
        delete.setOnClickListener(this);
        credit.setOnClickListener(this);
        debit.setOnClickListener(this);
        transfer.setOnClickListener(this);

       intentMain = new Intent(Profile.this,MainActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.logout :
                Toast.makeText(Profile.this, "Bye Bye "+username, Toast.LENGTH_SHORT).show();
                startActivity(intentMain);
                finish();
                break;
            case R.id.delete : pDelete();
                    break;
            case R.id.credit : pCredit();
                    break;
            case R.id.debit : pDebit();
                    break;
            case R.id.transfer : pTransfer();
                break;
        }
    }

    public void pCredit()
    {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Credit");
        edtAmount = new EditText(this);
        builder.setMessage("Enter Amount To Credit");
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edtAmount);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtAmount.getText().toString().isEmpty())
                    return;
                int amt = Integer.parseInt(edtAmount.getText().toString());
                Bal = Bal + amt;
                db.accUpdate(username,amt);
                Toast.makeText(Profile.this, "Amount Credited Successful", Toast.LENGTH_SHORT).show();
                edtbalance.setText("Current Balance : "+Bal+" ₹");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }

    public void pDebit()
    {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Debit");
        edtAmount = new EditText(this);
        builder.setMessage("Enter Amount To Debit");
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edtAmount);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtAmount.getText().toString().isEmpty())
                    return;
                int amt = Integer.parseInt(edtAmount.getText().toString());

                if(amt>Bal){
                    Toast.makeText(Profile.this, "Insufficient Amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bal = Bal - amt;
                db.accUpdate(username,-amt);
                Toast.makeText(Profile.this, "Amount Debited Successful", Toast.LENGTH_SHORT).show();
                edtbalance.setText("Current Balance : "+Bal+" ₹");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();

    }

    public void pDelete()
    {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation !!");
        builder.setMessage("Are You Sure You Want To DELETE The Account, "+username);
        builder.setCancelable(false);
        builder.setPositiveButton("Fuck , yeah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteAcc(username);
                Toast.makeText(Profile.this, "Account Deleted Successful", Toast.LENGTH_SHORT).show();
                startActivity(intentMain);
                finish();
            }
        });
        builder.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }

    public void pTransfer()
    {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Transfer");
        edtAmount = new EditText(this);
        edtName = new EditText(this);
        edtName.setInputType(InputType.TYPE_CLASS_TEXT);
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtAmount.setHint("Enter Amount To Transfer");
        edtName.setHint("Enter Username To Transfer");
        builder.setView(edtAmount);
        builder.setCancelable(false);
        LinearLayout lila = new LinearLayout(this);
        lila.setOrientation(LinearLayout.VERTICAL);
        lila.addView(edtName);
        lila.addView(edtAmount);
        builder.setView(lila);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = edtName.getText().toString();

                if (name.isEmpty())
                    return;
                if (edtAmount.getText().toString().isEmpty())
                    return;
                int amt = Integer.parseInt(edtAmount.getText().toString());
                db.openReadable();
                String forgPass =  db.forgot(name);
                if(forgPass.trim().isEmpty()) {
                    Toast.makeText(Profile.this,name+" Not Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(amt>Bal){
                    Toast.makeText(Profile.this, "Insufficient Amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bal = Bal - amt;
                db.accUpdate(username,-amt);
                db.accUpdate(name,amt);
                Toast.makeText(Profile.this, "Balance Transfer Successful to "+name, Toast.LENGTH_SHORT).show();
                edtbalance.setText("Current Balance : "+Bal+" ₹");
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }

}
