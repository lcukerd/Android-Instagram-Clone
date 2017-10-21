package lcukerd.com.instaswipe.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.HashMap;
import java.util.Map;

import lcukerd.com.instaswipe.Database.DbInteract;

/**
 * Created by Programmer on 21-10-2017.
 */

public class loadImage extends AsyncTask<Integer,Void,Bitmap>
{
    private Map<Integer, Bitmap> downloads = new HashMap<>();
    private DbInteract interact;
    private ImageView pic;
    private int type;

    protected loadImage(Context context,Map<Integer,Bitmap> download,ImageView pic,int type)
    {
        interact = new DbInteract(context);
        this.downloads = download;
        this.pic = pic;
        this.type = type;
    }

    @Override
    protected Bitmap doInBackground(Integer... params)
    {
        if (downloads.get(params[0]) != null)
            return downloads.get(params[0]);
        else
        {
            Bitmap image = interact.getDownloadedpics(params[0]);
            if (type==0)
                image = Bitmap.createScaledBitmap(image,(int)(360*((float)image.getWidth()/(float) image.getHeight())),360,false);
            downloads.put(params[0],image);
            return image;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        super.onPostExecute(bitmap);
        pic.setImageBitmap(bitmap);
    }
}
