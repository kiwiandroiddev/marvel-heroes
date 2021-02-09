package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model

/**
 * Wrapper for a subset of characters from the Marvel characters database.
 * The totalAvailable field indicates how many characters there are in the database that
 * could potentially be fetched.
 */
data class CharacterSummarySubList(
    val characters: List<CharacterSummary>,
    val totalAvailable: Int
)