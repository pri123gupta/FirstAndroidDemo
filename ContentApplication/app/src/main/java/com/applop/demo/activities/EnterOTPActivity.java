package com.applop.demo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;
import com.applop.demo.model.User;

import org.json.JSONObject;

public class EnterOTPActivity extends AppCompatActivity {
    //   EditText quantity;
    String number;
    EditText name;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        context = this;
        setContentView(R.layout.activity_enter_otp);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        SharedPreferences prefs = getSharedPreferences(NameConstant.APP_DATA_USER, MODE_PRIVATE);
        number = prefs.getString("number","");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Verify Mobile");
        loadResources();
    }

    private void loadResources(){
        name = (EditText) findViewById(R.id.edit_text_name);
        /*if (user.loginType.equalsIgnoreCase("")){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivityForResult(intent, NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN);
        }else {
            number.setText(user.phoneNumber);
            name.setText(user.name);
            //address.setText(user.address);

        }*/
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
            Toast.makeText(this,"Please enter OTP",Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context,"success",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context,ApplopDashBoard.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(context,"failure",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(context,"failure",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context,ApplopDashBoard.class);
                    startActivity(intent);
                }
            }

            @Override
            protected void VError(VolleyError error, String tag) {
                progressDialog.hide();
                Toast.makeText(context,"failure",Toast.LENGTH_LONG).show();
            }
        }.getJsonObject(getURLForRegistration(),false,"registration",this);
    }

    private String getURLForRegistration(){
        return NameConstant.API_FOR_OTP_VERIFICATION+"api_key=abc&mobile="+number+"&otp="+name.getText().toString();//+"&email="+email.getText().toString()+"&mobile="+number.getText().toString()+"&city="+city.getText().toString()+"&api_key=abc";
    }
}
