package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView

class MockCharacterListView : CharacterListView {
    private val viewIntentStream = PublishSubject.create<CharacterListView.ViewIntent>()
    val viewStatesRendered = mutableListOf<CharacterListView.ViewState>()

    override fun viewIntentStream(): Observable<CharacterListView.ViewIntent> {
        return viewIntentStream
    }

    override fun render(viewState: CharacterListView.ViewState) {
        println("got viewState: $viewState")
        viewStatesRendered.add(viewState)
    }

    fun emitViewIntent(intent: CharacterListView.ViewIntent) {
        viewIntentStream.onNext(intent)
    }

    val lastViewStateRendered: CharacterListView.ViewState?
        get() = viewStatesRendered.lastOrNull()
}