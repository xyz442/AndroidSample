package cz.androidsample.imageloader;

import android.app.Application;

import cz.androidsample.imageloader.callback.OnDisplayListener;


/**
 * Created by cz on 9/8/16.
 */
public interface IConfig<T> {
    void init(Application application, T t);

    void setOnDisplayListener(OnDisplayListener listener);

    boolean interceptDisplay();
}
