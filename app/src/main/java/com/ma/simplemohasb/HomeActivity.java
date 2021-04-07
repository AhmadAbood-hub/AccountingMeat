package com.ma.simplemohasb;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.ma.simplemohasb.BillAdapter.bill;

public class HomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String TAG = Config.TAG;
    public static int where_me = 0;


    public Menu menu;

    public static kills kills;

    public static String spMateral;

    public static int materalID;

    private SwipeRefreshLayout refreshSwipe;

    public static TextView txt_date_kill;

    public static List<String> Subjects;
    public static List<material_class> material_classes;

    public CardView crdDetailsBillItems;

    public CardView crdDetailsBills;

    public CardView crdDetailsSummary;

    public static int totalPrice;

    public static Spinner spn_Dialog_subject_bill_items;

    private final int REQUEST_EXTERNAL = 1000;

    private boolean isExternalGranted = false;

    private Uri bak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);


        Subjects = new ArrayList<>();
        Subjects.add("");

        material_classes = new ArrayList<>();

        File f = new File("mnt/sdcard/simple_mohasb/");
        if (!f.exists())
            f.mkdir();
        refreshSwipe = findViewById(R.id.refreshSwipe);

        crdDetailsBillItems = findViewById(R.id.crdDetailsBillItems);
        crdDetailsBills = findViewById(R.id.crdDetailsBills);
        crdDetailsSummary = findViewById(R.id.crdDetailsSummary);

        refreshSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (where_me == 1)
                    load_material();
                else if (where_me == 2)
                    load_kill();
                else if (where_me == 3)
                    load_bill();
                else if (where_me == 4)
                    load_bill_items();
                else if (where_me == 5)
                    load_spent();
                else if (where_me == 6)
                    load_summary();

                refreshSwipe.setRefreshing(false);
            }
        });


        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestRuntimePermission();
        } else {
            isExternalGranted = true;
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Config.CreateDB(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.i(TAG, "onNavigationItemSelected: 1");
                navigation_clieckd(menuItem);
                drawer.closeDrawers();
                return false;
            }
        });
    }

    private void navigation_clieckd(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_material:
                load_material();
                break;

            case R.id.nav_kill:
                load_kill();
                break;

            case R.id.nav_bill:
                load_bill();
                break;

            case R.id.nav_spent:
                load_spent();
                break;

            case R.id.nav_summry:
                load_summary();
                break;

            case R.id.nav_backup:
                backUp();
                break;

            case R.id.nav_restore:
                backUp();
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(DialogConfigs.DEFAULT_DIR);
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = null;
                properties.show_hidden_files = false;
                FilePickerDialog dialog = new FilePickerDialog(HomeActivity.this, properties);
                dialog.setTitle("Select a File");

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        restore(files[0]);
                    }
                });
                dialog.show();
                break;


        }
    }

    private void load_material() {
        where_me = 1;
        try {
            crdDetailsBillItems.setVisibility(View.GONE);
            crdDetailsBills.setVisibility(View.GONE);
            crdDetailsSummary.setVisibility(View.GONE);
            ArrayList<material_class> all_data = new ArrayList<>();
            SQLiteDatabase db = openOrCreateDatabase(Config.dbName, MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select * from materials ", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    material_class c = new material_class();
                    c.mid = cursor.getInt(cursor.getColumnIndex("mid"));
                    c.mname = cursor.getString(cursor.getColumnIndex("mname"));
                    all_data.add(c);
                    cursor.moveToNext();
                }
            }

            RecyclerView RCV = findViewById(R.id.recycle_data);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            RCV.setLayoutManager(linearLayoutManager);
            RCV.setNestedScrollingEnabled(false);
            MaterialAdapter adp = new MaterialAdapter(this, all_data);
            //   load_more_data();
            RCV.setAdapter(adp);
        } catch (Exception ex) {
        }
    }


    private void load_kill() {
        where_me = 2;
        try {
            crdDetailsBillItems.setVisibility(View.GONE);
            crdDetailsBills.setVisibility(View.GONE);
            crdDetailsSummary.setVisibility(View.GONE);
            ArrayList<kills> all_data = new ArrayList<>();
            SQLiteDatabase db = openOrCreateDatabase(Config.dbName, MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select * from kills ", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    kills = new kills();
                    kills.kid = cursor.getString(cursor.getColumnIndex("kid"));
                    kills.knotes = cursor.getString(cursor.getColumnIndex("knotes"));
                    kills.kdate = cursor.getString(cursor.getColumnIndex("kdate"));
                    all_data.add(kills);
                    cursor.moveToNext();
                }
            }

            RecyclerView RCV = findViewById(R.id.recycle_data);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            RCV.setLayoutManager(linearLayoutManager);
            RCV.setNestedScrollingEnabled(false);
            KillAdapter adp = new KillAdapter(this, all_data);
            //   load_more_data();
            RCV.setAdapter(adp);
        } catch (Exception ex) {
        }
    }

    public void load_bill() {
        where_me = 3;
        try {
            crdDetailsBillItems.setVisibility(View.GONE);
            crdDetailsBills.setVisibility(View.VISIBLE);
            crdDetailsSummary.setVisibility(View.GONE);
            ArrayList<bills> all_data = new ArrayList<>();
            SQLiteDatabase db = openOrCreateDatabase(Config.dbName, MODE_PRIVATE, null);
            // Cursor cursor = db.rawQuery("select * from bills where kid = " + kills.kid, null);
            Cursor cursor = db.rawQuery("select * from bills where kid = " + kills.kid, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    bills bill = new bills();
                    bill.bid = cursor.getString(cursor.getColumnIndex("bid"));
                    bill.baccountname = cursor.getString(cursor.getColumnIndex("baccountname"));
                    bill.bnotes = cursor.getString(cursor.getColumnIndex("bnotes"));
                    bill.kid = cursor.getString(cursor.getColumnIndex("kid"));

                    all_data.add(bill);
                    cursor.moveToNext();
                }
            }

            RecyclerView RCV = findViewById(R.id.recycle_data);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            RCV.setLayoutManager(linearLayoutManager);
            RCV.setNestedScrollingEnabled(false);
            BillAdapter adp = new BillAdapter(this, all_data);
            RCV.setAdapter(adp);

        } catch (Exception e) {

        }
    }


    public void load_spent() {
        where_me = 5;
        try {
            crdDetailsBillItems.setVisibility(View.GONE);
            crdDetailsBills.setVisibility(View.GONE);
            crdDetailsSummary.setVisibility(View.GONE);
            ArrayList<spents> all_data = new ArrayList<>();
            SQLiteDatabase db = openOrCreateDatabase(Config.dbName, MODE_PRIVATE, null);
            // Cursor cursor = db.rawQuery("select * from bills where kid = " + kills.kid, null);
            Cursor cursor = db.rawQuery("select * from spents where kid = " + Config.GET_KEY(this, "ID"), null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    spents spent = new spents();
                    spent.kid = cursor.getInt(cursor.getColumnIndex("kid"));
                    spent.samount = cursor.getInt(cursor.getColumnIndex("samount"));
                    spent.sid = cursor.getInt(cursor.getColumnIndex("sid"));
                    spent.snotes = cursor.getString(cursor.getColumnIndex("snotes"));

                    all_data.add(spent);
                    cursor.moveToNext();
                }
            }

            RecyclerView RCV = findViewById(R.id.recycle_data);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            RCV.setLayoutManager(linearLayoutManager);
            RCV.setNestedScrollingEnabled(false);
            SpentAdapter adp = new SpentAdapter(this, all_data);
            RCV.setAdapter(adp);

        } catch (Exception e) {

        }
    }

    public void load_summary() {
        where_me = 6;
        try {
            crdDetailsBillItems.setVisibility(View.GONE);
            crdDetailsBills.setVisibility(View.GONE);
            crdDetailsSummary.setVisibility(View.VISIBLE);
            ArrayList<bill_items> all_data = new ArrayList<>();
            SQLiteDatabase db = openOrCreateDatabase(Config.dbName, MODE_PRIVATE, null);
            // Cursor cursor = db.rawQuery("select * from bills where kid = " + kills.kid, null);
            Cursor cursor = db.rawQuery("select sum(bprice*bamount) as price ,sum(bamount) as amount,mmaterial,(select mname from materials where materials.mid =BillItems.mmaterial  ) " +
                    "as mName from BillItems where bbill in (select bid from bills where kid = " + Config.GET_KEY(this, "ID") + ") group by mmaterial ", null);
            Log.i(TAG, "load_summary: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int sum = 0;
                for (int i = 0; i < cursor.getCount(); i++) {
                    bill_items bill_items = new bill_items();
                    bill_items.mName = (cursor.getString(cursor.getColumnIndex("mName")));
                    bill_items.bamount = (cursor.getFloat(cursor.getColumnIndex("amount")));
                    bill_items.bprice = (cursor.getFloat(cursor.getColumnIndex("price")));


                    sum += bill_items.bprice;
                    all_data.add(bill_items);
                    cursor.moveToNext();

                }
                bill_items bill_items1 = new bill_items();
                bill_items1.mName = "المصاريف";
                bill_items1.bprice = Integer.valueOf(Config.ExecuteScalar(this, "select sum(samount) from spents where kid =  " + Config.GET_KEY(this, "ID"))) * -1;
                all_data.add(bill_items1);
                float a = bill_items1.bprice;

                bill_items1 = new bill_items();
                bill_items1.mName = "الملخص";
                bill_items1.bprice = sum + a;
                all_data.add(bill_items1);

                RecyclerView RCV = findViewById(R.id.recycle_data);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                RCV.setLayoutManager(linearLayoutManager);
                RCV.setNestedScrollingEnabled(false);
                SummaryAdapter adp = new SummaryAdapter(this, all_data);
                RCV.setAdapter(adp);

            }
        } catch (Exception e) {

        }
    }

    public void load_spinner_materal() {
        Subjects.clear();
        material_classes.clear();
        Subjects.add("");
        SQLiteDatabase db = openOrCreateDatabase(Config.dbName, MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select * from materials ", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                material_class c = new material_class();
                c.mname = cursor.getString(cursor.getColumnIndex("mname"));
                c.mid = cursor.getInt(cursor.getColumnIndex("mid"));
                material_classes.add(c);
                Subjects.add(c.mname);
                cursor.moveToNext();
            }


        }
    }

    public static void load_materal_id(String nameMateral) {

        for (material_class material_class : material_classes) {
            if (material_class.mname == nameMateral) {
                materalID = material_class.mid;
                return;
            }
        }

    }

    public void load_total_price_bill(String billID) {
        try {

            SQLiteDatabase db = openOrCreateDatabase(Config.dbName, MODE_PRIVATE, null);

            Cursor cursor = db.rawQuery("select Sum(bprice*bamount) as price from BillItems  where bbill =  " + billID, null);
            Log.i(TAG, "load_bill_items: " + cursor.getCount());

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    bill_items bill_items = new bill_items();

                    cursor.moveToFirst();

                    totalPrice = cursor.getInt(cursor.getColumnIndex("price"));

                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {

        }
    }


    public void load_bill_items() {
        where_me = 4;
        try {
            crdDetailsBillItems.setVisibility(View.VISIBLE);
            crdDetailsBills.setVisibility(View.GONE);
            ArrayList<bill_items> all_data = new ArrayList<>();
            SQLiteDatabase db = openOrCreateDatabase(Config.dbName, MODE_PRIVATE, null);
            Log.e(TAG, "load_bill_items: " + bill.bid);
            Cursor cursor = db.rawQuery("select * from BillItems where bbill =  " + bill.bid, null);
            Log.i(TAG, "load_bill_items: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    bill_items bill_items = new bill_items();

                    bill_items.mmaterial = (cursor.getInt(cursor.getColumnIndex("mmaterial")));
                    bill_items.bamount = (cursor.getFloat(cursor.getColumnIndex("bamount")));
                    bill_items.bprice = (cursor.getFloat(cursor.getColumnIndex("bprice")));
                    bill_items.bid = (cursor.getInt(cursor.getColumnIndex("bid")));
                    bill_items.bbill = (cursor.getInt(cursor.getColumnIndex("bbill")));

                    all_data.add(bill_items);
                    cursor.moveToNext();
                }
            }

            RecyclerView RCV = findViewById(R.id.recycle_data);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            RCV.setLayoutManager(linearLayoutManager);
            RCV.setNestedScrollingEnabled(false);
            BillItemsAdapter adp = new BillItemsAdapter(this, all_data);
            RCV.setAdapter(adp);

        } catch (Exception e) {

        }
    }


    private void add() {
        if (where_me == 1) {
            final Dialog d = new Dialog(this);
            d.setContentView(R.layout.dialog_edit_material);
            final EditText txt_name = d.findViewById(R.id.txt_name);
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
                        Config.ExecuteNonQuery(HomeActivity.this, "insert into materials(mname) values ('" + txt_name.getText().toString() + "') ");
                        load_material();
                        d.dismiss();
                    } else
                        Toast.makeText(HomeActivity.this, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
                }
            });
            d.show();
        }

        if (where_me == 2) {
            final Dialog d = new Dialog(this);
            d.setContentView(R.layout.dialog_edit_kill);
            final EditText txt_note_kill = d.findViewById(R.id.txt_note_kill);
            txt_date_kill = d.findViewById(R.id.txt_date_kill);
            txt_date_kill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment datePicker = new DialogDatePicker();
                    datePicker.show(getSupportFragmentManager(), "datepicker");
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
                    if (txt_note_kill.getText().length() > 0 && txt_date_kill.getText().length() > 0) {
                        Config.ExecuteNonQuery(HomeActivity.this, "insert into kills(knotes,kdate) values ('" + txt_note_kill.getText().toString() + "','" + txt_date_kill.getText().toString() + "') ");

                        load_kill();
                        d.dismiss();
                    } else
                        Toast.makeText(HomeActivity.this, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
                }
            });
            d.show();
        }


        if (where_me == 3) {
            final Dialog d = new Dialog(this);
            d.setContentView(R.layout.dialog_edit_bill);
            final EditText edt_Dialog_baccountname_bill = d.findViewById(R.id.edt_Dialog_baccountname_bill);
            final EditText edt_Dialog_note_bill = d.findViewById(R.id.edt_Dialog_note_bill);

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
                        Config.ExecuteNonQuery(HomeActivity.this, "insert into bills(bnotes,baccountname,kid) values ('" + edt_Dialog_note_bill.getText().toString() + "','"
                                + edt_Dialog_baccountname_bill.getText().toString() + "'," + kills.kid + ")");

                        load_bill();
                        d.dismiss();
                    } else
                        Toast.makeText(HomeActivity.this, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
                }
            });
            d.show();
        }

        if (where_me == 4) {
            final Dialog d = new Dialog(this);
            d.setContentView(R.layout.dialog_edit_bill_items);
            final EditText edt_Dialog_quantity_bill_items = d.findViewById(R.id.edt_Dialog_quantity_bill_items);
            final EditText edt_Dialog_price_bill_items = d.findViewById(R.id.edt_Dialog_price_bill_items);
            spn_Dialog_subject_bill_items = d.findViewById(R.id.spn_Dialog_subject_bill_items);
            TextView result = d.findViewById(R.id.result);

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
                    } catch (Exception e) {

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

                    try {
                        float a = Float.parseFloat(edt_Dialog_price_bill_items.getText().toString());
                        float b = Float.parseFloat(edt_Dialog_quantity_bill_items.getText().toString());
                        float c = a * b;
                        result.setText(c + "");
                    } catch (Exception e) {

                    }

                }
            });


            load_spinner_materal();


            ArrayAdapter adapterType = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Subjects);
            adapterType.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spn_Dialog_subject_bill_items.setAdapter(adapterType);


            spMateral = spn_Dialog_subject_bill_items.getSelectedItem().toString();


            d.findViewById(R.id.bt_cancel_bill_items).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.findViewById(R.id.bt_save_bill_items).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    spMateral = spn_Dialog_subject_bill_items.getSelectedItem().toString();
                    load_materal_id(spMateral);


                    if (edt_Dialog_price_bill_items.getText().length() > 0 && edt_Dialog_quantity_bill_items.getText().length() > 0) {
                        Config.ExecuteNonQuery(HomeActivity.this, "insert into BillItems(bamount,bprice,bbill,mmaterial) values (" + (edt_Dialog_quantity_bill_items.getText().toString()) + ","
                                + edt_Dialog_price_bill_items.getText().toString() + "," + bill.bid + "," + materalID + ")");

                        load_bill_items();
                        d.dismiss();
                    } else
                        Toast.makeText(HomeActivity.this, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
                }
            });
            d.show();
        }

        if (where_me == 5) {
            final Dialog d = new Dialog(this);
            d.setContentView(R.layout.dialog_edit_spent);
            final EditText edt_Dialog_price_spent = d.findViewById(R.id.edt_Dialog_price_spent);
            final EditText edt_Dialog_note_spent = d.findViewById(R.id.edt_Dialog_note_spent);

            d.findViewById(R.id.bt_cancel_spent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.findViewById(R.id.bt_save_spent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (edt_Dialog_note_spent.getText().length() > 0 && edt_Dialog_price_spent.getText().length() > 0) {
                        Config.ExecuteNonQuery(HomeActivity.this, "insert into spents(snotes,samount,kid)  values ('" + edt_Dialog_note_spent.getText().toString() + "',"
                                + edt_Dialog_price_spent.getText().toString() + "," + HomeActivity.kills.kid + ")");

                        load_spent();
                        d.dismiss();
                    } else
                        Toast.makeText(HomeActivity.this, "أدخل قيمة مقبولة ", Toast.LENGTH_SHORT).show();
                }
            });
            d.show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu1) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu1);

        menu = menu1;
        try {
            if (kills != null)
                menu.findItem(R.id.action_date_bill).setTitle(kills.kdate);
            else
                menu.findItem(R.id.action_date_bill).setTitle(Config.GET_KEY(this, "Date"));

        } catch (Exception e) {

        }
        return true;
    }

    public void showDialogDate() {
        DialogFragment datePicker = new DialogDatePicker();
        datePicker.show(getSupportFragmentManager(), "datepicker");
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        month += 1;
        txt_date_kill.setText(year + "/" + month + "/" + day);


    }

    public void refreshMenu() {
        try {
            if (kills != null)
                menu.findItem(R.id.action_date_bill).setTitle(kills.kdate);
            else
                menu.findItem(R.id.action_date_bill).setTitle("213");

        } catch (Exception e) {

        }

    }


    public void backUp() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "data/com.ma.simplemohasb/databases/" + Config.dbName;
                String backupDBPath = "/simple_mohasb/DB_" + Config.GET_DATE_DB() + ".bak";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.d("backupDB path", "" + backupDB.getAbsolutePath());

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(), "Backup is successful to SD card", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();


        }
    }


    public void restore(String s) {


        try {
            String destinationPath = "data/data/com.ma.simplemohasb/databases/" + Config.dbName;
            try {
                moveFile(s, destinationPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "Database Restored successfully", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(
                HomeActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_EXTERNAL);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isExternalGranted = true;
                }
            }
            break;
        }
    }


    private void moveFile(String inputFile, String outputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {


            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;


        } catch (Exception e) {
        }

    }

}

