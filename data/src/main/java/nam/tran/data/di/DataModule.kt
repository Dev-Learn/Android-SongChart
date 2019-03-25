package nam.tran.data.di

import dagger.Binds
import dagger.Module
import nam.tran.data.api.NetModule
import nam.tran.data.interactor.ISingerUseCase
import nam.tran.data.interactor.IWeekUseCase
import nam.tran.data.interactor.SingerUseCase
import nam.tran.data.interactor.WeekUseCase
import nam.tran.data.local.PreferenceModule
import javax.inject.Singleton


@Module(includes = [NetModule::class, PreferenceModule::class])
abstract class DataModule{

    @Binds
    @Singleton
    internal abstract fun provideWeekUseCase(weekUseCase: WeekUseCase): IWeekUseCase

    @Binds
    @Singleton
    internal abstract fun provideSingerUseCase(singerUseCase: SingerUseCase): ISingerUseCase
}
