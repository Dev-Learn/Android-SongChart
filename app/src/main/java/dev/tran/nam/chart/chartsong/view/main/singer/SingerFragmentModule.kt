package dev.tran.nam.chart.chartsong.view.main.singer

import androidx.fragment.app.Fragment

import dagger.Binds
import dagger.Module
import tran.nam.core.di.inject.PerFragment

/**
 * Provides singer fragment dependencies.
 */
@Module
abstract class SingerFragmentModule {

    @Binds
    @PerFragment
    internal abstract fun fragmentInject(fragment: SingerFragment): Fragment
}
