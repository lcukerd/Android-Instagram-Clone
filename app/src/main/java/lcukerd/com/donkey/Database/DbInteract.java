package lcukerd.com.donkey.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lcukerd.com.donkey.models.User;

/**
 * Created by Programmer on 14-09-2017.
 */

public class DbInteract
{
    private eventDBcontract dBcontract;
    private String[] projection1 = {
            eventDBcontract.ListofItem.columnuser,
            eventDBcontract.ListofItem.columnurl,
            eventDBcontract.ListofItem.columnquery,
            eventDBcontract.ListofItem.columnimage
    };
    private String[] projection2 = {
            eventDBcontract.ListofItem.columnowner,
            eventDBcontract.ListofItem.columnpic
    };
    private static String TAG = DbInteract.class.getSimpleName();
    private Context context;

    public DbInteract(Context context)
    {
        this.context = context;
        dBcontract = new eventDBcontract(context);
    }

    public ArrayList<User> readfromDB()
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName1, projection1, null, null, null, null, null);
        ArrayList<User> usernames = new ArrayList<>();
        while (cursor.moveToNext())
            usernames.add(new User(getImage(cursor.getBlob(cursor.getColumnIndex(eventDBcontract.ListofItem.columnimage))),
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnuser)),
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnurl)),
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnquery))));

        Log.d(TAG, "Returned " + String.valueOf(cursor.getCount()) + " usernames");
        return (usernames);
    }

    public void adduser(Bitmap profilePic, String username, String url, String query)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnimage, getBitmapAsByteArray(profilePic));
        values.put(eventDBcontract.ListofItem.columnuser, username);
        values.put(eventDBcontract.ListofItem.columnurl, url);
        values.put(eventDBcontract.ListofItem.columnquery, query);
        db.insert(eventDBcontract.ListofItem.tableName1, null, values);
        Log.d(TAG, "Add User " + username);
    }

    public void deleteuser(String username)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        Log.d(TAG, String.valueOf(db.delete(eventDBcontract.ListofItem.tableName1,
                eventDBcontract.ListofItem.columnuser + " = '" + username + "'", null)));
    }

    public void updateprofilepic(String username, Bitmap profilepic)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnimage, getBitmapAsByteArray(profilepic));
        db.update(eventDBcontract.ListofItem.tableName1, values, eventDBcontract.ListofItem.columnuser + " = ?", new String[]{username});
    }

    public void savepic(Bitmap pic)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        FileOutputStream out = null;
        try
        {
            File image = createImageFile();
            out = new FileOutputStream(image);
            pic.compress(Bitmap.CompressFormat.PNG, 100, out);
            values.put(eventDBcontract.ListofItem.columnpic, image.getPath());
            db.insert(eventDBcontract.ListofItem.tableName2, null, values);
            Log.i(TAG, "Pic saved " + image.getPath());
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException
    {
        String EName = "Image";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(EName, ".jpg", storageDir);
        return image;
    }

    public Bitmap getDownloadedpics(int index)
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName2, projection2, null, null, null, null, null);
        cursor.moveToPosition(index);
        Bitmap photo = null;
        try
        {
            Log.d(TAG, cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnpic)));
            File fos = new File(cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnpic)));
            photo = BitmapFactory.decodeStream(new FileInputStream(fos));
        } catch (IOException e)
        {
            Log.e(TAG, "Can't read image");
        }
        Log.d(TAG, "Returned " + String.valueOf(cursor.getCount()) + " pics");
        return (photo);
    }

    public int numberofdownloads()
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName2, projection2, null, null, null, null, null);
        Log.d(TAG, "Returned " + String.valueOf(cursor.getCount()) + " pics");
        return (cursor.getCount());
    }

    public void deletedownloadedpic(int index)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName2, projection2, null, null, null, null, null);
        cursor.moveToPosition(index);
        String uri = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnpic));
        Log.d(TAG, "Deleted " + String.valueOf(db.delete(eventDBcontract.ListofItem.tableName2,
                eventDBcontract.ListofItem.columnpic + " = '" + uri + "'", null)) + " pic.");
        File file = new File(uri);
        file.delete();
    }


    public static byte[] getBitmapAsByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getImage(byte[] image)
    {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
