package lcukerd.com.instaswipe.models;

import android.graphics.Bitmap;

/**
 * Created by User on 6/29/2017.
 */

public class AccountDetails
{

    private String followers;
    private String following;
    private String posts;
    private Bitmap profile_photo;
    private String username;

    public AccountDetails(String followers,
                          String following, String posts, Bitmap profile_photo, String username)
    {
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.profile_photo = profile_photo;
        this.username = username;
    }

    public AccountDetails()
    {

    }

    public String getFollowers()
    {
        return followers;
    }

    public void setFollowers(String followers)
    {
        this.followers = followers;
    }

    public String getFollowing()
    {
        return following;
    }

    public void setFollowing(String following)
    {
        this.following = following;
    }

    public String getPosts()
    {
        return posts;
    }

    public void setPosts(String posts)
    {
        this.posts = posts;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Bitmap getProfile_photo()
    {
        return profile_photo;
    }

    public void setProfile_photo(Bitmap image)
    {
        this.profile_photo = image;
    }


    @Override
    public String toString()
    {
        return "AccountDetails{" +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
