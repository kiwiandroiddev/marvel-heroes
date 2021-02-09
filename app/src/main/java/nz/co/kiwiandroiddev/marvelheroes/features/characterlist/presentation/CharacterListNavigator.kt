package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation

import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId

interface CharacterListNavigator {
    fun navigateToCharacter(characterId: CharacterId)
}
