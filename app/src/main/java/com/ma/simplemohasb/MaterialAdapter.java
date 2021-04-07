package com.ma.simplemohasb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MaterialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static ArrayList<material_class> alldata = new ArrayList<material_class>();


    Context context;

    public void addNewData(List<material_class> alldata1) {

        for (int i = 0; i < alldata1.size(); i++) {
            alldata.add(alldata1.get(i));
        }

        this.notifyDataSetChanged();
    }

    public void RefreshData() {
        this.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView name;
        public ImageView edit;
        public ImageView delete;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_material_name);
            edit = view.findViewById(R.id.item_material_edit);
            delete = view.findViewById(R.id.item_material_delete);

        }

    }


    int type = 0;

    public MaterialAdapter(Context context, ArrayList<material_class> alldata) {
        this.alldata = alldata;
        this.context = context;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        ((MyViewHolder) holder).name.setText(alldata.get(position).mname);

        ((MyViewHolder) holder).delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_material(alldata.get(position));
            }
        });


        ((MyViewHolder) holder).edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_materila(alldata.get(position));

            }
        });

    }

    private void edit_materila(final material_class material_class) {

        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.dialog_edit_material);
        final EditText txt_name = d.findViewById(R.id.txt_name);
        txt_name.setText(material_class.mname);
        d.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_name.getText().length() > 0) {
                    Config.ExecuteNonQuery(context, "update materials set mname='" + txt_name.getText().toString() + "' where mid=" + material_class.mid);
                    d.dismiss();
                } else
                    Toast.makeText(context, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
            }
        });

        d.show();

    }

    private void delete_material(final material_class material_class) {
        new AlertDialog.Builder(context)
                .setTitle("حذف")
                .setMessage("هل تريد حذف " + material_class.mname + "  ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        Config.ExecuteNonQuery(context, "delete from materials where mid=" + material_class.mid);
                        alldata.remove(material_class);
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
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, parent, false);
        return new MyViewHolder(v2);
    }
}