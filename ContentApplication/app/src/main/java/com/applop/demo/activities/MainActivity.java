package com.applop.demo.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.adapters.DrawerMenuAdapter;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.Category;
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;

import com.applop.demo.R;
import com.applop.demo.adapters.StoryAdapter;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
Toolbar toolbar;
    Button btnShare,btnEnquiry;
    String  appname="Demo App";
TextView myTitle;
    SwipeRefreshLayout swipeRefreshLayout;
    StoryAdapter storyAdapter;
    ProgressBar progressBar;
    String url2="http://healthxp.net/expert-tutor/api/get_stories.php?";
    String  urlHOme="http://healthxp.net/expert-tutor/api/get_stories.php?category=all&&page=1";
    Story storyCategory;
    RelativeLayout mDrawerRelativelayout;
    Context context;
    Category category;
    CardView cardview;
    private android.os.Handler mHandler = new android.os.Handler();
    static  int position;
    DrawerLayout mDrawerLayout;
    RelativeLayout mDrawerRealativeLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ArrayList<Category> jsoncategories= new ArrayList<Category>();
   static ArrayList<Story> stories=new ArrayList<Story>();
    DrawerMenuAdapter drawer1MenuAdapter;
    Category currentCategory;

    //recyclerview

    RecyclerView itemsRecyclerView;
    RecyclerView mDrawerList;


    private boolean isLoadingMoreData = false;
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int current_page = 1;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_drawer);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        storyAdapter=new StoryAdapter(stories,this);
        loadResources();
        setSideDrawer();
        return;
    }
    private void loadResources() {

        myTitle= (TextView) findViewById(R.id.mytitle);
        cardview= (CardView) findViewById(R.id.cardview);
        progressBar= (ProgressBar) findViewById(R.id.progress_bar);
        storyAdapter=new StoryAdapter(stories,this);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                //refreshItems();
                loadCategory(currentCategory, 1, true, false);
                //loadCategory(currentCategory, 1, true);
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer1MenuAdapter = new DrawerMenuAdapter(jsoncategories, context);
        mDrawerRelativelayout = (RelativeLayout) findViewById(R.id.relative_left_drawer);
       // NetworkHelper.setDetailsInDrawerlayout(mDrawerRelativelayout, this);
        mDrawerRealativeLayout = (RelativeLayout) findViewById(R.id.relative_left_drawer);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer_list);
        mDrawerList.setLayoutManager(new LinearLayoutManager(context));
        mDrawerList.setAdapter(drawer1MenuAdapter);
        ((RelativeLayout)findViewById(R.id.upperBar)).setBackgroundColor(Color.parseColor(AppConfiguration.getInstance(this).bgcolor));
        //for second drawer with id itemsRecyclerView
        itemsRecyclerView = (RecyclerView) findViewById(R.id.itemsRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsRecyclerView.setAdapter(storyAdapter);
        itemsRecyclerView.setVisibility(View.VISIBLE);
        itemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();

                totalItemCount = linearLayoutManager.getItemCount();
                //firstVisibleItem =
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();


                /*if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }*/
                if (!isLoadingMoreData) {
                    if ((totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + 1) && totalItemCount > 0) {
                        // End has been reached

                        // Do something
                        startLoadMore();
                        current_page++;
                        loadCategory(currentCategory, current_page, false, true);
                        //loading = true;
                    }
                }
            }
        });
        drawer1MenuAdapter.setmyOnItemClickListener(new DrawerMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DrawerMenuAdapter.ViewHolder viewHolder = (DrawerMenuAdapter.ViewHolder) view.getTag();
                category = (Category) viewHolder.itemName.getTag();
                mDrawerLayout.closeDrawer(mDrawerRelativelayout);
                menuItemClicked(category);
            }
        });
        storyAdapter.setOnItemClickListener(new StoryAdapter.OnItemClickListener() {
            @Override
            public void OnItemCick(View view, int position) {
                openDetailPage(position);
            }


        });
    }

    private void startLoadMore() {
        isLoadingMoreData = true;
        if (storyAdapter!=null)
            storyAdapter.insertMoreLoading();
    }

    private void endLoadMore() {
        isLoadingMoreData = false;
        if (storyAdapter!=null)
            storyAdapter.removeMoreLoading();
    }

    public void openDetailPage(int position){
        Intent intent= new Intent(context, StoryDetailActivity.class);
       // intent.putExtra("position", position);
        StoryDetailActivity.position=position;
        StoryDetailActivity.storiesArray=stories;
//            intent.putParcelableArrayListExtra("storydata", stories);
        if (currentCategory!=null){
            intent.putExtra("category", currentCategory);
        }
            startActivityForResult(intent, 1);
    }
    public void menuItemClicked(Category category){
            if (category.name.equalsIgnoreCase("enquiry")){
                Intent intent = new Intent(this,OverAllEnquiryMailActivity.class);
                startActivity(intent);
                return;
            }
            loadCategory(category, 1, false, false);
            return;

    }
    public  void setSideDrawer(){
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
//       getActionBar().setLogo(R.drawable.setting);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadMenu();
    }

    //for creating setting/hamburger icon on toolbar's left side
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void loadMenu(){
        String url = NameConstant.BASE_API_URL_V1+"getCategoriesByApiKey.php?APIKey="+ AppConfiguration.getInstance(context).websiteKey;
        MyRequestQueue.Instance(this).cancelPendingRequests("menu");
        new VolleyData(this) {
            @Override
            protected void VPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                itemsRecyclerView.setVisibility(View.GONE);
            }
            @Override
            protected void VResponse(JSONObject response, String tag) {
            JSONObject json=response;
                progressBar.setVisibility(View.GONE);
                itemsRecyclerView.setVisibility(View.VISIBLE);
                try{
                    if(json!= null){
                        try{
                            JSONArray jsonCategories=json.getJSONArray("categories");
                            drawer1MenuAdapter.clear();
                            ArrayList<Category> menuCategories=new ArrayList<Category>();
                            menuCategories.add(new Category("", "Enquiry", "enquiry"));
                            menuCategories.add(new Category("", "Home", "Home"));
                            for (int i=0;i<jsonCategories.length();i++){
                                menuCategories.add(new Category(jsonCategories.getJSONObject(i)));
                            }
                            //for getting story on home page.........only first line
                            loadCategory(menuCategories.get(1),1,false,false);
                            drawer1MenuAdapter.insertCategories(menuCategories);
                        }
                        catch (Exception e){
                        }
                    }
                }
                catch (Exception e){
                }
            }
            @Override
            protected void VError(VolleyError error, String tag) {
                progressBar.setVisibility(View.GONE);
                itemsRecyclerView.setVisibility(View.VISIBLE);
            }
        }.getJsonObject(url, true, "menu",this);;
    }
    //for loading page on click of button in navigation drawer list
    public void loadCategory(Category category, final int page,boolean isRefreshing,boolean isLoadingMore){
        if (category==null){
            itemsRecyclerView.setVisibility(View.INVISIBLE);
            return;
        }
        myTitle.setText(category.name);
        currentCategory = category;
        final Context context=this;
        String URL = getCategoryStoryURL(category,1);//1
        MyRequestQueue.Instance(this).cancelPendingRequests("stories");
        new VolleyData(this) {

            @Override
            protected void VPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                itemsRecyclerView.setVisibility(View.GONE);
                storyAdapter.clear();
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                swipeRefreshLayout.setRefreshing(false);
                endLoadMore();
                JSONObject json=response;
            try{
               if (json!= null) {
                   try {
                       progressBar.setVisibility(View.GONE);
                       JSONArray jsonStories=json.getJSONArray("stories");

                       ArrayList<Story> stories=new ArrayList<Story>();
                       for (int i=0;i<jsonStories.length();i++){
                            Story story=new Story(context,jsonStories.getJSONObject(i));
                           //if (!IsStoryAlreadyAdded(story)){
                               stories.add(story);
                           //}
                       }
                       itemsRecyclerView.setAdapter(storyAdapter);
                       itemsRecyclerView.setVisibility(View.VISIBLE);
                       storyAdapter.insertStories(stories);
                   }catch (Exception e){
                    Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
                   }
               }
            }catch(Exception e){
                Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
            }
            }

            @Override
            protected void VError(VolleyError error, String tag) {
                swipeRefreshLayout.setRefreshing(false);
                endLoadMore();
                progressBar.setVisibility(View.GONE);
                itemsRecyclerView.setVisibility(View.VISIBLE);
            }

        }.getJsonObject(URL, true, "stories",this);
    }
    private String getCategoryStoryURL(Category category, int page) {
        if (category.name.equalsIgnoreCase("home")){
            return NameConstant.BASE_API_URL_V1 + "getStoriesByCategory.php?categoryId=all&APIKey="+AppConfiguration.getInstance(context).websiteKey+"&page="+page;
        }
        return  NameConstant.BASE_API_URL_V1 + "getStoriesByCategory.php?categoryId="+category.categoryId+"&APIKey="+AppConfiguration.getInstance(context).websiteKey+"&page="+page;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    public static interface MyClickListener{
        public  void onClick(View view,int position);
    }
    public boolean IsStoryAlreadyAdded(Story story){
    for (int i=0;i<stories.size();i++){
        if (story.postId.equalsIgnoreCase(stories.get(i).postId))
        return true;
    }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_aboutUs:
                Intent aboutUs=new Intent(this,AboutUsActivity.class);
                startActivity(aboutUs);
                break;
            case R.id.action_contactUs:
                Intent contactUs=new Intent(this,ContactUsActivity.class);
                startActivity(contactUs);
                break;
            case R.id.action_feedback:
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"narayan.r@applop.com"});
                Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback For "+ getResources().getString(R.string.app_name)+" android application");
                Helper.MobileInfo mobileInfo = new Helper.MobileInfo(context);
                Email.putExtra(Intent.EXTRA_TEXT, "\tDevice Info :\n\tModel Name : " + mobileInfo.getMobileName() + "\n\tAndroid OS Version : " + mobileInfo.getMobileOs() + "\n\tMobile Resolution : " + mobileInfo.getMobileResolution() + "\n\tManufacturer : " + mobileInfo.getManufacturer() + "\n\tApplication Version : " + mobileInfo.getappVersionName() + "\n\n");
                startActivity(Intent.createChooser(Email, "Send Feedback:"));
                break;
            case R.id.action_rateUs:
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
                break;
            case R.id.action_shareApp:
                String appLink = getResources().getString(R.string.app_name)+" android application: " + getResources().getString(R.string.app_name) + "\n http://play.google.com/store/apps/details?id=" + getPackageName();
                Intent sendintent=new Intent();
                sendintent.setAction(Intent.ACTION_SEND);
                sendintent.putExtra(Intent.EXTRA_TEXT, appLink);
                sendintent.setType("text/plain");
                startActivity(sendintent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
