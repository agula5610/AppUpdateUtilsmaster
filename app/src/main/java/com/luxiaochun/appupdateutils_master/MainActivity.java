package com.luxiaochun.appupdateutils_master;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.luxiaochun.appupdateutils.AppUpdateManager;
import com.luxiaochun.appupdateutils.common.UpdateType;

public class MainActivity extends AppCompatActivity {
    Button normal;
    Button silence;
    Button force;
    Button tink;
    Button download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        normal = findViewById(R.id.normal);
        silence = findViewById(R.id.silence);
        force = findViewById(R.id.force);
        tink = findViewById(R.id.tink);
        download = findViewById(R.id.download);
        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AppUpdateManager
                        .Builder()
                        .setThemeColor(0xff16a9ff)
                        .setContext(MainActivity.this)
                        .setUrl("http://gdown.baidu.com/data/wisegame/e8235a956b670f0e/baiduwangpan_610.apk")
                        .setVersion("1.0")
                        .setNotes("人这一辈子，是有三次成长的。一是当你意识到这个世界上有些事并不会按照你的意愿来发展的时候，二是无论你怎么努力都会被怀疑嘲讽的时候，三是当你知道不会成功还会勇往直前的时候。")
                        .setTitle("App更新历险记")
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
                        .setContext(MainActivity.this)
                        .setType(UpdateType.Slience)
                        //更新地址
                        .setUrl("http://gdown.baidu.com/data/wisegame/e8235a956b670f0e/baiduwangpan_610.apk")
                        //实现httpManager接口的对象
                        .setVersion("1.0")
                        .setNotes("人这一辈子，是有三次成长的。一是当你意识到这个世界上有些事并不会按照你的意愿来发展的时候，二是无论你怎么努力都会被怀疑嘲讽的时候，三是当你知道不会成功还会勇往直前的时候。")
                        .setTitle("App更新历险记")
                        .build()
                        .update();
            }
        });
        force.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AppUpdateManager
                        .Builder()
                        //当前Activity
                        .setContext(MainActivity.this)
                        .setType(UpdateType.Force)
                        //更新地址
                        .setUrl("http://gdown.baidu.com/data/wisegame/e8235a956b670f0e/baiduwangpan_610.apk")
                        //实现httpManager接口的对象
                        .setVersion("1.0")
                        .setNotes("人这一辈子，是有三次成长的。一是当你意识到这个世界上有些事并不会按照你的意愿来发展的时候，二是无论你怎么努力都会被怀疑嘲讽的时候，三是当你知道不会成功还会勇往直前的时候。")
                        .setTitle("App更新历险记")
                        .build()
                        .update();
            }
        });
        tink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUpdateManager
                        .Builder()
                        //当前Activity
                        .setContext(MainActivity.this)
                        .setType(UpdateType.Hint)
                        //更新地址
                        .setUrl("http://gdown.baidu.com/data/wisegame/e8235a956b670f0e/baiduwangpan_610.apk")
                        //实现httpManager接口的对象
                        .setVersion("1.0")
                        .setNotes("人这一辈子，是有三次成长的。一是当你意识到这个世界上有些事并不会按照你的意愿来发展的时候，二是无论你怎么努力都会被怀疑嘲讽的时候，三是当你知道不会成功还会勇往直前的时候。")
                        .setTitle("App更新历险记")
                        .build()
                        .update();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUpdateManager
                        .Builder()
                        //当前Activity
                        .setContext(MainActivity.this)
                        .setType(UpdateType.Download)
                        //更新地址
                        .setUrl("http://gdown.baidu.com/data/wisegame/e8235a956b670f0e/baiduwangpan_610.apk")
                        //实现httpManager接口的对象
                        .setVersion("1.0")
                        .setNotes("人这一辈子，是有三次成长的。一是当你意识到这个世界上有些事并不会按照你的意愿来发展的时候，二是无论你怎么努力都会被怀疑嘲讽的时候，三是当你知道不会成功还会勇往直前的时候。")
                        .setTitle("App更新历险记")
                        .build()
                        .update();
            }
        });

    }
}
