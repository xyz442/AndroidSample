package cz.androidsample.ui.anim

import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ValueAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationSet
import android.view.animation.BounceInterpolator

import cz.androidsample.R
import kotlinx.android.synthetic.main.activity_animator1.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class Animator1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animator1)

        goldText.post {
            borrowFlag.translationY=creditLayout.height-goldText.bottom*1f
            creditLayout.translationY=creditLayout.height-goldText.bottom*1f
        }

        buttonStart.onClick {
            borrowFlag.translationY=creditLayout.height-goldText.bottom*1f
            creditLayout.translationY=creditLayout.height-goldText.bottom*1f

            val animatorSet=AnimatorSet()

            val animator1=ValueAnimator.ofFloat(creditLayout.translationY,0f)
            animator1.duration=1600
            animator1.addUpdateListener {
                val value=it.animatedValue as Float
                borrowFlag.translationY=value
                creditLayout.translationY=value
            }

            val animator2=ValueAnimator.ofFloat(10f)
            animator2.repeatMode=ValueAnimator.REVERSE
            animator2.repeatCount=3
            animator2.duration=600
            animator2.addUpdateListener {
                val value=it.animatedValue as Float
                innerLayout.translationY=value
            }

            val animator3=ValueAnimator.ofFloat(10f)
            animator3.repeatMode=ValueAnimator.REVERSE
            animator3.repeatCount=2
            animator3.duration=400
            animator3.addUpdateListener {
                val value=it.animatedValue as Float
                borrowFlag.translationY=value
            }

            animatorSet.play(animator1).before(animator2).before(animator3)
            animatorSet.start()


//            borrowFlag.animate().setInterpolator(BounceInterpolator()).setDuration(3000).translationY(0f)
//            creditLayout.animate().setInterpolator(BounceInterpolator()).setDuration(3000).translationY(0f)
        }
    }
}
