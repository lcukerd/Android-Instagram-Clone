package lcukerd.com.instaswipe.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import lcukerd.com.instaswipe.Database.DbInteract;
import lcukerd.com.instaswipe.ProfileActivity;
import lcukerd.com.instaswipe.R;
import lcukerd.com.instaswipe.Utils.Scrapper;
import lcukerd.com.instaswipe.models.User;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Programmer on 14-09-2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.EventViewHolder>
{
    private static final String LOG_TAG = UserListAdapter.class.getSimpleName();

    private ArrayList<User> userArrayList;
    private LayoutInflater inflater;
    private Context mContext;
    private DisplayMetrics metrics;
    private DbInteract interact;
    private ProgressBar progressBar;
    private boolean mylist = true;
    private SharedPreferences prefs;

    public UserListAdapter(ArrayList<User> eventArray, Context context, ProgressBar progressBar)
    {
        inflater = LayoutInflater.from(context);
        userArrayList = eventArray;
        mContext = context;
        prefs = context.getSharedPreferences("lcukerd.com.instaswipe", MODE_PRIVATE);
        interact = new DbInteract(context);
        metrics = context.getResources().getDisplayMetrics();
        this.progressBar = progressBar;
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
        final User u = userArrayList.get(position);

        holder.profilepic.setImageBitmap(u.profile);
        holder.username.setText(u.name);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(View.VISIBLE);
                Ion.with(mContext).load("https://www.instagram.com/" + u.query + "/").asString().setCallback(new FutureCallback<String>()
                {
                    @Override
                    public void onCompleted(Exception e, String result)
                    {
                        try
                        {
                            ArrayList<String> urls = new ArrayList<>();
                            ArrayList<String> fullurls = new ArrayList<>();
                            String id = "";
                            int pos = 0;
                            for (int i = 0; i < 12; i++)
                            {
                                String url = Scrapper.getimageUrl(result, pos,false);
                                if (url.equals("private"))
                                {
                                    Toast.makeText(mContext, "Account is private", Toast.LENGTH_SHORT).show();
                                    break;
                                } else if (url.equals("end"))
                                {
                                    Toast.makeText(mContext, "No more Posts", Toast.LENGTH_SHORT).show();
                                    break;
                                } else
                                {
                                    fullurls.add(Scrapper.getimageUrl(result,pos,true));

                                    url.replace("s640x640", "s360x360");

                                    urls.add(url);
                                    Log.d(LOG_TAG, url);
                                    if (i == 11)
                                    {
                                        id = Scrapper.getnextpageID(result, pos);
                                        id = "https://www.instagram.com/" + u.query + "/?max_id=" + id;
                                        Log.d(LOG_TAG, id);
                                    }
                                    pos = result.indexOf("\",", result.indexOf("thumbnail_src", pos));
                                }
                            }
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putStringArrayListExtra("urls", urls);
                            intent.putStringArrayListExtra("FullScreenURL",fullurls);
                            intent.putExtra("id", id);
                            intent.putExtra("username", u.name);
                            intent.putExtra("action", "online");
                            intent.putExtra("profile pic", interact.getBitmapAsByteArray(u.profile));
                            intent.putExtra("profile pic url", u.url);
                            intent.putExtra("source", result);
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            progressBar.setVisibility(View.GONE);
                            mContext.startActivity(intent);
                        } catch (NullPointerException ne)
                        {
                            Toast.makeText(mContext, "Internet Not Working", Toast.LENGTH_SHORT).show();
                            Log.e("User List Adapter", "Internet not working", ne);
                        } catch (StringIndexOutOfBoundsException finished)
                        {
                            Toast.makeText(mContext, "No More posts", Toast.LENGTH_SHORT).show();
                            Log.d("User List Adapter", "No more posts", e);
                        }
                    }
                });

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (mylist)
                {
                    interact.deleteuser(u.name);
                    int pos = userArrayList.indexOf(u);
                    Log.d(LOG_TAG, String.valueOf(pos));
                    userArrayList.remove(u);
                    notifyItemRemoved(pos);
                    Snackbar.make(v, "Account removed", Snackbar.LENGTH_SHORT).show();
                } else
                {
                    interact.adduser(u.profile, u.name, u.url, u.query);
                    Snackbar.make(v, "Account added", Snackbar.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    public void clear()
    {
        mylist = false;
        int size = this.userArrayList.size();
        this.userArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void add(User u)
    {
        if (prefs.getBoolean("intialLaunchOnline", true))
        {
            Toast.makeText(mContext, "Tap and hold on account to add it.", Toast.LENGTH_SHORT).show();
            prefs.edit().putBoolean("intialLaunchOnline", false).commit();
        }
        this.userArrayList.add(u);
        notifyItemChanged(userArrayList.size() - 1);
    }

    public void refill(ArrayList<User> users)
    {
        clear();
        if (prefs.getBoolean("intialLaunchOffline", true) && prefs.getBoolean("intialLaunchOnline", false))
        {
            Toast.makeText(mContext, "Tap and hold on account to delete it.", Toast.LENGTH_SHORT).show();
            prefs.edit().putBoolean("intialLaunchOffline", false).commit();
        }
        mylist = true;
        this.userArrayList.addAll(users);
        notifyItemRangeInserted(0, userArrayList.size());
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
