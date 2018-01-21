package lcukerd.com.instaswipe.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lcukerd.com.instaswipe.models.User;

/**
 * Created by Programmer on 15-09-2017.
 */

public class Scrapper
{
    private static final String tag = Scrapper.class.getSimpleName();

    public static ArrayList<User> getUsersfromsearch(String result)
    {
        ArrayList<User> userArrayList = new ArrayList<>();
        try
        {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("users");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                User u = new User();
                jsonObject = jsonArray.getJSONObject(i);
                jsonObject = jsonObject.getJSONObject("user");

                u.name = jsonObject.getString("full_name");
                u.query = jsonObject.getString("username");
                u.url = jsonObject.getString("profile_pic_url");
                u.verfied = jsonObject.getBoolean("is_verified");
                if (jsonObject.getBoolean("is_private") == true)
                    u.isprivate = " (private)";
                else
                    u.isprivate = "";
                userArrayList.add(u);
            }
        } catch (JSONException e)
        {
            Log.e(tag, "Error in Json " + result);
        }
        return userArrayList;
    }

    public static String getUsername(String result)
    {
        int start = result.indexOf("<meta property=\"og:title\" content=");
        int end = result.indexOf("Instagram photos", result.indexOf("<meta property=\"og:title\" content="));
        return result.substring(start + 35, end - 5);
    }

    public static String getProfilePicUrl(String result)
    {
        int start = result.indexOf("<meta property=\"og:image\" content=\"");
        int end = result.indexOf(" />", result.indexOf("<meta property=\"og:image\" content="));
        return result.substring(start + 35, end - 1);
    }

    /**
     * media link that are video start with v followedby code then '@' after that url of thumbnail starts.
     */
    public static String getimageUrl(String result, int pos, boolean full)
    {
        int start = result.indexOf("thumbnail_src", pos);
        if ((start == -1) && (pos == 0))
            return "private";
        else if (start == -1)
            return "end";
        int end = result.indexOf("\",", result.indexOf("thumbnail_src", pos));
        pos = end;
        int checkvideo = result.indexOf("\"is_video\"", pos);
        String isvideo = result.substring(checkvideo + 12, result.indexOf(',', checkvideo));
        if (isvideo.equals("true"))
        {
            int cstart = result.indexOf(',', checkvideo) + 11;
            int cend = result.indexOf('"', cstart);
            String code = result.substring(cstart, cend);
            Log.i(tag, "is video " + isvideo + "  " + code);
            return "v" + code + "@" + result.substring(start + 17, end);
        } else if (full)
        {
            start = result.indexOf("display_src", pos);
            end = result.indexOf("\",", result.indexOf("display_src", pos));
            return result.substring(start + 15, end);
        }
        else
            return result.substring(start + 17, end);
    }

    public static String getnextpageID(String result, int pos)
    {
        int index = 21;
        int start = result.indexOf("\"GraphImage\", \"id\"", pos);
        if (start == -1)
            start = result.indexOf("\"GraphVideo\", \"id\"", pos);
        if (start == -1)
        {
            index += 2;
            start = result.indexOf("\"GraphSidecar\", \"id\"", pos);
        }
        int end = result.indexOf("\",", start + 21);
        return result.substring(start + index, end);
    }

    public static String formatURLforfullscreen(String tempurl)
    {
        int istart = tempurl.indexOf("s640x640/", 0);
        if (istart == -1)
            istart = tempurl.indexOf("s360x360/", 0);
        if (istart == -1)
            istart = tempurl.indexOf("s150x150/", 0);
        if (istart == -1)
            return tempurl;

        int iend = tempurl.lastIndexOf('/');

        Log.i(tag, String.valueOf(istart) + " " + String.valueOf(iend) + " " + tempurl);

        return tempurl.substring(0, istart) + tempurl.substring(iend);
    }

    public static String getFollowers(String result)
    {
        int index = result.indexOf("Followers");
        int start = result.indexOf('"', index - 12);
        return result.substring(start + 1, index - 1);
    }

    public static String getFollowing(String result)
    {
        int index = result.indexOf("Following");
        int start = result.indexOf(',', index - 12);
        return result.substring(start + 2, index - 1);
    }

    public static String getPosts(String result)
    {
        int index = result.indexOf("Posts");
        int start = result.indexOf(',', index - 12);
        return result.substring(start + 2, index - 1);
    }

    public static String getVideoPageUrl(String code, String id)
    {
        int start = id.indexOf("m/") + 2;
        int end = id.indexOf("/", start);
        String url = "https://www.instagram.com/p/" + code + "/?taken-by=" + id.substring(start, end);
        Log.i(tag, url);
        return url;
    }

    public static String getVideoUrl(String result)
    {
        int start = result.indexOf("\"og:video\" content") + 20;
        int end = result.indexOf("\" />", start);
        String videoUrl = result.substring(start, end);
        Log.i(tag, "Video Url " + videoUrl);
        return videoUrl;
    }
}
