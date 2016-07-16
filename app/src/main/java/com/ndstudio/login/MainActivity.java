package com.ndstudio.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button signup,login,cancel,forgot;
    private EditText edtUsername,edtPassword;
    DBHandler dbh;
    String username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signup = (Button)findViewById(R.id.signUp);
        login = (Button)findViewById(R.id.login);
        edtUsername = (EditText)findViewById(R.id.name);
        edtPassword = (EditText)findViewById(R.id.pass);
        forgot = (Button)findViewById(R.id.forgot);
        cancel = (Button)findViewById(R.id.cancel);

        dbh = new DBHandler(this);

        signup.setOnClickListener(this);
        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
        forgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(edtUsername.getText().toString().equals(""))
            return;

        username = edtUsername.getText().toString();
        password = edtPassword.getText().toString();

        switch(v.getId())
        {
            case R.id.login :
                    if (password.equals(""))
                    {
                        Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else
                    {
                        boolean isCheck = dbh.checkEntry(username,password);
                        Intent intent = new Intent(MainActivity.this,Profile.class);
                        if(isCheck==true)
                        {
                            intent.putExtra("username",username);
                            startActivity(intent);
                            finish();
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                    }
                    break;
            case R.id.cancel :
                edtPassword.setText("");
                edtUsername.setText("");
                break;
            case R.id.forgot :
                dbh.openReadable();
                String forgPass =  dbh.forgot(username);
                if(forgPass.trim().isEmpty())
                    Toast.makeText(MainActivity.this,"User Not Found", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this,"Password is : "+forgPass, Toast.LENGTH_SHORT).show();
                break;
            case R.id.signUp :
                if(edtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
              long id = dbh.addUser(username,password);
                if(id>0)
                    Toast.makeText(MainActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Username Already Exist", Toast.LENGTH_SHORT).show();
        }

    }
}
