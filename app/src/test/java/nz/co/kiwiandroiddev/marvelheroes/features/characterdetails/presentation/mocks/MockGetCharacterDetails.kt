package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.mocks

import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.model.CharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.usecase.GetCharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId

class MockGetCharacterDetails : GetCharacterDetails {

    var result: Single<CharacterDetails> = Single.never()

    override fun getCharacterDetails(characterId: CharacterId): Single<CharacterDetails> {
        return result
    }

}