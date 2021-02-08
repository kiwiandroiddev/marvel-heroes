package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.RenderingScheduler
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase.GetCharacterSummaries
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListPresenter.PartialViewState.CharactersResult
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListPresenter.PartialViewState.FirstPageError
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListPresenter.PartialViewState.LoadingCharacters
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewIntent
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState
import javax.inject.Inject

/**
 * @param getCharacterSummaries use case to get a list of marvel characters
 * @param renderingScheduler scheduler used for calls to the view's render function (e.g.
 *  some kind of UI thread)
 */
class CharacterListPresenter @Inject constructor(
    private val getCharacterSummaries: GetCharacterSummaries,
    @RenderingScheduler private val renderingScheduler: Scheduler
) {

    companion object {
        private val InitialViewState = ViewState.Uninitialized
        private const val CharactersPerPage = 20
    }

    private sealed class PartialViewState {
        object LoadingCharacters : PartialViewState()
        data class CharactersResult(val characters: List<CharacterSummary>) : PartialViewState()
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

    /**
     * Fulfil intents from the user by (typically) making a call to the domain layer for some data
     * and converting the result into a partial view state.
     */
    private fun Observable<ViewIntent>.toPartialViewStates(): Observable<PartialViewState> {
        return this.flatMap { intent ->
            when (intent) {
                is ViewIntent.OnLoadNextPage -> loadNextPage(intent.currentCharacterCount)
                is ViewIntent.OnSelectCharacter -> Observable.empty()   // todo
                is ViewIntent.OnRetryFromError -> Observable.empty()   // todo
            }
        }
    }

    private fun loadFirstPage(): Observable<PartialViewState> {
        return getCharacterSummaries.getCharacters(0, CharactersPerPage)
            .map { characters ->
                CharactersResult(characters) as PartialViewState
            }
            .onErrorReturn { error ->
                FirstPageError as PartialViewState
            }
            .toObservable()
            .startWith(LoadingCharacters)
    }

    private fun loadNextPage(currentCharacterCount: Int): ObservableSource<out PartialViewState> {
        return getCharacterSummaries.getCharacters(currentCharacterCount, CharactersPerPage)
            .map { characters ->
                CharactersResult(characters) as PartialViewState
            }
            .onErrorReturn { error ->
                FirstPageError as PartialViewState
            }
            .toObservable()
            .startWith(LoadingCharacters)
    }

    /**
     * Computes a new complete view state to be sent to the view layer (shown to the user) given
     * the previous one and a partial view state.
     */
    private fun Observable<PartialViewState>.reduceToViewState(): Observable<ViewState> {
        return this.scan(InitialViewState) { previousViewState, partialViewState ->
            when (partialViewState) {
                is LoadingCharacters ->
                    when (previousViewState) {
                        is ViewState.Content -> previousViewState.copy(showLoadingMoreIndicator = true)
                        else -> ViewState.LoadingFirstPage
                    }

                is CharactersResult ->
                    reduceNewCharactersResult(previousViewState, partialViewState)

                is FirstPageError ->
                    ViewState.FirstPageError
            }
        }
    }

    private fun reduceNewCharactersResult(
        previousViewState: ViewState,
        partialViewState: CharactersResult
    ) = when (previousViewState) {
        is ViewState.Content -> previousViewState.copy(
            characters = previousViewState.characters + partialViewState.characters,
            showLoadingMoreIndicator = false
        )
        else -> ViewState.Content(
            characters = partialViewState.characters,
            showLoadingMoreIndicator = false
        )
    }

}

