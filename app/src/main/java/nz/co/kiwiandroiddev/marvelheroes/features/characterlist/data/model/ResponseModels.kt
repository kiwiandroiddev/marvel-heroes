package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.data.model

data class CharacterDataWrapper(
    val data: CharacterDataContainer
)

data class CharacterDataContainer(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<Character>
)

data class Character(
    val id: Int,
    val name: String,
    val thumbnail: Image
)

data class Image(
    val path: String,
    val extension: String
)
