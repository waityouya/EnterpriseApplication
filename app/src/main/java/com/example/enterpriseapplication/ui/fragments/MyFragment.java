package com.example.enterpriseapplication.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.enterpriseapplication.R;
import com.example.enterpriseapplication.adpter.UserFunctionAdapter;
import com.example.enterpriseapplication.model.UserMenu;
import com.example.enterpriseapplication.util.GlobalHandler;
import com.example.enterpriseapplication.util.MyApplication;

import java.util.ArrayList;
import java.util.List;


public class MyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String name;
    private String phoneNumber;
    //private UserInfo mUserInfo;
    private GlobalHandler mHandler;
    private SharedPreferences sp;
    String token;
    String userId;
    private String mParam1;
    private String mParam2;
    private TextView textViewUserName;
    private List<UserMenu> list1;
    private List<UserMenu> list2;
    private ListView userFunctionListView1;
    private ListView userFunctionListView2;
    private UserFunctionAdapter adapter1;
    private UserFunctionAdapter adapter2;

    private ImageView imageViewHead;

    public MyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
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
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        initView(view);
        return view;
    }
    private void initView(View view){
        imageViewHead = view.findViewById(R.id.h_head);
        textViewUserName = view.findViewById(R.id.user_name);
        textViewUserName.setText(mParam1);

        Glide.with(this).load(R.drawable.gong)
                .apply(RequestOptions.bitmapTransform(new CropCircleTransformation()))
                .into(imageViewHead);

        userFunctionListView1 = view.findViewById(R.id.user_function_list_view1);
        userFunctionListView2 = view.findViewById(R.id.user_function_list_view2);

        list1 = new ArrayList<>();
        UserMenu userMenu1 = new UserMenu(R.mipmap.feijidongc,"备案车辆");

        UserMenu userMenu3 = new UserMenu(R.mipmap.xiugaimima,"修改密码");
        UserMenu userMenu4 = new UserMenu(R.mipmap.shezhi,"设置");
        list1.add(userMenu1);





        adapter1 = new UserFunctionAdapter(list1, MyApplication.getContext());
        userFunctionListView1.setDivider(new ColorDrawable(Color.parseColor("#E0EEE0")));
        userFunctionListView1.setDividerHeight(3);
        userFunctionListView1.setAdapter(adapter1);

        list2 = new ArrayList<>();
        list2.add(userMenu3);
        list2.add(userMenu4);

        adapter2 = new UserFunctionAdapter(list2, MyApplication.getContext());
        userFunctionListView2.setDivider(new ColorDrawable(Color.parseColor("#E0EEE0")));
        userFunctionListView2.setDividerHeight(3);
        userFunctionListView2.setAdapter(adapter2);

        userFunctionListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
//                        Intent intent = new Intent(getActivity(), MyUpCaseActivity.class);
//                        startActivity(intent);
                        break;
                    case 1:
                        break;
                }
            }
        });

        userFunctionListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
//                        Intent intent = new Intent(getActivity(), UpdatePassWordActivity.class);
//                        startActivity(intent);
                        break;
                    case 1:
                        break;
                }
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

}
