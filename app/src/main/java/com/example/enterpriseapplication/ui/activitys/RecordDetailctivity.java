package com.example.enterpriseapplication.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.enterpriseapplication.R;
import com.example.enterpriseapplication.adpter.TimeLineAdapter;
import com.example.enterpriseapplication.model.Record;
import com.example.enterpriseapplication.model.TimeLineModel;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;
import java.util.List;

public class RecordDetailctivity extends AppCompatActivity {
    private TextView mTextViewTitle;
    private TextView mTextViewName;
    private TextView mTextViewIdNumber;
    private TextView mTextViewCarNumber;
    private TextView mTextViewVin;
    private Record  record;
    private ListView mListView;
    private List<TimeLineModel> mTimeLineModels = new ArrayList<>();
    private TimeLineAdapter mTimeLineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detailctivity);
        init();
        getData();
        if(record != null){
            showView();
        }
    }

    private void init(){
        mTextViewTitle = findViewById(R.id.tv_title);
        mTextViewName = findViewById(R.id.tv_driver_name);
        mTextViewIdNumber = findViewById(R.id.tv_driver_number);
        mTextViewCarNumber = findViewById(R.id.tv_car_number);
        mTextViewVin = findViewById(R.id.tv_car_vin);
        mListView = findViewById(R.id.lvTrace);

        mTextViewTitle.setText("备案详情");
    }

    private void getData(){
        record = (Record) getIntent().getSerializableExtra("record");
        mTimeLineModels.add(new TimeLineModel(record.getUpTime(),"提交申请备案"));
        if(record.getAduitTime() !=null && record.getAduitTime().length() != 0){
            if(record.getAduit() == 1){
                mTimeLineModels.add(new TimeLineModel(record.getAduitTime(),"审核结果："+"通过\n理由："+record.getNotReason()));
            }else {
                mTimeLineModels.add(new TimeLineModel(record.getAduitTime(),"审核结果："+"未通过\n理由："+record.getNotReason()));

            }


        }
    }
    private void showView(){
        mTextViewName.setText(record.getDriverName());
        mTextViewIdNumber.setText(record.getDriverNumber());
        mTextViewCarNumber.setText(record.getPlateNumber());
        mTextViewVin.setText(record.getVin());
        mTimeLineAdapter = new TimeLineAdapter(this,mTimeLineModels);
        mListView.setAdapter(mTimeLineAdapter);

        //srollview 下listview只显示一行数据，处理方案：
        int totalHeight = 0;
        // 如果没有设置数据适配器，则ListView没有子项，返回。
        for (int index = 0, len = mTimeLineAdapter.getCount(); index < len; index++)
        {
            View listViewItem = mTimeLineAdapter.getView(index, null, mListView);
            // 计算子项View 的宽高
            listViewItem.measure(0, 0);
            // 计算所有子项的高度和
            totalHeight += listViewItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        // listView.getDividerHeight()获取子项间分隔符的高度
        // params.height设置ListView完全显示需要的高度
        params.height = totalHeight + (mListView.getDividerHeight() * (mTimeLineAdapter.getCount() - 1));
        mListView.setLayoutParams(params);

    }
}
