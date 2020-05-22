package com.example.enterpriseapplication.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import com.example.enterpriseapplication.R;
import com.example.enterpriseapplication.model.Case;
import com.example.enterpriseapplication.model.ReturnLoginInfo;
import com.example.enterpriseapplication.ui.activitys.IllegalCaseDetailectivity;
import com.example.enterpriseapplication.ui.activitys.LoginActivity;
import com.example.enterpriseapplication.ui.activitys.RecordActivity;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

public class NotifictionUtil {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private static String channeId = "testtset";

    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {

        AppOpsManager mAppOps =
                (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;

        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod =
                    appOpsClass.getMethod(CHECK_OP_NO_THROW,
                            Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);

            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                    AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showNotificationIsFalse(Context context) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content("检测到您没有打开通知权限，去打开接收推送消息")
                .btnNum(1)
                .btnText("确定")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", MyApplication.getContext().getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);

                    localIntent.setClassName("com.android.settings",
                            "com.android.settings.InstalledAppDetails");

                    localIntent.putExtra("com.android.settings.ApplicationPkgName",
                            MyApplication.getContext().getPackageName());
                }
                MyApplication.getContext().startActivity(localIntent);
            }
        });


    }

    private static void sendSubscribeMsg(String title, String text,String contentText) {
        NotificationManager manager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(MyApplication.getContext(), IllegalCaseDetailectivity.class);//将要跳转的界面
        intent.putExtra("pushMessage",contentText);
        PendingIntent intentPend = PendingIntent.getActivity(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new NotificationCompat.Builder(MyApplication.getContext(), "push")
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.qiye)
                .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.qiye))
                .setAutoCancel(true)
                .setContentIntent(intentPend)
                .build();

        manager.notify(2, notification);
    }


    public static void showNotifictionIcon(String title, String contentText) {
        Case aCase = new Gson().fromJson(contentText,Case.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "push";//设置通道的唯一ID
            String channelName = "推送消息";//设置通道名
            int importance = NotificationManager.IMPORTANCE_HIGH;//设置通道优先级
            createNotificationChannel(channelId, channelName, importance, title, aCase.getOffPlateNumber()+"车辆违法信息推送",contentText);
        } else {
            sendSubscribeMsg(title, aCase.getOffPlateNumber()+"车辆违法信息推送",contentText);
        }


    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(String channelId, String channelName, int importance, String title, String text,String contentText) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        sendSubscribeMsg(title, text,contentText);
    }


}
