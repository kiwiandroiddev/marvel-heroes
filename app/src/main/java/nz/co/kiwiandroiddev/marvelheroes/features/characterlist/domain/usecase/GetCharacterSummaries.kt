package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase

import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary

interface GetCharacterSummaries {

    /**
     * Gets a subset of characters from the Marvel database, ordered alphabetically by name.
     */
    fun getCharacters(offset: Int, limit: Int): Single<List<CharacterSummary>>

}