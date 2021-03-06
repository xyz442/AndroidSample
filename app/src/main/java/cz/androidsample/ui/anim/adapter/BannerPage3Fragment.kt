package cz.androidsample.ui.anim.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cz.androidsample.R
import kotlinx.android.synthetic.main.fragment_banner_page3.*
import org.jetbrains.anko.find

/**
 * Created by cz on 2017/11/21.
 */
class BannerPage3Fragment:BaseBannerPageFragment(){
    var animatorSet: AnimatorSet?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_banner_page3,container,false)
    }
    override fun startPageAnimator(view: View) {
        val animator0 = alpha(bottom, 1f, 1000)
        val scaleAnimator1 = scale(border1,1f, 600, 1, ValueAnimator.REVERSE)
        val animator1 = to(border1, pageFlag.left,pageFlag.top, 600,3,ValueAnimator.REVERSE)
        val animator2 = to(border2, pageFlag.left,pageFlag.top, 600,3,ValueAnimator.REVERSE)
        animatorSet = AnimatorSet().apply {
            //所有联动动画
            val numberAnimator = numberChange(textScore, 450, 500, 1000)
            playTogether(numberAnimator, alpha(textScore,1f),alpha(textCreditInfo,1f),
                    alpha(textInfo,1f), animator0, scaleAnimator1, animator1, animator2)
            start()
        }
    }

    override fun startClickPageAnimator(view: View) {
        val radialView = activity.find<ImageView>(R.id.radialView)

        val animator0 = alpha(bottom, 1f, 1000)
        val scaleAnimator1 = scale(border1,1f, 600, 1, ValueAnimator.REVERSE)
        val animator1 = to(border1, pageFlag.left,pageFlag.top, 600,1,ValueAnimator.REVERSE)
        val animator2 = to(border2, pageFlag.left,pageFlag.top, 600,1,ValueAnimator.REVERSE)
        //平移两个控件
        val animator5 = x(textScore, -textScore.width, 300)
        val animator6 = x(textCreditInfo, -textCreditInfo.width, 400)
        val animator7 = x(textInfo, -textInfo.width, 1000)
        val animator8 = x(pageLayout, pageSpace.left)
        val animator9 = scaleX(pageLayout, 1.2f)
        val animator10 = scaleY(pageLayout, 1.2f)

        //光晕背景跟随Page标记一起移动
        val animator11 = transitionX(pageText, pageSpace.left-pageLayout.left)
        val animator12 = translationDrawable(radialView, 0.75f, 0.25f, 600)

        val scaleAnimator2 = scale(border1,1f, 600, 1, ValueAnimator.REVERSE)
        val animator13 = to(border1, pageFlag.left,pageFlag.top, 600, 1, ValueAnimator.REVERSE)
        val animator14 = to(border2, pageFlag.left,pageFlag.top, 600, 1, ValueAnimator.REVERSE)

        val animator15 = alpha(pageText, 0f, 600)
        val animator16 = alpha(pageLayout, 0f, 600)
        val animator17 = alpha(bottom, 0f, 600)
        val animator18 = translationDrawable(radialView, 0.25f, 0.75f)

        //重新计算显示文本大小
        pageText.layoutParams.width = pageSpace.width
        pageText.requestLayout()
        animatorSet?.cancel()
        animatorSet = AnimatorSet().apply {
            //所有联动动画
            playTogether(animator0,scaleAnimator1,animator1,animator2)
            playSequentially(animator2,animator5)
            animator11.startDelay = 200
            animator12.startDelay = 300
            playTogether(animator5,animator6,animator7,animator8,animator9,
                    animator10,animator11,animator12)
            playSequentially(animator12,animator13)
            playTogether(animator13,scaleAnimator2,animator14)
            playSequentially(animator14,animator15)

            animator15.startDelay=2000
            playTogether(animator15,animator16,animator17)
            playSequentially(animator17,animator18)
            animator18.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    cancelAnimator(animatorSet)
                    prePageAnimator()
                    //起始渐变
                    val alphaAnimatorSet=AnimatorSet()
                    alphaAnimatorSet.playTogether(alpha(textScore, 1f, 600),
                            alpha(textCreditInfo, 1f, 600),
                            alpha(textInfo, 1f, 600),
                            alpha(pageLayout, 1f, 600),
                            alpha(bottom, 1f, 600))
                    alphaAnimatorSet.start()
                }
            })
            start()
        }
    }

    override fun prePageAnimator() {
        textScore.alpha = 0f
        textScore.x = textScore.left * 1f
        textCreditInfo.alpha = 0f
        textCreditInfo.x = textCreditInfo.left * 1f
        textInfo.alpha = 0f
        textInfo.x = textInfo.left * 1f

        pageLayout.x=pageLayout.left*1f
        pageLayout.scaleX=1f
        pageLayout.scaleY=1f
        pageLayout.alpha = 0f

        pageText.alpha = 1f
        pageText.x = pageText.left * 1f

        bottom.alpha=0f
    }

    override fun onDestroyView() {
        cancelAnimator(animatorSet)
        super.onDestroyView()
    }
}