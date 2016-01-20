package com.applop.demo.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.applop.demo.model.JsonCategory;
import com.applop.demo.R;

import java.util.ArrayList;

public class JsonDrawerAdapter extends RecyclerView.Adapter<JsonDrawerAdapter.ViewHolder>{
    Context context;
    View view;
    int Holderid;
    int TYPE_ITEM=1;
    int TYPE_HEADER=0;

    ImageView headerImage1,headerImage2,childImage;
    TextView headerText,childText;
    int HolderId;
    Drawable collapsed,expanded,months,categories,home,aboutUs,setting,myNotes,questions;
    AdapterView.OnItemClickListener mItemClickListener;
    ArrayList<JsonCategory> data= new ArrayList<JsonCategory>();
    public JsonDrawerAdapter(ArrayList<JsonCategory> arrayList,Context context)
    {
        this.data=arrayList;
        this.context=context;
        this.context = context;

    }
    public void setOnItemClickListener(final AdapterView.OnItemClickListener a) {
        mItemClickListener = a;
    }


    @Override
    public JsonDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType==0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.json_drawer_header
                    , parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.json_drawer_child_item
                    , parent, false);
        }
        return new ViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(JsonDrawerAdapter.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        holder.itemView.setTag(holder);
        holder.itemIcon.setImageDrawable(null);
        holder.itemName.setTag(data.get(position));


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void insert(JsonCategory category, int position) {
        data.add(position,category);
        notifyItemInserted(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName;
        public ImageView itemIcon;
        public View itemView;
        public ImageView expansionIcon;
        public Drawable expanded;
        public Drawable collapsed;

        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            this.itemView = itemView;
            if (viewType==0) {
                headerImage1= (ImageView) view.findViewById(R.id.header_image1);
                headerImage2= (ImageView) view.findViewById(R.id.header_image2);
                headerText= (TextView) view.findViewById(R.id.header_text);
                //groupItem
                HolderId=1;
            }else if (viewType==1) {
                //ChildItem

                childText= (TextView) view.findViewById(R.id.child_name);
                HolderId=0;

            }
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
               // mItemClickListener.onItemClick(v,getPosition());
            }
        }
    }
    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}