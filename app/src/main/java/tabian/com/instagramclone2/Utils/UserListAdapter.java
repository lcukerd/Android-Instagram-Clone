package tabian.com.instagramclone2.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import tabian.com.instagramclone2.Database.DbInteract;
import tabian.com.instagramclone2.Profile.ProfileActivity;
import tabian.com.instagramclone2.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Programmer on 14-09-2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.EventViewHolder>
{
    private static final String LOG_TAG = UserListAdapter.class.getSimpleName();

    private ArrayList<user> userArrayList;
    private LayoutInflater inflater;
    private Context mContext;
    private DisplayMetrics metrics;
    private DbInteract interact;

    public UserListAdapter(ArrayList<user> eventArray, Context context)
    {
        inflater = LayoutInflater.from(context);
        userArrayList = eventArray;
        mContext = context;
        interact = new DbInteract(context);
        metrics = context.getResources().getDisplayMetrics();
    }

    @Override
    public long getItemId(int position)
    {
        return super.getItemId(getItemCount() - position - 1);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = inflater.inflate(R.layout.usersview, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position)
    {
        final user u = userArrayList.get(position);

        holder.profilepic.setImageBitmap(u.profile);
        holder.username.setText(u.name);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Ion.with(mContext).load("https://www.instagram.com/" + u.query + "/").asString().setCallback(new FutureCallback<String>()
                {
                    @Override
                    public void onCompleted(Exception e, String result)
                    {
                        ArrayList<String> urls = new ArrayList<>();
                        String id = "";
                        int pos=0;
                        for (int i=0;i<12;i++)
                        {
                            String url = result.substring(result.indexOf("thumbnail_src",pos) + 17,
                                    result.indexOf ("\",",result.indexOf("thumbnail_src",pos)) );
                            url.replace("s640x640","s360x360");
                            urls.add(url);
                            Log.d(LOG_TAG,url);
                            if (i==11)
                            {
                                int start = result.indexOf("\"GraphImage\", \"id\"",pos);
                                if (start == -1)
                                    start = result.indexOf("\"GraphVideo\", \"id\"",pos);
                                int end = result.indexOf("\",",result.indexOf("\"GraphImage\", \"id\"",pos)+21);
                                id = result.substring(start + 21,end);
                                id = "https://www.instagram.com/" + u.query + "/?max_id=" + id;
                                Log.d(LOG_TAG,id);
                            }
                            pos = result.indexOf ("\",",result.indexOf("thumbnail_src",pos)) ;
                        }
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putStringArrayListExtra("urls",urls);
                        intent.putExtra("id",id);
                        mContext.startActivity(intent);
                    }
                });

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                try
                {
                    final LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.actionbuttons, (ViewGroup) v.findViewById(R.id.actionButtons));
                    Log.d("Popup", String.valueOf(metrics.widthPixels));
                    final PopupWindow pw = new PopupWindow(layout, 350 * (metrics.widthPixels / 1080), 200, true);
                    pw.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
                    pw.setOutsideTouchable(true);
                    int coord[] = new int[2];
                    v.getLocationOnScreen(coord);

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        Fade explode = new Fade();
                        pw.setEnterTransition(explode);
                    }

                    pw.showAtLocation(v, Gravity.NO_GRAVITY, 500, coord[1] - 100);

                    Button add = (Button) layout.findViewById(R.id.popupadd);
                    Button del = (Button) layout.findViewById(R.id.popupdel);
                    add.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            interact.adduser(u.profile,
                                    u.name,
                                    u.url,
                                    u.query);
                            pw.dismiss();
                        }
                    });
                    del.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            interact.deleteuser(u.name);
                            int pos = userArrayList.indexOf(u);
                            Log.d(LOG_TAG,String.valueOf(pos));
                            userArrayList.remove(u);
                            notifyItemRemoved(pos);
                            pw.dismiss();
                        }
                    });
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                return true;

            }
        });
    }

    public void clear()
    {
        int size = this.userArrayList.size();
        this.userArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void add(user u)
    {
        this.userArrayList.add(u);
        notifyItemChanged(0);
    }
    public void refill(ArrayList<user> users)
    {
        clear();
        this.userArrayList.addAll(users);
        notifyItemRangeInserted(0,userArrayList.size());
    }


    @Override
    public int getItemCount()
    {
        return userArrayList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder
    {
        ImageView profilepic;
        TextView username;

        public EventViewHolder(View itemView)
        {
            super(itemView);
            profilepic = (ImageView) itemView.findViewById(R.id.user_profile);
            username = (TextView) itemView.findViewById(R.id.user_username);
            getItemCount();
        }
    }


}
