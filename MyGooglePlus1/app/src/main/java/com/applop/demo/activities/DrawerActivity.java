package com.applop.demo.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
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
import com.applop.demo.adapters.DrawerMenuAdapter;
import com.applop.demo.model.JsonCategory;
import com.applop.demo.classes.MyRequestQueue;

import com.applop.demo.R;
import com.applop.demo.adapters.StoryAdapter;
import com.applop.demo.model.Story;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DrawerActivity extends AppCompatActivity {
Toolbar toolbar;
    Button btnShare,btnEnquiry;
    String  appname="Demo App";
TextView myTitle;
    StoryAdapter storyAdapter;
    ProgressBar progressBar;
    String url2="http://healthxp.net/expert-tutor/api/get_stories.php?";
    JsonCategory jsonCategory;
    String  urlHOme="http://healthxp.net/expert-tutor/api/get_stories.php?category=all&&page=1";
    Story storyCategory;
    RelativeLayout mDrawerRelativelayout;
    Context context;
    JsonCategory category;
    CardView cardview;
    private android.os.Handler mHandler = new android.os.Handler();
    static  int position;
    DrawerLayout mDrawerLayout;
    RelativeLayout mDrawerRealativeLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ArrayList<JsonCategory> jsoncategories= new ArrayList<JsonCategory>();
   static ArrayList<Story> stories=new ArrayList<Story>();
    DrawerMenuAdapter drawer1MenuAdapter;
    JsonCategory currentCategory;

    //recyclerview

    RecyclerView itemsRecyclerView;
    RecyclerView mDrawerList;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storyAdapter=new StoryAdapter(stories,this);
      ///  getSupportActionBar().setTitle("Agriculture");

        loadResources();
        setSideDrawer();
       // toolbar.hideOverflowMenu();
        return;
    }
    private void loadResources() {
        btnShare= (Button) findViewById(R.id.share);
        btnEnquiry= (Button) findViewById(R.id.abc);
        myTitle= (TextView) findViewById(R.id.mytitle);
        cardview= (CardView) findViewById(R.id.cardview);
        progressBar= (ProgressBar) findViewById(R.id.progress_bar);
        storyAdapter=new StoryAdapter(stories,this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer1MenuAdapter = new DrawerMenuAdapter(jsoncategories, context);
        mDrawerRelativelayout = (RelativeLayout) findViewById(R.id.relative_left_drawer);
       // JsonDrawerHelper.setDetailsInDrawerlayout(mDrawerRelativelayout, this);
        mDrawerRealativeLayout = (RelativeLayout) findViewById(R.id.relative_left_drawer);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer_list);
        mDrawerList.setLayoutManager(new LinearLayoutManager(context));
        mDrawerList.setAdapter(drawer1MenuAdapter);
        //for second drawer with id itemsRecyclerView
        itemsRecyclerView = (RecyclerView) findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsRecyclerView.setAdapter(storyAdapter);
        itemsRecyclerView.setVisibility(View.VISIBLE);

        drawer1MenuAdapter.setmyOnItemClickListener(new DrawerMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DrawerMenuAdapter.ViewHolder viewHolder = (DrawerMenuAdapter.ViewHolder) view.getTag();
                jsonCategory = (JsonCategory) viewHolder.itemName.getTag();
                menuItemClicked(jsonCategory);

            }
        });
        storyAdapter.setOnItemClickListener(new StoryAdapter.OnItemClickListener() {
            @Override
            public void OnItemCick(View view, int position) {

                openDetailPage(position);
            }


        });
    }
    public void openDetailPage(int position){
        Intent intent= new Intent(context, ViewPagerActivity1.class);
       // intent.putExtra("position", position);
        ViewPagerActivity1.position=position;
        ViewPagerActivity1.storiesArray=stories;
//            intent.putParcelableArrayListExtra("storydata", stories);
        if (currentCategory!=null){
            intent.putExtra("category", currentCategory);
        }
            startActivityForResult(intent, 1);
    }
    public void menuItemClicked(JsonCategory category){
        if (category.name.equalsIgnoreCase("About us")){
            Intent intent=new Intent(this,AboutUsActivity.class);
            startActivity(intent);
        }else {
            mDrawerLayout.closeDrawer(mDrawerRelativelayout);
            loadCategory(category, 1, false, false);
            myTitle.setText(category.name);
            return;
        }
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
                            loadCategory(jsonCategory, 1, false, false);//nm
                            drawer1MenuAdapter.clear();
                            ArrayList<JsonCategory> menuCategories=new ArrayList<JsonCategory>();
                            menuCategories.add(new JsonCategory("", "About Us", "About Us"));
                            menuCategories.add(new JsonCategory("", "Home", "Home"));
                            for (int i=0;i<jsonCategories.length();i++){
                                menuCategories.add(new JsonCategory(jsonCategories.getJSONObject(i)));
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
        }.getJsonObject("http://healthxp.net/expert-tutor/api/get_categories.php", true, "menu");;
    }
    //for loading page on click of button in navigation drawer list
    public void loadCategory(JsonCategory category, final int page,boolean isRefreshing,boolean isLoadingMore){
        if (category==null){
            itemsRecyclerView.setVisibility(View.INVISIBLE);
            return;
        }
        itemsRecyclerView.setVisibility(View.VISIBLE);
        currentCategory = category;
        final Context context=this;
        String URL = getCategoryStoryURL(category,1);//1
        MyRequestQueue.Instance(this).cancelPendingRequests("stories");
        new VolleyData(this) {
            @Override
            protected void VPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                itemsRecyclerView.setVisibility(View.GONE);
            }
            @Override
            protected void VResponse(JSONObject response, String tag) {
                JSONObject json=response;
            try{
               if (json!=null){
                   try {
                       progressBar.setVisibility(View.GONE);
                       itemsRecyclerView.setVisibility(View.VISIBLE);
                       JSONArray jsonStories=json.getJSONArray("stories");
                       storyAdapter.clear();
                       itemsRecyclerView.setAdapter(null);
                       ArrayList<Story> storyCategory=new ArrayList<Story>();
                       for (int i=1;i<jsonStories.length();i++){
                       Story story=new Story(context,jsonStories.getJSONObject(i));
                           if (!IsStoryAlreadyAdded(story)){
                             storyCategory.add(story);
                           }
                       }
                       itemsRecyclerView.setAdapter(storyAdapter);
                       storyAdapter.insertStories(storyCategory);
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
            progressBar.setVisibility(View.GONE);
                itemsRecyclerView.setVisibility(View.VISIBLE);
            }
        }.getJsonObject(URL, true, "categoryStories");
    }
    private String getCategoryStoryURL(JsonCategory category, int page) {
        page=1;
        if (category.name.equalsIgnoreCase("home")){
            return urlHOme;
        }
        return  url2 + "category="+category.categoryId+"&page=1";

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
                if (resultCode==RESULT_OK){
                    if (data!=null){
                        JsonCategory category = (JsonCategory)data.getExtras().get("category");
                        loadCategory(category,1,false,false);                    }
                }
        }catch (Exception ex){
        }
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.logo){
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }
public void  onShareButtnPressed(){
    int index=ViewPagerActivity1.viewPager.getCurrentItem();
    String title="Title : "+stories.get(index).title+"\n\n";
    String body="Description :\n"+stories.get(index).excerpt+"\n\n";
    String appLink = "To Read Full Story Download: "+appname+"\n http://play.google.com/store/apps/details?id=" + getPackageName();
    Intent sendintent=new Intent();
    sendintent.setAction(Intent.ACTION_SEND);
    sendintent.putExtra(Intent.EXTRA_TEXT, title + body + appLink);
    sendintent.setType("text/plain");
    startActivity(sendintent);
}
    }
