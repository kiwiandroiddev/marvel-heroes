package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model

/**
 * Using an inline class here for type safety (prevents a class of programmer errors - e.g.
 * some other non-ID integer accidentally)
 */
inline class CharacterId(val value: Int)