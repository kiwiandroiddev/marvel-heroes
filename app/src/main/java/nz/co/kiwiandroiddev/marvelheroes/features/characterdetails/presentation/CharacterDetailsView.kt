package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation

import io.reactivex.Observable
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.model.CharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId

interface CharacterDetailsView {

    // Todo it's a bit weird to pass the character ID again with each intent. This is because
    //  presenters are stateless. An improvement to be made here could be to scope the presenter
    //  by character ID i.e. request a presenter factory/provider from the DI container,
    //  passing the character ID as an argument.
    sealed class ViewIntent {
        data class OnRetryFromError(val characterId: CharacterId) : ViewIntent()
        data class OnRefresh(val characterId: CharacterId) : ViewIntent()
        data class OnViewReady(val characterId: CharacterId) : ViewIntent()
    }

    sealed class ViewState {
        object Uninitialized : ViewState()
        object Loading : ViewState()
        object Error : ViewState()
        data class Content(val characterDetails: CharacterDetails) : ViewState()
    }

    fun viewIntentStream(): Observable<ViewIntent>
    fun render(viewState: ViewState)

}