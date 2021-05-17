package com.pingvi.gazdam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;



public class NotificationDialogActivity extends Activity {
    private Button btn_ok;
    private  Button btn_link;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialogactivty);
        btn_ok = findViewById(R.id.btn_ok);
        btn_link = findViewById(R.id.btn_link);
        TextView textTiltle = findViewById(R.id.txt_title);
        TextView textContent = findViewById(R.id.txt_content);
        String link = getIntent().getStringExtra("link");
        if(link==null || link.isEmpty())
            btn_link.setVisibility(View.GONE);
        btn_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(link!=null && !link.isEmpty()) {
                Intent intent = new Intent(NotificationDialogActivity.this, MainActivity.class);
                intent.putExtra("linkTextView", link );
                startActivity(intent);
                finish();
               }
            }
        });
        ImageView img_view = findViewById(R.id.imgView);
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        if(link!=null)
            content = content + " " + link;
        textTiltle.setText(title);
        textContent.setText(content);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
