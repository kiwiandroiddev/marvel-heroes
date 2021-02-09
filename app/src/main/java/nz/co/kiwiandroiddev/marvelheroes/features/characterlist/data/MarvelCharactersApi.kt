package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.data

import io.reactivex.Single
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.data.model.CharacterDataWrapper
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelCharactersApi {

    @GET("characters")
    fun getCharacters(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Single<CharacterDataWrapper>

    @GET("characters/{id}")
    fun getCharacterDetails(
        @Path("id") characterId: Int
    ): Single<CharacterDataWrapper>
}
