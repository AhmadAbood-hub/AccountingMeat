package com.ma.simplemohasb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.ma.simplemohasb.HomeActivity.totalPrice;
import static com.ma.simplemohasb.HomeActivity.txt_date_kill;

public class BillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static ArrayList<bills> alldata = new ArrayList<bills>();
    public static bills bill;

    Context context;

    public void addNewData(List<bills> alldata1) {

        for (int i = 0; i < alldata1.size(); i++) {
            alldata.add(alldata1.get(i));
        }

        this.notifyDataSetChanged();
    }

    public void RefreshData() {
        this.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView item_baccountname_bill;
        public TextView item_note_bill;
        public TextView txt_total_price_bill;

        public ImageView item_bill_edit;
        public ImageView item_bill_delete;


        public MyViewHolder(View view) {
            super(view);
            item_baccountname_bill = view.findViewById(R.id.item_baccountname_bill);
            item_note_bill = view.findViewById(R.id.item_note_bill);
            txt_total_price_bill = view.findViewById(R.id.txt_total_price_bill);
            item_bill_edit = view.findViewById(R.id.item_bill_edit);
            item_bill_delete = view.findViewById(R.id.item_bill_delete);

        }

    }


    int type = 0;

    public BillAdapter(Context context, ArrayList<bills> alldata) {
        this.alldata = alldata;
        this.context = context;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        ((MyViewHolder) holder).item_baccountname_bill.setText(alldata.get(position).baccountname);

        ((MyViewHolder) holder).item_note_bill.setText(alldata.get(position).bnotes);


        ((HomeActivity) context).load_total_price_bill(alldata.get(position).bid);


        ((MyViewHolder) holder).txt_total_price_bill.setText(totalPrice+"");


        ((MyViewHolder) holder).item_bill_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_bill(alldata.get(position));
            }
        });


        ((MyViewHolder) holder).item_bill_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_bill(alldata.get(position));

            }
        });
        ((MyViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bill =alldata.get(position);
                ((HomeActivity) context).load_bill_items();
                ((HomeActivity) context).refreshMenu();
            }
        });


    }

    private void edit_bill(final bills bill) {

        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.dialog_edit_bill);
        final EditText edt_Dialog_baccountname_bill = d.findViewById(R.id.edt_Dialog_baccountname_bill);
        final EditText edt_Dialog_note_bill = d.findViewById(R.id.edt_Dialog_note_bill);
        edt_Dialog_baccountname_bill.setText(bill.baccountname);
        edt_Dialog_note_bill.setText(bill.bnotes);

        d.findViewById(R.id.bt_cancel_bill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.findViewById(R.id.bt_save_bill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_Dialog_baccountname_bill.getText().length() > 0 && edt_Dialog_note_bill.getText().length() > 0) {

                    Config.ExecuteNonQuery(context, "update bills set baccountname='" + edt_Dialog_baccountname_bill.getText().toString() + "' where bid=" + bill.bid);
                    Config.ExecuteNonQuery(context, "update bills set bnotes='" + edt_Dialog_note_bill.getText().toString() + "' where bid=" + bill.bid);
                    d.dismiss();
                } else
                    Toast.makeText(context, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
            }
        });

        d.show();

    }

    private void delete_bill(final bills bill) {
        new AlertDialog.Builder(context)
                .setTitle("حذف")
                .setMessage("هل تريد الحذف " + "  ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        Config.ExecuteNonQuery(context, "delete from bills where bid=" + bill.bid);
                        alldata.remove(bill);
                        notifyDataSetChanged();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("الغاء", null)
                .setIcon(R.drawable.delete)
                .show();
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
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new MyViewHolder(v2);
    }
}