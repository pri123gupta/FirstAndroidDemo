package com.applop.demo.classes;

import android.content.Context;

import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;

/**
 * Created by narayan on 28/8/15.
 */
public class MyRequestQueue {

    Context context;
    private RequestQueue mRequestQueue;
    static MyRequestQueue myRequestQueue;
    protected static final String TAG = "AllRequest";

    public static MyRequestQueue Instance(Context context) {
        if (myRequestQueue==null){
            return new MyRequestQueue(context);
        }
        return myRequestQueue;
    }

    private MyRequestQueue(Context context) {
        this.context = context;
    }

    public RequestQueue getRequestQueue() {
        try{
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(context);
            }
        }catch (Exception Ex){

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public String getCache(int method,String url) {
        String data = null;
        try {
            Cache cache = Instance(context).getRequestQueue().getCache();
            Cache.Entry entry = cache.get(method +":"+url);
            if (entry != null) {
                data = new String(entry.data, "UTF-8");
            }

        } catch (UnsupportedEncodingException e) {
            //MyLog.d(TAG, e.toString());
        } catch(OutOfMemoryError e) {
            //MyLog.d(TAG, e.toString());
        } catch(Exception e) {
            //MyLog.d(TAG, e.toString());
        }
        return data;
    }
}
