package com.david.app.smartrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.david.app.cc.CC;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CC.obtainBuilder("module1")
                        .setActionName("test").setContext(MainActivity.this).build().call();
            }
        });
        CC.obtainBuilder("smart")
                .setActionName("get").setContext(this).build().call();
    }
}
