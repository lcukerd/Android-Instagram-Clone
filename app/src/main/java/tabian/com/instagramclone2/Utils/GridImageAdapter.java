package tabian.com.instagramclone2.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import tabian.com.instagramclone2.Profile.ProfileActivity;
import tabian.com.instagramclone2.R;

/**
 * Created by User on 6/4/2017.
 */

public class GridImageAdapter extends ArrayAdapter<String>
{
    private static final String tag = GridImageAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;
    private String idurl;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs,String id)
    {
        super(context, layoutResource, imgURLs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
        idurl = id;
    }

    private static class ViewHolder
    {
        SquareImageView image;
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        final ViewHolder holder;
        if (imgURLs.size()== position+1)
        {
            Log.d(tag,"end reached");
            loadmore();
        }
        if (convertView == null)
        {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);

            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        String imgURL = getItem(position);

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(imgURL, holder.image, new ImageLoadingListener()
        {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
                if (holder.mProgressBar != null)
                {
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason)
            {
                if (holder.mProgressBar != null)
                {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {
                if (holder.mProgressBar != null)
                {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view)
            {
                if (holder.mProgressBar != null)
                {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }
    private void loadmore()
    {
        Ion.with(mContext).load(idurl).asString().setCallback(new FutureCallback<String>()
        {
            @Override
            public void onCompleted(Exception e, String result)
            {
                String id = "";
                int pos=0;
                for (int i=0;i<12;i++)
                {
                    String url = result.substring(result.indexOf("thumbnail_src",pos) + 17,
                            result.indexOf ("\",",result.indexOf("thumbnail_src",pos)) );
                    url.replace("s640x640","s360x360");
                    imgURLs.add(url);
                    Log.d(tag,url);
                    if (i==11)
                    {                               //fails if last element is video "view-source:https://www.instagram.com/officialpiperblush/?max_id=1379842223931348404"
                        int start = result.indexOf("\"GraphImage\", \"id\"",pos);
                        int end = result.indexOf("\",",result.indexOf("\"GraphImage\", \"id\"",pos)+21);
                        id = result.substring(start + 21,end);
                        idurl = idurl.substring(0,idurl.indexOf('=')+1) + id;
                        Log.d(tag,idurl);
                    }
                    pos = result.indexOf ("\",",result.indexOf("thumbnail_src",pos)) ;
                }
            }
        });
    }

}



















