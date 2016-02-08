package com.applop.demo.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.applop.demo.R;
import com.applop.demo.helperClasses.Helper;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;

public class AboutUsActivity extends AppCompatActivity {
Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.getInstance(this).iconTheme.equalsIgnoreCase(NameConstant.ICON_THEME_LIGHT)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_about_us);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Helper.setToolbarColor(this);
        getSupportActionBar().setTitle("About Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (getPackageName().equalsIgnoreCase("com.applop.demo"))
            loadAboutUsDataDemo();
        else
            loadAboutUsData();
    }
    private void loadAboutUsDataDemo() {
        String webstr = "<html><head><style>@font-face {font-family:'opensans';src: url('file:///android_asset/fonts/regular.ttf';font-weight: normal);}body {font-family:'opensans';}img{PADDING-BOTTOM: 5px;max-width:100%;}</style><script>window.onload = callOnload;window.onscroll=callOnScroll;function showLoader(){document.getElementById('loader').style.display='inline'};function hideLoader(){document.getElementById('loader').style.display='none'};function toggleFont(){var d=document.getElementById('multicolumn');var d1=document.getElementById('headline');if(d.className == 'large'){d.className='';}else{d.className='large';}if(d1.className == 'large'){d1.className='title';}else{d1.className='large';}};function callOnload(){ hideLoader();}</script></head><body >";
        webstr += "<h3 style='font-family:\"opensans\";line-height:25px;text-align:justify;color:'#00000'; font-size:16;'><strong>Applop : </strong></h3>";
        webstr += "<p style='font-family:\"opensans\";line-height:25px;text-align:justify;color:'#00000'; font-size:18;'>";
        webstr += getResources().getString(R.string.our_mission)+"</p>";
        webstr += "<h3 style='font-family:\"opensans\";line-height:25px;text-align:justify;color:'#00000'; font-size:16;'><strong>How to make app?</strong></h3>";
        webstr += "<p style='font-family:\"opensans\";line-height:25px;text-align:justify;color:'#00000'; font-size:18;'>";
        webstr += getResources().getString(R.string.who_we_are)+"</p>";
        webstr += "</body></html>";
        WebView webview= (WebView) findViewById(R.id.webViewAboutUs);
        webview.getSettings().getJavaScriptEnabled();
        webview.loadDataWithBaseURL("", webstr, "text/html", "UTF-8", "");
        webview.setBackgroundColor(Color.WHITE);
    }
    private void loadAboutUsData() {
        String webstr = "<html><head><style>@font-face {font-family:'opensans';src: url('file:///android_asset/fonts/regular.ttf';font-weight: normal);}body {font-family:'opensans';}img{PADDING-BOTTOM: 5px;max-width:100%;}</style><script>window.onload = callOnload;window.onscroll=callOnScroll;function showLoader(){document.getElementById('loader').style.display='inline'};function hideLoader(){document.getElementById('loader').style.display='none'};function toggleFont(){var d=document.getElementById('multicolumn');var d1=document.getElementById('headline');if(d.className == 'large'){d.className='';}else{d.className='large';}if(d1.className == 'large'){d1.className='title';}else{d1.className='large';}};function callOnload(){ hideLoader();}</script></head><body >";
        webstr += "<p style='font-family:\"opensans\";line-height:25px;text-align:justify;color:'#00000'; font-size:18;'>";
        webstr += AppConfiguration.getInstance(this).appDescription+"</p>";
        webstr += "</body></html>";
        WebView webview= (WebView) findViewById(R.id.webViewAboutUs);
        webview.getSettings().getJavaScriptEnabled();
        webview.loadDataWithBaseURL("", webstr, "text/html", "UTF-8", "");
        webview.setBackgroundColor(Color.WHITE);
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
