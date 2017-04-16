package edu.gsu.cr04956.nasaviewer;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by pradipta on 2/22/2017.
 */

public class FlickrFetchr {
    private static final String TAG = "NASAImages";
    private static final String API_KEY = "Ad82m7vba2qVADjvYWRpYEIg9rbY4JEZ24Y1MZpo";
    private static final int NUM_DAYS = 30;
    private int skipped = 0;

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems() {
        List<GalleryItem> items = new ArrayList<>();

        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            for(int i = 0; i < NUM_DAYS + skipped; i++){


                //YYYY-MM-DD
                String date = format.format(calendar.getTime());
                String url = Uri.parse("https://api.nasa.gov/planetary/apod")
                    .buildUpon()
                    //.appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("hd", "True")
                    .appendQueryParameter("date", date)
                    //.appendQueryParameter("extras", "url_s")
                    .build().toString();
                String jsonString = getUrlString(url);
                Log.i(TAG, "Received JSON: " + jsonString);
                JSONObject jsonBody = new JSONObject(jsonString);

                parseItem(items, jsonBody);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            }
            //add soundcloud stuff
            String soundUrl = Uri.parse("https://api.nasa.gov/planetary/sounds")
                    .buildUpon()
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("limit", String.valueOf(NUM_DAYS))
                    .build().toString();
            String jString = getUrlString(soundUrl);
            JSONObject jsonHolder = new JSONObject(jString);
            JSONArray jArray = jsonHolder.getJSONArray("results");
            for(int i = 0; i < jArray.length(); i++) {
                JSONObject soundObject = jArray.getJSONObject(i);
                items.get(i).setSoundUrl(soundObject.getString("stream_url"));
                Log.i(TAG, "Sound URL: " + items.get(i).getSoundUrl());
            }

            //results is a jsonarray. stream_url is name of internal streaming url
        } catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items;
    }

    private void parseItem(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {
        if(!"image".equals(jsonBody.getString("media_type"))) {
            skipped++;
            return;
        }
//        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
//        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
//        for (int i = 0; i < photoJsonArray.length(); i++) {
//            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
        GalleryItem item = new GalleryItem();
        item.setId(jsonBody.getString("title"));
        item.setCaption(jsonBody.getString("explanation"));
//            if (!photoJsonObject.has("url_s")) {
//                continue;
//            }
        item.setUrl(jsonBody.getString("url"));
        item.setHdUrl(jsonBody.getString("hdurl"));
        items.add(item);
//        }
    }
}
