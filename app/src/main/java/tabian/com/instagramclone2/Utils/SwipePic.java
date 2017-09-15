package tabian.com.instagramclone2.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import tabian.com.instagramclone2.R;

public class SwipePic extends AppCompatActivity
{
    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static ArrayList<String> urls = new ArrayList<>();
    private static String urlid;
    private static final String tag = SwipePic.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipepic);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Intent intent = getIntent();
        urlid = intent.getStringExtra("id");
        urls = intent.getStringArrayListExtra("urls");
        PlaceholderFragment.formaturls(0, urls.size());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Log.d(tag, String.valueOf(intent.getIntExtra("position", 0)));
        mViewPager.setCurrentItem(intent.getIntExtra("position", 0));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }

    public static class PlaceholderFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private boolean wait = false;

        public PlaceholderFragment()
        {
        }

        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_swipepic, container, false);
            ImageView pic = (ImageView) rootView.findViewById(R.id.imageView);
            Glide.with(getActivity())
                    .load(urls.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1))
                    .into(pic);
            if (wait == false)
            {
                if (getArguments().getInt(ARG_SECTION_NUMBER) == urls.size() - 2)
                    addurls();
            }
            Log.d(tag,String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        private void addurls()
        {
            wait = true;
            Log.i(tag,"id: "+urlid);
            Ion.with(this).load(urlid).asString().setCallback(new FutureCallback<String>()
            {
                @Override
                public void onCompleted(Exception e, String result)
                {
                    try
                    {
                        String id = "";
                        int pos = 0;
                        for (int i = 0; i < 12; i++)
                        {
                            String url = result.substring(result.indexOf("thumbnail_src", pos) + 17,
                                    result.indexOf("\",", result.indexOf("thumbnail_src", pos)));
                            Log.i(tag, "add url: " + url);
                            urls.add(url);
                            if (i == 11)
                            {
                                int start = result.indexOf("\"GraphImage\", \"id\"", pos);
                                if (start == -1)
                                    start = result.indexOf("\"GraphVideo\", \"id\"", pos);
                                int end = result.indexOf("\",", start + 21);
                                id = result.substring(start + 21, end);
                                urlid = urlid.substring(0, urlid.indexOf('=') + 1) + id;
                            }
                            pos = result.indexOf("\",", result.indexOf("thumbnail_src", pos));
                        }
                    } catch (NullPointerException ne)
                    {
                        Toast.makeText(getContext(), "Internet Not Working", Toast.LENGTH_SHORT).show();
                        Log.e(tag, "Internet not working", ne);
                    } catch (StringIndexOutOfBoundsException finished)
                    {
                        Toast.makeText(getContext(), "No More posts", Toast.LENGTH_SHORT).show();
                        Log.i(tag, "No more posts", e);
                    }
                    formaturls(urls.size() - 12, urls.size());
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    wait = false;
                }
            });
        }

        private static void formaturls(int start, int end)
        {
            ArrayList<String> temp = new ArrayList<>();
            for (int i = start; i < end; i++)
            {
                String tempurl = urls.get(start);
                int istart = tempurl.indexOf("s640x640/", 0);
                if (istart == -1)
                    istart = tempurl.indexOf("s360x360/", 0);
                if (istart == -1)
                    istart = tempurl.indexOf("/e", 0);
                int iend = tempurl.lastIndexOf('/');
                Log.i(tag, String.valueOf(istart) + " " + String.valueOf(iend) + " "
                        + String.valueOf(i) + " " + String.valueOf(urls.size()) + " " + tempurl);
                tempurl = tempurl.substring(0, istart) + tempurl.substring(iend);
                temp.add(tempurl);
                urls.remove(start);
                Log.i(tag, tempurl);
            }
            urls.addAll(temp);
            Log.i(tag, urlid);
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount()
        {
            return urls.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return null;
        }
    }
}