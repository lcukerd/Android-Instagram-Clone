package lcukerd.com.donkey.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import lcukerd.com.donkey.models.User;

/**
 * Created by Programmer on 15-09-2017.
 */

public class Scrapper {
    private static final String tag = Scrapper.class.getSimpleName();

    public static ArrayList<ArrayList<String>> getimageUrls(String source){
        ArrayList<String> photosUrl = new ArrayList<>();
        ArrayList<String> fullphotosUrl = new ArrayList<>();
        ArrayList<ArrayList<String>> urls = new ArrayList<>();
        Document doc = Jsoup.parse(source);
        System.out.println(doc.title());
        Elements imagePages = doc.select(".updates").first().getElementsByTag("li");
        for (Element imagePage : imagePages) {
            Element imagehref = imagePage.getElementsByTag("a").first();
            Element imagesrc = imagePage.getElementsByTag("img").first();
            photosUrl.add(imagesrc.attr("src"));
            fullphotosUrl.add(imagehref.absUrl("href"));
            Log.d(tag,imagesrc.attr("src"));
        }
        urls.add(photosUrl);
        urls.add(fullphotosUrl);
        return urls;
    }

    public static String getFullImageUrls(String source){
        Document doc = Jsoup.parse(source);
        Element imageUrl = doc.select(".viewer-image").first().getElementsByTag("img").first();
        return  imageUrl.attr("src");
    }
}
