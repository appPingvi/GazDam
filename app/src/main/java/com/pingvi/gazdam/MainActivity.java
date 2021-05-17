package com.pingvi.gazdam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    WebView web;
    ProgressBar progres;
    NotificationManager notification;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String link = getIntent().getStringExtra("linkTextView");
        notification = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        web = findViewById(R.id.webView);
        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if (getIntPref("darkMode") != nightModeFlags || savedInstanceState == null) {
            WebSettings settings = web.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setBuiltInZoomControls(true);
            settings.setUseWideViewPort (true);
            String lastUrl = getLastUrl();
            if(link!=null && !link.isEmpty())
                web.loadUrl(link);
            else if (lastUrl != null && !lastUrl.isEmpty())
                web.loadUrl(lastUrl);
            else
                web.loadUrl(getResources().getString(R.string.full_uri));
            progres = findViewById(R.id.progress);
            web.setWebViewClient(new WVCApp(this, progres));
            web.setWebChromeClient(new WebChromeClient());

        }
        saveIntPref("darkMode", nightModeFlags);

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        web.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
        this.saveLastUrl(web.getUrl());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        web.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if(web.canGoBack()) {
            web.goBack();
            progres.setVisibility(View.VISIBLE);
        } else {
            saveLastUrl(web.getUrl());
            super.onBackPressed();
        }
    }
    public void showNotification(View view)
    {

    }
    public Integer getIntPref(String type)
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("gazdam_lasturl" , Context.MODE_PRIVATE);
        Integer noShow = sharedPreferences.getInt(type, 0);
        return noShow;
    }
    public void saveIntPref(String type,Integer value)
    {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("gazdam_lasturl" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(type, value);
        editor.apply();
    }
    public String getLastUrl()
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("gazdam_lasturl" , Context.MODE_PRIVATE);
        String lastUrl = sharedPreferences.getString("lasturl", null);
        return lastUrl;
    }
    public void saveLastUrl(String url)
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("gazdam_lasturl" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lasturl", url);
        editor.apply();
    }
    public void onButtonClick(View view)
    {
        switch(view.getId())
        {
            case R.id.btn_call:
                Intent call = new Intent(Intent.ACTION_DIAL);
                String uriPhone = "tel:" + getResources().getString(R.string.phonenumber);
                call.setData(Uri.parse(uriPhone));
                startActivity(call);
                break;
            case R.id.btn_exit:
                finishAndRemoveTask();
                break;
            case R.id.btn_screen:
                this.requestRead(view);
                break;
            case R.id.btn_err:
                web.loadUrl(getResources().getString(R.string.url_err));
                break;
            case R.id.btn_www:
                String currenUrl = web.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(currenUrl)) ;
                startActivity(intent);
                break;
            case R.id.btn_WhatsAppp:
                String url = getResources().getString(R.string.whatsapplink) + getResources().getString(R.string.phonenumber);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }
    public void requestRead(View v) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            takeScreenshot();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeScreenshot();
                } else {
                    Snackbar.make(getWindow().getDecorView(), "Опс! Неудалось сохранить screenshot!", Snackbar.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            File sdcard = Environment.getExternalStorageDirectory();
            if (sdcard != null) {
                File mediaDir = new File(sdcard, "DCIM/Camera");
                if (!mediaDir.exists()) {
                    mediaDir.mkdirs();
                }
            }
            /*String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                    "GazDamScreenShot.jpg", null);*/
            ContentValues values=new ContentValues();
            values.put(MediaStore.Images.Media.TITLE,"Title");
            values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
            Uri path=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

            Snackbar.make(getWindow().getDecorView(), getResources().getString(R.string.savescreen), Snackbar.LENGTH_SHORT).show();
        } catch (Throwable e) {
            Snackbar.make(getWindow().getDecorView(), "Опс! Неудалось сохранить screenshot!", Snackbar.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}

