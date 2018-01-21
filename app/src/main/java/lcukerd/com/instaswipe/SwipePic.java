package lcukerd.com.instaswipe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lcukerd.com.instaswipe.Database.DbInteract;
import lcukerd.com.instaswipe.Utils.Scrapper;
import lcukerd.com.instaswipe.Utils.loadImage;

public class SwipePic extends AppCompatActivity
{
    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static ArrayList<String> urls = new ArrayList<>();
    private static Map<Integer, Bitmap> downloads = new HashMap<>();
    private static String urlid;
    private int noofdownloads;
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
        SharedPreferences prefs = getSharedPreferences("lcukerd.com.instaswipe", MODE_PRIVATE);
        if (prefs.getBoolean("intialLaunchF", true))
        {
            Toast.makeText(this, "Tap and hold on image to save it.", Toast.LENGTH_SHORT).show();
            prefs.edit().putBoolean("intialLaunchF", false).commit();
        }
        if (urlid.equals("downloads"))
        {
            DbInteract interact = new DbInteract(this);
            downloads.put(intent.getIntExtra("position", 0),interact.getDownloadedpics(intent.getIntExtra("position", 0)));
            noofdownloads = interact.numberofdownloads();
        }
        else
        {
            urls = intent.getStringArrayListExtra("urls");
            PlaceholderFragment.formaturls(0, urls.size());
        }

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

    public static class PlaceholderFragment extends Fragment implements EasyVideoCallback
    {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private boolean wait = false;
        private EasyVideoPlayer player;
        private ProgressBar progressBar;

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            final View rootView = inflater.inflate(R.layout.fragment_swipepic, container, false);
            final PhotoView pic = (PhotoView) rootView.findViewById(R.id.imageView);
            player = (EasyVideoPlayer) rootView.findViewById(R.id.player);
            progressBar = (ProgressBar) rootView.findViewById(R.id.swipepicProgressBar);
            final int pos = getArguments().getInt(ARG_SECTION_NUMBER) - 1;

            if (urlid.equals("downloads"))
            {
                Log.d(tag,"downloads" + String.valueOf(pos));
                pic.setVisibility(View.VISIBLE);
                loadImagefull loader = new loadImagefull(getContext(),downloads,pic);
                loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,pos);
            }
            else
            {
                final String url = urls.get(pos);

                if (url.charAt(0) == 'v')
                {
                    progressBar.setVisibility(View.VISIBLE);
                    player.setVisibility(View.VISIBLE);
                    player.setCallback(this);

                    String code = url.substring(1, url.indexOf('@'));
                    String videoPageUrl = Scrapper.getVideoPageUrl(code, urlid);

                    Ion.with(getContext()).load(videoPageUrl).asString().setCallback(new FutureCallback<String>()
                    {
                        @Override
                        public void onCompleted(Exception e, String result)
                        {
                            try
                            {
                                String videoUrl = Scrapper.getVideoUrl(result);

                                urls.remove(url);
                                urls.add(pos, "@" + videoUrl);  //adds '@' before videourl so that there
                                //is no need to get that again from thumbnail

                                player.setSource(Uri.parse(videoUrl));
                            } catch (NullPointerException el)
                            {
                                Toast.makeText(getContext(), "Intenet not working!", Toast.LENGTH_SHORT).show();
                                Log.d(tag, "Internet not working");
                            }
                        }
                    });

                } else if (url.charAt(0) == '@')
                {
                    progressBar.setVisibility(View.GONE);
                    player.setVisibility(View.VISIBLE);
                    player.setCallback(this);
                    player.setSource(Uri.parse(url.substring(1)));
                } else
                {
                    pic.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    pic.setOnLongClickListener(new View.OnLongClickListener()
                    {
                        @Override
                        public boolean onLongClick(View v)
                        {
                            Snackbar.make(rootView, "Image has been saved!", Snackbar.LENGTH_SHORT).show();
                            DbInteract interact = new DbInteract(getContext());
                            interact.savepic(((BitmapDrawable)pic.getDrawable()).getBitmap());
                            return true;
                        }
                    });
                    Glide.with(getActivity())
                            .load(urls.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1))
                            .listener(new RequestListener<Drawable>()
                            {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                            Target<Drawable> target, boolean isFirstResource)
                                {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model,
                                                               Target<Drawable> target, DataSource dataSource,
                                                               boolean isFirstResource)
                                {
                                    if (progressBar != null)
                                    {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    return false;
                                }
                            })
                            .into(pic);

                }
                if (urlid.equals("-1") == false)
                {
                    if (wait == false)
                    {
                        if (getArguments().getInt(ARG_SECTION_NUMBER) == urls.size() - 2)
                            addurls();
                    }
                }
            }
            return rootView;
        }

        private void addurls()
        {
            wait = true;
            Log.i(tag, "id: " + urlid);
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
                            String url = Scrapper.getimageUrl(result, pos,true);
                            Log.i(tag, "add url: " + url);
                            urls.add(url);
                            if (i == 11)
                            {
                                id = Scrapper.getnextpageID(result, pos);
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
                tempurl = Scrapper.formatURLforfullscreen(tempurl);
                temp.add(tempurl);
                urls.remove(start);
                Log.i(tag, tempurl);
            }
            urls.addAll(temp);
            Log.i(tag, urlid);
        }

        @Override
        public void onPause()
        {
            Log.d(tag, "paused");
            super.onPause();
            // Make sure the player stops playing if the User presses the home button.
            player.pause();
        }

        // Methods for the implemented EasyVideoCallback

        @Override
        public void onPreparing(EasyVideoPlayer player)
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPrepared(EasyVideoPlayer player)
        {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onBuffering(int percent)
        {
            // TODO handle if needed
        }

        @Override
        public void onError(EasyVideoPlayer player, Exception e)
        {
            // TODO handle
        }

        @Override
        public void onCompletion(EasyVideoPlayer player)
        {
            // TODO handle if needed
        }

        @Override
        public void onRetry(EasyVideoPlayer player, Uri source)
        {
            // TODO handle if used
        }

        @Override
        public void onSubmit(EasyVideoPlayer player, Uri source)
        {
            // TODO handle if used
        }

        @Override
        public void onStarted(EasyVideoPlayer player)
        {
            // TODO handle if needed
        }

        @Override
        public void onPaused(EasyVideoPlayer player)
        {
            // TODO handle if needed
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
            if (urlid.equals("downloads"))
                return noofdownloads;
            else
                return urls.size();

        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return null;
        }
    }

    static class loadImagefull extends loadImage
    {
        loadImagefull(Context context,Map<Integer,Bitmap> down,PhotoView pic)
        {
            super(context,down,pic,1);
        }
    }
}