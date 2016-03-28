package com.applop.demo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.adapters.BookingAdapter;
import com.applop.demo.adapters.CheckOutAdapter;
import com.applop.demo.helperClasses.AnalyticsHelper;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.BookedItem;
import com.applop.demo.model.CheckedOutItem;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class YourBookingActivity extends AppCompatActivity {

    User user;
    RecyclerView bookingRecyclerView;
    BookingAdapter checkOutAdapter;
    ArrayList<BookedItem> checkedOutItems = new ArrayList<BookedItem>();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_your_bookings);
        user = User.getInstance(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Your Bookings");
        Helper.setToolbarColor(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        bookingRecyclerView = (RecyclerView)findViewById(R.id.checkOutList);
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkOutAdapter = new BookingAdapter(checkedOutItems,this);
        bookingRecyclerView.setAdapter(checkOutAdapter);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if (user.loginType.equalsIgnoreCase("")){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivityForResult(intent, NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN);
            finish();
        }else{
            loadCheckOutList();
        }
        try {
            String categoryName = "Application";
            String label = "Your Booking";
            String action = "Opened";
            AnalyticsHelper.trackEvent(categoryName, action, label, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCheckOutList(){
        final Context context=this;
        String URL = "http://applop.biz/merchant/api/getAllBookingsByPackageNameAndUserName.php";
        MyRequestQueue.Instance(this).cancelPendingRequests("checkOut");
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("userEmail", user.email);
        params.put("packageName", getPackageName());
        new VolleyData(this) {

            @Override
            protected void VPreExecute() {
                checkOutAdapter.clear();
                checkOutAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                progressBar.setVisibility(View.GONE);
                JSONObject json = response;
                try {
                    if (json != null) {
                        try {
                            JSONArray jsonStories = json.getJSONArray("AllBookings");
                            ArrayList<BookedItem> stories = new ArrayList<BookedItem>();;
                            for (int i = 0; i < jsonStories.length(); i++) {
                                BookedItem story = new BookedItem(context, jsonStories.getJSONObject(i));
                                //if (!IsStoryAlreadyAdded(story)){
                                stories.add(story);
                                //}
                            }
                            bookingRecyclerView.setAdapter(checkOutAdapter);
                            bookingRecyclerView.setVisibility(View.VISIBLE);
                            checkOutAdapter.insertStories(stories);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void VError(VolleyError error, String tag) {
                progressBar.setVisibility(View.GONE);
            }

        }.getPOSTJsonObject(URL, "checkOut", params);

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
}
