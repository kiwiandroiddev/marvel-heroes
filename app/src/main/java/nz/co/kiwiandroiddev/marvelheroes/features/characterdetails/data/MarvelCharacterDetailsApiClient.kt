package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.data

import io.reactivex.Scheduler
import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.common.data.MarvelCharactersApi
import nz.co.kiwiandroiddev.marvelheroes.common.data.model.Character
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.NetworkScheduler
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.model.CharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.usecase.GetCharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import javax.inject.Inject

/**
 * In a production application, we'd insert a repository layer for caching and perhaps a
 * use case implementation with some business logic rather than having this Api client implement
 * the GetCharacterSummaries use case directly. At this stage, these layers aren't needed.
 */
class MarvelCharacterDetailsApiClient @Inject constructor(
    private val api: MarvelCharactersApi,
    @NetworkScheduler private val networkScheduler: Scheduler
) : GetCharacterDetails {

    override fun getCharacterDetails(characterId: CharacterId): Single<CharacterDetails> {
        return api.getCharacterDetails(characterId.value)
            .map { wrapper ->
                wrapper.data.results.first().toDomainModel()
            }
            .subscribeOn(networkScheduler)
    }

    private fun Character.toDomainModel(): CharacterDetails {
        return CharacterDetails(
            id = CharacterId(this.id),
            name = this.name,
            description = this.description,
            comicsAppearedIn = this.comics.items.map { it.name },
            imagePath = thumbnail.path + "/detail." + thumbnail.extension
        )
    }

}
