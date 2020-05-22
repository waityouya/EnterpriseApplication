package com.example.enterpriseapplication.ui.activitys;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;

import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.enterpriseapplication.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.enterpriseapplication.model.ReturnLoginInfo;
import com.example.enterpriseapplication.model.User;
import com.example.enterpriseapplication.util.Contract;
import com.example.enterpriseapplication.util.GlobalHandler;
import com.example.enterpriseapplication.util.JsonUtils;
import com.example.enterpriseapplication.util.LoadingDailogUtil;
import com.example.enterpriseapplication.util.LogUtil;
import com.example.enterpriseapplication.util.MD5Util;

import com.example.enterpriseapplication.util.OkHttpUtils;
import com.example.enterpriseapplication.util.RSAUtils;
import com.example.enterpriseapplication.util.ToastUtil;
import com.google.gson.Gson;
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GlobalHandler.HandleMsgListener{



    private EditText mUserNameEditText;
    private EditText mPassWordEditText;
    private Button mLoginButton;

    private GlobalHandler mHandler;
    private String [] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE};
    private List<String> mPermissionList = new ArrayList<>();
    private String mUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initWidget();
        setListen();
        initPermission();
    }

    private void initWidget(){
        mHandler = GlobalHandler.getInstance();
        mHandler.setHandleMsgListener(this);
        mUserNameEditText = findViewById(R.id.et_username);
        mPassWordEditText = findViewById(R.id.et_password);
        mLoginButton = findViewById(R.id.login);


    }

    private void setListen(){
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mUserNameEditText.getText().toString();
                mUserName =userName;
                String passWord = mPassWordEditText.getText().toString();
                if(userName.isEmpty() || passWord.isEmpty()){
                    ToastUtil.showShortToast("请填写完整");
                }else {
                    LoadingDailogUtil.showLoadingDialog(LoginActivity.this,"验证中...");
                    String passWordMd5 = MD5Util.MD5(passWord);
                    OkHttpUtils okHttpUtils = new OkHttpUtils();
                    User user = new User(userName,passWordMd5);
                    String loginJson = JsonUtils.conversionJsonString(user);

                    try {
                        String publicEncryptJson = RSAUtils.publicEncrypt(loginJson,RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
                        okHttpUtils.postInfo(Contract.SERVER_ADDRESS+"EnterpriseLogin",publicEncryptJson);

                    }catch (Exception e){
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShortToast("未知错误");
                                LoadingDailogUtil.cancelLoadingDailog();

                            }

                        });
                    }



//
                }
            }
        });



    }
    @Override
    public void handleMsg(Message msg) {
        switch (msg.what){
            case 0:
                ToastUtil.showShortToast("网络出错，请检查网络");
                LoadingDailogUtil.cancelLoadingDailog();
                break;
            case 1:
                ReturnLoginInfo returnLoginInfo = new Gson().fromJson(msg.getData().getString("backInfo"),ReturnLoginInfo.class);
                switch (returnLoginInfo.getCode()){
                    case "ok":
                        Intent intent = new Intent(LoginActivity.this,UserActivity.class);
                        //Log.i("loginsucess",String.valueOf(returnLoginInfo.getData().getUid()));
                        intent.putExtra("userId",returnLoginInfo.getData().getUserId());
                        intent.putExtra("userName",mUserName);
                        //intent.putExtra("token")
                        //token保存到本地
                        SharedPreferences sp = getSharedPreferences("login", 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("token",returnLoginInfo.getData().getAppToken());
                        editor.putString("userId",returnLoginInfo.getData().getUserId());
                        editor.putBoolean("isTokenValid",true);
                        editor.commit();
                        LoadingDailogUtil.cancelLoadingDailog();
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.i("111");
                                ToastUtil.showShortToast("账号或密码错误");
                                LoadingDailogUtil.cancelLoadingDailog();

                            }

                        });


                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁移除所有消息，避免内存泄露
        mHandler.removeCallbacks(null);
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

