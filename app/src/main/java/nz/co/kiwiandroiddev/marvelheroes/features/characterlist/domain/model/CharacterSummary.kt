package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model

data class CharacterSummary(
    val id: CharacterId,
    val name: String? = null,
    val thumbnailImagePath: String? = null
)
