package lcukerd.com.instaswipe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import lcukerd.com.instaswipe.R;
import lcukerd.com.instaswipe.SwipePic;
import lcukerd.com.instaswipe.Utils.SquareImageView;

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

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs, String id)
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
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        final ViewHolder holder;
        if (imgURLs.size() - 4 == position + 1)
        {
            Log.d(tag, "end reached");
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

        Glide.with(mContext)
                .load(imgURL)
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
                        if (holder.mProgressBar != null)
                        {
                            holder.mProgressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(holder.image);
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, SwipePic.class);
                intent.putStringArrayListExtra("urls",imgURLs);
                intent.putExtra("id",idurl);
                intent.putExtra("position",position);
                mContext.startActivity(intent);
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
                int pos = 0;
                for (int i = 0; i < 12; i++)
                {
                    try
                    {
                        String url = result.substring(result.indexOf("thumbnail_src", pos) + 17,
                                result.indexOf("\",", result.indexOf("thumbnail_src", pos)));
                        url.replace("s640x640", "s360x360");
                        imgURLs.add(url);
                        Log.d(tag, url);
                        if (i == 11)
                        {
                            int start = result.indexOf("\"GraphImage\", \"id\"", pos);
                            if (start == -1)
                                start = result.indexOf("\"GraphVideo\", \"id\"", pos);
                            int end = result.indexOf("\",", start + 21);
                            id = result.substring(start + 21, end);
                            idurl = idurl.substring(0, idurl.indexOf('=') + 1) + id;
                            Log.d(tag, idurl);
                        }
                        pos = result.indexOf("\",", result.indexOf("thumbnail_src", pos));
                    } catch (NullPointerException ne)
                    {
                        Toast.makeText(mContext, "Internet Not Working", Toast.LENGTH_SHORT).show();
                        Log.e(tag, "Internet not working", ne);
                    } catch (StringIndexOutOfBoundsException finished)
                    {
                        Toast.makeText(mContext, "No More posts", Toast.LENGTH_SHORT).show();
                        Log.d(tag, "No more posts", e);
                    }
                }
            }
        });
    }

}



















