package com.applop.demo.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.helperClasses.AnalyticsHelper;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;
import com.applop.demo.model.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MakeAppActivity extends AppCompatActivity {
    //   EditText quantity;
    EditText number;
    EditText email;
    TextView country;
    EditText city;
    EditText name;
    User user;
    static public Story item;
    Context context;
    String[] countries = new String[9];
    public int selectedCountryIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        countries[0]="india";
        countries[1]="singapore";
        countries[2]="china";
        countries[3]="japan";
        countries[4]="indonesia";
        countries[5]="sri lanka";
        countries[6]="nepal";
        countries[7]="thailand";
        countries[8]="united states";
        context = this;
        setContentView(R.layout.activity_make_app);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        user = User.getInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Make App Now");
        SharedPreferences prefs = getSharedPreferences(NameConstant.APP_DATA_USER, MODE_PRIVATE);
        String account_pin = prefs.getString("account_pin","");
        if (!account_pin.equalsIgnoreCase("")){
            Intent intent = new Intent(this,ApplopDashBoard.class);
            startActivity(intent);
            finish();
        }
        loadResources();
    }

    public void setCountry(View v){
        showCountrySelection();
    }

    public void showCountrySelection()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Country");
        builder.setSingleChoiceItems(countries, selectedCountryIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                selectedCountryIndex = which;
                country.setText(countries[selectedCountryIndex]+" (Click to change)");
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void loadResources(){
        number = (EditText)findViewById(R.id.edit_text_phone_no);
        //quantity= (EditText) findViewById(R.id.edit_text_quantity);
        email = (EditText)findViewById(R.id.edit_text_email);
        country = (TextView) findViewById(R.id.edit_enquiry_country);
        city = (EditText) findViewById(R.id.edit_enquiry_city);
        name = (EditText) findViewById(R.id.edit_text_name);
        if (user.loginType.equalsIgnoreCase("")){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivityForResult(intent, NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN);
        }else {
            number.setText(user.phoneNumber);
            name.setText(user.name);
            city.setText(user.city);
            email.setText(user.email);
            //address.setText(user.address);

        }
        country.setText(countries[selectedCountryIndex]+" (Click to change)");
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

        if (email.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter your e-mail",Toast.LENGTH_LONG).show();
            return;
        }

        if (city.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter your city",Toast.LENGTH_LONG).show();
            return;
        }
        String url = getURLForRegistration();
        final ProgressDialog progressDialog = new ProgressDialog(context);
        MyRequestQueue.Instance(this).cancelPendingRequests("registration");
        new VolleyData(this) {
            @Override
            protected void VPreExecute() {
                progressDialog.setTitle("Registering");
                progressDialog.show();
            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                progressDialog.hide();
                try {
                    response = response.getJSONObject("response");
                    if (response.getString("status").equalsIgnoreCase("sucess")){
                        SharedPreferences.Editor editor = context.getSharedPreferences(NameConstant.APP_DATA_USER,context.MODE_PRIVATE).edit();
                        editor.putString("number", number.getText().toString());
                        editor.putString("account_pin", response.getString("accountid"));
                        editor.commit();
                        Intent intent = new Intent(context,EnterOTPActivity.class);
                        startActivity(intent);
                        Toast.makeText(context,"success",Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        Toast.makeText(context,"failure : "+response.getString("status"),Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(context,"failure",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void VError(VolleyError error, String tag) {
                progressDialog.hide();
                Toast.makeText(context,"Error Please Try Again",Toast.LENGTH_LONG).show();
            }
        }.getJsonObject(getURLForRegistration(),false,"registration",this);
    }

    private String getURLForRegistration(){
        String tempName = name.getText().toString().replace(" ","");
        return NameConstant.API_FOR_MAKE_APP_NOW+"country="+countries[selectedCountryIndex]+"&Name="+tempName+"&email="+email.getText().toString()+"&mobile="+number.getText().toString()+"&city="+city.getText().toString()+"&api_key=abc";
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode==NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN){
                if (resultCode==RESULT_OK){
                    user = User.getInstance(this);
                    name.setText(user.name);
                    number.setText(user.phoneNumber);
                    city.setText(user.city);
                    email.setText(user.email);
                    //  quantity.setText(user.quantity);
                }
            }
        }catch (Exception ex){

        }

    }
}
