package tran.nam.util

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import androidx.annotation.RequiresApi

class RippleUtil {

    companion object {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun getRippleStrokeDrawable(normalColor: Int, rippleColor: Int, cornerRadius: Float, strokeWidth: Int, strokeColor: Int): Drawable {
            return RippleDrawable(ColorStateList.valueOf(rippleColor),
                    getStrokeDrawable(normalColor, cornerRadius, strokeWidth, strokeColor),
                    getDrawable(rippleColor , cornerRadius))
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun getRippleDrawable(normalColor: Int, rippleColor: Int, cornerRadius: Float, leftTopCornerRadius: Float,
                              rightTopCornerRadius: Float, leftBottomCornerRadius: Float, rightBottomCornerRadius: Float): Drawable {
            return RippleDrawable(ColorStateList.valueOf(rippleColor),
                    getDrawable(normalColor, cornerRadius, leftTopCornerRadius, rightTopCornerRadius, leftBottomCornerRadius, rightBottomCornerRadius),
                    getDrawable(rippleColor, cornerRadius, leftTopCornerRadius, rightTopCornerRadius, leftBottomCornerRadius, rightBottomCornerRadius))
        }

        private fun getStrokeDrawable(color: Int, cornerRadius: Float, strokeWidth: Int, strokeColor: Int): Drawable {
            val shapeDrawable = GradientDrawable()
            shapeDrawable.cornerRadius = cornerRadius
            shapeDrawable.setColor(color)
            shapeDrawable.shape = GradientDrawable.RECTANGLE
            shapeDrawable.setStroke(strokeWidth, strokeColor)
            return shapeDrawable
        }

        private fun getDrawable(color: Int, cornerRadius: Float = 0f, leftTopCornerRadius: Float = 0f, rightTopCornerRadius: Float = 0f,
                                leftBottomCornerRadius: Float = 0f, rightBottomCornerRadius: Float = 0f): Drawable {
            var r: RoundRectShape? = null
            val outerRadii: FloatArray?
            if (cornerRadius > 0f) {
                outerRadii = FloatArray(8)
                outerRadii.fill(cornerRadius, 0, 8)
                r = RoundRectShape(outerRadii, null, null)
            } else if (leftTopCornerRadius != 0f || rightTopCornerRadius != 0f ||
                    leftBottomCornerRadius != 0f || rightBottomCornerRadius != 0f) {
                outerRadii = floatArrayOf(leftTopCornerRadius, leftTopCornerRadius, rightTopCornerRadius, rightTopCornerRadius,
                        rightBottomCornerRadius, rightBottomCornerRadius, leftBottomCornerRadius, leftBottomCornerRadius)
                r = RoundRectShape(outerRadii, null, null)
            }
            val shapeDrawable = ShapeDrawable(r)
            shapeDrawable.paint.color = color
            shapeDrawable.paint.isAntiAlias = true
            return shapeDrawable
        }

    }

}