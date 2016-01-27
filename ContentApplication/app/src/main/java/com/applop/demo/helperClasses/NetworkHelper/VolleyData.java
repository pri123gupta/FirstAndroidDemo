package com.applop.demo.helperClasses.NetworkHelper;

import android.content.Context;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.applop.demo.helperClasses.Helper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ram on 5/5/15.
 */
public abstract class VolleyData {
    private static final int MY_SOCKET_TIMEOUT_MS = 7*24 * 60 * 60 * 1000;
    private Context _activity;

    protected abstract void VPreExecute();
    protected abstract void VResponse(JSONObject response, String tag);
    protected abstract void VError(VolleyError error, String tag);


    public VolleyData(Context instance) {
        _activity = instance;
        VPreExecute();
    }

    public void getJsonObject(final String _url, final Boolean _cache_response, final String  _tag, final Context context) {
        if(!NetworkHelper.isNetworkAvailable(_activity)) {
                try {
                    String response = Helper.getCachedDataForUrl(_url, context);
                    Log.v("chache", "returned cache data");
                    VResponse(new JSONObject(response), _tag);
                    return;
                } catch (JSONException e) {
                    Log.v("chache", "error in cache data");
                    VError(new VolleyError(), _tag);
                    e.printStackTrace();
                }

        } else {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    _url, (String) null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //MyLog.d(_tag, " volley response Response.Listener >>" + response);
                            Log.e("response", "returned response data");
                            Helper.saveCacheDataForUrl(_url,response.toString(),context);
                            VResponse(response, _tag);
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //MyLog.d(_tag, " volley Error: " + error.getMessage());
                            // VolleyLog.d(_tag, "Error: " + error.getMessage());
                            String response = Helper.getCachedDataForUrl(_url, context);

                                try {
                                    Log.v("chache", "returned cache data");
                                    VResponse(new JSONObject(response), _tag);
                                    return;
                                } catch (JSONException e) {
                                    Log.v("chache", "error in cache data");
                                    VError(error, _tag);
                                    e.printStackTrace();
                                }

                        }
                    }
            );
            jsonObjReq.setShouldCache(_cache_response);
            jsonObjReq.setTag(_tag);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyRequestQueue.Instance(_activity).addToRequestQueue(jsonObjReq, _url);
        }
    }

    public void getFreshJSONObject(String _url, Boolean _cache_response, final String  _tag) {
        String cached_response = MyRequestQueue.Instance(_activity).getCache(Request.Method.GET,_url);
        if(!NetworkHelper.isNetworkAvailable(_activity) && cached_response != null) {
            JSONObject response = null;
            try {
                response = new JSONObject(cached_response);
            } catch (JSONException e) {

            }
            VResponse(response, _tag);
        } else {
            MyRequestQueue.Instance(_activity).getRequestQueue().getCache().remove(_url);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    _url, (String) null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            //MyLog.d(_tag, " volley response Response.Listener >>" + response);
                            VResponse(response, _tag);

                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VError(error, _tag);
                        }
                    }
            );
            jsonObjReq.setShouldCache(true);
            jsonObjReq.setTag(_tag);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyRequestQueue.Instance(_activity).addToRequestQueue(jsonObjReq, _url);
        }
    }

    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
        final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }

    public void getPOSTJsonObject(final String _url, final String  _tag,  final HashMap<String,String>  params) {


        final CustomVolleyRequest jsonObjReq = new CustomVolleyRequest(Request.Method.POST,
                _url,params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //MyLog.d(_tag, " volley response Response.Listener >>" + response);
                        VResponse(response, _tag);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //MyLog.d(_tag, " volley Error: " + error.getMessage());
                        // VolleyLog.d(_tag, "Error: " + error.getMessage());
                        VError(error, _tag);
                    }
                })

            {
                @Override
                public Map<String, String> getParams() {
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "key=ce2f2fa3-2edd-11e5-9bc9-002590f371ee");
                    headers.put("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded";
                }
            };


            jsonObjReq.setShouldCache(false);
            jsonObjReq.setTag(_tag);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyRequestQueue.Instance(_activity).addToRequestQueue(jsonObjReq, _url);
    }

}
