package com.applop.demo.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.applop.demo.fragments.Tab1Fragment;
import com.applop.demo.R;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoryDetailActivity extends AppCompatActivity {
static ViewPager viewPager;
    ProgressBar progressBar;
    TextView currentPageNo;
    String  appname="Demo App";
    //fragment;;;;load data
   static int position;
    Toolbar toolbar;
  static   String  positionStr;
    public ArrayList<Story> stories=new ArrayList<Story>();
    public static ArrayList <Story > storiesArray=new ArrayList<Story>();
    ViewPagerAdapter adapter;
    private static final int NUM_PAGES = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_view_pager1);
        loadResources();
        getBundleData();
        setViewPager();
        toGetNextPageNo();

    }
    public void loadResources(){
        progressBar= (ProgressBar) findViewById(R.id.progress_bar);
        currentPageNo= (TextView) findViewById(R.id.current_page_no);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            currentPageNo.setTextColor(Color.WHITE);
        }else {
            currentPageNo.setTextColor(Color.BLACK);
        }
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    public void setViewPager(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int p, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int p) {
                position = p;
                toGetNextPageNo();
                // toolbar.setTitle("wwww");
                Tab1Fragment c = (Tab1Fragment) adapter.getItem(p);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }
    public void getBundleData(){
       try{
           if (getIntent().getExtras().getBoolean("notification",false)){
               Story story = (Story) getIntent().getExtras().get("story");
               stories.clear();
               if (story != null) {
                   stories.add(story);
               } else {
                   onBackPressed();
                   return;
               }
           }else {
               for (int i = 0; i < storiesArray.size(); i++) {
                   stories.add(new Story(this, new JSONObject(storiesArray.get(i).storyJSONString)));
               }
           }

       }catch (Exception e){
            e.printStackTrace();
       }
//
        try {
            position = Integer.parseInt(positionStr);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        List<Fragment> fragments;

        public class MyCache extends LruCache<Integer,Fragment> {
            public MyCache(int maxsize){
                super(maxsize);
            }
            @Override
            protected Fragment create(Integer key) {
                Story story = stories.get(key);
                return Tab1Fragment.newInstance(story, key);

            }
        }

        MyCache mCache;
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mCache=new MyCache(3);
        }

        @Override
        public Fragment getItem(int position) {
            return mCache.get(position);
           }

        @Override
        public int getCount() {
            return stories.size();
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mCache.remove(position);
            super.destroyItem(container, position, object);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_open_detail, menu);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            menu.findItem(R.id.share).setIcon(R.drawable.share);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        switch (id){
            case R.id.share:
                shareStoryButtonPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void shareStoryButtonPressed(){
        openShareDialog();
    }
    public void  openShareDialog(){
        int index=viewPager.getCurrentItem();
        String title=stories.get(index).title+"\n\n";
        String body=stories.get(index).excerpt+"\n\n";
        String appLink = "To Read Full Story Download: " + getResources().getString(R.string.app_name) + "\n http://play.google.com/store/apps/details?id=" + getPackageName();
        Intent sendintent=new Intent();
        sendintent.setAction(Intent.ACTION_SEND);
        sendintent.putExtra(Intent.EXTRA_TEXT, title + body + appLink);
        sendintent.setType("text/plain");
        startActivity(sendintent);
    }
    public void toGetNextPageNo(){
      int i=  viewPager.getCurrentItem();
     int j= adapter.getCount();
       // Toast.makeText(getApplicationContext(),""+(i+1)+"/"+j,Toast.LENGTH_LONG).show();
        currentPageNo.setText(""+(i+1)+"/"+j);
    }
}
