package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import nz.co.kiwiandroiddev.marvelheroes.MarvelHeroesApplication
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListPresenter
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView
import javax.inject.Inject

class CharacterListViewModel(app: Application) : AndroidViewModel(app), CharacterListView {

    @Inject
    internal lateinit var presenter: CharacterListPresenter

    private val disposable: Disposable

    val viewStateSubject = BehaviorSubject.create<CharacterListView.ViewState>()
    private val viewIntentSubject = PublishSubject.create<CharacterListView.ViewIntent>()

    init {
        (app as MarvelHeroesApplication).appComponent.inject(this)
        disposable = presenter.attachView(this)
    }

    override fun viewIntentStream(): Observable<CharacterListView.ViewIntent> {
        return viewIntentSubject
    }

    fun signalIntent(intent: CharacterListView.ViewIntent) {
        viewIntentSubject.onNext(intent)
    }

    override fun render(viewState: CharacterListView.ViewState) {
        viewStateSubject.onNext(viewState)
    }

    override fun onCleared() {
        if (!disposable.isDisposed) disposable.dispose()
    }

}