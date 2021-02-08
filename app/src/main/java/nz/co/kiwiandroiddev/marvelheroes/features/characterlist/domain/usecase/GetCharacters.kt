package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase

import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.MarvelCharacter

interface GetCharacters {

    fun getCharacters(): Single<List<MarvelCharacter>>

}