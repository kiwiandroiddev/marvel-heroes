package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.model

import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId

data class CharacterDetails(
    val id: CharacterId,
    val name: String? = null,
)