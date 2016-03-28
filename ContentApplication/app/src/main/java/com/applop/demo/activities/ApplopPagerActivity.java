package com.applop.demo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.view.ViewGroup;

import com.applop.demo.R;
import com.applop.demo.fragments.ApplopPagerFragment;
import com.applop.demo.fragments.Tab1Fragment;
import com.applop.demo.helperClasses.AnalyticsHelper;
import com.applop.demo.model.Story;
import com.applop.demo.myads.MyInterstitialAd;

import java.util.List;

public class ApplopPagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    ViewPagerAdapter adapter;
    Context context;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applop_pager);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        context = this;
        setViewPager();
    }

    public class LoadNextPage implements Runnable {

        public void run()
        {
            if (viewPager.getCurrentItem()==4){
                Intent intent = new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                mHandler.postDelayed(new LoadNextPage(),5000);
            }
        }

    }

    public void setViewPager(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        mHandler.postDelayed(new LoadNextPage(), 2000);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int p, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int p) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        List<Fragment> fragments;

        public class MyCache extends LruCache<Integer,Fragment> {

            public MyCache(int maxsize){
                super(maxsize);
            }

            @Override
            protected Fragment create(Integer key) {
                return ApplopPagerFragment.newInstance(key);

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
            return 5;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mCache.remove(position);
            super.destroyItem(container, position, object);
        }
    }
}
