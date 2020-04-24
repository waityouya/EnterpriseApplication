package com.example.enterpriseapplication.ui.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.enterpriseapplication.model.ReturnLoginInfo;
import com.example.enterpriseapplication.util.LogUtil;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

import com.example.enterpriseapplication.R;
import com.example.enterpriseapplication.model.Record;
import com.example.enterpriseapplication.model.UpRecordData;
import com.example.enterpriseapplication.util.BitmapUtil;
import com.example.enterpriseapplication.util.Contract;
import com.example.enterpriseapplication.util.GlobalHandler;
import com.example.enterpriseapplication.util.JsonUtils;
import com.example.enterpriseapplication.util.LoadingDailogUtil;
import com.example.enterpriseapplication.util.MyApplication;
import com.example.enterpriseapplication.util.OkHttpUtils;
import com.example.enterpriseapplication.util.RSAUtils;
import com.example.enterpriseapplication.util.ToastUtil;
import com.github.chrisbanes.photoview.PhotoView;
import com.goyourfly.multi_picture.ImageLoader;
import com.goyourfly.multi_picture.MultiPictureView;
import com.goyourfly.vincent.Vincent;
import com.google.gson.Gson;
import com.sh.zsh.code.form.FormInit;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecordActivity extends AppCompatActivity implements GlobalHandler.HandleMsgListener {
    private MultiPictureView multiPictureView;
    private PhotoView photoView;
    private List<String> images = new LinkedList<>();
    private List<Uri> Added = new LinkedList<>();
    private int imageCount = 0;
    private View parent;
    private TextView title;
    private Button mButton;
    private SharedPreferences sp;
    private String token;
    private String userId;

    private GlobalHandler mHandler;
    private EditText mEditTextName;
    private EditText mEditTextIdCard;
    private EditText mEditTextCarNumber;
    private EditText mEditTextvVin;

    private static final int REQUEST_ADD_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        init();
        setListener();
    }

    private void init() {
        title = findViewById(R.id.tv_title);
        photoView = findViewById(R.id.photoview);
        multiPictureView = findViewById(R.id.multi_image_view);
        mEditTextName = findViewById(R.id.name);
        mEditTextIdCard = findViewById(R.id.id_number);
        mEditTextCarNumber = findViewById(R.id.car_number);
        mEditTextvVin = findViewById(R.id.vin);
        mButton = findViewById(R.id.add);
        parent = findViewById(R.id.ll);
        mHandler = GlobalHandler.getInstance();
        mHandler.setHandleMsgListener(this);
        MultiPictureView.setImageLoader(new ImageLoader() {
            @Override
            public void loadImage(@NonNull ImageView imageView, @NonNull Uri uri) {
                Vincent.with(RecordActivity.this)
                        .load(uri)
                        .placeholder(R.drawable.ic_placeholder_loading)
                        .error(R.drawable.ic_placeholder_loading)
                        .into(imageView);
            }
        });
        title.setText("备案");
        sp = MyApplication.getContext().getSharedPreferences("login", 0);
        token = sp.getString("token", null);
        userId = sp.getString("userId", null);
        FormInit.injection(this);
    }

    private void setListener() {
        multiPictureView.setAddClickCallback(new MultiPictureView.AddClickCallback() {
            @Override
            public void onAddClick(View view) {
                addImage();

            }

        });

        multiPictureView.setDeleteClickCallback(new MultiPictureView.DeleteClickCallback() {
            @Override
            public void onDeleted(@NonNull View view, int i) {
                images.remove(i);
                Added.remove(i);
                multiPictureView.removeItem(i);
            }
        });

        multiPictureView.setItemClickCallback(new MultiPictureView.ItemClickCallback() {
            @Override
            public void onItemClicked(@NonNull View view, int i, @NonNull ArrayList<Uri> arrayList) {

                photoView.setImageURI(arrayList.get(i));
                photoView.setVisibility(View.VISIBLE);
                parent.setVisibility(View.GONE);
            }
        });

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                photoView.setVisibility(View.GONE);
                parent.setVisibility(View.VISIBLE);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEditTextName.getText().toString();
                String idNumber = mEditTextIdCard.getText().toString();
                String carNumber = mEditTextCarNumber.getText().toString();
                String vin = mEditTextvVin.getText().toString();
                imageCount = multiPictureView.getCount();
                if (name.isEmpty() || vin.isEmpty() || carNumber.isEmpty() ||
                        idNumber.isEmpty() || imageCount <= 0) {
                    ToastUtil.showShortToast("请填写完整");
                } else {
                    LoadingDailogUtil.showLoadingDialog(RecordActivity.this, "提交中...");

                    OkHttpUtils okHttpUtils = new OkHttpUtils();
                    Record record = new Record(Integer.valueOf(userId), name, carNumber, idNumber, vin, token);
                    try {
                        String caseJson = JsonUtils.conversionJsonString(record);
                        String caseEncryptJson = RSAUtils.publicEncrypt(caseJson, RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
                        UpRecordData upData = new UpRecordData(caseEncryptJson, images);
                        String up = JsonUtils.conversionJsonString(upData);
                        okHttpUtils.postInfo(Contract.SERVER_ADDRESS + "EnterpriseUpRecord", up);
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShortToast("未知错误");
                                LoadingDailogUtil.cancelLoadingDailog();

                            }

                        });
                    }

                }
            }
        });

    }

    private void addImage() {

        Matisse.from(RecordActivity.this)
                .choose(MimeType.ofAll())
                .maxSelectable(9 - multiPictureView.getCount())
                .thumbnailScale(0.85f)
                .imageEngine(new ImageEngine() {
                    @Override
                    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
                        Vincent.with(context)
                                .load(uri)
                                .placeholder(R.drawable.ic_placeholder_loading)
                                .error(R.drawable.ic_placeholder_loading)
                                .into(imageView);
                    }

                    @Override
                    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
                        Vincent.with(context)
                                .load(uri)
                                .placeholder(R.drawable.ic_placeholder_loading)
                                .error(R.drawable.ic_placeholder_loading)
                                .into(imageView);
                    }

                    @Override
                    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
                        Vincent.with(context)
                                .load(uri)
                                .placeholder(R.drawable.ic_placeholder_loading)
                                .error(R.drawable.ic_placeholder_loading)
                                .into(imageView);
                    }

                    @Override
                    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
                        Vincent.with(context)
                                .load(uri)
                                .placeholder(R.drawable.ic_placeholder_loading)
                                .error(R.drawable.ic_placeholder_loading)
                                .into(imageView);
                    }

                    @Override
                    public boolean supportAnimatedGif() {
                        return false;
                    }
                }).forResult(REQUEST_ADD_IMAGE);
    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 0:
                ToastUtil.showShortToast("网络出错，请检查网络");
                LoadingDailogUtil.cancelLoadingDailog();
                break;
            case 1:
                //也可以用这个接收

                ReturnLoginInfo returnLoginInfo = new Gson().fromJson(msg.getData().getString("backInfo"), ReturnLoginInfo.class);
                switch (returnLoginInfo.getCode()) {
                    case "ok":
                        LoadingDailogUtil.cancelLoadingDailog();
                        UpOkDialog();
                        break;
                    case "003":
                        LoadingDailogUtil.cancelLoadingDailog();
                        checkInvalidDialog();
                        break;
                    default:
                        ToastUtil.showShortToast("服务器错误");
                        LoadingDailogUtil.cancelLoadingDailog();


                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_ADD_IMAGE && resultCode == RESULT_OK) {
            LoadingDailogUtil.showLoadingDialog(RecordActivity.this, "上传中...");
            multiPictureView.addItem(Matisse.obtainResult(data));

            for (Uri uri : multiPictureView.getList()
            ) {
                int flag = 0;
                for (Uri uri1 : Added
                ) {
                    if (uri == uri1) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    //获取路径
                    String path = BitmapUtil.getRealFilePath(MyApplication.getContext(), uri);
                    //获取压缩后的路径
                    String pathNew = BitmapUtil.compressImage(path);
                    //转Bitmap
                    Bitmap bitmap = BitmapUtil.getSmallBitmap(pathNew);
                    String base64 = BitmapUtil.bitmapTobase64NONseal(bitmap);
                    //转把base4并异或加密
                    String image = BitmapUtil.imageEncrypt(base64);
                    images.add(image);
                    Added.add(uri);
                }


            }
            LoadingDailogUtil.cancelLoadingDailog();
        }
    }

    @Override
    protected void onDestroy() {
        /**
         * 注销表单
         */
        LogUtil.d("destroy");
        //销毁移除所有消息，避免内存泄露
        mHandler.removeCallbacksAndMessages(null);
        FormInit.deleteInjection(this);
        super.onDestroy();
    }


    private void checkInvalidDialog() {
        final NormalDialog dialog = new NormalDialog(RecordActivity.this);
        dialog.content("身份失效，请重新登录")
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
                Intent intent = new Intent(RecordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void UpOkDialog() {
        // 进入动画
        BounceTopEnter mBasIn = new BounceTopEnter();
        // 退出动画
        SlideBottomExit mBasOut = new SlideBottomExit();
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("上传成功~\n如没有发现数据，可下拉刷新")//
                .btnNum(1)
                .btnText("确定")//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)
                .show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                LogUtil.d("确定移除");
                mHandler.removeCallbacksAndMessages(null);
                finish();

            }
        });
    }
    /**
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭当前Activity.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtil.d("返回移除");
        mHandler.removeCallbacksAndMessages(null);
    }


}
