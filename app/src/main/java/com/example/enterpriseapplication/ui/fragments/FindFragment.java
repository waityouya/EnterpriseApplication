package com.example.enterpriseapplication.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.enterpriseapplication.R;
import com.example.enterpriseapplication.adpter.CaseAdapter;
import com.example.enterpriseapplication.model.Case;
import com.example.enterpriseapplication.model.Record;
import com.example.enterpriseapplication.ui.activitys.LoginActivity;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FindFragment extends Fragment implements GlobalHandler.HandleMsgListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Case> mCases = new ArrayList<>();
    private SharedPreferences sp;
    private GlobalHandler mHandler;
    private CaseAdapter adapter;
    private SearchView searchView;
    String token;
    String userId;
    private RecyclerView mRecyclerView;

    public FindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
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
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        initView(view);
        setLister();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    private void setLister() {
       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {
               LoadingDailogUtil.showLoadingDialog(getActivity(),"搜索中...");
               getData(query,userId,token);
               searchView.clearFocus();
               return false;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               return false;
           }
       });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                searchView.setQuery("", false);
                searchView.clearFocus();
                return true;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("find：resume");
        mHandler.setHandleMsgListener(this);
    }

    private void getData(String searchKey, String userId, String token) {
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        Record record = new Record(searchKey, Integer.valueOf(userId), token);
        String getDataJson = JsonUtils.conversionJsonString(record);
        try {
            String publicEncryptJson = RSAUtils.publicEncrypt(getDataJson, RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
            okHttpUtils.postInfo(Contract.SERVER_ADDRESS + "EnterpriseSearchGetCase", publicEncryptJson);
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
                Intent intent = new Intent(MyApplication.getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }


    @Override
    public void onDetach() {
        super.onDetach();

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
        searchView = view.findViewById(R.id.searchView);

        mRecyclerView = view.findViewById(R.id.find_recycler_view);
        sp = getContext().getSharedPreferences("login", 0);
        token = sp.getString("token", null);
        userId = sp.getString("userId", null);
        mHandler = GlobalHandler.getInstance();
        mHandler.setHandleMsgListener(this);
        // searchView.setSubmitButtonEnabled(true);
        searchView.setSubmitButtonEnabled(true);
        // emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view,null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyApplication.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        mRecyclerView.setLayoutManager(layoutManager);
        //  mRecyclerView.setEmptyView(emptyView);
        adapter = new CaseAdapter(mCases);
        mRecyclerView.setAdapter(adapter);


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if ( !hidden) {
            LogUtil.d("find：show");
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
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            ArrayList<Case> serachCases = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Case case1 = new Case();
                                case1.setOffName(object.getString("offName"));
                                case1.setOffCertificateNumber(object.getString("offCertificateNumber"));
                                case1.setCaseId(object.getInt("caseId"));
                                case1.setOffType(object.getString("offType"));
                                case1.setOffTime(object.getString("offTime"));
                                serachCases.add(case1);

                            }
                            adapter.update(serachCases);
                            LoadingDailogUtil.cancelLoadingDailog();
                            ToastUtil.showShortToast("搜索成功");
                            break;
                        case "003":
                            checkInvalidDialog();
                            LoadingDailogUtil.cancelLoadingDailog();
                            break;
                        default:
                            LoadingDailogUtil.cancelLoadingDailog();
                            ToastUtil.showShortToast("服务器错误");
                    }
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadingDailogUtil.cancelLoadingDailog();
                    ToastUtil.showShortToast("未知错误");

                }

        }
    }
}