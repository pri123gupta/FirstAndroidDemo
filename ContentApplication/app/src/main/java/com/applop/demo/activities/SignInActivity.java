package com.applop.demo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;
import com.applop.demo.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    Context context;
    String name;
    String email;
    Bitmap bitmap;
    Toolbar toolbar;
    final  String userDetailURL = "http://applop.biz/merchant/api/getUserByEmail.php?email=";
    final  String submitUserDetailURL = "http://applop.biz/merchant/api/submitUserTable.php";
    private static final String TAG = "ExampleActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    private boolean mIntentInProgress;
    private ProgressDialog mConnectionProgressDialog;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    // private ConnectionResult mConnectionResult;

    Button signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_sign_in);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null) {
            Toast.makeText(this, token.getToken(), Toast.LENGTH_LONG).show();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");
        Helper.setToolbarColor(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button loginButton = (Button) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");

                        final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    final JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                final ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setTitle("Loading");
                                progressDialog.show();
                                try {
                                    name = object.getString("name");
                                    email = object.getString("email");
                                    new VolleyData(context){
                                        @Override
                                        protected void VPreExecute() {

                                        }

                                        @Override
                                        protected void VResponse(JSONObject response, String tag) {
                                            try {
                                                JSONArray userInfo = response.getJSONArray("UserInfo");
                                                if (userInfo.length()==0){
                                                    final HashMap<String, String> params = new HashMap<String, String>();
                                                    params.put("email", email);
                                                    Toast.makeText(context,"length = 0",Toast.LENGTH_SHORT).show();
                                                    params.put("name", name);
                                                    params.put("address", "");
                                                    params.put("phoneNumber", "");
                                                    params.put("packageName",getPackageName());
                                                    params.put("photoLink",ImageRequest.getProfilePictureUri(object.getString("id"), 100, 100).toString());
                                                    new VolleyData(context){
                                                        @Override
                                                        protected void VPreExecute() {

                                                        }

                                                        @Override
                                                        protected void VResponse(JSONObject response, String tag) {
                                                            progressDialog.hide();
                                                            try {
                                                                ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context, ImageRequest.getProfilePictureUri(object.getString("id"), 100, 100));
                                                                ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
                                                                    public void onCompleted(ImageResponse response) {
                                                                        bitmap = response.getBitmap();
                                                                        Toast.makeText(context, "Signed In", Toast.LENGTH_LONG).show();
                                                                        try {
                                                                            progressDialog.hide();
                                                                            User.setUser(context, email, name, NameConstant.LOGIN_TYPE_FACEBOOK, bitmap, ImageRequest.getProfilePictureUri(object.getString("id"), 100, 100).toString(), "", "");
                                                                            setResult(RESULT_OK);
                                                                            finish();
                                                                        } catch (Exception ex) {
                                                                            progressDialog.hide();
                                                                            Toast.makeText(context, "1 :" + ex.getMessage(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                }).build();
                                                                ImageDownloader.downloadAsync(request);
                                                            }catch (Exception ex){
                                                                Toast.makeText(context,1+ex.getMessage(),Toast.LENGTH_LONG).show();
                                                                progressDialog.hide();
                                                            }
                                                        }

                                                        @Override
                                                        protected void VError(VolleyError error, String tag) {
                                                            Toast.makeText(context,1+error.getMessage(),Toast.LENGTH_LONG).show();
                                                            progressDialog.hide();
                                                        }
                                                    }.getPOSTJsonObject(submitUserDetailURL,"post_user",params);
                                                }else {
                                                    progressDialog.hide();
                                                    final String address = userInfo.getJSONObject(0).getString("address");
                                                    final String phoneNumber = userInfo.getJSONObject(0).getString("phoneNumber");
                                                    ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context, ImageRequest.getProfilePictureUri(object.getString("id"), 100, 100));
                                                    ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
                                                        public void onCompleted(ImageResponse response) {
                                                            bitmap = response.getBitmap();
                                                            Toast.makeText(context, "Signed In", Toast.LENGTH_LONG).show();
                                                            try {
                                                                progressDialog.hide();
                                                                User.setUser(context, email, name, NameConstant.LOGIN_TYPE_FACEBOOK, bitmap, ImageRequest.getProfilePictureUri(object.getString("id"), 100, 100).toString(), address, phoneNumber);
                                                                setResult(RESULT_OK);
                                                                finish();
                                                            } catch (Exception ex) {
                                                                progressDialog.hide();

                                                            }
                                                        }
                                                    }).build();
                                                    ImageDownloader.downloadAsync(request);
                                                }
                                            }catch (Exception ex){
                                                Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
                                                progressDialog.hide();
                                                ex.printStackTrace();
                                            }
                                        }

                                        @Override
                                        protected void VError(VolleyError error, String tag) {
                                            try {
                                                progressDialog.hide();
                                                ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context, ImageRequest.getProfilePictureUri(object.getString("id"), 100, 100));
                                                ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
                                                    public void onCompleted(ImageResponse response) {
                                                        bitmap = response.getBitmap();
                                                        Toast.makeText(context, "Signed In", Toast.LENGTH_LONG).show();
                                                        try {
                                                            progressDialog.hide();
                                                            User.setUser(context, email, name, NameConstant.LOGIN_TYPE_FACEBOOK, bitmap, ImageRequest.getProfilePictureUri(object.getString("id"), 100, 100).toString(), "", "");
                                                            setResult(RESULT_OK);
                                                            finish();
                                                        } catch (Exception ex) {
                                                            progressDialog.hide();
                                                            Toast.makeText(context, "1 :" + ex.getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }).build();
                                                ImageDownloader.downloadAsync(request);
                                            }catch (Exception ex){
                                                Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
                                                ex.printStackTrace();
                                            }
                                        }
                                    }.getJsonObject(userDetailURL + email+"&packageName="+getPackageName(), true, "userDetail", context);

                                } catch (Exception ex) {
                                    progressDialog.hide();
                                    Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
                                    ex.printStackTrace();
                                }
                                Log.v("LoginActivity", response.toString());
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SignInActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(SignInActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile","email"));
            }
        });

    /*    Profile profile = Profile.getCurrentProfile();
        String firstName = profile.getFirstName();
        String photoURL = String.valueOf(profile.getProfilePictureUri(20, 20));*/

        signIn = (Button)findViewById(R.id.sign_in_button);
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                            @Override
                            public void onResult(People.LoadPeopleResult loadPeopleResult) {
                                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                                if (currentPerson != null) {
                                    name = currentPerson.getDisplayName();
                                    final String imageUrl = currentPerson.getImage().getUrl();
                                    final Uri uri = Uri.parse(imageUrl);
                                    email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                                    final ProgressDialog progressDialog = new ProgressDialog(context);
                                    progressDialog.setTitle("Loading");
                                    progressDialog.show();
                                    new VolleyData(context){
                                        @Override
                                        protected void VPreExecute() {

                                        }

                                        @Override
                                        protected void VResponse(JSONObject response, String tag) {
                                            try {
                                                JSONArray userInfo = response.getJSONArray("UserInfo");
                                                if (userInfo.length()==0){
                                                    final HashMap<String, String> params = new HashMap<String, String>();
                                                    params.put("email", email);
                                                    Toast.makeText(context,"length = 0",Toast.LENGTH_SHORT).show();
                                                    params.put("name", name);
                                                    params.put("address", "");
                                                    params.put("phoneNumber", "");
                                                    params.put("packageName",getPackageName());
                                                    params.put("photoLink",uri.toString());
                                                    new VolleyData(context){
                                                        @Override
                                                        protected void VPreExecute() {

                                                        }

                                                        @Override
                                                        protected void VResponse(JSONObject response, String tag) {
                                                            progressDialog.hide();
                                                            try {
                                                                ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context,uri);
                                                                ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
                                                                    public void onCompleted(ImageResponse response) {
                                                                        bitmap = response.getBitmap();
                                                                        Toast.makeText(context, "Signed In", Toast.LENGTH_LONG).show();
                                                                        try {
                                                                            progressDialog.hide();
                                                                            User.setUser(context, email, name, NameConstant.LOGIN_TYPE_FACEBOOK, bitmap, uri.toString(), "", "");
                                                                            setResult(RESULT_OK);
                                                                            finish();
                                                                        } catch (Exception ex) {
                                                                            progressDialog.hide();
                                                                            Toast.makeText(context, "1 :" + ex.getMessage(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                }).build();
                                                                ImageDownloader.downloadAsync(request);
                                                            }catch (Exception ex){
                                                                Toast.makeText(context,1+ex.getMessage(),Toast.LENGTH_LONG).show();
                                                                progressDialog.hide();
                                                            }
                                                        }

                                                        @Override
                                                        protected void VError(VolleyError error, String tag) {
                                                            Toast.makeText(context,1+error.getMessage(),Toast.LENGTH_LONG).show();
                                                            progressDialog.hide();
                                                        }
                                                    }.getPOSTJsonObject(submitUserDetailURL,"post_user",params);
                                                }else {
                                                    progressDialog.hide();
                                                    final String address = userInfo.getJSONObject(0).getString("address");
                                                    final String phoneNumber = userInfo.getJSONObject(0).getString("phoneNumber");
                                                    ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context, uri);
                                                    ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
                                                        public void onCompleted(ImageResponse response) {
                                                            bitmap = response.getBitmap();
                                                            Toast.makeText(context, "Signed In", Toast.LENGTH_LONG).show();
                                                            try {
                                                                progressDialog.hide();
                                                                User.setUser(context, email, name, NameConstant.LOGIN_TYPE_FACEBOOK, bitmap, uri.toString(), address, phoneNumber);
                                                                setResult(RESULT_OK);
                                                                finish();
                                                            } catch (Exception ex) {
                                                                progressDialog.hide();

                                                            }
                                                        }
                                                    }).build();
                                                    ImageDownloader.downloadAsync(request);
                                                }
                                            }catch (Exception ex){
                                                Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
                                                progressDialog.hide();
                                                ex.printStackTrace();
                                            }
                                        }

                                        @Override
                                        protected void VError(VolleyError error, String tag) {
                                            try {
                                                progressDialog.hide();
                                                ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context, uri);
                                                ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
                                                    public void onCompleted(ImageResponse response) {
                                                        bitmap = response.getBitmap();
                                                        Toast.makeText(context, "Signed In", Toast.LENGTH_LONG).show();
                                                        try {
                                                            progressDialog.hide();
                                                            User.setUser(context, email, name, NameConstant.LOGIN_TYPE_FACEBOOK, bitmap, uri.toString(), "", "");
                                                            setResult(RESULT_OK);
                                                            finish();
                                                        } catch (Exception ex) {
                                                            progressDialog.hide();

                                                        }
                                                    }
                                                }).build();
                                                ImageDownloader.downloadAsync(request);
                                            }catch (Exception ex){
                                                Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
                                                ex.printStackTrace();
                                            }
                                        }
                                    }.getJsonObject(userDetailURL + email + "&packageName=" + getPackageName(), true, "userDetail", context);

                                }else{
                                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        if (result.hasResolution()) {
                            try {
                                result.startResolutionForResult((SignInActivity)context, // your activity
                                        RC_SIGN_IN);
                                //startIntentSenderForResult(result.getResolution().getIntentSender(),
                                  //      RC_SIGN_IN, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) {
                                // The intent was canceled before it was sent.  Return to the default
                                // state and attempt to connect to get an updated ConnectionResult.
                                mGoogleApiClient.connect();
                            }
                        }
                    }
                })
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.EMAIL))
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.PLUS_ME))
                .build();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleApiClient.connect();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enquiry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
