package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.mocks

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView

class MockCharacterDetailsView : CharacterDetailsView {
    private val viewIntentStream = PublishSubject.create<CharacterDetailsView.ViewIntent>()
    val viewStatesRendered = mutableListOf<CharacterDetailsView.ViewState>()

    override fun viewIntentStream(): Observable<CharacterDetailsView.ViewIntent> {
        return viewIntentStream
    }

    override fun render(viewState: CharacterDetailsView.ViewState) {
        println("got viewState: $viewState")
        viewStatesRendered.add(viewState)
    }

    fun emitViewIntent(intent: CharacterDetailsView.ViewIntent) {
        viewIntentStream.onNext(intent)
    }

    val lastViewStateRendered: CharacterDetailsView.ViewState?
        get() = viewStatesRendered.lastOrNull()
}
