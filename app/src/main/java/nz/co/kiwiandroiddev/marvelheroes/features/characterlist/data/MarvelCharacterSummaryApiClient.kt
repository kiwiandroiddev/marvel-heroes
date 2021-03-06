package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.data

import io.reactivex.Scheduler
import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.common.data.MarvelCharactersApi
import nz.co.kiwiandroiddev.marvelheroes.common.data.model.Character
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.NetworkScheduler
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummarySubList
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase.GetCharacterSummaries
import javax.inject.Inject

/**
 * In a production application, we'd insert a repository layer for caching and perhaps a
 * use case implementation with some business logic rather than having this Api client implement
 * the GetCharacterSummaries use case directly. At this stage, these layers aren't needed.
 */
class MarvelCharacterSummaryApiClient @Inject constructor(
    private val api: MarvelCharactersApi,
    @NetworkScheduler private val networkScheduler: Scheduler
) : GetCharacterSummaries {

    override fun getCharacters(offset: Int, limit: Int): Single<CharacterSummarySubList> {
        return api.getCharacters(limit, offset)
            .map { wrapper ->
                val characterSummaries = wrapper.data.results.map { it.toDomainModel() }
                CharacterSummarySubList(characterSummaries, wrapper.data.total)
            }
            .subscribeOn(networkScheduler)
    }

    private fun Character.toDomainModel(): CharacterSummary {
        return CharacterSummary(
            id = CharacterId(id),
            name = name,
            thumbnailImagePath = thumbnail.path + "/standard_amazing." + thumbnail.extension
        )
    }
}
