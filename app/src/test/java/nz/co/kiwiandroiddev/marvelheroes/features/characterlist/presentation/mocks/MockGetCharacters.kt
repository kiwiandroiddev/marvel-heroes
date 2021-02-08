package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks

import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.MarvelCharacter
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase.GetCharacters

class MockGetCharacters : GetCharacters {

    var neverComplete: Boolean = false
    var throwError: Boolean = false
    var result: List<MarvelCharacter> = emptyList()

    override fun getCharacters(): Single<List<MarvelCharacter>> {
        return when {
            neverComplete -> Single.never()
            throwError -> Single.error(RuntimeException())
            else -> Single.just(result)
        }
    }

}