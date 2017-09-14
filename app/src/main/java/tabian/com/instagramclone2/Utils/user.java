package tabian.com.instagramclone2.Utils;

import android.graphics.Bitmap;

/**
 * Created by Programmer on 14-09-2017.
 */

public class user
{
    public Bitmap profile;
    public String name;
    public String url;

    public user(Bitmap p,String n,String u)
    {
        profile = p;
        name = n;
        url = u;
    }
}
