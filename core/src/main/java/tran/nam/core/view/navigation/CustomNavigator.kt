package tran.nam.core.view.navigation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("custom_fragment")  // Use as custom tag at navigation.xml
class CustomNavigator(
        private val context: Context,
        private val manager: FragmentManager,
        private val containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    override fun navigate(
            destination: Destination,
            args: Bundle?,
            navOptions: NavOptions?,
            navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        val tag = destination.id.toString()
        val transaction = manager.beginTransaction()

        val currentFragment = manager.primaryNavigationFragment
        var initialNavigate = false
        if (currentFragment != null) {
            transaction.detach(currentFragment)
        } else {
            initialNavigate = true
        }

        var fragment = manager.findFragmentByTag(tag)
        if (fragment == null) {
            val className = destination.className
            fragment = instantiateFragment(context, manager, className, args)
            transaction.add(containerId, fragment, tag)
        } else {
            transaction.attach(fragment)
        }

        transaction.setPrimaryNavigationFragment(fragment)
//        transaction.setReorderingAllowed(true)
        transaction.commit()

        return if (initialNavigate) {
            // If always return null, selected BottomNavigation item is not same as app:startDestination in first time.
            destination
        } else {
            null
        }
    }


}