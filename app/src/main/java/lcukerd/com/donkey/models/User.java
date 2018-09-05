package lcukerd.com.donkey.models;

import android.graphics.Bitmap;


/**
 * Created by Programmer on 14-09-2017.
 */

public class User
{
    public Bitmap profile;
    public String name;
    public String url;
    public String query;
    public String isprivate;
    public boolean verfied = false;

    public User()
    {}

    public User(Bitmap p, String n, String u, String q)
    {
        profile = p;
        name = n;
        url = u;
        query = q;
    }
}
