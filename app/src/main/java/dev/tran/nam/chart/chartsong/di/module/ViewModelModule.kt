package dev.tran.nam.chart.chartsong.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.tran.nam.chart.chartsong.view.main.chart.viewmodel.ChartSongViewModel
import dev.tran.nam.chart.chartsong.view.main.singer.viewmodel.SingerViewModel
import tran.nam.core.di.ViewModelFactory
import tran.nam.core.di.inject.ViewModelKey

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ChartSongViewModel::class)
    internal abstract fun bindChartSongViewModel(model: ChartSongViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SingerViewModel::class)
    internal abstract fun bindSingerViewModel(model: SingerViewModel): ViewModel
}
