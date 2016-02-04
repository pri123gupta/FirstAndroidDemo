package com.applop.demo.activities;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;
import com.applop.demo.model.User;

import org.json.JSONObject;

import java.util.HashMap;

public class OverAllEnquiryMailActivity extends AppCompatActivity {
    EditText number;
    EditText address;
    EditText message;
    EditText name;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_enquiry_mail);
       Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        user = User.getInstance(this);
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
        if (user.loginType.equalsIgnoreCase("")){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivityForResult(intent, NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN);
        }else {
            number.setText(user.phoneNumber);
            name.setText(user.name);
            address.setText(user.address);
        }
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
        if (number.getText().toString().length()!=10){
            Toast.makeText(this,"Please enter 10 digit phone number",Toast.LENGTH_LONG).show();
            return;
        }
        if (message.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter your message",Toast.LENGTH_LONG).show();
            return;
        }
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{AppConfiguration.getInstance(this).email});
        Email.putExtra(Intent.EXTRA_SUBJECT, "General Enquiry");
        Email.putExtra(Intent.EXTRA_TEXT, "Name : "+name.getText().toString()
                +"\n\nAddress : "+address.getText().toString()
                +"\n\nPhone no. : "+number.getText().toString()
                +"\n\nMessage : "+message.getText().toString());
        //startActivity(Intent.createChooser(Email, "Send Enquiry:"));
        final HashMap<String, String> params = new HashMap<String, String>();
        if (user.loginType.equalsIgnoreCase("")){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivityForResult(intent, NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN);
            return;
        }
        params.put("email", user.email);
        params.put("name", user.name);
        params.put("address", address.getText().toString());
        params.put("phoneNumber", number.getText().toString());
        params.put("packageName", getPackageName());
        params.put("photoLink", user.imageUrl);
        User.setUser(this, user.email, user.name, user.loginType, user.bitmap, user.imageUrl, user.address, user.phoneNumber);
        new VolleyData(this){
            @Override
            protected void VPreExecute() {

            }

            @Override
            protected void VResponse(JSONObject response, String tag) {

            }

            @Override
            protected void VError(VolleyError error, String tag) {

            }
        }.getPOSTJsonObject("http://applop.biz/merchant/api/submitUserTable.php", "post_user", params);
        final HashMap<String, String> paramsBooking = new HashMap<String, String>();
        paramsBooking.put("userEmail", user.email);
        paramsBooking.put("packageName", getPackageName());
        paramsBooking.put("msg",message.getText().toString());
        new VolleyData(this){
            @Override
            protected void VPreExecute() {

            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                try {
                    if (response.getBoolean("status")){
                        Toast.makeText(OverAllEnquiryMailActivity.this,"Booked Successfully",Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }else {
                        Toast.makeText(OverAllEnquiryMailActivity.this,"Error : Please try again",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(OverAllEnquiryMailActivity.this,"Error : Please try again",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected void VError(VolleyError error, String tag) {
                Toast.makeText(OverAllEnquiryMailActivity.this,"Error : Please try again",Toast.LENGTH_LONG).show();
            }
        }.getPOSTJsonObject("http://applop.biz/merchant/api/submitBooking.php", "post_user", paramsBooking);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode==NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN){
                if (resultCode==RESULT_OK){
                    user = User.getInstance(this);
                    name.setText(user.name);
                    address.setText(user.address);
                    number.setText(user.phoneNumber);
                }
            }
        }catch (Exception ex){

        }

    }
}
