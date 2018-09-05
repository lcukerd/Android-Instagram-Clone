package lcukerd.com.donkey.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.HashMap;
import java.util.Map;

import lcukerd.com.donkey.Database.DbInteract;
import lcukerd.com.donkey.SwipePic;
import lcukerd.com.donkey.Utils.Scrapper;
import lcukerd.com.donkey.R;
import lcukerd.com.donkey.Utils.SquareImageView;
import lcukerd.com.donkey.Utils.loadImage;

/**
 * Created by User on 6/4/2017.
 */

public class GridImageAdapter extends BaseAdapter {
    private static final String tag = GridImageAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource, noofdownload;
    private String mAppend;
    private ArrayList<String> imgURLs, fullSizeURL;
    private Map<Integer, Bitmap> downloads = new HashMap<>();
    private String idurl;
    private DbInteract interact;
    private boolean wait = false, nomoreposts = false, Private = false, internetworking = true;


    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs,ArrayList<String> fullSizeURL, String id) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        interact = new DbInteract(context);
        this.layoutResource = layoutResource;
        mAppend = append;
        if (id.equals("downloads") == false) {
            this.imgURLs = imgURLs;
        } else
            noofdownload = interact.numberofdownloads();
        idurl = id;
        this.fullSizeURL = fullSizeURL;
    }

    private static class ViewHolder {
        SquareImageView image;
        SquareImageView showifvideo;
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);
            holder.showifvideo = (SquareImageView) convertView.findViewById(R.id.videoicon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (idurl.equals("downloads")) {
            loadImageGrid loader = new loadImageGrid(mContext, downloads, holder.image);
            loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, position);
            holder.mProgressBar.setVisibility(View.GONE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SwipePic.class);
                    intent.putExtra("id", idurl);
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    interact.deletedownloadedpic(position);
                    Snackbar.make(v, "Pic deleted!", Snackbar.LENGTH_SHORT).show();
                    downloads = new HashMap<>();
                    noofdownload--;
                    notifyDataSetChanged();
                    return true;
                }
            });
        } else {
            String imgURL = imgURLs.get(position);

            if (imgURL.charAt(0) == 'v') {
                holder.showifvideo.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(imgURL.substring(imgURL.indexOf('@') + 1))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                if (holder.mProgressBar != null) {
                                    holder.mProgressBar.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .into(holder.image);
            } else {
                holder.showifvideo.setVisibility(View.GONE);
                Log.d(tag,imgURL);
                Glide.with(mContext)
                        .load(imgURL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                if (holder.mProgressBar != null) {
                                    holder.mProgressBar.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .into(holder.image);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SwipePic.class);
                    intent.putStringArrayListExtra("urls", fullSizeURL);
                    intent.putExtra("id", idurl);
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    @Override
    public int getCount() {
        if (idurl.equals("downloads")) {
            return noofdownload;
        } else {
            return imgURLs.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (idurl.equals("downloads")) {
            return downloads.get(position);
        } else {
            return imgURLs.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class loadImageGrid extends loadImage {
        loadImageGrid(Context context, Map<Integer, Bitmap> down, SquareImageView pic) {
            super(context, down, pic, 0);
        }

    }

}



















