package nz.co.kiwiandroiddev.marvelheroes.common.data.model

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
    val description: String,
    val thumbnail: Image,
    val comics: ComicList
)

data class Image(
    val path: String,
    val extension: String
)

data class ComicList(
    val items: List<ComicSummary>
)

data class ComicSummary(
    val name: String
)
