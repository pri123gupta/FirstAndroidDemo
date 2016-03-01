package com.applop.demo.activities;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.adapters.StoryAdapter;
import com.applop.demo.helperClasses.DatabaseHelper;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.Category;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;
import com.applop.demo.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    RecyclerView cartRecyclerView;
    StoryAdapter storyAdapter;
    ArrayList<Story> stories = new ArrayList<Story>();
    User user;
    TextView totalPriceTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_cart);
        user = User.getInstance(this);
        totalPriceTV = (TextView) findViewById(R.id.totalPrice);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Your Cart");
        Helper.setToolbarColor(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        cartRecyclerView = (RecyclerView)findViewById(R.id.cartList);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //stories = new DatabaseHelper(this).getAllPostsBookmarked(this);
        storyAdapter = new StoryAdapter(stories,this);
        storyAdapter.setIsCart(true);
        cartRecyclerView.setAdapter(storyAdapter);
        storyAdapter.setOnItemClickListener(new StoryAdapter.OnItemClickListener() {
            @Override
            public void OnItemCick(View view, int position) {
                openDetailPage(position);
            }


        });
        if (user.loginType.equalsIgnoreCase("")){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivityForResult(intent, NameConstant.REQUEST_CODE_BACK_FROM_SIGN_IN);
            finish();
        }else{
            loadCart();
        }
    }

    public void loadCart(){
        final Context context=this;
        String URL = "http://applop.biz/merchant/api/getItemsFromCart.php";//1
        MyRequestQueue.Instance(this).cancelPendingRequests("stories");
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("userEmail", user.email);
        params.put("packageName", getPackageName());
        new VolleyData(this) {

            @Override
            protected void VPreExecute() {

                storyAdapter.clear();
                storyAdapter.notifyDataSetChanged();

            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                JSONObject json = response;
                try {
                    if (json != null) {
                        try {
                            JSONArray jsonStories = json.getJSONArray("cartItems");
                            ArrayList<Story> stories = new ArrayList<Story>();;
                            for (int i = 0; i < jsonStories.length(); i++) {
                                Story story = new Story(context, jsonStories.getJSONObject(i));

                                //if (!IsStoryAlreadyAdded(story)){
                                stories.add(story);
                                //}
                            }
                            cartRecyclerView.setAdapter(storyAdapter);
                            cartRecyclerView.setVisibility(View.VISIBLE);
                            storyAdapter.insertStories(stories);
                            setTotalPrice();
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

            }

        }.getPOSTJsonObject(URL, "post_user", params);

    }

    public void checkOutClick(View v){
        if (stories.size()>0) {
            Intent intent = new Intent(this, CheckOutActivity.class);
            intent.putExtra("totalPrice", totalPriceTV.getText().toString());
            startActivityForResult(intent, NameConstant.REQUEST_CODE_ORDER_PLACED);
        }else {
            Toast.makeText(this,"No Item to Check Out",Toast.LENGTH_SHORT).show();
        }
        //checkOutProcess();
    }

    public void openDetailPage(int position){
        /*Intent intent= new Intent(this, StoryDetailActivity.class);
        // intent.putExtra("position", position);
        StoryDetailActivity.position=position;
        StoryDetailActivity.storiesArray=stories;
//            intent.putParcelableArrayListExtra("storydata", stories);
        startActivityForResult(intent, 1);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_open_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void setTotalPrice(){
        int totalPrice = 0;
        for (int i = 0; i < stories.size(); i++) {
            totalPrice = totalPrice+(Integer.parseInt(stories.get(i).price)*stories.get(i).quantity);
        }
        totalPriceTV.setText(AppConfiguration.getInstance(this).currencySymbol+" "+totalPrice);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == NameConstant.REQUEST_CODE_ORDER_PLACED) {
                if (resultCode == RESULT_OK) {
                    finish();
                }
            }
        } catch (Exception ex) {

        }
    }
}
