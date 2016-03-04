package com.applop.demo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.applop.demo.R;
import com.applop.demo.activities.BookMailActivity;
import com.applop.demo.activities.CartActivity;
import com.applop.demo.activities.EnquiryMailActivity;
import com.applop.demo.activities.SignInActivity;
import com.applop.demo.helperClasses.DatabaseHelper;
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.CheckedOutItem;
import com.applop.demo.model.Story;
import com.applop.demo.model.User;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by intel on 2/9/2016.
 */

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {
    ArrayList<CheckedOutItem> data=new ArrayList<CheckedOutItem>();
    Context context;
    RequestQueue requestQueue;
    IconDrawable shareIcon;
    IconDrawable enquiryIcon;
    public CheckOutAdapter(ArrayList<CheckedOutItem> data, final Context context){
        this.data=data;
        this.context=context;
        this.requestQueue= Volley.newRequestQueue(context);
        shareIcon = new IconDrawable(context, Iconify.IconValue.fa_share )
                .colorRes(R.color.grey)
                .actionBarSize();
        enquiryIcon = new IconDrawable(context, Iconify.IconValue.fa_question )
                .colorRes(R.color.grey)
                .actionBarSize();

    }
    @Override
    public CheckOutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_out_order_card, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if (viewType==0) {
            final CheckedOutItem item = data.get(position);
            holder.orderId.setText("Order Id : "+item.orderId);
            holder.totalItems.setText(item.quantity + " items");
            holder.totalPrice.setText("Total Price : "+AppConfiguration.getInstance(context).currencySymbol+" "+item.totalPrice);
            LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder.items_ll.removeAllViews();
            for (int i=0;i<item.items.size();i++) {
                View view = inflater.inflate(R.layout.checkout_cart_item, null);
                ((TextView)view.findViewById(R.id.title)).setText(item.items.get(i).title);
                ((TextView)view.findViewById(R.id.itemsCount)).setText(" ("+item.items.get(i).quantity+")");
                ((TextView)view.findViewById(R.id.itemsPrice)).setText(" ("+AppConfiguration.getInstance(context).currencySymbol+" "+item.items.get(i).totalPrice+")");
                holder.items_ll.addView(view);
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
TextView orderId,totalPrice,totalItems;
       LinearLayout items_ll;
        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            if (viewType==0) {
                items_ll = (LinearLayout) itemView.findViewById(R.id.orderedItems);
                orderId = (TextView) itemView.findViewById(R.id.orderId);
                totalPrice = (TextView) itemView.findViewById(R.id.totalPrice);
                totalItems = (TextView) itemView.findViewById(R.id.itemsCount);
            }else {

            }
        }

        @Override
        public void onClick(View v) {

        }
    }



    public void insert(CheckedOutItem story,int position){
        data.add(position,story);
        notifyItemInserted(position);

    }

    public void insertStories(ArrayList<CheckedOutItem> stories){
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
