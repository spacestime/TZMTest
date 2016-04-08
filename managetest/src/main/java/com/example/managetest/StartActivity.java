package com.example.managetest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartActivity extends AppCompatActivity {

    @ViewInject(R.id.updateText)
    private TextView updateText;

    private SharedPreferences config;
    private int versionCode;
    private String versionNameSer;
    private String description;
    private String downloadURL;


    private HttpHandler handler;
    private AlertDialog.Builder builder;
    private HttpUtils http;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ViewUtils.inject(this);
        initUI();

    }

    private void initUI() {
        //获取服务器版本信息
        http = new HttpUtils();
        updateText.setVisibility(View.INVISIBLE);   //设置文字隐藏
        config = getSharedPreferences("config", MODE_PRIVATE);
        boolean isUpdate = config.getBoolean("isUpdate", true);
        if (isUpdate) {
            checkVersion();
        }else
        {
            showHome();
        }

    }

    /**
     * 检查版本号
     */
    public void checkVersion() {
        try {
            //获取版本号
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        String url = "http://10.0.2.2:5566/version.json";
        String sdurl = Environment.getExternalStorageDirectory().getPath() + "/version.json";
        handler = http.download(url,
                sdurl,
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {
                    public void onStart() {
                        // testTextView.setText("conn...");
                    }

                    public void onLoading(long total, long current, boolean isUploading) {
                        // testTextView.setText(current + "/" + total);
                        //updateText.setText("正确下载..." + current / total * 100 + "%");
                    }

                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        StringBuilder stringBuilder = new StringBuilder();
                        try {
                            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(responseInfo.result)));
                            String line;
                            while ((line = bf.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject json = new JSONObject(stringBuilder.toString());

                            int versionCodeSer = json.getInt("VersionCode");
                            updateText.setText("SD:" + versionCodeSer);
                            if (versionCode < versionCodeSer) {
                                versionNameSer = json.getString("VersionName");
                                description = json.getString("Description");
                                downloadURL = json.getString("DownloadURL");
                                showSelecteUpdateView();
                            } else {
                                // updateText.setText("ddddd:"+versionCodeSer+"x" +versionNameSer+description+downloadURL);
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    public void onFailure(HttpException error, String msg) {
                        //testTextView.setText(msg);
                        showHome();
                    }
                });
    }


    private void showSelecteUpdateView() {
        builder = new AlertDialog.Builder(this);

        builder.setTitle("最新版本" + versionNameSer);

        builder.setMessage(description);

        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UpdateProj(downloadURL);
            }
        });

        builder.setNegativeButton("忽略", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showHome();
            }
        });

        builder.show();

    }

    private void UpdateProj(String url) {

        String sdurl = Environment.getExternalStorageDirectory().getPath() + "/update.apk";
        // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
        // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。

        handler = http.download(url,
                sdurl,
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    public void onStart() {
                        // testTextView.setText("conn...");
                    }

                    public void onLoading(long total, long current, boolean isUploading) {
                        // testTextView.setText(current + "/" + total);
                        updateText.setText("正在下载..." + current / total * 100 + "%");
                    }

                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        // testTextView.setText("downloaded:" + responseInfo.result.getPath());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setDataAndType(Uri.fromFile(responseInfo.result),
                                "application/vnd.android.package-archive");
                        startActivityForResult(intent, 0);

                    }

                    public void onFailure(HttpException error, String msg) {
                        //testTextView.setText(msg);
                        showHome();
                    }
                });
    }

    public void showHome() {
        startActivity(new Intent(StartActivity.this, HomeActivity.class));
        finish();
    }

    /***
     * 销毁数据时处理打开的资源
     */
    protected void onDestroy() {
        super.onDestroy();

        if (builder != null) {
            builder = null;
        }
        if (handler.isCancelled() == false) {
            handler.cancel();
        }
        if (http != null) {
            http = null;
        }
    }

}
