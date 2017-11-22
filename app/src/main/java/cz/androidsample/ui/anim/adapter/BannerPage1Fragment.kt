package cz.androidsample.ui.anim.adapter

import android.animation.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cz.androidsample.R
import kotlinx.android.synthetic.main.fragment_banner_page1.*
import org.jetbrains.anko.find

/**
 * Created by cz on 2017/11/21.
 */
class BannerPage1Fragment:BaseBannerPageFragment(){
    var animatorSet:AnimatorSet?=null
    var ballAnimatorSet:AnimatorSet?=null
    private var firstAnimator=false
    private var count=1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_banner_page1,container,false)
    }

    override fun startPageAnimator() {
        //编排动画
        //动态计划显示文本宽度
        val radialView = activity.find<ImageView>(R.id.radialView)

        //第二组,三个小球开始运行
        setTargetArcFraction(arcImage,ball1,0.1f)
        val animator2 = arc(ball1, arcImage, 2000, -1)
        val animator3 = x(ball2, ball2.left + 10, 1000, -1, ValueAnimator.REVERSE)
        val animator4 = x(ball3, ball3.left + 20, 1200, -1, ValueAnimator.REVERSE)
        //平移两个控件
        val animator5 = x(textScore, -textScore.width, 400)
        val animator6 = x(textCreditInfo, -textCreditInfo.width, 500)
        val animator7 = x(textInfo, -textInfo.width, 1000)
        val animator8 = x(pageLayout, pageSpace.left)
        val animator9 = scaleX(pageLayout, 1.2f)
        val animator10 = scaleY(pageLayout, 1.2f)
        val animator11 = transitionX(pageText, pageSpace.left-pageLayout.left)
        val animator12 = translationDrawable(radialView, 0.75f, 0.3f,600)
        val animator13 = translationDrawable(radialView, 0.3f, 0.75f)
        val animator14 = alpha(pageLayout, 0f, 600)
        val animator15 = alpha(pageText, 0f, 600)

        //重新计算显示文本大小
        pageText.layoutParams.width = pageSpace.width
        pageText.requestLayout()
        animatorSet?.cancel()
        animatorSet = AnimatorSet().apply {
            //所有联动动画
            if(!firstAnimator){
                firstAnimator=true
                val animator1 = numberChange(textScore, 450, 500,1000)
                playTogether(animator1,alpha(textScore,1f))
                playSequentially(animator1,animator2)
                playTogether(animator2,animator3,animator4)
                animator5.startDelay=1000
                playSequentially(animator1,animator5)
            } else {
                animator5.startDelay=2000
                playTogether(animator2,animator3,animator4,animator5)
            }
            animator11.startDelay = 200
            animator12.startDelay = 300
            playTogether(animator5,animator6,animator7,animator8,animator9,animator10,animator11,animator12)
            animator14.startDelay = 1000
            animator15.startDelay=1000
            playSequentially(animator12,animator13)
            playTogether(animator13,animator14,animator15)
            animator13.addListener(object :AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    //取消动画
                    cancelAnimator(animatorSet)
                    //终止动画
                    prePageAnimator()
//                    //起始渐变
                    val alphaAnimatorSet=AnimatorSet()
                    alphaAnimatorSet.playTogether(alpha(textScore, 1f, 600), alpha(textCreditInfo, 1f, 600),
                            alpha(textInfo, 1f, 600),alpha(pageLayout, 1f, 600))
                    alphaAnimatorSet.start()
                    //设定执行次数
                    if(0<count--){
                        startPageAnimator()
                    } else {
                        ballAnimatorSet?.start()
                    }
                }
            })
            //停止球晃动动画
            ballAnimatorSet?.cancel()
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
        pageText.x = pageText.left * 1f
        pageText.alpha = 1f

        pageLayout.x = pageLayout.left * 1f
        pageLayout.scaleX=1f
        pageLayout.scaleY=1f
        pageLayout.alpha = 0f

        //重置轨道球位置
        setTargetArcFraction(arcImage,ball1,0.1f)
        //让球滚动
        ballAnimatorSet=AnimatorSet().apply {
            playTogether(x(ball2, ball2.left + 10, 1000, -1, ValueAnimator.REVERSE),
            x(ball3, ball3.left + 20, 1200, -1, ValueAnimator.REVERSE))
        }
    }

    override fun onDestroyView() {
        cancelAnimator(ballAnimatorSet)
        cancelAnimator(animatorSet)
        super.onDestroyView()
    }
}