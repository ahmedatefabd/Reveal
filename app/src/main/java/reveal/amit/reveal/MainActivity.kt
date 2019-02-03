package reveal.amit.reveal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.transition.ArcMotion
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.widget.RelativeLayout

import kotlinx.android.synthetic.main.activity_main.*
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.transition.Transition
import android.R.attr.left
import android.R.attr.top
import android.animation.ObjectAnimator
import android.graphics.Rect


class MainActivity : AppCompatActivity() {

    private var revealed = false
    private val duration = 200L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)
        getMatrix()

        fab.setOnClickListener { view ->
            //move child of existing view to left
            for (i in 0..linearTexts.childCount){
                ObjectAnimator.ofFloat(linearTexts.getChildAt(i), "translationX", -1000f).apply {
                    duration = duration
                    start()
                }
            }

            //start fab animation
            val anim = ChangeBounds()
            anim.pathMotion = ArcMotion()
            anim.duration = duration
            anim.addListener(object: Transition.TransitionListener{
                override fun onTransitionEnd(p0: Transition?) {
                    revealView(revealView)
                    Handler().postDelayed({ fab.visibility = View.GONE }, 50)

                    revealed = true
                }

                override fun onTransitionResume(p0: Transition?) {
                }

                override fun onTransitionPause(p0: Transition?) {
                }

                override fun onTransitionCancel(p0: Transition?) {
                }

                override fun onTransitionStart(p0: Transition?) {
                }

            })
            TransitionManager.beginDelayedTransition(
                container,
                anim
            )

            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)

            params.addRule(RelativeLayout.BELOW, R.id.stripView)
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
            params.topMargin = convertDpToPixel(100f, this).toInt()
            params.marginEnd = convertDpToPixel(100f, this).toInt()
            fab.layoutParams = params
        }

        cancelButton.setOnClickListener {
            for (i in 0..linearTexts.childCount){
                ObjectAnimator.ofFloat(linearTexts.getChildAt(i), "translationX", 0f).apply {
                    duration = duration
                    start()
                }
            }
            hideView(revealView)
            revealed = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun revealView(myView: View) {

        println("Fab ${fab.left}")
        val cx = (fab.left + fab.right) / 2
        val cy = ((fab.top + fab.bottom) / 2) - lowerView.y
        println("Fab x $cx y $cy")

        val finalRadius = Math.max(myView.width, myView.height)


        val anim = ViewAnimationUtils.createCircularReveal(myView, cx , cy.toInt(), 0f, finalRadius.toFloat())
        anim.duration = duration

        myView.visibility = View.VISIBLE

        anim.start()
    }

    private fun hideView(myView: View) {
        val cx = (fab.left + fab.right) / 2
        val cy = ((fab.top + fab.bottom) / 2) - lowerView.y

        val initialRadius = myView.width

        val anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy.toInt(), initialRadius.toFloat(), 0f)
        anim.duration = duration
        anim.interpolator = AccelerateInterpolator()

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                myView.visibility = View.INVISIBLE

                val arcAnim = ChangeBounds()
                arcAnim.pathMotion = ArcMotion()
                arcAnim.duration = duration
                TransitionManager.beginDelayedTransition(
                    container,
                    arcAnim
                )

                val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT) /*fab.layoutParams as RelativeLayout.LayoutParams*/

                params.addRule(RelativeLayout.ALIGN_PARENT_END)
                params.addRule(RelativeLayout.BELOW, R.id.upperView)
                params.marginEnd = convertDpToPixel(16f, this@MainActivity).toInt()
                params.topMargin = convertDpToPixel(-25f, this@MainActivity).toInt()

                fab.layoutParams = params
            }
        })


        //Normally I would restore visibility when the hide animation has ended, but it doesn't look as good, so I'm doing it earlier.
        Handler().postDelayed({ fab.visibility = View.VISIBLE }, 200)

        anim.start()

    }
    
    fun getMatrix(){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        println("Height $height width $width")
    }

}
