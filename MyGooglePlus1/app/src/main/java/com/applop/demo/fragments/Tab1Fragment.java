package com.applop.demo.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.activities.ViewPagerActivity1;
import com.applop.demo.activities.VolleyData;
import com.applop.demo.model.Story;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


public class Tab1Fragment extends Fragment {
    View.OnClickListener photoGAlleryListener;
    int index;
    View.OnClickListener relatedStoryListener;
    int fontSize=16;
    WebView webView;
    Story story;
    Toolbar toolbar;
    ImageView coverImage;
   // Button categoryNameButton;
    LayoutInflater inflater;
    TextView time,storyTitle;
    LinearLayout relatedStoryItemLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_tab1, container, false);
        coverImage= (ImageView) rootView.findViewById(R.id.storyThumbImage);
      //  categoryNameButton= (Button) rootView.findViewById(R.id.categoryNameButton);
        time= (TextView) rootView.findViewById(R.id.byAndTime);
        webView= (WebView) rootView.findViewById(R.id.webView);
        storyTitle= (TextView) rootView.findViewById(R.id.storyTitle);
        relatedStoryItemLayout= (LinearLayout) rootView.findViewById(R.id.relatedReadingView);
        coverImage.setOnClickListener(photoGAlleryListener);

        relatedStoryItemLayout.removeAllViews();
        relatedStoryListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int)v.getTag();
                Intent intent = new Intent(getActivity(), ViewPagerActivity1.class);
                intent.putExtra("position", String.valueOf(position));
                ViewPagerActivity1.storiesArray = story.relatedStories;
                getActivity().startActivityForResult(intent,1);
            }
        };
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(View.VISIBLE);
            }
        });


        setData();
        if (story.body.equalsIgnoreCase("")) {
            loadStoryFromURL();
        }else {
            Toast.makeText(getActivity(), "has data" , Toast.LENGTH_LONG).show();
            setData();
        }
        return rootView;
    }
    public static Tab1Fragment newInstance(Story story,int position){
        Tab1Fragment fragment1=new Tab1Fragment();
        fragment1.index=position;
        fragment1.story=story;
        Tab1Fragment instance= new Tab1Fragment();
//        Bundle barg=new Bundle();
//        barg.putInt("pos", position);
//        instance.setArguments(barg);

        return fragment1;
    }
        public void loadStoryFromURL(){
        String url;
            try{
                url=getStoryUrl();
             //   Toast.makeText(getActivity(), "111" , Toast.LENGTH_LONG).show();
                new VolleyData(getActivity()) {
                    @Override
                    protected void VPreExecute() {
                   //     Toast.makeText(getActivity(), "222" , Toast.LENGTH_LONG).show();
                    }
                    @Override
                    protected void VResponse(JSONObject response, String tag) {
                    JSONObject json= response;
                      //  Toast.makeText(getActivity(), "hhgg" , Toast.LENGTH_LONG).show();
                        try {
                       if (json!=null){
                           JSONObject storyJSON=json.getJSONObject("story");
                           story.storyJSONString=storyJSON.toString();
                           story=new Story(getActivity(),storyJSON);
                           setData();
                       }
                        }catch (Exception e){
                            Toast.makeText(getActivity(), ""+e , Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    protected void VError(VolleyError error, String tag) {
                        Toast.makeText(getActivity(), ""+error , Toast.LENGTH_LONG).show();
                    }
                }.getJsonObject(url,true,"stories");
            }catch (Exception e){
                Toast.makeText(getActivity(), ""+e , Toast.LENGTH_LONG).show();
            }
        }
    public String  getStoryUrl(){
        return  "http://www.healthxp.net/expert-tutor/api/detail.php?"+ "id=" + story.postId;
    }
public void setData() {
//    toolbar.setTitle(story.title);

  //  toolbar.setTitle(story.);
        Picasso.with(getActivity()).load(story.fullImage).placeholder(R.drawable.newplaceholder).into(coverImage);
    webView.loadDataWithBaseURL("file:///android_asset/", getHtmlStringData(story.body), "text/html", "UTF-8", null);
    time.setText(story.timeAgo);
    storyTitle.setText(story.title);

       // categoryNameButton.setText(story.categories.get(0).name);
      //  categoryNameButton.setVisibility(View.GONE);
        loadRelatedStoryLayout();
}
    public String loadRelatedStoryLayout() {
        String url;
        Context co;
        try {
            url = getLoadStoryUrl();
            new VolleyData(getActivity()) {
                @Override
                protected void VPreExecute() {
                }
                @Override
                protected void VResponse(JSONObject response, String tag) {
                JSONObject json=response;
                    try{
                        if(json!=null){
                          //  Toast.makeText(getActivity(),"sss11",Toast.LENGTH_LONG).show();
                            story.relatedStories.clear();
                            JSONArray storiesArrayJson=json.getJSONArray("stories");
                            for(int i=0;i<storiesArrayJson.length();i++){
                                story.relatedStories.add(new Story(getActivity(),storiesArrayJson.getJSONObject(i)));
                            }

                        JSONObject jsonStory=json.getJSONObject("stories");
                            story.storyJSONString=jsonStory.toString();
                            story=new Story(getActivity(),jsonStory);
                            setData();
                        }
                    }catch (Exception e){
                     //   Toast.makeText(getActivity(),"sss"+e,Toast.LENGTH_LONG).show();
                    if (story.body.equalsIgnoreCase("")){
                        relatedStoryItemLayout.setVisibility(View.GONE);
                    }
                        try {
                         //   Toast.makeText(getActivity(),"sss22",Toast.LENGTH_LONG).show();
                            if (story.relatedStories.size()>0) {
                                for (int i = 0; i < story.relatedStories.size(); i++) {
                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    Story relStory = story.relatedStories.get(i);
                                    RelativeLayout relatedItemLayout= (RelativeLayout) inflater.inflate(R.layout.related_story_layout, relatedStoryItemLayout, false);
                                    relatedItemLayout.setOnClickListener(relatedStoryListener);
                                    relatedItemLayout.setTag(i);
                                    LinearLayout relItemSuperView= (LinearLayout) relatedItemLayout.findViewById(R.id.relatedLayoutItem);
                                    TextView relStoryTitle= (TextView) relatedItemLayout.findViewById(R.id.relatedStoryTitle);
                                    relStoryTitle.setText(relStory.title);

                                    ImageView imageView= (ImageView) relatedItemLayout.findViewById(R.id.relatedCoverImage);
                                    imageView.setImageResource(0);
                                    Picasso.with(getActivity()).load(relStory.fullImage).placeholder(R.drawable.marry).into(imageView);
                                    relatedStoryItemLayout.addView(relatedItemLayout);
                                }
                            }
                        }catch (Exception e1){
                            Toast.makeText(getActivity(),"sss"+e,Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                protected void VError(VolleyError error, String tag) {
                    if (story.body.equalsIgnoreCase("")){
                        relatedStoryItemLayout.setVisibility(View.GONE);
                    }
                }
            }.getJsonObject(url,true,"story");

            return null;
        } catch (Exception e) {

        }
        return null;
    }
    public String getLoadStoryUrl(){
        //String url="api/detail.php?"+"id="+story.postId;
        String URL =  "api/get_similar.php?" + "id=" + story.postId;
        return URL;
    }
   public String  getHtmlStringData(String body){
       String htmlStr = "<style type=text/css>" +
               "@font-face {\n" +
               "    font-family: 'Desc-Font';\n" +
               "    src: url('fonts/descfont.ttf');\n" +
               "}"+
               "#multicolumn{column-count:1;column-gap: 5px;column-rule:1px solid#E8E8E8;\n" +
               "line-height:1.4;" +
               "margin:0 0;}\n" +
               " body {} " +
               "a {color: #0000cc;}"+
               "h{color:#AC182E; font-family:'Desc-Font'; line-height:1.2;}\n" +
               "img {vertical-align:center;display:block; horizontal-align:center;margin-bottom:5; min-height:50;height:auto; margin-left: 0;   margin-right: 0; max-width:100%; border:none; }\n" +
               "div {max-width:100%;height:auto;}"+
               "</style>" +
               "<html><head></head><body>" +
               "<div style='margin:0 5;' >";
       htmlStr = htmlStr+"<div id=multicolumn style='color:#4f4f4f;font-size:"+fontSize+"; font-family:'Desc-Font' line-height:1.5;'>"+body+"</div>" + "</div></body></html>";
       return htmlStr;
   }

}
