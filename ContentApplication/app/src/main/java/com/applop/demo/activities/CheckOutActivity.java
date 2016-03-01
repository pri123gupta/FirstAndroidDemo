package com.applop.demo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.helperClasses.DatabaseHelper;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;
import com.applop.demo.model.User;

import org.json.JSONObject;

import java.util.HashMap;

public class CheckOutActivity extends AppCompatActivity {
 //   EditText quantity;
    EditText number;
    EditText address;
    EditText message;
    EditText name;
    TextView totalPriceTV;
    User user;
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
        setContentView(R.layout.activity_check_out);
        totalPriceTV = (TextView) findViewById(R.id.totalPrice);
        totalPriceTV.setText(getIntent().getExtras().getString("totalPrice",""));
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        user = User.getInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Check Out");
        loadResources();
    }

    private void loadResources(){
        number = (EditText)findViewById(R.id.edit_text_phone_no);
        //quantity= (EditText) findViewById(R.id.edit_text_quantity);
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

    public void placeOrderClick(View v){
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
        if (address.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this,"Please enter your address",Toast.LENGTH_LONG).show();
            return;
        }
//      if (quantity.getText().toString().length()==10){
//          Toast.makeText(this,"Please enter 10 digit phone number",Toast.LENGTH_LONG).show();
//          return;
//      }
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
        //params.put("quantity",quantity.getText().toString());
        params.put("packageName", getPackageName());
        params.put("photoLink", user.imageUrl);
        User.setUser(this, user.email, user.name, user.loginType, user.bitmap, user.imageUrl, address.getText().toString(), number.getText().toString());
        new VolleyData(this){
            @Override
            protected void VPreExecute() {

            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                checkOutProcess();
            }

            @Override
            protected void VError(VolleyError error, String tag) {
                Toast.makeText(context,"Error: Please Try Again",Toast.LENGTH_SHORT).show();
            }
        }.getPOSTJsonObject("http://applop.biz/merchant/api/submitUserTable.php", "post_user", params);
        //startActivity(Intent.createChooser(Email, "Send Booking:"));
    }

    public void checkOutProcess(){
        final Context context=this;
        String URL = "http://applop.biz/merchant/api/checkOutItemsFromCart.php";//1
        MyRequestQueue.Instance(this).cancelPendingRequests("checkOutItemsFromCart");
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("userEmail", user.email);
        params.put("packageName", getPackageName());
        final ProgressDialog progressDialog = new ProgressDialog(context);
        new VolleyData(this) {

            @Override
            protected void VPreExecute() {

                progressDialog.setTitle("Checking Out");
                progressDialog.show();

            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                progressDialog.hide();
                try {
                    if (response.getBoolean("status")){
                        Toast.makeText(context,"Order Placed Successfully",Toast.LENGTH_LONG).show();
                        (new DatabaseHelper(context)).removeFromBookmarked();
                        setResult(RESULT_OK);
                        finish();
                    }else {
                        Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
                }

            }
            @Override
            protected void VError(VolleyError error, String tag) {
                progressDialog.hide();
                Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
            }

        }.getPOSTJsonObject(URL, "checkOut", params);
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
                  //  quantity.setText(user.quantity);
                }
            }
        }catch (Exception ex){

        }

    }
}