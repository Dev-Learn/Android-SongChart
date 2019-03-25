package tran.nam.widget

import android.animation.AnimatorInflater
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import tran.nam.core.R

class AnimButton : AppCompatButton {

    companion object {
        private const val DEFAULT_SQUARE_FOLLOW_HEIGHT = false
        private const val DEFAULT_SHOULD_SQUARE = false
    }

    private var mShouldSquare = DEFAULT_SHOULD_SQUARE
    private var mSquareFollowHeight = DEFAULT_SQUARE_FOLLOW_HEIGHT

    private var mDrawableWidth = 0

    private var mDrawableHeight = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimButton, defStyleAttr, 0)

        mDrawableWidth = a.getDimensionPixelSize(R.styleable.AnimButton_ab_drawable_width, -1)
        mDrawableHeight = a.getDimensionPixelSize(R.styleable.AnimButton_ab_drawable_height, -1)
        mShouldSquare = a.getBoolean(R.styleable.AnimButton_ab_should_square, DEFAULT_SQUARE_FOLLOW_HEIGHT)
        mSquareFollowHeight = a.getBoolean(R.styleable.AnimButton_ab_follow_height, DEFAULT_SQUARE_FOLLOW_HEIGHT)

        if (mDrawableWidth > 0 || mDrawableHeight > 0) {
            initCompoundDrawableSize()
        }

        init(context)

        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!mShouldSquare) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val should = if (mSquareFollowHeight) heightMeasureSpec else widthMeasureSpec
        super.onMeasure(should, should)
    }

    private fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.common_button_anim)
        }
    }

    private fun initCompoundDrawableSize() {
        val drawables = compoundDrawables
        for (drawable in drawables) {
            if (drawable == null) {
                continue
            }

            val realBounds = drawable.bounds
            val scaleFactor = realBounds.height() / realBounds.width().toFloat()

            var drawableWidth = realBounds.width().toFloat()
            var drawableHeight = realBounds.height().toFloat()

            if (mDrawableWidth > 0) {
                if (drawableWidth > mDrawableWidth) {
                    drawableWidth = mDrawableWidth.toFloat()
                    drawableHeight = drawableWidth * scaleFactor
                }
            }
            if (mDrawableHeight > 0) {

                if (drawableHeight > mDrawableHeight) {
                    drawableHeight = mDrawableHeight.toFloat()
                    drawableWidth = drawableHeight / scaleFactor
                }
            }

            realBounds.right = realBounds.left + Math.round(drawableWidth)
            realBounds.bottom = realBounds.top + Math.round(drawableHeight)

            drawable.bounds = realBounds
        }
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
    }
}