package com.applop.demo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applop.demo.model.JsonCategory;
import com.applop.demo.R;

import java.util.ArrayList;

/**
 * Created by intel on 2/8/2016.
 */
public class DrawerMenuAdapter extends   RecyclerView.Adapter<DrawerMenuAdapter.ViewHolder>{
    OnItemClickListener mItemClickListener;
    View view;
    Context context;
    ArrayList<JsonCategory> data;
    public DrawerMenuAdapter(ArrayList<JsonCategory> data, final Context context){
        this.data=data;
        this.context=context;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public DrawerMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.json_drawer_child_item,parent,false);

        return new ViewHolder(view,viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return data.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    int viewType= getItemViewType(position);
        holder.itemView.setTag(holder);
        holder.itemName.setTag(data.get(position));
        holder.itemName.setText(data.get(position).name);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public  class  ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        public TextView itemName;
        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            itemName= (TextView) itemView.findViewById(R.id.child_name);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v,getPosition());
            }
        }
    }
    public void insertCategories(ArrayList<JsonCategory> categories) {
        for (int i=0;i<categories.size();i++) {
            data.add(data.size(), categories.get(i));
            notifyItemInserted(data.size()-1);
        }
    }
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
    //no
    public void setmyOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    //no

}
