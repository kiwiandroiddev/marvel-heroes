package nz.co.kiwiandroiddev.marvelheroes.di

import dagger.Component
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view.CharacterListFragment
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class
    ]
)
interface ApplicationComponent {

    fun inject(target: CharacterListFragment)

}