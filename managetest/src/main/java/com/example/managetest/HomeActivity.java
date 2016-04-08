package com.example.managetest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.MyBaseAdapter.MyBaseAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @ViewInject(R.id.gridView)
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ViewUtils.inject(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |     WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        initView();
    }

    public void initView() {
        List<String> functions = new ArrayList<String>();
        functions.add("手机防盗");
        functions.add("通讯管理");
        functions.add("软件管理");
        functions.add("进程管理");
        functions.add("流量统计");
        functions.add("手机杀毒");
        functions.add("缓存管理");
        functions.add("高级工具");
        functions.add("设置中心");
        MyAdapter myAdapter = new MyAdapter(functions,this);
        gridView.setAdapter(myAdapter);
        SetGirdViewItemClickListener();
    }

    class MyAdapter extends MyBaseAdapter<String> {
        private MyAdapter(List<String> lists, Context context) {
            super(lists, context);
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(HomeActivity.this, R.layout.home_gridview, null);
                holder.tv =  (TextView) convertView.findViewById(R.id.tv_girdview);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(lists.get(position));
            return convertView;
        }
    }

   static class ViewHolder {
        TextView tv;
    }

    /***
     * 设置点击监听
     */
    public void SetGirdViewItemClickListener()
    {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 8:
                        startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
                        break;

                }

            }
        });

    }

}
