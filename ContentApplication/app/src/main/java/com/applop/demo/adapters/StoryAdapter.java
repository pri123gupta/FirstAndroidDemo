package com.applop.demo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.applop.demo.R;
import com.applop.demo.activities.BookMailActivity;
import com.applop.demo.activities.EnquiryMailActivity;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.Story;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by intel on 2/9/2016.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    ArrayList<Story> data=new ArrayList<Story>();
    Context context;
    View.OnClickListener titleListener;
OnItemClickListener mItemClickListener;
    private boolean isLoadingMoreData =false ;
    RequestQueue requestQueue;
    IconDrawable shareIcon;
    IconDrawable bookIcon;
    IconDrawable enquiryIcon;
    public StoryAdapter(ArrayList<Story> data, final Context context){
        this.data=data;
        this.context=context;
        this.requestQueue= Volley.newRequestQueue(context);
        shareIcon = new IconDrawable(context, Iconify.IconValue.fa_share )
                .colorRes(R.color.grey)
                .actionBarSize();
        enquiryIcon = new IconDrawable(context, Iconify.IconValue.fa_question )
                .colorRes(R.color.grey)
                .actionBarSize();
        bookIcon = new IconDrawable(context, Iconify.IconValue.fa_shopping_cart )
                .colorRes(R.color.grey)
                .actionBarSize();
    }
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_row_grid, parent, false);
            return new ViewHolder(view, viewType);
        }else {
//For Loading
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_bottom_progressbar
                    , parent, false);
            return new ViewHolder(view,viewType);
        }
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    int viewType=getItemViewType(position);
        final Story item=data.get(position);
      holder.introduction.setText(item.excerpt);
        holder.homeFeedPostTime.setText(Html.fromHtml(item.categoryNameAndTime));
        holder.titleName.setText(item.title);
        //
        holder.itemView.setTag(holder);
       // holder.titleName.setTag(data.get(position));
        if (!AppConfiguration.getInstance(context).isEnquryEnable){
            holder.enquiry_tv.setVisibility(View.GONE);
        }
        if (!AppConfiguration.getInstance(context).isBookingEnable){
            holder.book_tv.setVisibility(View.GONE);
        }
        if (!AppConfiguration.getInstance(context).isShareEnable){
            holder.share_tv.setVisibility(View.GONE);
        }
        holder.titleName.setText(data.get(position).title);
        holder.homeFeedPostTime.setText(Html.fromHtml(item.categoryNameAndTime));
        holder.share_image_view.setImageDrawable(shareIcon);
        holder.enquiry_image_view.setImageDrawable(enquiryIcon);
        holder.book_image_view.setImageDrawable(bookIcon);
        holder.share_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = item.title + "\n\n";
                String body = item.excerpt + "\n\n";
                String appLink = "To Read Full Story Download: " + context.getResources().getString(R.string.app_name) + "\n http://play.google.com/store/apps/details?id=" + context.getPackageName();
                Intent sendintent = new Intent();
                sendintent.setAction(Intent.ACTION_SEND);
                sendintent.putExtra(Intent.EXTRA_TEXT, title + body + appLink);
                sendintent.setType("text/plain");
                context.startActivity(sendintent);
            }
        });

        holder.book_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookMailActivity.class);
                BookMailActivity.item = item;
                context.startActivity(intent);
            }
        });

        holder.enquiry_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnquiryMailActivity.class);
                EnquiryMailActivity.item = item;
                context.startActivity(intent);
            }
        });
    //   holder.introduction.setText(data.get(position).body);
        if(item.fullImage.equalsIgnoreCase("")) {
            //Toast.makeText(context,"no image",Toast.LENGTH_LONG).show();
        }else{
            Picasso.with(context).load(item.fullImage).placeholder(R.drawable.newplaceholder).into(holder.coverImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (isLoadingMoreData)
            return data.size()+1;
        else
            return data.size();
    }

    public void removeMoreLoading(){
        if (isLoadingMoreData) {
            isLoadingMoreData = false;
            try {
                notifyItemRemoved(data.size());
            }catch (Exception ex){

            }

        }
    }
   public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
TextView titleName,homeFeedPostTime,introduction;
        ImageView coverImageView;
       LinearLayout share_tv;
       LinearLayout enquiry_tv;
       LinearLayout book_tv;
       ImageView share_image_view;
       ImageView enquiry_image_view;
       ImageView book_image_view;
        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            if (viewType==0) {
                share_tv = (LinearLayout) itemView.findViewById(R.id.share_ll);
                enquiry_tv = (LinearLayout) itemView.findViewById(R.id.enquiry_ll);
                book_tv = (LinearLayout) itemView.findViewById(R.id.book_ll);
                share_image_view = (ImageView) itemView.findViewById(R.id.share_image);
                enquiry_image_view = (ImageView) itemView.findViewById(R.id.enquiry_image);
                book_image_view = (ImageView) itemView.findViewById(R.id.book_image);
                titleName = (TextView) itemView.findViewById(R.id.titleName);
                homeFeedPostTime = (TextView) itemView.findViewById(R.id.homeFeedPostTime);
                coverImageView = (ImageView) itemView.findViewById(R.id.coverImageView);
                // Picasso.with(context).load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg").into(coverImageView);
                introduction = (TextView) itemView.findViewById(R.id.introduction);
                itemView.setOnClickListener(this);
            }else {

            }
        }

        @Override
        public void onClick(View v) {
        if(mItemClickListener!=null){
            mItemClickListener.OnItemCick(v,getPosition());
        }
        }
    }

    public void insertMoreLoading(){
        isLoadingMoreData = true;
        try {
            notifyItemInserted(data.size());
        }catch (Exception ex){

        }

    }
    public interface OnItemClickListener{
        public  void OnItemCick(View view,int position);
    }
    public void setOnItemClickListener (final OnItemClickListener mItemClickListener){
        this.mItemClickListener=mItemClickListener;

    }
    public void insert(Story story,int position){
    data.add(position,story);
        notifyItemInserted(position);

    }
    public void insertStories(ArrayList<Story> stories){
        for (int i=0;i<stories.size();i++){
            data.add(data.size(),stories.get(i));
            notifyItemInserted(data.size()-1);
        }
    }
    public void remove(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }
    public void  clear(){
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position==data.size())
            return -1;
        else
            return 0;
    }
}
