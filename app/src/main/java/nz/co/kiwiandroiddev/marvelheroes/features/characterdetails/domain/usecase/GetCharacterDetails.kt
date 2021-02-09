package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.usecase

import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.model.CharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId

interface GetCharacterDetails {

    fun getCharacterDetails(characterId: CharacterId): Single<CharacterDetails>

}