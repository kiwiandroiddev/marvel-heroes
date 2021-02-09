package nz.co.kiwiandroiddev.marvelheroes.navigation

import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListNavigator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationDispatcher @Inject constructor(
    private val mainActivityProvider: MainActivityProvider
) : CharacterListNavigator {

    override fun navigateToCharacter(characterId: CharacterId) {
        mainActivityProvider.mainActivity!!.navigateToCharacter(characterId)
    }

}