package tabian.com.instagramclone2.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tabian.com.instagramclone2.R;

/**
 * Created by Programmer on 14-09-2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.EventViewHolder>
{

    private static final String LOG_TAG = UserListAdapter.class.getSimpleName();

    private ArrayList<user> userArrayList;

    private LayoutInflater inflater;
    private Context mContext;


    public UserListAdapter(ArrayList<user> eventArray, Context context)
    {
        inflater = LayoutInflater.from(context);
        userArrayList = eventArray;
        mContext = context;
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
        Log.d(LOG_TAG, "view holder called");
        holder.profilepic.setImageBitmap(userArrayList.get(position).profile);
        holder.username.setText(userArrayList.get(position).name);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //open that user
            }
        });
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
