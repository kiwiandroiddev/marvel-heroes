package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation

import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.MarvelCharacter
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState.*
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks.MockCharacterListView
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks.MockGetCharacters
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class CharacterListPresenterTest {

    companion object {
        val SampleMarvelCharacters1 = listOf(
            MarvelCharacter()
        )
    }

    lateinit var mockGetCharacters: MockGetCharacters
    lateinit var mockView: MockCharacterListView
    lateinit var presenter: CharacterListPresenter

    var viewStateDisposable: Disposable? = null

    @Before
    fun setUp() {
        mockGetCharacters = MockGetCharacters()
        mockView = MockCharacterListView()
        presenter = CharacterListPresenter(
            getCharacters = mockGetCharacters,
            renderingScheduler = Schedulers.trampoline()
        )
    }

    @Test
    fun `shows loading indicator initially`() {
        givenLoadingWillNotComplete()

        whenIOpenTheCharacterListScreen()

        thenIShouldSeeTheFirstPageLoading()
    }

    @Test
    fun `shows first page load error`() {
        givenLoadingFirstPageWillFail()

        whenIOpenTheCharacterListScreen()

        thenIShouldSeeAFirstPageLoadingError()
    }

    @Test
    fun `show first page characters`() {
        givenLoadingFirstPageWillSucceed(SampleMarvelCharacters1)

        whenIOpenTheCharacterListScreen()

        thenIShouldSeeTheseCharacters(SampleMarvelCharacters1)
    }

    private fun givenLoadingFirstPageWillFail() {
        mockGetCharacters.throwError = true
    }

    private fun givenLoadingWillNotComplete() {
        mockGetCharacters.neverComplete = true
    }

    private fun givenLoadingFirstPageWillSucceed(characters: List<MarvelCharacter>) {
        mockGetCharacters.result = characters
    }

    private fun whenIOpenTheCharacterListScreen() {
        viewStateDisposable = presenter.attachView(mockView)
    }

    private fun thenIShouldSeeTheFirstPageLoading() {
        assertThat(mockView.lastViewStateRendered)
            .isExactlyInstanceOf(LoadingFirstPage::class.java)
    }

    private fun thenIShouldSeeAFirstPageLoadingError() {
        assertThat(mockView.lastViewStateRendered)
            .isExactlyInstanceOf(FirstPageError::class.java)
    }

    private fun thenIShouldSeeTheseCharacters(characters: List<MarvelCharacter>) {
        assertThat(mockView.lastViewStateRendered)
            .isEqualTo(Content(characters))
    }
}