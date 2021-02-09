package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import nz.co.kiwiandroiddev.marvelheroes.MarvelHeroesApplication
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsPresenter
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import javax.inject.Inject

class CharacterDetailsViewModel(app: Application, characterId: CharacterId) :
    AndroidViewModel(app), CharacterDetailsView {

    @Inject
    internal lateinit var presenter: CharacterDetailsPresenter

    private val disposable: Disposable

    val viewStateSubject = BehaviorSubject.create<CharacterDetailsView.ViewState>()
    private val viewIntentSubject = PublishSubject.create<CharacterDetailsView.ViewIntent>()

    init {
        (app as MarvelHeroesApplication).appComponent.inject(this)
        disposable = presenter.attachView(this)

        signalIntent(CharacterDetailsView.ViewIntent.OnViewReady(characterId))
    }

    override fun viewIntentStream(): Observable<CharacterDetailsView.ViewIntent> {
        return viewIntentSubject
    }

    fun signalIntent(intent: CharacterDetailsView.ViewIntent) {
        viewIntentSubject.onNext(intent)
    }

    override fun render(viewState: CharacterDetailsView.ViewState) {
        viewStateSubject.onNext(viewState)
    }

    override fun onCleared() {
        if (!disposable.isDisposed) disposable.dispose()
    }
}

class CharacterDetailsViewModelFactory(
    private val app: Application,
    private val characterId: CharacterId
) : ViewModelProvider.AndroidViewModelFactory(app) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CharacterDetailsViewModel(app, characterId) as T
    }
}