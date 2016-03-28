package com.applop.demo.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.applop.demo.helperClasses.AnalyticsHelper;
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
    String[] paymentTypes = new String[2];
    int totalPrice;
    int selectedPaymentIndex=0;
    TextView paymentMethodTV;

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
        paymentMethodTV = (TextView) findViewById(R.id.paymentMethod_tv);
        if (AppConfiguration.getInstance(this).isPaymentEnable&&!AppConfiguration.getInstance(this).payUSaltKey.equalsIgnoreCase("")&&!AppConfiguration.getInstance(this).payUMerchantKey.equalsIgnoreCase(""))
        {
            paymentTypes[0] = "PayU Payment";
            paymentTypes[1] = "Cash On Delivery";
        }else {
            paymentTypes[0] = "Cash On Delivery";
        }
        paymentMethodTV.setText("Payment : " + paymentTypes[0] + " (Click to change)");

        totalPriceTV = (TextView) findViewById(R.id.totalPrice);

        totalPrice = getIntent().getExtras().getInt("totalPrice",0);
        totalPriceTV.setText(AppConfiguration.getInstance(this).currencySymbol+" "+totalPrice);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        user = User.getInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Check Out");
        loadResources();
    }

    public void showCountrySelection()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Country");
        builder.setSingleChoiceItems(paymentTypes, selectedPaymentIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                selectedPaymentIndex = which;
                paymentMethodTV.setText("Payment : " + paymentTypes[selectedPaymentIndex]+" (Click to change)");
                dialog.cancel();
            }
        });
        builder.show();
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
        try {
            String categoryName = "Cart";
            String label = "Checkout";
            String action = "Order Placed";
            AnalyticsHelper.trackEvent(categoryName, action, label, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        User.setUser(this, user.email, user.name, user.loginType, user.bitmap, user.imageUrl, address.getText().toString(), number.getText().toString(),"","");
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

    public void paymentChangeClick(View v){
        showCountrySelection();
    }

    public void checkOutProcess(){
        if (paymentTypes[selectedPaymentIndex].equalsIgnoreCase("PayU Payment")){
            String url = "http://applop.biz/merchant/api/getNextCheckOutId.php";
            new VolleyData(this){
                @Override
                protected void VPreExecute() {

                }

                @Override
                protected void VResponse(JSONObject response, String tag) {
                    try {
                        if (!response.getString("id").equalsIgnoreCase("")) {
                            Intent intent = new Intent(context, PayUMoneyActivity.class);
                            intent.putExtra("name", name.getText().toString());
                            intent.putExtra("email", user.email);
                            intent.putExtra("amount", totalPrice);
                            intent.putExtra("phone", number.getText().toString());
                            intent.putExtra("id", Integer.parseInt(response.getString("id")));
                            startActivityForResult(intent, NameConstant.REQUEST_PAYMENT);
                        }
                    }catch (Exception ex){

                    }

                }

                @Override
                protected void VError(VolleyError error, String tag) {
                    Toast.makeText(context,"Error: Please Try Again",Toast.LENGTH_SHORT).show();
                }
            }.getJsonObject(url, false, "id", this);
        }else {
            final Context context=this;
            String URL = "http://applop.biz/merchant/api/checkOutItemsFromCart.php";//1
            MyRequestQueue.Instance(this).cancelPendingRequests("checkOutItemsFromCart");
            final HashMap<String, String> params = new HashMap<String, String>();
            params.put("userEmail", User.getInstance(this).email);
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
                            try {
                                String categoryName = "Cart";
                                String label = "Checkout";
                                String action = "Order Placed";
                                AnalyticsHelper.trackEvent(categoryName, action, label, CheckOutActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
            if (requestCode==NameConstant.REQUEST_PAYMENT){
                if (resultCode==RESULT_OK){
                    user = User.getInstance(this);
                    name.setText(user.name);
                    address.setText(user.address);
                    number.setText(user.phoneNumber);
                    Toast.makeText(context,"Order Placed Successfully",Toast.LENGTH_LONG).show();
                    (new DatabaseHelper(context)).removeFromBookmarked();
                    try {
                        String categoryName = "Cart";
                        String label = "Checkout";
                        String action = "Order Placed";
                        AnalyticsHelper.trackEvent(categoryName, action, label, CheckOutActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setResult(RESULT_OK);
                    Intent intent = new Intent(context,YourOrderActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(context,"Please try again",Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception ex){

        }

    }
}
