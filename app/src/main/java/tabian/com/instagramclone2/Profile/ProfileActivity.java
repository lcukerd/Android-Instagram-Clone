package tabian.com.instagramclone2.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import tabian.com.instagramclone2.R;
import tabian.com.instagramclone2.Utils.BottomNavigationViewHelper;
import tabian.com.instagramclone2.Utils.GridImageAdapter;
import tabian.com.instagramclone2.Utils.UniversalImageLoader;
import tabian.com.instagramclone2.Utils.ViewCommentsFragment;
import tabian.com.instagramclone2.Utils.ViewPostFragment;
import tabian.com.instagramclone2.Utils.photonew;
import tabian.com.instagramclone2.models.Photo;
import tabian.com.instagramclone2.models.UserAccountSettings;
import tabian.com.instagramclone2.models.UserSettings;

/**
 * Created by User on 5/28/2017.
 */

public class ProfileActivity extends AppCompatActivity
{
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private ProgressBar mProgressBar;

    private ArrayList<String> photos = new ArrayList<>();
    String idurl;
    private BottomNavigationViewEx bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        Log.d(TAG, "onCreate: started.");

        photos = getIntent().getStringArrayListExtra("urls");
        idurl = getIntent().getStringExtra("id");

        mDisplayName = (TextView) findViewById(R.id.display_name);
        mUsername = (TextView) findViewById(R.id.username);
        mWebsite = (TextView) findViewById(R.id.website);
        mDescription = (TextView) findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo);
        mPosts = (TextView) findViewById(R.id.tvPosts);
        mFollowers = (TextView) findViewById(R.id.tvFollowers);
        mFollowing = (TextView) findViewById(R.id.tvFollowing);
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        gridView = (GridView) findViewById(R.id.gridView);
        toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);

        mProgressBar.setVisibility(View.GONE); // remove this line later

        setupBottomNavigationView();
        setupGridView();
        //setProfileWidgets();
    }

    private void setupGridView()
    {
        Log.d(TAG, "setupGridView: Setting up image grid.");

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);


        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview,
                "", photos,idurl);
        gridView.setAdapter(adapter);


    }

    private void setProfileWidgets(UserSettings userSettings)
    {
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());
        //User user = userSettings.getUser();

        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mProgressBar.setVisibility(View.GONE);
    }

    private void setupBottomNavigationView()
    {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(this, this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
