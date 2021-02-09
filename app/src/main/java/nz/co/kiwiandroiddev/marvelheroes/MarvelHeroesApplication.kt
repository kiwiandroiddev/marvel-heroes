package nz.co.kiwiandroiddev.marvelheroes

import android.app.Application
import nz.co.kiwiandroiddev.marvelheroes.di.ApplicationComponent
import nz.co.kiwiandroiddev.marvelheroes.di.DaggerApplicationComponent

class MarvelHeroesApplication : Application() {

    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        setupDependencyInjection()
    }

    private fun setupDependencyInjection() {
        appComponent = DaggerApplicationComponent.builder().build()
    }

}