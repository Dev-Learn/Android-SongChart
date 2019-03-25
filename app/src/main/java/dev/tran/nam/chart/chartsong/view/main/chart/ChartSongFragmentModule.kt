package dev.tran.nam.chart.chartsong.view.main.chart

import androidx.fragment.app.Fragment

import dagger.Binds
import dagger.Module
import tran.nam.core.di.inject.PerFragment

/**
 * Provides chart song fragment dependencies.
 */
@Module
abstract class ChartSongFragmentModule {

    @Binds
    @PerFragment
    internal abstract fun fragmentInject(fragment: ChartSongFragment): Fragment
}
