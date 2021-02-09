package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation

    import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.RenderingScheduler
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase.GetCharacterSummaries
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListPresenter.PartialViewState.FirstPageError
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewIntent
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState
import javax.inject.Inject
    import javax.inject.Singleton

/**
 * @param getCharacterSummaries use case to get a list of marvel characters
 * @param renderingScheduler scheduler used for calls to the view's render function (e.g.
 *  some kind of UI thread)
 */
@Singleton
class CharacterListPresenter @Inject constructor(
    private val getCharacterSummaries: GetCharacterSummaries,
    private val navigator: CharacterListNavigator,
    @RenderingScheduler private val renderingScheduler: Scheduler
) {

    companion object {
        private val InitialViewState = ViewState.Uninitialized
        private const val CharactersPerPage = 50
    }

    private sealed class PartialViewState {
        object LoadingInitialCharacters : PartialViewState()
        object LoadingMoreCharacters : PartialViewState()
        data class InitialCharactersResult(val characters: List<CharacterSummary>) :
            PartialViewState()

        data class MoreCharactersResult(val characters: List<CharacterSummary>) : PartialViewState()
        object FirstPageError : PartialViewState()
    }

    fun attachView(view: CharacterListView): Disposable {
        val partialViewStates = Observable.merge(
            loadFirstPage(),
            view.viewIntentStream().toPartialViewStates()
        )

        return partialViewStates
            .reduceToViewState()
            .distinctUntilChanged()
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
                is ViewIntent.OnRetryFromError -> loadFirstPage()
                is ViewIntent.OnSelectCharacter -> navigateToCharacter(intent.characterId)
                is ViewIntent.OnRefresh -> loadFirstPage()
            }
        }
    }

    private fun navigateToCharacter(characterId: CharacterId): Observable<out PartialViewState> {
        return Completable.fromAction {
            navigator.navigateToCharacter(characterId)
        }.toObservable()
    }

    private fun loadFirstPage(): Observable<PartialViewState> {
        return getCharacterSummaries.getCharacters(0, CharactersPerPage)
            .map { characters ->
                PartialViewState.InitialCharactersResult(characters) as PartialViewState
            }
            .onErrorReturn { _ -> FirstPageError }
            .toObservable()
            .startWith(PartialViewState.LoadingInitialCharacters)
    }

    private fun loadNextPage(currentCharacterCount: Int): ObservableSource<out PartialViewState> {
        return getCharacterSummaries.getCharacters(currentCharacterCount, CharactersPerPage)
            .map { characters ->
                PartialViewState.MoreCharactersResult(characters) as PartialViewState
            }
            .onErrorReturn { _ -> FirstPageError }     // todo bug - show inline error in this case
            .toObservable()
            .startWith(PartialViewState.LoadingMoreCharacters)
    }

    /**
     * Computes a new complete view state to be sent to the view layer (shown to the user) given
     * the previous one and a partial view state.
     */
    private fun Observable<PartialViewState>.reduceToViewState(): Observable<ViewState> {
        return this.scan(InitialViewState) { previousViewState, partialViewState ->
            when (partialViewState) {
                is PartialViewState.LoadingInitialCharacters ->
                    ViewState.LoadingInitialCharacters

                is PartialViewState.InitialCharactersResult ->
                    ViewState.Content(
                        characters = partialViewState.characters,
                        showLoadingMoreIndicator = false
                    )

                is PartialViewState.LoadingMoreCharacters ->
                    (previousViewState as ViewState.Content).copy(
                        showLoadingMoreIndicator = true
                    )

                is PartialViewState.MoreCharactersResult ->
                    (previousViewState as ViewState.Content).copy(
                        characters = previousViewState.characters + partialViewState.characters,
                        showLoadingMoreIndicator = false
                    )

                is FirstPageError ->
                    ViewState.InitialCharactersError
            }
        }
    }
}

