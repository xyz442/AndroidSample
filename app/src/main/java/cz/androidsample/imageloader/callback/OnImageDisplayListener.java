package cz.androidsample.imageloader.callback;


import cz.androidsample.imageloader.DisplayLifeCycle;
import cz.androidsample.imageloader.WrapperView;

/**
 * Created by cz on 9/9/16.
 */
public interface OnImageDisplayListener {
    void callback(DisplayLifeCycle lifeCycle, String url, WrapperView view);
}
