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

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.ma.simplemohasb.HomeActivity.txt_date_kill;


public class KillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static ArrayList<kills> alldata = new ArrayList<kills>();


    Context context;

    public void addNewData(List<kills> alldata1) {

        for (int i = 0; i < alldata1.size(); i++) {
            alldata.add(alldata1.get(i));
        }

        this.notifyDataSetChanged();
    }

    public void RefreshData() {
        this.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView item_date_kill;
        public TextView item_note_kill;
        public ImageView edit;
        public ImageView delete;


        public MyViewHolder(View view) {
            super(view);
            item_date_kill = view.findViewById(R.id.item_date_kill);
            item_note_kill = view.findViewById(R.id.item_note_kill);
            edit = view.findViewById(R.id.item_kill_edit);
            delete = view.findViewById(R.id.item_kill_delete);

        }

    }


    int type = 0;

    public KillAdapter(Context context, ArrayList<kills> alldata) {
        this.alldata = alldata;
        this.context = context;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        ((MyViewHolder) holder).item_date_kill.setText(alldata.get(position).kdate);

        ((MyViewHolder) holder).item_note_kill.setText(alldata.get(position).knotes);

        ((MyViewHolder) holder).delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_kill(alldata.get(position));
            }
        });

        ((MyViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.kills = alldata.get(position);
                Config.SaveKEY(context,"ID", HomeActivity.kills.kid);
                Config.SaveKEY(context,"Date", HomeActivity.kills.kdate);
                ((HomeActivity) context).load_bill();
                ((HomeActivity) context).refreshMenu();
            }
        });


        ((MyViewHolder) holder).edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_kill(alldata.get(position));

            }
        });

    }

    private void edit_kill(final kills kills) {

        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.dialog_edit_kill);
        final EditText txt_note_kill = d.findViewById(R.id.txt_note_kill);
        txt_date_kill = d.findViewById(R.id.txt_date_kill);
        txt_note_kill.setText(kills.knotes);
        txt_date_kill.setText(kills.kdate);
        txt_date_kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((HomeActivity) context).showDialogDate();


            }
        });
        d.findViewById(R.id.bt_cancel_kill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.findViewById(R.id.bt_save_kill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_note_kill.getText().length() > 0) {
                    Config.ExecuteNonQuery(context, "update kills set knotes='" + txt_note_kill.getText().toString() + "' where kid=" + kills.kid);
                    Config.ExecuteNonQuery(context, "update kills set kdate='" + txt_date_kill.getText().toString() + "' where kid=" + kills.kid);
                    d.dismiss();
                } else
                    Toast.makeText(context, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
            }
        });

        d.show();

    }

    private void delete_kill(final kills kills) {
        new AlertDialog.Builder(context)
                .setTitle("حذف")
                .setMessage("هل تريد الحذف " + "  ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        Config.ExecuteNonQuery(context, "delete from kills where kid=" + kills.kid);
                        alldata.remove(kills);
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
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kill, parent, false);
        return new MyViewHolder(v2);
    }
}