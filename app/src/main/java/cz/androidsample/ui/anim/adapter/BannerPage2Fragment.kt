package cz.androidsample.ui.anim.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cz.androidsample.R
import kotlinx.android.synthetic.main.fragment_banner_page2.*
import org.jetbrains.anko.find

/**
 * Created by cz on 2017/11/21.
 */
class BannerPage2Fragment:BaseBannerPageFragment(){
    var animatorSet: AnimatorSet?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_banner_page2,container,false)
    }
    override fun startPageAnimator(view: View) {
        view.isClickable=false
        val animator1 = numberChange(textScore, 450, 500)
        val lightAnimator = alpha(lightLayout, 1f, 1000)
        val animator2 = y(box1, box1.top + 10, 1000, 3, ValueAnimator.REVERSE)
        val animator3 = y(box2, box2.top + 10, 1000, 3, ValueAnimator.REVERSE)
        val animator4 = y(box3, box2.top + 10, 1000, 3, ValueAnimator.REVERSE)
        AnimatorSet().apply {
            playTogether(animator1,alpha(textScore,1f),
                    alpha(textCreditInfo,1f), alpha(textInfo,1f),lightAnimator)
            playTogether(lightAnimator,animator2,animator3,animator4)
            lightAnimator.addListener(object :AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    view.isClickable=true
                }
            })
            start()
        }
    }

    override fun startClickPageAnimator(view: View) {
        val radialView = activity.find<ImageView>(R.id.radialView)

        val lightAnimator = alpha(lightLayout, 1f, 1000)
        val animator2 = y(box1, box1.top + 10, 1000, -1, ValueAnimator.REVERSE)
        val animator3 = y(box2, box2.top + 10, 1200, -1, ValueAnimator.REVERSE)
        val animator4 = y(box3, box2.top + 10, 1200, -1, ValueAnimator.REVERSE)
        //平移两个控件
        val animator5 = x(textScore, -textScore.width, 300)
        val animator6 = x(textCreditInfo, -textCreditInfo.width, 400)
        val animator7 = x(textInfo, -textInfo.width, 1000)
        val animator8 = x(pageLayout, pageSpace.left)
        val animator9 = scaleX(pageLayout, 1.2f)
        val animator10 = scaleY(pageLayout, 1.2f)
        val animator11 = transitionX(pageText, pageSpace.left-pageLayout.left)
        val animator12 = translationDrawable(radialView, 0.75f, 0.25f, 600)
        val animator13 = alpha(pageLayout, 0f, 600)
        val animator14 = alpha(pageText, 0f, 600)
        val animator15 = translationDrawable(radialView, 0.25f, 0.75f)

        //重新计算显示文本大小
        pageText.layoutParams.width = pageSpace.width
        pageText.requestLayout()
        animatorSet?.cancel()
        animatorSet = AnimatorSet().apply {
            //所有联动动画
            playTogether(lightAnimator,animator2,animator3,animator4,animator5)
            animator11.startDelay = 200
            animator12.startDelay = 300
            playTogether(animator5,animator6,animator7,animator8,animator9,animator10,animator11,animator12)
            playSequentially(animator12,animator13)
            animator13.startDelay=2000
            playTogether(animator13,animator14)
            playSequentially(animator14,animator15)
            animator15.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    //取消动画
                    cancelAnimator(animatorSet)
                    prePageAnimator()
                    //起始渐变
                    val alphaAnimatorSet=AnimatorSet()
                    alphaAnimatorSet.playTogether(alpha(textScore, 1f, 600),
                            alpha(textCreditInfo, 1f, 600),
                            alpha(textInfo, 1f, 600),
                            alpha(pageLayout, 1f, 600))
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

        lightLayout.alpha=1f

        box1.y=box1.top*1f
        box2.y=box2.top*1f
        box3.y=box3.top*1f
    }

    override fun onDestroyView() {
        cancelAnimator(animatorSet)
        super.onDestroyView()
    }
}