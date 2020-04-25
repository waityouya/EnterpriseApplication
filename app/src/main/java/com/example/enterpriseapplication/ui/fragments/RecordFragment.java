package com.example.enterpriseapplication.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.enterpriseapplication.R;
import com.example.enterpriseapplication.adpter.RecordAdapter;
import com.example.enterpriseapplication.model.Record;
import com.example.enterpriseapplication.model.RequestParams;
import com.example.enterpriseapplication.ui.activitys.LoginActivity;
import com.example.enterpriseapplication.ui.activitys.RecordActivity;
import com.example.enterpriseapplication.ui.activitys.UserActivity;
import com.example.enterpriseapplication.util.Contract;
import com.example.enterpriseapplication.util.GlobalHandler;
import com.example.enterpriseapplication.util.JsonUtils;
import com.example.enterpriseapplication.util.LoadingDailogUtil;
import com.example.enterpriseapplication.util.LogUtil;
import com.example.enterpriseapplication.util.MyApplication;
import com.example.enterpriseapplication.util.OkHttpUtils;
import com.example.enterpriseapplication.util.RSAUtils;
import com.example.enterpriseapplication.util.ToastUtil;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.github.clans.fab.FloatingActionButton;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;


public class RecordFragment extends Fragment implements GlobalHandler.HandleMsgListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton floatingActionButton;
    private XRecyclerView mRecyclerView;
    private ArrayList<Record> mRecords = new ArrayList<>();
    private SharedPreferences sp;
    String token;
    String userId;
    private GlobalHandler mHandler;
    private RecordAdapter mRecordAdapter;
    public RecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        initView(view);
        getData(userId, token);
        setmListener();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("record：resume");
        mHandler.setHandleMsgListener(this);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    private void initView(View view) {
        floatingActionButton = view.findViewById(R.id.fab);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        sp = MyApplication.getContext().getSharedPreferences("login", 0);
        token = sp.getString("token", null);
        userId = sp.getString("userId", null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyApplication.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setNoMore(true);
        mRecyclerView.setLoadingMoreEnabled(false);
        mHandler = GlobalHandler.getInstance();
        mHandler.setHandleMsgListener(this);
        mRecordAdapter = new RecordAdapter(mRecords);
        mRecyclerView.setAdapter(mRecordAdapter);
        mRecyclerView.getDefaultFootView().setLoadingHint("正在刷新");
    }

    private void setmListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(null);
                Intent intent = new Intent(getContext(), RecordActivity.class);
                startActivity(intent);

            }
        });
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getData(userId, token);

                mRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {

            }
        });

    }

    private void getData(String userId, String token) {
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        RequestParams requestParams = new RequestParams(userId, token);
        String getDataJson = JsonUtils.conversionJsonString(requestParams);
        try {
            String publicEncryptJson = RSAUtils.publicEncrypt(getDataJson, RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
            okHttpUtils.postInfo(Contract.SERVER_ADDRESS + "EnterpriseGetRecord", publicEncryptJson);
        } catch (Exception e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showShortToast("未知错误");
                    LoadingDailogUtil.cancelLoadingDailog();

                }

            });
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if ( !hidden) {
            LogUtil.d("record：show");
            mHandler.setHandleMsgListener(this);
        }
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
                try {
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("backInfo"));
                    switch (jsonObject.getString("code")) {
                        case "ok":
                            mRecords.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Record record = new Record();
                                record.setDriverName(object.getString("driverName"));
                                record.setDriverNumber(object.getString("driverNumber"));
                                record.setRecordId(object.getInt("recordId"));
                                record.setEnterpriseId(object.getInt("enterpriseId"));
                                record.setAduit(object.getInt("aduit"));
                                record.setAduitTime(object.getString("aduitTime"));
                                record.setUpTime(object.getString("upTime"));
                                record.setPlateNumber(object.getString("plateNumber"));
                                record.setNotReason(object.getString("notReason"));
                                record.setvIN(object.getString("vIN"));
                                mRecords.add(record);
                            }

                            mRecordAdapter.update(mRecords);
                            ToastUtil.showShortToast("加载成功");

                            break;
                        case "003":
                            checkInvalidDialog();
                            break;
                        default:
                            ToastUtil.showShortToast("服务器错误");

                    }
                    break;
                } catch (
                        Exception e) {
                    e.printStackTrace();

                    ToastUtil.showShortToast("未知错误");

                }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁移除所有消息，避免内存泄露
        mHandler.removeCallbacks(null);
    }

    private void checkInvalidDialog() {
        final NormalDialog dialog = new NormalDialog(MyApplication.getContext());
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
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

}
