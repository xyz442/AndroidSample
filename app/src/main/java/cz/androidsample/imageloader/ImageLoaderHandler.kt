package cz.androidsample.imageloader

import cz.androidsample.imageloader.callback.OnImageDisplayListener

/**
 * Created by cz on 2017/6/28.
 */
object ImageLoaderHandler{
    private val listenerItems= mutableListOf<OnImageDisplayListener>()
    fun addDisplayListener(listener:OnImageDisplayListener){
        if(!listenerItems.contains(listener)){
            listenerItems.add(listener)
        }
    }

    fun removeDisplayListener(listener:OnImageDisplayListener){
        listenerItems.remove(listener)
    }

    fun forEach(closure:(OnImageDisplayListener)->Unit)=listenerItems.forEach(closure::invoke)
}