package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks

import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase.GetCharacterSummaries

class MockGetCharacterSummaries : GetCharacterSummaries {

    var firstPageResult: Single<List<CharacterSummary>> = Single.just(emptyList())
    var nextPagesResult: Single<List<CharacterSummary>> = Single.just(emptyList())

    override fun getCharacters(offset: Int, limit: Int): Single<List<CharacterSummary>> {
        return when (offset) {
            0 -> firstPageResult
            else -> nextPagesResult
        }
    }

}