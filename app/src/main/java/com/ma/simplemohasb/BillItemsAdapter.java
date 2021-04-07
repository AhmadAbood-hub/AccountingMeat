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

import javax.security.auth.Subject;

import static com.ma.simplemohasb.BillAdapter.bill;
import static com.ma.simplemohasb.Config.TAG;
import static com.ma.simplemohasb.HomeActivity.Subjects;
import static com.ma.simplemohasb.HomeActivity.materalID;
import static com.ma.simplemohasb.HomeActivity.material_classes;
import static com.ma.simplemohasb.HomeActivity.spn_Dialog_subject_bill_items;

public class BillItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static ArrayList<bill_items> alldata = new ArrayList<bill_items>();

    Context context;
    static String nameMateral;
    boolean BolMatereal;
    static int bb;

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


        public TextView item_subject_bill_items;
        public TextView item_quantity_bill_items;
        public TextView item_price_bill_items;
        public TextView item_price_totale_bill_items;

        public ImageView item_bill_items_edit;
        public ImageView item_bill_items_delete;


        public MyViewHolder(View view) {
            super(view);
            item_subject_bill_items = view.findViewById(R.id.item_subject_bill_items);
            item_quantity_bill_items = view.findViewById(R.id.item_quantity_bill_items);
            item_price_bill_items = view.findViewById(R.id.item_price_bill_items);
            item_price_totale_bill_items = view.findViewById(R.id.item_price_totale_bill_items);

            item_bill_items_edit = view.findViewById(R.id.item_bill_items_edit);
            item_bill_items_delete = view.findViewById(R.id.item_bill_items_delete);

        }

    }


    public BillItemsAdapter(Context context, ArrayList<bill_items> alldata) {
        this.alldata = alldata;
        this.context = context;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ((HomeActivity) context).load_spinner_materal();

        for (material_class material_class : material_classes) {
            if (alldata.get(position).mmaterial == material_class.mid) {
                nameMateral = material_class.mname;
                ((MyViewHolder) holder).item_subject_bill_items.setText(nameMateral + "");
            }
        }

        ((MyViewHolder) holder).item_quantity_bill_items.setText(alldata.get(position).bamount + "");

        ((MyViewHolder) holder).item_price_bill_items.setText(alldata.get(position).bprice + "");

        float a = alldata.get(position).bamount;
        float b = alldata.get(position).bprice;
        float c = a * b;


        ((MyViewHolder) holder).item_price_totale_bill_items.setText(c+ "");

        ((MyViewHolder) holder).item_bill_items_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_bill(alldata.get(position));
            }
        });


        ((MyViewHolder) holder).item_bill_items_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_bill(alldata.get(position));
            }
        });


    }

    private void edit_bill(final bill_items bill_items) {

        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.dialog_edit_bill_items);
        final EditText edt_Dialog_quantity_bill_items = d.findViewById(R.id.edt_Dialog_quantity_bill_items);
        final EditText edt_Dialog_price_bill_items = d.findViewById(R.id.edt_Dialog_price_bill_items);
        edt_Dialog_quantity_bill_items.setText(String.valueOf(bill_items.bamount));
        edt_Dialog_price_bill_items.setText(String.valueOf(bill_items.bprice));



        TextView result = d.findViewById(R.id.result);

        float a = Float.parseFloat(edt_Dialog_price_bill_items.getText().toString());
        float b = Float.parseFloat(edt_Dialog_quantity_bill_items.getText().toString());
        float c = a * b;
        result.setText(c + "");


        edt_Dialog_price_bill_items.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                try {
                    float a = Float.parseFloat(edt_Dialog_price_bill_items.getText().toString());
                    float b = Float.parseFloat(edt_Dialog_quantity_bill_items.getText().toString());
                    float c = a * b;
                    result.setText(c + "");
                }
                catch (Exception e)
                {

                }
            }

        });



        edt_Dialog_quantity_bill_items.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try
                {
                    float a = Float.parseFloat(edt_Dialog_price_bill_items.getText().toString());
                    float b = Float.parseFloat(edt_Dialog_quantity_bill_items.getText().toString());
                    float c = a * b;
                    result.setText(c + "");
                }
                catch (Exception e)
                {

                }

            }
        });









        spn_Dialog_subject_bill_items = d.findViewById(R.id.spn_Dialog_subject_bill_items);
        ((HomeActivity) context).load_spinner_materal();

        ArrayAdapter adapterType = new ArrayAdapter(context, android.R.layout.simple_spinner_item, Subjects);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spn_Dialog_subject_bill_items.setAdapter(adapterType);


        Log.i(TAG, "nameMateral: " + nameMateral);
        bb = 0;

        for (int i = 0; i < material_classes.size(); i++) {

            if (material_classes.get(i).mid == bill_items.mmaterial) {
                bb = i;
            }


        }


        Log.i(TAG, "Ahmad: " + Subjects.get(1));
        spn_Dialog_subject_bill_items.setSelection(bb + 1);


        HomeActivity.spMateral = spn_Dialog_subject_bill_items.getSelectedItem().toString();

        ((HomeActivity) context).load_materal_id(HomeActivity.spMateral);


        d.findViewById(R.id.bt_cancel_bill_items).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.findViewById(R.id.bt_save_bill_items).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_Dialog_price_bill_items.getText().length() > 0 && edt_Dialog_quantity_bill_items.getText().length() > 0) {

                    HomeActivity.spMateral = spn_Dialog_subject_bill_items.getSelectedItem().toString();

                    ((HomeActivity) context).load_materal_id(HomeActivity.spMateral);

                    Config.ExecuteNonQuery(context, "update BillItems set bprice='" + edt_Dialog_price_bill_items.getText().toString() + "' where bid=" + bill_items.bid);
                    Config.ExecuteNonQuery(context, "update BillItems set bamount='" + edt_Dialog_quantity_bill_items.getText().toString() + "' where bid=" + bill_items.bid);
                    Config.ExecuteNonQuery(context, "update BillItems set mmaterial='" + materalID + "' where bid=" + bill_items.bid);
                    d.dismiss();
                } else
                    Toast.makeText(context, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
            }
        });

        d.show();

    }

    private void delete_bill(final bill_items bill_items) {
        new AlertDialog.Builder(context)
                .setTitle("حذف")
                .setMessage("هل تريد الحذف " + "  ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        Config.ExecuteNonQuery(context, "delete from BillItems where bid=" + bill_items.bid);
                        alldata.remove(bill_items);
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
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_items, parent, false);
        return new MyViewHolder(v2);
    }
}