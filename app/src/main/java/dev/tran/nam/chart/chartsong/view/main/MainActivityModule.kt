package dev.tran.nam.chart.chartsong.view.main

import androidx.appcompat.app.AppCompatActivity

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.tran.nam.chart.chartsong.view.main.chart.ChartSongFragment
import dev.tran.nam.chart.chartsong.view.main.chart.ChartSongFragmentModule
import dev.tran.nam.chart.chartsong.view.main.singer.SingerFragment
import dev.tran.nam.chart.chartsong.view.main.singer.SingerFragmentModule
import tran.nam.core.di.inject.PerActivity
import tran.nam.core.di.inject.PerFragment

/**
 * Provides main activity dependencies.
 */
@Module
abstract class MainActivityModule {

    @Binds
    @PerActivity
    internal abstract fun activity(activity: MainActivity): AppCompatActivity

    /**
     * Provides the injector for the [ChartSongFragmentModule], which has access to the dependencies
     * provided by this application instance (singleton scoped objects).
     */
    @PerFragment
    @ContributesAndroidInjector(modules = [ChartSongFragmentModule::class])
    internal abstract fun injectorChartSongFragment(): ChartSongFragment

    /**
     * Provides the injector for the [SingerFragmentModule], which has access to the dependencies
     * provided by this application instance (singleton scoped objects).
     */
    @PerFragment
    @ContributesAndroidInjector(modules = [SingerFragmentModule::class])
    internal abstract fun injectoSingerFragment(): SingerFragment
}
