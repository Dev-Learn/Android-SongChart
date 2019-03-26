package dev.tran.nam.chart.chartsong.binding

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.tran.nam.chart.chartsong.R
import dev.tran.nam.chart.chartsong.di.module.GlideApp

object BindingView{
    @JvmStatic
    @BindingAdapter("loadImageSong")
    fun loadImageNetwork(image: AppCompatImageView, urlImage: String?) {
        urlImage?.let {
            val circularProgressDrawable = CircularProgressDrawable(image.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            GlideApp.with(image).load(urlImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions.withCrossFade()).placeholder(circularProgressDrawable)
                .error(R.drawable.image_error).into(image)
        }
    }
}