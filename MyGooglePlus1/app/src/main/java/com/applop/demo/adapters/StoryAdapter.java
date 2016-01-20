package com.applop.demo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.applop.demo.R;
import com.applop.demo.model.Story;
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
    RequestQueue requestQueue;
    public StoryAdapter(ArrayList<Story> data, final Context context){
        this.data=data;
        this.context=context;
        this.requestQueue= Volley.newRequestQueue(context);
    }
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.other_row_grid,parent,false);
        return new ViewHolder(view,viewType);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    int viewType=getItemViewType(position);
        Story item;
        item=data.get(position);
      holder.introduction.setText(item.excerpt);
        holder.homeFeedPostTime.setText(Html.fromHtml(item.categoryNameAndTime));
        holder.titleName.setText(item.title);
        //
        holder.itemView.setTag(holder);
       // holder.titleName.setTag(data.get(position));

        holder.titleName.setText(data.get(position).title);
        holder.homeFeedPostTime.setText(Html.fromHtml(item.categoryNameAndTime));
    //   holder.introduction.setText(data.get(position).body);
        if(item.fullImage==null || item.fullImage.equalsIgnoreCase("")) {
            Toast.makeText(context,"no image",Toast.LENGTH_LONG).show();
        }else{
            Picasso.with(context).load(item.fullImage).placeholder(R.drawable.newplaceholder).into(holder.coverImageView);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
   public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
TextView titleName,homeFeedPostTime,introduction;
        ImageView coverImageView;
        public ViewHolder(View itemView,int page) {
            super(itemView);
            titleName= (TextView) itemView.findViewById(R.id.titleName);
            homeFeedPostTime= (TextView) itemView.findViewById(R.id.homeFeedPostTime);
            coverImageView= (ImageView) itemView.findViewById(R.id.coverImageView);
           // Picasso.with(context).load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg").into(coverImageView);
            introduction= (TextView) itemView.findViewById(R.id.introduction);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        if(mItemClickListener!=null){
            mItemClickListener.OnItemCick(v,getPosition());
        }
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
}
