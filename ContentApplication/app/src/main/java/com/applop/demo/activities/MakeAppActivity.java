package com.applop.demo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.applop.demo.R;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;

public class MakeAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_buy_now);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        getSupportActionBar().setTitle("Make App Now");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        final WebView webView = (WebView) findViewById(R.id.webView);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        webView.loadUrl("http://applop.com/MakeAppForApp/");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_open_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
