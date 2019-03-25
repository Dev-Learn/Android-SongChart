package nam.tran.data.local

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class PreferenceModule {

    @Binds
    @Singleton
    abstract fun providePreference(preferenceProvider: Preference): IPreference
}
