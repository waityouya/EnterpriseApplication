package com.example.enterpriseapplication.adpter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.enterpriseapplication.R;
import com.example.enterpriseapplication.model.Record;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;

    private ArrayList<Record> list;




    public void update(ArrayList<Record> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public RecordAdapter(ArrayList<Record> list) {
        this.list = list;
    }

    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }


           View view = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent, false);
            return new ViewHolder(view);


    }
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof  ViewHolder){
            ViewHolder ViewHolder1 = (ViewHolder) viewHolder;
            Record record = list.get(position);
            ViewHolder1.tv_name.setText(record.getDriverName());
            ViewHolder1.car_number.setText( record.getPlateNumber());
            if(record.getAduit() == 0){
                ViewHolder1.tv_record_aditu.setText( "未审核");
            }else if(record.getAduit() == 1){
                ViewHolder1.tv_record_aditu.setText( "审核通过");
            }else {
                ViewHolder1.tv_record_aditu.setText( "审核未通过");
            }
            ViewHolder1.tv_record_time.setText( record.getUpTime());
        }


    }


    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView car_number;
        TextView tv_name;
        TextView tv_record_aditu;
        TextView tv_record_time;


        ViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_record_name_item);
            car_number = view.findViewById(R.id.tv_record_car_number_item);
            tv_record_aditu = view.findViewById(R.id.aduit);
            tv_record_time = view.findViewById(R.id.tv_record_time_item);



        }

    }


}
