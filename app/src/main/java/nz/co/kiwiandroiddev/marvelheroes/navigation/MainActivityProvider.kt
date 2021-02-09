package nz.co.kiwiandroiddev.marvelheroes.navigation

import nz.co.kiwiandroiddev.marvelheroes.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainActivityProvider @Inject constructor() {

    var mainActivity: MainActivity? = null
        private set

    fun register(mainActivity: MainActivity) {
        this.mainActivity = mainActivity
    }

    fun unregister(mainActivity: MainActivity) {
        if (this.mainActivity == mainActivity) {
            this.mainActivity = null
        }
    }

}
