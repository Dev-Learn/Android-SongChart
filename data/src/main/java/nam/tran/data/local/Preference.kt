package nam.tran.data.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class Preference @Inject
internal constructor(mApp: Application) : IPreference {

    private val mPref: SharedPreferences

    init {
        mPref = mApp.getSharedPreferences(SHARED_REFERENCE_NAME, Context.MODE_PRIVATE)
    }

    companion object {

        /**
         * normal configurations
         */
        private val SHARED_REFERENCE_NAME = "Application Config"

    }
}
