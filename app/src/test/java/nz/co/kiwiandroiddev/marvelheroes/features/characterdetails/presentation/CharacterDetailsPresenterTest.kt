package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.model.CharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.mocks.MockCharacterDetailsView
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.mocks.MockGetCharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class CharacterDetailsPresenterTest {

    companion object {
        val SampleCharacterDetails1 = CharacterDetails(
            id = CharacterId(1),
            name = "Wolverine",
            description = "Logan",
            comicsAppearedIn = listOf("X-Men #001"),
            imagePath = "http://marvel.com/wolverine.jpg"
        )
    }

    lateinit var mockGetCharacterDetails: MockGetCharacterDetails
    lateinit var mockView: MockCharacterDetailsView
    lateinit var presenter: CharacterDetailsPresenter

    var viewStateDisposable: Disposable? = null

    @Before
    fun setUp() {
        mockGetCharacterDetails = MockGetCharacterDetails()
        mockView = MockCharacterDetailsView()
        presenter = CharacterDetailsPresenter(
            getCharacterDetails = mockGetCharacterDetails,
            renderingScheduler = Schedulers.trampoline()
        )
    }

    @Test
    fun `shows loading initially`() {
        givenLoadingWillNotComplete()

        whenIOpenTheCharacterDetailsScreen(SampleCharacterDetails1.id)

        thenIShouldSeeALoadingIndicator()
    }

    @Test
    fun `shows error`() {
        givenLoadingWillFail()

        whenIOpenTheCharacterDetailsScreen(SampleCharacterDetails1.id)

        thenIShouldSeeALoadingError()
    }

    @Test
    fun `show character details`() {
        givenLoadingWillSucceed(SampleCharacterDetails1)

        whenIOpenTheCharacterDetailsScreen(SampleCharacterDetails1.id)

        thenIShouldSeeTheseCharacterDetails(SampleCharacterDetails1)
    }

    @Test
    fun `show character details after retry`() {
        givenLoadingWillFail()
        givenTheCharacterDetailsScreenIsOpen(SampleCharacterDetails1.id)
        givenLoadingWillSucceed(SampleCharacterDetails1)

        whenIRetry(SampleCharacterDetails1.id)

        thenIShouldSeeTheseCharacterDetails(SampleCharacterDetails1)
    }

    @Test
    fun `shows updated character details on refresh`() {
        givenLoadingWillSucceed(SampleCharacterDetails1)
        givenTheCharacterDetailsScreenIsOpen(SampleCharacterDetails1.id)
        givenLoadingWillSucceed(SampleCharacterDetails1.copy(name = "Wolverine (Ultimate)"))

        whenIRefresh(SampleCharacterDetails1.id)

        thenIShouldSeeTheseCharacterDetails(SampleCharacterDetails1.copy(name = "Wolverine (Ultimate)"))
    }

    private fun givenLoadingWillNotComplete() {
        mockGetCharacterDetails.result = Single.never()
    }

    private fun givenLoadingWillFail() {
        mockGetCharacterDetails.result = Single.error(RuntimeException())
    }

    private fun givenLoadingWillSucceed(characterDetails: CharacterDetails) {
        mockGetCharacterDetails.result = Single.just(characterDetails)
    }

    private fun givenTheCharacterDetailsScreenIsOpen(forCharacterId: CharacterId) {
        whenIOpenTheCharacterDetailsScreen(forCharacterId)
    }

    private fun whenIOpenTheCharacterDetailsScreen(forCharacterId: CharacterId) {
        viewStateDisposable = presenter.attachView(mockView)
        mockView.emitViewIntent(CharacterDetailsView.ViewIntent.OnViewReady(forCharacterId))
    }

    private fun whenIRetry(forCharacterId: CharacterId) {
        mockView.emitViewIntent(CharacterDetailsView.ViewIntent.OnRetryFromError(forCharacterId))
    }

    private fun whenIRefresh(forCharacterId: CharacterId) {
        mockView.emitViewIntent(CharacterDetailsView.ViewIntent.OnRefresh(forCharacterId))
    }

    private fun thenIShouldSeeALoadingIndicator() {
        Assertions.assertThat(mockView.lastViewStateRendered)
            .isExactlyInstanceOf(CharacterDetailsView.ViewState.Loading::class.java)
    }

    private fun thenIShouldSeeALoadingError() {
        Assertions.assertThat(mockView.lastViewStateRendered)
            .isExactlyInstanceOf(CharacterDetailsView.ViewState.Error::class.java)
    }

    private fun thenIShouldSeeTheseCharacterDetails(characterDetails: CharacterDetails) {
        Assertions.assertThat(mockView.lastViewStateRendered)
            .isEqualTo(
                CharacterDetailsView.ViewState.Content(
                    characterDetails = characterDetails
                )
            )
    }
}