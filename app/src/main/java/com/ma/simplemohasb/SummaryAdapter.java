package com.ma.simplemohasb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.ma.simplemohasb.BillItemsAdapter.nameMateral;
import static com.ma.simplemohasb.Config.TAG;
import static com.ma.simplemohasb.HomeActivity.Subjects;
import static com.ma.simplemohasb.HomeActivity.materalID;
import static com.ma.simplemohasb.HomeActivity.material_classes;
import static com.ma.simplemohasb.HomeActivity.spn_Dialog_subject_bill_items;

public class SummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static ArrayList<bill_items> alldata = new ArrayList<bill_items>();

    Context context;


    public void addNewData(List<bill_items> alldata1) {

        for (int i = 0; i < alldata1.size(); i++) {
            alldata.add(alldata1.get(i));
        }

        this.notifyDataSetChanged();
    }

    public void RefreshData() {
        this.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView item_subject_summary;
        public TextView item_price_summary;
        public TextView item_quantity_summary;



        public MyViewHolder(View view) {
            super(view);
            item_subject_summary = view.findViewById(R.id.item_subject_summary);
            item_price_summary = view.findViewById(R.id.item_price_summary);
            item_quantity_summary = view.findViewById(R.id.item_quantity_summary);

        }

    }


    public SummaryAdapter(Context context, ArrayList<bill_items> alldata) {
        this.alldata = alldata;
        this.context = context;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        ((HomeActivity) context).load_spinner_materal();


        ((MyViewHolder) holder).item_price_summary.setText(alldata.get(position).bprice+"");


        ((MyViewHolder) holder).item_quantity_summary.setText(alldata.get(position).bamount + "");


        ((MyViewHolder) holder).item_subject_summary.setText(alldata.get(position).mName + "");



    }


    @Override
    public int getItemCount() {
        return alldata.size();
    }

    public static int getIcCount() {
        return alldata.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_summary, parent, false);
        return new MyViewHolder(v2);
    }
}