package nz.co.kiwiandroiddev.marvelheroes.di

import dagger.Component
import nz.co.kiwiandroiddev.marvelheroes.MainActivity
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.view.CharacterDetailsViewModel
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view.CharacterListViewModel
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class
    ]
)
interface ApplicationComponent {

    fun inject(target: MainActivity)
    fun inject(target: CharacterListViewModel)
    fun inject(target: CharacterDetailsViewModel)

}