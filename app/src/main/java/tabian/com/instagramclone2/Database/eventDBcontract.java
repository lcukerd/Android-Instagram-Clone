package tabian.com.instagramclone2.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Created by Programmer on 14-09-2017.
 */

public class eventDBcontract extends SQLiteOpenHelper
{

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ListofItem.tableName + " (" +
                    ListofItem.columnurl + " TEXT, " +
                    ListofItem.columnimage + " BLOB, " +
                    ListofItem.columnuser + " TEXT );";

    public static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "InstaSwipe.db";

    public eventDBcontract(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("Database", "created");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public static class ListofItem
    {
        public static final String tableName = "Users",
                columnuser = "username",
                columnurl = "url",
                columnimage = "profile_pic";
    }
}
