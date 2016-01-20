package com.applop.demo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applop.demo.R;
import com.applop.demo.helperClasses.User;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends Activity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "MainActivity";
    private static final int PROFILE_PIC_SIZE = 400;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;

    //for database

    Context context;
    String name;
    String email;
    Bitmap bitmap;
    Toolbar toolbar;
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private ProgressDialog mConnectionProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        context =this;

        //check if cache memory is null
        User user = User.getInstance(this);
      boolean b=  user!=null;
        if (b) {
            Intent i=new Intent(MainActivity.this,GoogleAccountActivity.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(),"has cache ",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Candidate is not login",Toast.LENGTH_LONG).show();
        }
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

        // Initializing google plus api client
        btnSignIn.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        getProfileInformation();
        mSignInClicked = true;
        //resolveSignInError();
        //to store in data base
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(People.LoadPeopleResult loadPeopleResult) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                if (currentPerson != null) {
                    name = currentPerson.getDisplayName();
                    final String imageUrl = currentPerson.getImage().getUrl();
                    Uri uri = Uri.parse(imageUrl);
                    //req
                    email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    User.setUser(context, email, name, "google", imageUrl);
                    /*ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context, uri);
                    ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
                        public void onCompleted(ImageResponse response) {
                            bitmap = response.getBitmap();
//                            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                            User.setUser(context, email, name, "google", imageUrl);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }).build();
                    ImageDownloader.downloadAsync(request);*/
                } else {
                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signInWithGplus() {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        //to store in data base
//        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
//            @Override
//            public void onResult(People.LoadPeopleResult loadPeopleResult) {
//                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//                if (currentPerson != null) {
//                    name = currentPerson.getDisplayName();
//                    final String imageUrl = currentPerson.getImage().getUrl();
//                    Uri uri = Uri.parse(imageUrl);
//                    //req
//                    User.setUser(context, email, name, "google", imageUrl);
//                    email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//                    ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context, uri);
//                    ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
//                        public void onCompleted(ImageResponse response) {
//                            bitmap = response.getBitmap();
//                            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
//                            User.setUser(context, email, name, "google", imageUrl);
//                            setResult(RESULT_OK);
//                            finish();
//                        }
//                    }).build();
//                    ImageDownloader.downloadAsync(request);
//                } else {
//                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        updateUI(true);
//
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
           /* btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);*/
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
           /* btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);*/
        }

    }
public void getProfileInformation(){
    try {
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
           Intent i= new Intent(this,GoogleAccountActivity.class);
            startActivity(i);

        } else {

        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

        updateUI(true);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {


        //gplus login
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }
        if (!mIntentInProgress) {

            mConnectionResult = result;

            if (mSignInClicked) {

                resolveSignInError();
            }
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        signInWithGplus();
    }

}
