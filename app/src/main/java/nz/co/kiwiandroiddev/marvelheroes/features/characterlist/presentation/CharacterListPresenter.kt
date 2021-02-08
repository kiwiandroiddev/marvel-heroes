package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.MarvelCharacter
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase.GetCharacters

/**
 * @param getCharacters use case to get a list of marvel characters
 * @param renderingScheduler scheduler used for calls to the view's render function (should be
 *  some kind of UI thread)
 */
class CharacterListPresenter(
    private val getCharacters: GetCharacters,
    private val renderingScheduler: Scheduler
) {

    companion object {
        private val InitialViewState = CharacterListView.ViewState.LoadingFirstPage
    }

    private sealed class PartialViewState {
        data class CharactersResult(val characters: List<MarvelCharacter>) : PartialViewState()
        object FirstPageError : PartialViewState()
    }

    fun attachView(view: CharacterListView): Disposable {
        val partialViewStates = Observable.merge(
            loadFirstPage(),
            view.viewIntentStream().toPartialViewStates()
        )

        return partialViewStates
            .reduceToViewState()
            .observeOn(renderingScheduler)
            .subscribe(view::render)
    }

    private fun loadFirstPage(): Observable<PartialViewState> {
        return getCharacters.getCharacters()
            .map { characters ->
                PartialViewState.CharactersResult(characters) as PartialViewState
            }
            .onErrorReturn { error ->
                PartialViewState.FirstPageError as PartialViewState
            }
            .toObservable()
    }

    /**
     * Fulfil intents from the user by (typically) making a call to the domain layer for some data
     * and converting the result into a partial view state.
     */
    private fun Observable<CharacterListView.ViewIntent>.toPartialViewStates(): Observable<PartialViewState> {
        return Observable.empty()
    }

    /**
     * Computes a new complete view state to be sent to the view layer (shown to the user) given
     * the previous one and a partial view state.
     */
    private fun Observable<PartialViewState>.reduceToViewState(): Observable<CharacterListView.ViewState> {
        return this.scan(InitialViewState) { previousViewState, partialViewState ->
            when (partialViewState) {
                is PartialViewState.CharactersResult ->
                    CharacterListView.ViewState.Content(partialViewState.characters)

                is PartialViewState.FirstPageError ->
                    CharacterListView.ViewState.FirstPageError
            }
        }
    }

}

