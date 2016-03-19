package com.applop.demo.activities;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.helperClasses.AnalyticsHelper;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;
import com.applop.demo.model.User;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class OverAllEnquiryMailActivity extends AppCompatActivity {
    EditText number;
    EditText address;
    EditText message;
    EditText name;
    ImageView browseImage;
    Bitmap bitmap;
    String ext;
    Context context;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_overall_enquiry_mail);
        context = this;
       Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        user = User.getInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Enquiry");
        if (getPackageName().equalsIgnoreCase("com.applop")){
            getSupportActionBar().setTitle("Make App Now");
        }
        try {
            String categoryName = "Application";
            String label = "General Enquiry";
            String action = "Enquired";
            AnalyticsHelper.trackEvent(categoryName, action, label, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadResources();
    }

    public void chooseImage(View v) {
        /*Intent intent = new Intent();
        intent.setType("image");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), NameConstant.PICK_IMAGE_REQUEST);
        /*
        // Create intent to Open Image applications like Gallery, Google Photos
         */
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, NameConstant.PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, 0);
        return encodedImage;
    }

    private void loadResources(){
        number = (EditText)findViewById(R.id.edit_text_phone_no);
        address = (EditText)findViewById(R.id.edit_text_Address);
        message = (EditText) findViewById(R.id.edit_enquiry_message);
        name = (EditText) findViewById(R.id.edit_text_name);
        browseImage = (ImageView) findViewById(R.id.browseImage);
        LinearLayout imageLayout = (LinearLayout) findViewById(R.id.imageLayout);
        if (AppConfiguration.getInstance(context).isImageEnableInGeneralEnquiry){
            imageLayout.setVisibility(View.VISIBLE);
        }else {
            imageLayout.setVisibility(View.GONE);
        }
        if (user.loginType.equalsIgnoreCase("")){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivityForResult(intent, NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN);
        }else {
            number.setText(user.phoneNumber);
            name.setText(user.name);
            if (!getPackageName().equalsIgnoreCase("com.applop")) {
                address.setText(user.address);
            }
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
        if (!getPackageName().equalsIgnoreCase("com.applop")) {
            if (number.getText().toString().length()!=10){
                Toast.makeText(this,"Please enter 10 digit phone number",Toast.LENGTH_LONG).show();
                return;
            }
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
        //startActivity(Intent.createChooser(Email, "Send enquiry_icon:"));
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
        User.setUser(this, user.email, user.name, user.loginType, user.bitmap, user.imageUrl, address.getText().toString(), number.getText().toString());
        final ProgressDialog progressDialog = new ProgressDialog(context);
        new VolleyData(this){
            @Override
            protected void VPreExecute() {
                progressDialog.setTitle("Sending");
                progressDialog.show();
            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                final HashMap<String, String> paramsBooking = new HashMap<String, String>();
                paramsBooking.put("userEmail", user.email);
                paramsBooking.put("packageName", getPackageName());
                if (bitmap!=null) {
                    paramsBooking.put("image", getStringImage(bitmap));
                    paramsBooking.put("ext", ext);
                }
                paramsBooking.put("msg",message.getText().toString());
                new VolleyData(context){
                    @Override
                    protected void VPreExecute() {

                    }

                    @Override
                    protected void VResponse(JSONObject response, String tag) {
                        progressDialog.hide();
                        try {
                            if (response.getBoolean("status")){
                                if (getPackageName().equalsIgnoreCase("com.applop")){
                                    Toast.makeText(OverAllEnquiryMailActivity.this, "Successfully Created Will contact you soon", Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(OverAllEnquiryMailActivity.this, "Enquired Successfully", Toast.LENGTH_LONG).show();
                                }
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
                        progressDialog.hide();
                        Toast.makeText(OverAllEnquiryMailActivity.this,"Error : Please try again",Toast.LENGTH_LONG).show();
                    }
                }.getPOSTJsonObject("http://applop.biz/merchant/api/submitGeneralEnquiry.php", "GeneralEnquiry", paramsBooking);
            }

            @Override
            protected void VError(VolleyError error, String tag)
            {
                progressDialog.hide();
            }
        }.getPOSTJsonObject("http://applop.biz/merchant/api/submitUserTable.php", "post_user", params);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode==NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN){
                if (resultCode==RESULT_OK){
                    user = User.getInstance(this);
                    name.setText(user.name);
                    if (!getPackageName().equalsIgnoreCase("com.applop")) {
                        address.setText(user.address);
                    }
                    number.setText(user.phoneNumber);
                }
            }else {
                if (requestCode == NameConstant.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    String fileNameSegments[] = picturePath.split("/");
                    String fileName = fileNameSegments[fileNameSegments.length - 1];
                    ext = fileName.substring(fileName.lastIndexOf(".") + 1);
                    //Toast.makeText(context,ext,Toast.LENGTH_LONG).show();
                    //bitmap = BitmapFactory.decodeFile(picturePath);
                    //Getting the Bitmap from Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                    browseImage.setImageBitmap(bitmap);
                }
            }
        }catch (Exception ex){

        }

    }
}
