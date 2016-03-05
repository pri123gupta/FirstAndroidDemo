package com.applop.demo.adapters;

import android.app.Activity;
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
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.Story;
import com.applop.demo.model.User;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by intel on 2/9/2016.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    ArrayList<Story> data=new ArrayList<Story>();
    Activity context;
    View.OnClickListener titleListener;
OnItemClickListener mItemClickListener;
    private boolean isLoadingMoreData =false ;
    RequestQueue requestQueue;
    IconDrawable shareIcon;
    IconDrawable bookIcon;
    DatabaseHelper databaseHelper;
    IconDrawable enquiryIcon;
    boolean isCart = false;
    public StoryAdapter(ArrayList<Story> data, final Activity context){
        this.data=data;
        this.context=context;
        databaseHelper = new DatabaseHelper(context);
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

    public void setIsCart(boolean isCart) {
        this.isCart = isCart;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if (viewType==0) {
            final Story item = data.get(position);
            holder.introduction.setText(Html.fromHtml(item.excerpt).toString());
            holder.titleName.setText(item.title);
            holder.itemView.setTag(holder);
            if (!AppConfiguration.getInstance(context).isEnquryEnable) {
                holder.enquiry_tv.setVisibility(View.GONE);
            }
            if (!AppConfiguration.getInstance(context).isCartEnable) {
                holder.cart_ll.setVisibility(View.GONE);
                if (!AppConfiguration.getInstance(context).isBookingEnable){
                    holder.book_tv.setVisibility(View.GONE);
                }
            }else {
                holder.book_tv.setVisibility(View.GONE);
            }
            if (!AppConfiguration.getInstance(context).isShareEnable) {
                holder.share_tv.setVisibility(View.GONE);
            }
            if (item.price.equalsIgnoreCase("")) {
                holder.priceAndQuantity.setVisibility(View.GONE);
                holder.book_tv.setVisibility(View.GONE);
                holder.cart_ll.setVisibility(View.GONE);
            } else {
                holder.priceAndQuantity.setVisibility(View.VISIBLE);
                if (isCart){
                    holder.negativeImage.setVisibility(View.VISIBLE);
                    holder.positiveImage.setVisibility(View.VISIBLE);
                    holder.quantityTV.setVisibility(View.VISIBLE);
                    holder.quantityTV.setText(String.valueOf(item.quantity));
                }else {
                    holder.negativeImage.setVisibility(View.GONE);
                    holder.positiveImage.setVisibility(View.GONE);
                    holder.quantityTV.setVisibility(View.GONE);
                }
                if (AppConfiguration.getInstance(context).isCartEnable)
                    holder.cart_ll.setVisibility(View.VISIBLE);
                else
                    holder.book_tv.setVisibility(View.VISIBLE);
            }
            holder.homeFeedPostTime.setText(AppConfiguration.getInstance(context).currencySymbol + " " + item.price);
            //holder.titleName.setText(data.get(position).title);
            //holder.homeFeedPostTime.setText(Html.fromHtml(item.categoryNameAndTime));
            holder.share_image_view.setImageDrawable(shareIcon);
            holder.enquiry_image_view.setImageDrawable(enquiryIcon);
            holder.book_image_view.setImageDrawable(bookIcon);
            holder.cart_image_view.setImageDrawable(bookIcon);
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

            if (databaseHelper.checkIfBookmarked(item.postId)){
                holder.cart_text_view.setText("Remove");
            }else {
                holder.cart_text_view.setText("Add to Cart");
            }
            holder.cart_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.getInstance(context).loginType.equalsIgnoreCase("")) {
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                        return;
                    }
                    if (databaseHelper.checkIfBookmarked(item.postId)) {
                        removeFromCart(item, User.getInstance(context), holder, position);

                    } else {
                        addToCart(item, User.getInstance(context), holder);


                    }
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

            holder.positiveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.getInstance(context).loginType.equalsIgnoreCase("")) {
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                        return;
                    }
                    increamentQuantity(holder, item, User.getInstance(context));
                }
            });

            holder.negativeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.getInstance(context).loginType.equalsIgnoreCase("")) {
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                        return;
                    }
                    decreaseQuantity(holder, item, User.getInstance(context));
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
            if (item.fullImage.equalsIgnoreCase("")) {
                holder.coverImageView.setVisibility(View.GONE);
                //Toast.makeText(context,"no image",Toast.LENGTH_LONG).show();
            } else {
                holder.coverImageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(item.fullImage).into(holder.coverImageView);
            }
            if (!item.youtubeURL.equalsIgnoreCase("")){
                holder.playIcon.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String VIDEO = item.youtubeURL;
                        int startIndex = VIDEO.indexOf("?v=") + 3;
                        try {
                            String videoID = VIDEO.substring(startIndex, startIndex + 11);
                            Intent intent = YouTubeStandalonePlayer.createVideoIntent(context, NameConstant.DEVELOPER_KEY, videoID, 0, true, false);
                            context.startActivity(intent);
                        } catch (Exception ex) {
                            Toast.makeText(context, "Unable to play video", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                holder.playIcon.setVisibility(View.GONE);
            }
        }
    }

    private void increamentQuantity(ViewHolder holder,Story item,User user){
        holder.quantityTV.setText(String.valueOf((Integer.parseInt(holder.quantityTV.getText().toString())) + 1));

        updateQuantity(holder,user,item);
    }

    private void decreaseQuantity(ViewHolder holder,Story item,User user){
        if (!holder.quantityTV.getText().toString().equalsIgnoreCase("0")){
            holder.quantityTV.setText(String.valueOf((Integer.parseInt(holder.quantityTV.getText().toString()))-1));
            updateQuantity(holder,user,item);
        }
    }

    private void updateQuantity(final ViewHolder holder,User user,final Story item){
        String URL = "http://applop.biz/merchant/api/modifyQuantityInCart.php";//1
        MyRequestQueue.Instance(context).cancelPendingRequests("modifyQuantity");
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("userEmail", user.email);
        params.put("cartId", item.cartId);
        params.put("packageName", context.getPackageName());
        params.put("quantity",holder.quantityTV.getText().toString());

        new VolleyData(context) {

            @Override
            protected void VPreExecute() {

            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                try {
                    if (response.getBoolean("status")){
                        Toast.makeText(context,"Quantity Updated Successfully",Toast.LENGTH_LONG).show();
                        item.quantity=Integer.parseInt(holder.quantityTV.getText().toString());
                        ((CartActivity)context).setTotalPrice();
                    }else {
                        Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
                }

            }
            @Override
            protected void VError(VolleyError error, String tag) {
                Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
            }
        }.getPOSTJsonObject(URL, "modifyQuantity", params);
    }

    private void removeFromCart(final Story item,User user,final ViewHolder holder,final int position){
        final HashMap<String, String> paramsBooking = new HashMap<String, String>();

        paramsBooking.put("userEmail", user.email);
        paramsBooking.put("storyId", item.postId);
        paramsBooking.put("packageName", context.getPackageName());

        final ProgressDialog progressDialog = new ProgressDialog(context);
        new VolleyData(context){
            @Override
            protected void VPreExecute() {

                progressDialog.setTitle("Removing");
                progressDialog.show();
            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                progressDialog.hide();
                try {
                    if (response.getBoolean("status")){
                        databaseHelper.removeFromBookmarked(item.postId);
                        holder.cart_text_view.setText("Add to Cart");
                        if (isCart) {
                            remove(position);
                            ((CartActivity)context).setTotalPrice();
                        }
                        Toast.makeText(context,"Removed from Cart",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
                }

            }
            @Override
            protected void VError(VolleyError error, String tag) {
                progressDialog.hide();
                Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
            }
            //For testing:-
            //  }.getPOSTJsonObject("http://applop.biz/merchant/api/submitBook.php", "post_user", paramsBooking);
        }.getPOSTJsonObject("http://applop.biz/merchant/api/removeFromCart.php", "remove from cart", paramsBooking);
    }

    private void addToCart(final Story item,User user,final ViewHolder holder){
        final HashMap<String, String> paramsBooking = new HashMap<String, String>();
        paramsBooking.put("userEmail", user.email);
        paramsBooking.put("storyId", item.postId);
        paramsBooking.put("quantity","1");
        final ProgressDialog progressDialog = new ProgressDialog(context);
        new VolleyData(context){
            @Override
            protected void VPreExecute() {

                progressDialog.setTitle("Adding");
                progressDialog.show();
            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                progressDialog.hide();
                try {
                    if (response.getBoolean("status")){
                        databaseHelper.addToBookmarked(item);
                        holder.cart_text_view.setText("Remove");
                        Toast.makeText(context,"Added to Cart",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
                }

            }
            @Override
            protected void VError(VolleyError error, String tag) {
                progressDialog.hide();
                Toast.makeText(context,"Error : Please try again",Toast.LENGTH_LONG).show();
            }
            //For testing:-
            //  }.getPOSTJsonObject("http://applop.biz/merchant/api/submitBook.php", "post_user", paramsBooking);
        }.getPOSTJsonObject("http://applop.biz/merchant/api/addToCart.php", "add to cart", paramsBooking);
    }


    @Override
    public int getItemCount() {
        /*if (isLoadingMoreData)
            return data.size()+1;
        else*/
            return data.size();
    }

    public void removeMoreLoading(){
        if (isLoadingMoreData) {
            isLoadingMoreData = false;
            try {
                //notifyItemRemoved(data.size());
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
       LinearLayout cart_ll;
       RelativeLayout priceAndQuantity;
       TextView cart_text_view;
       ImageView playIcon;
       TextView quantityTV;
       CircleImageView positiveImage;
       CircleImageView negativeImage;
       ImageView share_image_view;
       ImageView cart_image_view;
       ImageView enquiry_image_view;
       ImageView book_image_view;
        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            if (viewType==0) {
                share_tv = (LinearLayout) itemView.findViewById(R.id.share_ll);
                playIcon = (ImageView) itemView.findViewById(R.id.playIcon);
                quantityTV = (TextView) itemView.findViewById(R.id.quantityTV);
                positiveImage = (CircleImageView) itemView.findViewById(R.id.positiveImage);
                negativeImage = (CircleImageView) itemView.findViewById(R.id.negativeImage);
                priceAndQuantity = (RelativeLayout) itemView.findViewById(R.id.priceAndQuantity);
                cart_ll = (LinearLayout) itemView.findViewById(R.id.add_to_cart_ll);
                enquiry_tv = (LinearLayout) itemView.findViewById(R.id.enquiry_ll);
                book_tv = (LinearLayout) itemView.findViewById(R.id.book_ll);
                share_image_view = (ImageView) itemView.findViewById(R.id.share_image);
                cart_image_view = (ImageView) itemView.findViewById(R.id.add_to_cart_image);
                enquiry_image_view = (ImageView) itemView.findViewById(R.id.enquiry_image);
                book_image_view = (ImageView) itemView.findViewById(R.id.book_image);
                titleName = (TextView) itemView.findViewById(R.id.titleName);
                cart_text_view = (TextView) itemView.findViewById(R.id.add_to_cart);
                homeFeedPostTime = (TextView) itemView.findViewById(R.id.homeFeedPostTime);
                coverImageView = (ImageView) itemView.findViewById(R.id.coverImageView);
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
            //notifyItemInserted(data.size());
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
        if (position==data.size())
            return -1;
        else
            return 0;
    }
}
