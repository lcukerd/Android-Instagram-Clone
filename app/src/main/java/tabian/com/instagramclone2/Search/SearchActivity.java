package tabian.com.instagramclone2.Search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import tabian.com.instagramclone2.Database.DbInteract;
import tabian.com.instagramclone2.R;
import tabian.com.instagramclone2.Utils.BottomNavigationViewHelper;
import tabian.com.instagramclone2.Utils.UserListAdapter;
import tabian.com.instagramclone2.Utils.user;

/**
 * Created by User on 5/28/2017.
 */

public class SearchActivity extends AppCompatActivity
{
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;
    private RecyclerView userlist;
    private ArrayList<user> usersArrayList;
    private UserListAdapter adapter;
    private DbInteract interact;

    private Context mContext = SearchActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemewithmenu);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate: started.");

        interact = new DbInteract(this);

        findViewById(R.id.relLayout1).setVisibility(View.GONE);

        userlist = (RecyclerView) findViewById(R.id.recycler_users);
        userlist.setVisibility(View.VISIBLE);
        usersArrayList = new ArrayList<>();
        usersArrayList.addAll(interact.readfromDB());

        adapter = new UserListAdapter(usersArrayList, this);
        userlist.setLayoutManager(new LinearLayoutManager(this));
        userlist.setAdapter(adapter);

        setupBottomNavigationView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_search, menu);

        MenuItem searchIcon = menu.findItem(R.id.toolbar_searcher);
        SearchView searchView = new SearchView(this);
        searchView.setBackground(getResources().getDrawable(R.drawable.et_border));
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.BLACK);
        searchAutoComplete.setHint("Search");
        searchAutoComplete.setHintTextColor(Color.GRAY);
        MenuItemCompat.setShowAsAction(searchIcon, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(searchIcon, searchView);

        SearchView search = (SearchView) searchIcon.getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(final String query)
            {
                Log.d(TAG, "Search Text " + query);
                adapter.clear();
                Ion.with(getApplicationContext()).load("https://www.instagram.com/" + query + "/").asString().setCallback(new FutureCallback<String>()
                {
                    @Override
                    public void onCompleted(Exception e, String result)
                    {
                        adapter.clear();
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        try
                        {
                            final String username = result.substring(result.indexOf("<meta property=\"og:title\" content=")
                                    + 35, result.indexOf("Instagram photos", result.indexOf("<meta property=\"og:title\" content=")) - 5);
                            final String url = result.substring(result.indexOf("<meta property=\"og:image\" content=\"")
                                    + 35, result.indexOf(" />", result.indexOf("<meta property=\"og:image\" content=")) - 1);
                            imageLoader.loadImage(url, new SimpleImageLoadingListener()
                            {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                                {
                                    adapter.add(new user(loadedImage, username, url,query));
                                }
                            });
                            Log.d(TAG, username);
                            Log.d(TAG, url);
                        }
                        catch (StringIndexOutOfBoundsException ex)
                        {
                            Log.d(TAG,"",ex);
                            Toast.makeText(getApplicationContext(),"User not found!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (TextUtils.isEmpty(newText))
                {
                    adapter.refill(interact.readfromDB());
                }
                return true;
            }
        });
        return true;
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView()
    {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
