package nz.co.kiwiandroiddev.marvelheroes

import android.app.Application
import nz.co.kiwiandroiddev.marvelheroes.di.ApplicationComponent
import nz.co.kiwiandroiddev.marvelheroes.di.DaggerApplicationComponent
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view.CharacterListFragment

class MarvelHeroesApplication : Application() {

    private lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        setupDependencyInjection()
    }

    private fun setupDependencyInjection() {
        appComponent = DaggerApplicationComponent.builder().build()
    }

    fun inject(target: CharacterListFragment) {
        appComponent.inject(target)
    }
}