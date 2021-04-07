package com.ma.simplemohasb;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class Config {
    public static final String TAG = "KILL-TAGS";
    public static String dbName = "killDB";
    static String k = "4nod24agfff2323s";
    static String sa = "nof3scdgt8sdcvff";

    public static String EM(String message) {
        ENC encryption = ENC.getDefault(k, sa, new byte[16]);
        String encrypted = encryption.encryptOrNull(message);
        return encrypted;
    }

    public static String DM(String cipherText) {
        ENC encryption = ENC.getDefault(k, sa, new byte[16]);
        String decrypted = encryption.decryptOrNull(cipherText);
        return decrypted;
    }

    private static String key = "kvmj124#%&*ivps>";

    public static String Decrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance
                    ("AES/CBC/PKCS5Padding"); //this parameters should not be changed
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");
            int len = b.length;
            if (len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] results = new byte[text.length()];
            try {
                results = cipher.doFinal(Base64.decode(text, 0));
            } catch (Exception e) {
                Log.i("Erron in Decryption", e.toString());
            }
            String ss = new String(results, "UTF-8");
            return ss;// it returns the result as a String
        } catch (Exception ex) {
        }
        return "-1";
    }

    public static String Encrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");
            int len = b.length;
            if (len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
            return Base64.encodeToString(results, 0); // it returns the result as a String
        } catch (Exception ex) {

        }
        return "-1";
    }


    public static void CreateDB(Context ctx) {
        try {
            SQLiteDatabase db = ctx.openOrCreateDatabase(Config.dbName, ctx.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS [values] (\n" +
                    "  [key] [nvaRCHAR(50)], \n" +
                    "  [val] [nvaRCHAR(50)]);\n");


            db.execSQL("CREATE TABLE if not exists [BillItems](\n" +
                    "[bid] integer PRIMARY KEY AUTOINCREMENT\n" +
                    ",[bbill] integer\n" +
                    ",[mmaterial] integer\n" +
                    ",[bamount] float\n" +
                    ",[bprice] float\n" +
                    "   \n" +
                    ");");

            db.execSQL("CREATE TABLE if not exists [bills](\n" +
                    "[bid] integer PRIMARY KEY AUTOINCREMENT\n" +
                    ",[kid] integer\n" +
                    ",[baccountname] nvarchar(100)\n" +
                    ",[bnotes] nvarchar(1000)\n" +
                    "   \n" +
                    ");");

            db.execSQL("CREATE TABLE if not exists [materials](\n" +
                    "[mid] integer PRIMARY KEY AUTOINCREMENT\n" +
                    ",[mname] nvarchar(100)\n" +
                    "   \n" +
                    ");");


            db.execSQL("CREATE TABLE if not exists[kills](\n" +
                    "[kid] integer PRIMARY KEY AUTOINCREMENT\n" +
                    ",[kdate] bigint\n" +
                    ",[knotes] nvarchar(1000)\n" +
                    "   \n" +
                    ");\n" +
                    "\n");

            db.execSQL("CREATE TABLE if not exists [spents](\n" +
                    "[sid] integer PRIMARY KEY AUTOINCREMENT\n" +
                    ",[kid] integer\n" +
                    ",[samount] integer\n" +
                    ",[snotes] nvarchar(1000)\n" +
                    "   \n" +
                    ");\n" +
                    "\n");
            db.close();


            if (GET_KEY(ctx, "tag").equals("No KEY")) {
                SaveKEY(ctx, "tag", "0");
            }



            if (GET_KEY(ctx, "ID").equals("No KEY")) {
                SaveKEY(ctx, "ID", "0");
            }


        } catch (Exception ex) {
        }
    }

    public static void SaveKEY(Context ctx, String key, String val) {
        try {
            SQLiteDatabase db = ctx.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select count(*) from [values] where [key]='" + EM(key) + "' ;", null);
            cursor.moveToFirst();
            if (cursor.getInt(0) > 0) {
                db.execSQL("update [values] set [val]='" + EM(val) + "' where [key]='" + EM(key) + "' ;");
            } else {
                db.execSQL("insert into [values]([key],[val]) values ('" + EM(key) + "' , '" + EM(val) + "') ;");
            }
            db.close();
        } catch (Exception ex) {
        }
    }


    public static String GET_KEY(Context ctx, String key) {
        try {
            SQLiteDatabase db = ctx.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select [val] from [values] where [key]='" + EM(key) + "' ;", null);
            cursor.moveToFirst();
            String s = cursor.getString(cursor.getColumnIndex("val"));
            s = DM(s);
            db.close();
            return s;
        } catch (Exception ex) {
            return "No KEY";
        }
    }


    public static void ExecuteNonQuery(Context ctx, String q) {
        try {
            Log.i(TAG, "ExecuteNonQuery: " + q);
            SQLiteDatabase db = ctx.openOrCreateDatabase(dbName, ctx.MODE_PRIVATE, null);
            db.execSQL(q);
            db.close();
        } catch (Exception ex) {
            Log.d(TAG, "ExecuteNonQuery: " + ex.getMessage());
        }
    }


    public static String GET_PATH(Context ctx) {
        String path = ctx.getFilesDir().getAbsolutePath() + "/";
        File f = new File(path);
        if (!f.exists())
            f.mkdirs();
        return path;
    }

    public static String ExecuteScalar(Context ctx, String query) {
        try {
            SQLiteDatabase db = ctx.openOrCreateDatabase(dbName, ctx.MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String s = cursor.getString(0);
                if (s == null)
                    return "0";
                return s;
            }
            db.close();
        } catch (Exception ex) {
            Log.d(TAG, "ExecuteScalar: " + ex.getMessage());
        }
        return "0";
    }

    public static String GET_DATE() {
        try {
            Date c = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            String formattedDate = df.format(c);
            return formattedDate;
        } catch (Exception ex) {
            return "";
        }
    }
    public static String GET_DATE_DB() {
        try {
            Date c = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            String formattedDate = df.format(c);
            return formattedDate;
        } catch (Exception ex) {
            return "";
        }
    }
    public static long GET_DATE_NOW() {
        try {
            Date c = new Date();
            return c.getTime() / 1000;
        } catch (Exception ex) {
        }
        return 0;
    }

    public static String GET_DATE_FROM_INT(long dd) {
        try {
            Date c = new Date(dd);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
            String formattedDate = df.format(c);
            return formattedDate;
        } catch (Exception ex) {
            return "";
        }


    }
}
