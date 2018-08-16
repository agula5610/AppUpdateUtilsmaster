package com.luxiaochun.appupdateutils_master;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.luxiaochun.appupdateutils.AppUpdateManager;
import com.luxiaochun.appupdateutils.http.OkGoUpdateHttpUtil;

public class MainActivity extends AppCompatActivity {
    Button normal;
    Button silence;
    Button force;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        normal = findViewById(R.id.normal);
        silence = findViewById(R.id.silence);
        force = findViewById(R.id.force);
        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AppUpdateManager
                        .Builder()
                        //当前Activity
                        .setActivity(MainActivity.this)
                        //更新地址
                        .setUpdateUrl("http://gdown.baidu.com/data/wisegame/e8235a956b670f0e/baiduwangpan_610.apk")
                        //实现httpManager接口的对象
                        .setHttpManager(new OkGoUpdateHttpUtil())
                        .setNewVersion("1.0")
                        .build()
                        .update();
            }
        });
        silence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AppUpdateManager
                        .Builder()
                        //当前Activity
                        .setActivity(MainActivity.this)
                        //更新地址
                        .setUpdateUrl("http://gdown.baidu.com/data/wisegame/e8235a956b670f0e/baiduwangpan_610.apk")
                        //实现httpManager接口的对象
                        .setHttpManager(new OkGoUpdateHttpUtil())
                        .setNewVersion("1.0")
                        .build()
                        .silenceUpdate();
            }
        });
        force.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AppUpdateManager
                        .Builder()
                        //当前Activity
                        .setActivity(MainActivity.this)
                        //更新地址
                        .setUpdateUrl("http://gdown.baidu.com/data/wisegame/e8235a956b670f0e/baiduwangpan_610.apk")
                        //实现httpManager接口的对象
                        .setHttpManager(new OkGoUpdateHttpUtil())
                        .setNewVersion("1.0")
                        .build()
                        .forceUpdate();
            }
        });
    }
}
