package com.applop.demo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.applop.demo.R;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.Story;

public class EnquiryMailActivity extends AppCompatActivity {

    EditText number;
    EditText address;
    EditText message;
    EditText name;
    static public Story item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_mail);
       Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Enquiry");
        loadResources();
    }

    private void loadResources(){
        number = (EditText)findViewById(R.id.edit_text_phone_no);
        address = (EditText)findViewById(R.id.edit_text_Address);
        message = (EditText) findViewById(R.id.edit_enquiry_message);
        name = (EditText) findViewById(R.id.edit_text_name);
        //item = getIntent().getExtras().getParcelable("item");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.enquiry, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMail(View v){
        if (name.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter your name",Toast.LENGTH_LONG).show();
            return;
        }
        if (number.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter your phone number",Toast.LENGTH_LONG).show();
            return;
        }
        if (message.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter your address",Toast.LENGTH_LONG).show();
            return;
        }
        if (item==null){
            Toast.makeText(this,"Error Please Try Again",Toast.LENGTH_LONG).show();
            return;
        }
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{AppConfiguration.getInstance(this).email});
        Email.putExtra(Intent.EXTRA_SUBJECT, "Enquiry For : "+ item.title );
        Email.putExtra(Intent.EXTRA_TEXT, "Name : "+name.getText().toString()
                +"\n\nAddress : "+address.getText().toString()
                +"\n\nMessage : "+message.getText().toString());
        startActivity(Intent.createChooser(Email, "Send Enquiry:"));
    }
}
