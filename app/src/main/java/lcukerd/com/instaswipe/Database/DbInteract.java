package lcukerd.com.instaswipe.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import lcukerd.com.instaswipe.models.user;

/**
 * Created by Programmer on 14-09-2017.
 */

public class DbInteract
{
    private eventDBcontract dBcontract;
    private String[] projection = {
            eventDBcontract.ListofItem.columnuser,
            eventDBcontract.ListofItem.columnurl,
            eventDBcontract.ListofItem.columnquery,
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
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnurl)),
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnquery))));

        Log.d(TAG, "Returned " + String.valueOf(cursor.getCount()) + " usernames");
        return (usernames);
    }


    public void adduser(Bitmap profilePic,String username,String url,String query)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnimage, getBitmapAsByteArray(profilePic));
        values.put(eventDBcontract.ListofItem.columnuser, username);
        values.put(eventDBcontract.ListofItem.columnurl, url);
        values.put(eventDBcontract.ListofItem.columnquery, query);
        db.insert(eventDBcontract.ListofItem.tableName, null, values);
        Log.d(TAG, "Add User " + username);
    }

    public void deleteuser(String username)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        Log.d(TAG, String.valueOf(db.delete(eventDBcontract.ListofItem.tableName,
                eventDBcontract.ListofItem.columnuser + " = '" + username + "'", null)));
    }

    public void updateprofilepic(String username,Bitmap profilepic)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnimage,getBitmapAsByteArray(profilepic));
        db.update(eventDBcontract.ListofItem.tableName,values,eventDBcontract.ListofItem.columnuser + " = ?",new String[]{username});
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
