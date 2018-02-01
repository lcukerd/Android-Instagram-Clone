package lcukerd.com.instaswipe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import lcukerd.com.instaswipe.Database.DbInteract;
import lcukerd.com.instaswipe.Utils.BottomNavigationViewHelper;
import lcukerd.com.instaswipe.Utils.Scrapper;
import lcukerd.com.instaswipe.adapter.GridImageAdapter;
import lcukerd.com.instaswipe.models.AccountDetails;

/**
 * Created by User on 5/28/2017.
 */

public class ProfileActivity extends AppCompatActivity
{
    private static final String tag = ProfileActivity.class.getSimpleName();
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    private TextView mPosts, mFollowers, mFollowing, mUsername;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private ProgressBar mProgressBar;

    private ArrayList<String> photos = new ArrayList<>();
    private ArrayList<String> fullphotos = new ArrayList<>();
    private ArrayList<Bitmap> downloads = new ArrayList<>();
    String idurl, sourceCode, username, profilepicURL;
    Bitmap profilePic;

    private BottomNavigationViewEx bottomNavigationView;
    private DbInteract interact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        Log.d(tag, "onCreate: started.");

        mUsername = (TextView) findViewById(R.id.username);
        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo);
        mPosts = (TextView) findViewById(R.id.tvPosts);
        mFollowers = (TextView) findViewById(R.id.tvFollowers);
        mFollowing = (TextView) findViewById(R.id.tvFollowing);
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        gridView = (GridView) findViewById(R.id.gridView);
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        interact = new DbInteract(this);

        if (getIntent().getStringExtra("action").equals("downloads"))
        {
            SharedPreferences prefs = getSharedPreferences("lcukerd.com.instaswipe", MODE_PRIVATE);
            if (prefs.getBoolean("intialLaunchG", true))
            {
                Toast.makeText(this, "Tap and hold on image to delete it.", Toast.LENGTH_SHORT).show();
                prefs.edit().putBoolean("intialLaunchG", false).commit();
            }
            LinearLayout profile_detail = (LinearLayout) findViewById(R.id.linLayout);
            profile_detail.setVisibility(View.GONE);
            idurl = getIntent().getStringExtra("action");
            mUsername.setText("Downloads");
            mProgressBar.setVisibility(View.GONE);
        } else
        {
            photos = getIntent().getStringArrayListExtra("urls");
            fullphotos = getIntent().getStringArrayListExtra("FullScreenURL");
            idurl = getIntent().getStringExtra("id");
            sourceCode = getIntent().getStringExtra("source");
            username = getIntent().getStringExtra("username");
            profilePic = interact.getImage(getIntent().getByteArrayExtra("profile pic"));
            profilepicURL = getIntent().getStringExtra("profile pic url");
            setProfileWidgets();
        }


        setupBottomNavigationView();
        setupGridView();
    }

    private void setupGridView()
    {
        Log.d(tag, "setupGridView: Setting up image grid.");

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);


        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview,
                "", photos,fullphotos, idurl);
        gridView.setAdapter(adapter);


    }

    private void setProfileWidgets()
    {
        final AccountDetails account = new AccountDetails(Scrapper.getFollowers(sourceCode),
                Scrapper.getFollowing(sourceCode), Scrapper.getPosts(sourceCode), profilePic, username);
        if (profilepicURL.equals(Scrapper.getProfilePicUrl(sourceCode)) == false)
        {
            Log.d(tag, "Profile Pic updated");
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.loadImage(Scrapper.getProfilePicUrl(sourceCode),
                    new SimpleImageLoadingListener()
                    {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                        {
                            profilePic = loadedImage;
                            profilepicURL = Scrapper.getProfilePicUrl(sourceCode);
                            interact.updateprofilepic(username, loadedImage);
                            account.setProfile_photo(profilePic);
                            mProfilePhoto.setImageBitmap(loadedImage);
                        }
                    });
        }
        mProfilePhoto.setImageBitmap(account.getProfile_photo());
        mUsername.setText(account.getUsername());
        mPosts.setText(String.valueOf(account.getPosts()));
        mFollowing.setText(account.getFollowing());
        mFollowers.setText(account.getFollowers());
        mProgressBar.setVisibility(View.GONE);

        mProfilePhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(Scrapper.getProfilePicHDUrl(sourceCode));
                Intent intent = new Intent(ProfileActivity.this, SwipePic.class);
                intent.putStringArrayListExtra("urls", temp);
                intent.putExtra("id", "-1");
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });
    }

    private void setupBottomNavigationView()
    {
        Log.d(tag, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(this, this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
