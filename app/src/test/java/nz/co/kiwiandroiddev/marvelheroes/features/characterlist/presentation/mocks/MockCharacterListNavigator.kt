package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks

import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListNavigator

class MockCharacterListNavigator : CharacterListNavigator {

    var lastCalledWithCharacterId: CharacterId? = null

    override fun navigateToCharacter(characterId: CharacterId) {
        lastCalledWithCharacterId = characterId
    }
}
