package com.example.enterpriseapplication.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.enterpriseapplication.R;
import com.example.enterpriseapplication.service.MyMqttService;
import com.example.enterpriseapplication.ui.fragments.FindFragment;
import com.example.enterpriseapplication.ui.fragments.MyFragment;
import com.example.enterpriseapplication.ui.fragments.RecordFragment;
import com.example.enterpriseapplication.util.NotifictionUtil;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private Fragment recordFragment;
    private Fragment myFragment;
    private Fragment findFragment;
    private String [] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE};
    private List<String> mPermissionList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
        initPermission();
        MyMqttService.startService(this);
    }
    private void initView(){
        AHBottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("备案",R.drawable.beian);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("查询",R.drawable.find);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("我的",R.drawable.user);


        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setAccentColor(Color.parseColor("#74B9FA"));
        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setTranslucentNavigationEnabled(true);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        //设第一个界面为默认界面
        if(recordFragment == null){
            recordFragment = RecordFragment.newInstance("1","2");
            transaction.add(R.id.content,recordFragment);
        }else {
            transaction.show(recordFragment);
        }
        transaction.commit();

        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                FragmentTransaction transaction= fragmentManager.beginTransaction();
                hideAllFragment(transaction);
                switch (position){
                    case 0:
                        if(recordFragment == null){
                            recordFragment = RecordFragment.newInstance("1","2");
                            transaction.add(R.id.content,recordFragment);
                        }else {
                            transaction.show(recordFragment);
                        }
                        break;
                    case 1:
                        if(findFragment == null){
                            findFragment = FindFragment.newInstance("1","2");
                            transaction.add(R.id.content,findFragment);
                        }else {
                            transaction.show(findFragment);
                        }
                        break;
                    case 2:
                        if(myFragment == null){
                            myFragment = MyFragment.newInstance("1","2");
                            transaction.add(R.id.content,myFragment);
                        }else {
                            transaction.show(myFragment);
                        }
                        break;


                    default:
                        break;
                }
                transaction.commit();
                return true;
            }
        });

        if(!NotifictionUtil.isNotificationEnabled(UserActivity.this)){
            NotifictionUtil.showNotificationIsFalse(UserActivity.this);
        }
    }

    //隐藏Fragment界面
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(myFragment != null){
            fragmentTransaction.hide(myFragment);
        }
        if(recordFragment != null){
            fragmentTransaction.hide(recordFragment);
        }
        if(findFragment != null){
            fragmentTransaction.hide(findFragment);
        }

    }

    private void initPermission(){
        mPermissionList.clear();
        for (int i=0;i<permissions.length;i++){
            if(ContextCompat.checkSelfPermission(this,permissions[i])!= PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(permissions[i]);
            }
        }

        //申请权限
        if(mPermissionList.size() >0){
            ActivityCompat.requestPermissions(this,permissions,1);
        }
    }
}
