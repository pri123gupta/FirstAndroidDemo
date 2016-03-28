package com.applop.demo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.applop.demo.R;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.BookedItem;
import com.applop.demo.model.CheckedOutItem;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

/**
 * Created by intel on 2/9/2016.
 */

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    ArrayList<BookedItem> data=new ArrayList<BookedItem>();
    Context context;
    RequestQueue requestQueue;
    public BookingAdapter(ArrayList<BookedItem> data, final Context context){
        this.data=data;
        this.context=context;
        this.requestQueue= Volley.newRequestQueue(context);

    }
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if (viewType==0) {
            final BookedItem item = data.get(position);
            holder.orderId.setText("Booking Id : "+item.orderId);
            holder.address.setText(item.address);
            holder.phoneNumber.setText(item.phoneNumber);
            holder.productTitle.setText(item.productTitle);
            if (item.totalPrice.equalsIgnoreCase("0")){
                holder.prize.setText("");
            }else {
                holder.prize.setText("Price : " + AppConfiguration.getInstance(context).currencySymbol + " " + item.totalPrice);
            }
            LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (item.status.equalsIgnoreCase("0")){
                holder.status.setText("Pending for Approval");
            }else if (item.status.equalsIgnoreCase("1")){
                holder.status.setText("Booking Confirmed");
            }else {
                holder.status.setText("Booking Canceled");
            }
        }
    }


    @Override
    public int getItemCount() {
        /*if (isLoadingMoreData)
            return data.size()+1;
        else*/
            return data.size();
    }


   public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
TextView orderId,productTitle,address,phoneNumber,status,prize;
        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            if (viewType==0) {
                status = (TextView) itemView.findViewById(R.id.status);
                orderId = (TextView) itemView.findViewById(R.id.orderId);
                productTitle = (TextView) itemView.findViewById(R.id.itemsTitle);
                prize = (TextView) itemView.findViewById(R.id.totalPrice);
                address = (TextView) itemView.findViewById(R.id.address);
                phoneNumber = (TextView) itemView.findViewById(R.id.phoneNumber);
            }else {

            }
        }

        @Override
        public void onClick(View v) {

        }
    }



    public void insert(BookedItem story,int position){
        data.add(position,story);
        notifyItemInserted(position);

    }

    public void insertStories(ArrayList<BookedItem> stories){
        for (int i=0;i<stories.size();i++){
            data.add(data.size(),stories.get(i));
            notifyItemInserted(data.size());
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
            return 0;
    }
}
