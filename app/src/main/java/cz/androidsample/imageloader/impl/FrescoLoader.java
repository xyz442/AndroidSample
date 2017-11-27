package cz.androidsample.imageloader.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import cz.androidsample.imageloader.DisplayLifeCycle;
import cz.androidsample.imageloader.ILoader;
import cz.androidsample.imageloader.WrapperView;
import cz.androidsample.imageloader.callback.OnDownloadImageListener;
import cz.androidsample.imageloader.callback.OnImageDisplayListener;

/**
 * Created by cz on 9/8/16.
 */
public class FrescoLoader implements ILoader {

    @Override
    public void display(final WrapperView view, final String url, final OnImageDisplayListener listener) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(url))
                .setTapToRetryEnabled(true)
                .setOldController(view.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onSubmit(String id, Object callerContext) {
                        super.onSubmit(id, callerContext);
                        if (null != listener) {
                            listener.callback(DisplayLifeCycle.START,url, view);
                        }
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (null != listener) {
                            listener.callback(DisplayLifeCycle.COMPLETE,url, view);
                        }
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        super.onIntermediateImageFailed(id, throwable);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        if (null != listener) {
                            listener.callback(DisplayLifeCycle.FAILED,url, view);
                        }
                    }

                    @Override
                    public void onRelease(String id) {
                        super.onRelease(id);
                    }
                }).build();
        view.setController(controller);
    }

    @Override
    public void downloadImage(Context context, final String url,final OnDownloadImageListener listener) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).setProgressiveRenderingEnabled(true).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                if(null!=listener){
                    listener.callback(url,bitmap);
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                if(null!=listener){
                    listener.callback(url,null);
                }
            }
        }, CallerThreadExecutor.getInstance());

        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
                .setFadeDuration(300)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        DraweeHolder<GenericDraweeHierarchy> draweeHolder = DraweeHolder.create(hierarchy, context);
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(draweeHolder.getController())
                .setImageRequest(imageRequest)
                .build();
        controller.onClick();
    }

    @Override
    public void pause() {
        Fresco.getImagePipeline().pause();
    }

    @Override
    public void resume() {
        Fresco.getImagePipeline().resume();
    }
}
