package lcukerd.com.instaswipe.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Programmer on 14-09-2017.
 */

public class eventDBcontract extends SQLiteOpenHelper
{

    private static final String SQL_CREATE_ENTRIES1 =
            "CREATE TABLE " + ListofItem.tableName1 + " (" +
                    ListofItem.columnurl + " TEXT, " +
                    ListofItem.columnquery + " TEXT, " +
                    ListofItem.columnimage + " BLOB, " +
                    ListofItem.columnuser + " TEXT );";

    private static final String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE " + ListofItem.tableName2 + " (" +
                    ListofItem.columnpic + " BLOB, " +
                    ListofItem.columnowner + " TEXT );";

    public static int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "InstaSwipe.db";

    public eventDBcontract(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES1);
        db.execSQL(SQL_CREATE_ENTRIES2);
        Log.d("Database", "created");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        switch (newVersion)
        {
            case 2:
                db.execSQL(SQL_CREATE_ENTRIES2);
        }
    }

    public static class ListofItem
    {
        public static final String tableName1 = "Users",
                columnuser = "username",
                columnurl = "url",
                columnquery = "query",
                columnimage = "profile_pic";
        public static final String tableName2 = "Downloads",
                columnowner = "pic_owner",
                columnpic = "pic";

    }
}
