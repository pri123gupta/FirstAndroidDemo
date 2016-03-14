package com.applop.demo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


public class ApplopPagerFragment extends Fragment {

    public int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.applop_pager_fragment, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.backgroundImage);
        TextView textView = (TextView) rootView.findViewById(R.id.middleText);
        if (index==0){
            imageView.setImageResource(R.drawable.pattern_1);
            ((TextView)rootView.findViewById(R.id.firstText)).setText("Welcome to Applop");
            textView.setText("Make Mobile App Without Coding");
        }else if (index==1){
            imageView.setImageResource(R.drawable.pattern_2);
            textView.setText("Update unlimited contents and send unlimited notification in language of your choice");
        }else if (index==2){
            imageView.setImageResource(R.drawable.pattern_3);
            textView.setText("Start your own e-commerce store, display your products in app and accept payment online");
        }else if (index==3){
            imageView.setImageResource(R.drawable.pattern_4);
            textView.setText("Start your content app, news app, community app, video app, personal app");
        }else {
            imageView.setImageResource(R.drawable.pattern_5);
            textView.setText("With Applop possibilities are unlimited with zero line of code and 5 minutes of effort");
        }
        return rootView;
    }
    public static ApplopPagerFragment newInstance(int position){
        ApplopPagerFragment fragment1=new ApplopPagerFragment();
        fragment1.index = position;
        return fragment1;
    }

}
