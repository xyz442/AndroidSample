package cz.androidsample.imageloader.impl;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import cz.androidsample.imageloader.IConfig;
import cz.androidsample.imageloader.callback.OnDisplayListener;


/**
 * Created by cz on 9/8/16.
 */
public class FrescoConfig implements IConfig<ImagePipelineConfig> {
    private OnDisplayListener displayListener;

    @Override
    public void init(Application application, ImagePipelineConfig imagePipelineConfig) {
        Fresco.initialize(application, imagePipelineConfig);
    }

    @Override
    public void setOnDisplayListener(OnDisplayListener listener) {
        this.displayListener=listener;
    }

    @Override
    public boolean interceptDisplay() {
        return null == displayListener || displayListener.interceptDisplay();
    }


}
