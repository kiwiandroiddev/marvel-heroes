package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks

import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummarySubList
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase.GetCharacterSummaries

class MockGetCharacterSummaries : GetCharacterSummaries {

    private val emptySublist = CharacterSummarySubList(characters = emptyList(), totalAvailable = 0)

    var firstPageResult: Single<CharacterSummarySubList> = Single.just(emptySublist)
    var nextPagesResult: Single<CharacterSummarySubList> = Single.just(emptySublist)

    override fun getCharacters(offset: Int, limit: Int): Single<CharacterSummarySubList> {
        return when (offset) {
            0 -> firstPageResult
            else -> nextPagesResult
        }
    }
}