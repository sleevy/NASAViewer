package edu.gsu.cr04956.nasaviewer;

/**
 * Created by pradipta on 2/22/2017.
 */

public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    private String hdUrl;
    private String soundUrl;
    @Override
    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setHdUrl(String url) {
        hdUrl= url;
    }

    public String getHdUrl() {return hdUrl;}

    public String getSoundUrl() {return soundUrl;}

    public void setSoundUrl(String url) {
        soundUrl = url;
    }
}
