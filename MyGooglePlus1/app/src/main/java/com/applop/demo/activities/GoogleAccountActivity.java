package com.applop.demo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applop.demo.R;
import com.applop.demo.helperClasses.GoogleplusHelper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class GoogleAccountActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "MainActivity";
    private static final int PROFILE_PIC_SIZE = 400;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    Context context;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private Button btnSignOut, btnRevokeAccess;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;
    //for data base
    public String name = "";
    public String email = "";
    public String imageUrl;
    public Bitmap bitmap;
    public String loginType = "";
    static GoogleAccountActivity myclass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_google);
        context=this;
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
      mGoogleApiClient.disconnect();
             GoogleplusHelper helper=new GoogleplusHelper(context);
                helper.removeUser();
                Toast.makeText(getApplicationContext(),"Successfully logged out",Toast.LENGTH_LONG).show();
               // onBackPressed();
            }
        });
        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        getProfileInformation();
        mGoogleApiClient.connect();
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
//                    ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context, uri);
//                    ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
//                        public void onCompleted(ImageResponse response) {
//                            bitmap = response.getBitmap();
////                            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
//                            User.setUser(context, email, name, "google", imageUrl);
//                            setResult(RESULT_OK);
//                            finish();
//                        }
//                    }).build();
//                    ImageDownloader.downloadAsync(request);
                } else {
                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(GoogleAccountActivity.this, "Google Plus connected", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.disconnect();
        updateUI(true);
        signOutFromGplus();
    }
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
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
    public void getProfileInformation(){
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
Toast.makeText(getApplicationContext(),"not null",Toast.LENGTH_LONG).show();
                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);
                txtName.setText(personName);
                txtEmail.setText(email);
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
               new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
//database
//                name = currentPerson.getDisplayName();
//                final String imageUrl = currentPerson.getImage().getUrl();
//                Uri uri = Uri.parse(imageUrl);
//                //req
//                email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//                ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context, uri);
//                final String finalEmail = email;
//                ImageRequest request = requestBuilder.setAllowCachedRedirects(true).setCallerTag(this).setCallback(new ImageRequest.Callback() {
//                    public void onCompleted(ImageResponse response) {
//                        bitmap = response.getBitmap();
//                        Toast.makeText(getApplication(), "Successful", Toast.LENGTH_SHORT).show();
//                        User.setUser(context, finalEmail, name, "google", imageUrl);
//                        setResult(RESULT_OK);
//                        finish();
//                    }
//                }).build();
//                ImageDownloader.downloadAsync(request);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }}
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                            updateUI(false);
                        }

                    });
        }
    }
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateUI(false);
            Toast.makeText(getApplicationContext(),"Successfully logged out",Toast.LENGTH_LONG).show();
        }
    }
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
        }else {
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
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
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
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
}
