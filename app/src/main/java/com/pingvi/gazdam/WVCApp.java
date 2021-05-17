package com.pingvi.gazdam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WVCApp extends WebViewClient {
    Context context;
    ProgressBar progress;
    public WVCApp(Context _context,ProgressBar _progress)
    {
        this.context = _context;
        progress = _progress;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if(request.getUrl().getLastPathSegment()!=null)
        {
            if(request.getUrl().getLastPathSegment().contains(".pdf"))
            view.loadUrl("https://docs.google.com/viewer?url=" + request.getUrl());
        }
        if(request.getUrl().getHost().endsWith( context.getResources().getString(R.string.short_uri))) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
        view.getContext().startActivity(intent);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        progress.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progress.setVisibility(View.GONE);
    }


}
