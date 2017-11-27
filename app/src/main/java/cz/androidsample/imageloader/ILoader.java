package cz.androidsample.imageloader;

import android.content.Context;

import cz.androidsample.imageloader.callback.OnDownloadImageListener;
import cz.androidsample.imageloader.callback.OnImageDisplayListener;

/**
 * Created by cz on 9/8/16.
 */
public interface ILoader {
    void display(WrapperView view, String url, final OnImageDisplayListener listener);

    void downloadImage(Context context, String url, OnDownloadImageListener listener);

    void pause();

    void resume();
}
