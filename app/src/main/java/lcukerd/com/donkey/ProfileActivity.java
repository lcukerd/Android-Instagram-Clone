package lcukerd.com.donkey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lcukerd.com.donkey.Utils.Scrapper;
import lcukerd.com.donkey.Database.DbInteract;
import lcukerd.com.donkey.R;
import lcukerd.com.donkey.Utils.BottomNavigationViewHelper;
import lcukerd.com.donkey.adapter.GridImageAdapter;
import lcukerd.com.donkey.models.AccountDetails;

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
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        gridView = (GridView) findViewById(R.id.gridView);
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        interact = new DbInteract(this);

        if (getIntent().getAction() != null)
        {
            idurl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            Ion.with(this).load(idurl).asString().setCallback(new FutureCallback<String>()
            {
                @Override
                public void onCompleted(Exception e, String result)
                {
                    ArrayList<ArrayList<String>> urls= Scrapper.getimageUrls(result);
                    photos = urls.get(0);
                    fullphotos = urls.get(1);
                    setProfileWidgets();
                    setupGridView();
                }
            });
        } else
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
            setupGridView();
        }


        setupBottomNavigationView();
    }

    private void setupGridView()
    {
        Log.d(tag, "setupGridView: Setting up image grid.");

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);


        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview,
                "", photos, fullphotos, idurl);
        gridView.setAdapter(adapter);


    }

    private void setProfileWidgets()
    {
        mProgressBar.setVisibility(View.GONE);
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
