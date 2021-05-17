package com.pingvi.gazdam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class WelcomeActivity extends Activity {
    WebView web;
    private final  String TAG= "WelcomeApp";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_welcome);
        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "";
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);

                    }
                });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                    }
                });


        web = findViewById(R.id.webView);
        if (getStatusShow("noShowWelcome") != 0) {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            this.finish();
        }
        int nightModeFlags = getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        if (getStatusShow("darkMode") != nightModeFlags||savedInstanceState == null)
        {
            web.getSettings().setJavaScriptEnabled(true);
            web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            web.getSettings().setBuiltInZoomControls(true);
            web.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }
            });
            web.loadUrl(getResources().getString(R.string.welcomePage));
        }

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("gazdam_lasturl" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("darkMode", nightModeFlags);
        editor.apply();

       MaterialButton close = findViewById(R.id.btn_close);

        MaterialCheckBox checkBox = findViewById(R.id.check);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked())
                {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("gazdam_lasturl" , Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("noShowWelcome", 1);
                    editor.apply();
                }
                Intent main = new Intent(view.getContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }



    @Override
    public void onBackPressed() {
        if(web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }
    public Integer getStatusShow(String type)
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("gazdam_lasturl" , Context.MODE_PRIVATE);
        Integer noShow = sharedPreferences.getInt(type, 0);
        return noShow;
    }
}
