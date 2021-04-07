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

public class SpentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static ArrayList<spents> alldata = new ArrayList<>();


    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView txt_price_spent;
        public TextView txt_note_spent;

        public ImageView item_spent_delete;
        public ImageView item_spent_edit;


        public MyViewHolder(View view) {
            super(view);

            txt_price_spent = view.findViewById(R.id.txt_price_spent);
            txt_note_spent = view.findViewById(R.id.txt_note_spent);

            item_spent_delete = view.findViewById(R.id.item_spent_delete);
            item_spent_edit = view.findViewById(R.id.item_spent_edit);

        }

    }


    public SpentAdapter(Context context, ArrayList<spents> alldata) {
        this.alldata = alldata;
        this.context = context;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        ((MyViewHolder) holder).txt_note_spent.setText(alldata.get(position).snotes+"");

        ((MyViewHolder) holder).txt_price_spent.setText(alldata.get(position).samount+"");


        ((MyViewHolder) holder).item_spent_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_spent(alldata.get(position));
            }
        });


        ((MyViewHolder) holder).item_spent_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 edit_spent(alldata.get(position));

            }
        });

    }

    private void edit_spent(final spents spent) {

        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.dialog_edit_spent);
        final EditText edt_Dialog_price_spent = d.findViewById(R.id.edt_Dialog_price_spent);
        final EditText edt_Dialog_note_spent = d.findViewById(R.id.edt_Dialog_note_spent);
        edt_Dialog_price_spent.setText(spent.samount+"");
        edt_Dialog_note_spent.setText(spent.snotes+"");

        d.findViewById(R.id.bt_cancel_spent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.findViewById(R.id.bt_save_spent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_Dialog_price_spent.getText().length() > 0 && edt_Dialog_note_spent.getText().length() > 0) {
                    Config.ExecuteNonQuery(context, "update spents set samount='" + edt_Dialog_price_spent.getText().toString() + "' where sid=" + spent.sid);
                    Config.ExecuteNonQuery(context, "update spents set snotes='" + edt_Dialog_note_spent.getText().toString() + "' where sid=" + spent.sid);
                    d.dismiss();
                } else
                    Toast.makeText(context, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
            }
        });

        d.show();

    }

    private void delete_spent(final spents spent) {
        new AlertDialog.Builder(context)
                .setTitle("حذف")
                .setMessage("هل تريد الحذف " + "  ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        Config.ExecuteNonQuery(context, "delete from spents where sid=" + spent.sid);
                        alldata.remove(spent);
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
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spent, parent, false);
        return new MyViewHolder(v2);
    }
}