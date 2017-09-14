package tabian.com.instagramclone2.Database;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.CheckBox;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import tabian.com.instagramclone2.Utils.user;

/**
 * Created by Programmer on 14-09-2017.
 */

public class DbInteract
{
    private eventDBcontract dBcontract;
    private String[] projection = {
            eventDBcontract.ListofItem.columnuser,
            eventDBcontract.ListofItem.columnurl,
            eventDBcontract.ListofItem.columnimage
    };
    private static String TAG = DbInteract.class.getSimpleName();

    public DbInteract(Context context)
    {
        dBcontract = new eventDBcontract(context);
    }

    public ArrayList<user> readfromDB()
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName, projection, null, null, null, null, null);

        ArrayList<user> usernames = new ArrayList<>();

        while (cursor.moveToNext())
            usernames.add(new user(getImage(cursor.getBlob(cursor.getColumnIndex(eventDBcontract.ListofItem.columnimage))),
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnuser)),
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnuser))));

        Log.d(TAG, "Returned " + String.valueOf(cursor.getCount()) + " usernames");
        return (usernames);
    }


    public void adduser(Bitmap profilePic,String username,String url)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnimage, getBitmapAsByteArray(profilePic));
        values.put(eventDBcontract.ListofItem.columnuser, username);
        values.put(eventDBcontract.ListofItem.columnurl, url);
        db.insert(eventDBcontract.ListofItem.tableName, null, values);
        Log.d(TAG, "Add User " + username);
    }

    public void deleteuser(String username)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        Log.d(TAG, String.valueOf(db.delete(eventDBcontract.ListofItem.tableName,
                eventDBcontract.ListofItem.columnuser + " = '" + username + "'", null)));
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
