package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.RenderingScheduler
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.usecase.GetCharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView.ViewIntent
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView.ViewState
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterDetailsPresenter @Inject constructor(
    private val getCharacterDetails: GetCharacterDetails,
    @RenderingScheduler private val renderingScheduler: Scheduler
) {

    fun attachView(view: CharacterDetailsView): Disposable {
        return view.viewIntentStream()
            .toViewStateStream()
            .distinctUntilChanged()
            .observeOn(renderingScheduler)
            .subscribe(view::render)
    }

    private fun loadCharacterDetails(characterId: CharacterId): Observable<ViewState> {
        return getCharacterDetails.getCharacterDetails(characterId)
            .map { ViewState.Content(it) as ViewState }
            .onErrorReturn { ViewState.Error }
            .toObservable()
            .startWith(ViewState.Loading)
    }

    private fun Observable<ViewIntent>.toViewStateStream(): Observable<ViewState> {
        return this.flatMap { viewIntent ->
            when (viewIntent) {
                is ViewIntent.OnViewReady -> loadCharacterDetails(viewIntent.characterId)
                is ViewIntent.OnRefresh -> loadCharacterDetails(viewIntent.characterId)
                is ViewIntent.OnRetryFromError -> loadCharacterDetails(viewIntent.characterId)
            }
        }
    }
}
