package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation

import io.reactivex.Observable
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary

interface CharacterListView {

    sealed class ViewIntent {
        data class OnLoadNextPage(val currentCharacterCount: Int) : ViewIntent()
        object OnRetryFromError : ViewIntent()
        object OnRefresh : ViewIntent()

        data class OnSelectCharacter(val characterId: CharacterId) : ViewIntent()
    }

    sealed class ViewState {
        object Uninitialized : ViewState()
        object LoadingInitialCharacters : ViewState()
        object InitialCharactersError : ViewState()

        // store some meta-data with character list - total size. Add calculated property
        // 'hasMore', view can use this to know whether to ask for next page on scroll to the end.
        // The intent needs to pass the ID/index of the last character the view has; the presenter
        // can use this as an offset plus an (internal) limit value to request the next page
        // from the domain layer.
        data class Content(
            val characters: List<CharacterSummary>,
            val showLoadingMoreIndicator: Boolean,
            val canLoadMore: Boolean
        ) : ViewState()

        // Convenience functions
        fun showLoadingMoreIndicator(): Boolean =
            (this as? Content)?.showLoadingMoreIndicator == true

        fun canLoadMore(): Boolean =
            (this as? Content)?.canLoadMore == true

        fun haveAnyCharacters(): Boolean =
            this is Content
    }

    fun viewIntentStream(): Observable<ViewIntent>
    fun render(viewState: ViewState)
}