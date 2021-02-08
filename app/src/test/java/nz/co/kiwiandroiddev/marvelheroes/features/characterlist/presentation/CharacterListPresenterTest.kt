package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewIntent
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState.Content
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState.FirstPageError
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState.LoadingFirstPage
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks.MockCharacterListView
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks.MockGetCharacterSummaries
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class CharacterListPresenterTest {

    companion object {
        val SampleMarvelCharacters1 = listOf(
            CharacterSummary()
        )
        val SampleMarvelCharacters2 = listOf(
            CharacterSummary()
        )
    }

    lateinit var mockGetCharacterSummaries: MockGetCharacterSummaries
    lateinit var mockView: MockCharacterListView
    lateinit var presenter: CharacterListPresenter

    var viewStateDisposable: Disposable? = null

    @Before
    fun setUp() {
        mockGetCharacterSummaries = MockGetCharacterSummaries()
        mockView = MockCharacterListView()
        presenter = CharacterListPresenter(
            getCharacterSummaries = mockGetCharacterSummaries,
            renderingScheduler = Schedulers.trampoline()
        )
    }

    @Test
    fun `shows loading indicator initially`() {
        givenLoadingFirstPageWillNotComplete()

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

    @Test
    fun `load next page intent shows loading more indicator`() {
        givenLoadingFirstPageWillSucceed(SampleMarvelCharacters1)
        givenLoadingNextPagesWillNotComplete()
        givenTheCharacterListScreenIsOpen()

        whenIRequestTheNextPage(currentCharacterCount = 20)

        thenISeeThatMoreCharactersAreLoading()
    }

    @Test
    fun `load next page intent adds characters on success`() {
        givenLoadingFirstPageWillSucceed(SampleMarvelCharacters1)
        givenLoadingNextPageWillSucceed(SampleMarvelCharacters2)
        givenTheCharacterListScreenIsOpen()

        whenIRequestTheNextPage(currentCharacterCount = 20)

        thenIShouldSeeTheseCharacters(SampleMarvelCharacters1 + SampleMarvelCharacters2)
    }

    // todo - pull to refresh

    private fun givenTheCharacterListScreenIsOpen() {
        whenIOpenTheCharacterListScreen()
    }

    private fun givenLoadingFirstPageWillFail() {
        mockGetCharacterSummaries.firstPageResult = Single.error(RuntimeException())
    }

    private fun givenLoadingFirstPageWillNotComplete() {
        mockGetCharacterSummaries.firstPageResult = Single.never()
    }

    private fun givenLoadingFirstPageWillSucceed(characters: List<CharacterSummary>) {
        mockGetCharacterSummaries.firstPageResult = Single.just(characters)
    }

    private fun givenLoadingNextPagesWillNotComplete() {
        mockGetCharacterSummaries.nextPagesResult = Single.never()
    }

    private fun givenLoadingNextPageWillSucceed(characters: List<CharacterSummary>) {
        mockGetCharacterSummaries.nextPagesResult = Single.just(characters)
    }

    private fun whenIOpenTheCharacterListScreen() {
        viewStateDisposable = presenter.attachView(mockView)
    }

    private fun whenIRequestTheNextPage(currentCharacterCount: Int) {
        mockView.emitViewIntent(ViewIntent.OnLoadNextPage(currentCharacterCount))
    }

    private fun thenIShouldSeeTheFirstPageLoading() {
        assertThat(mockView.lastViewStateRendered)
            .isExactlyInstanceOf(LoadingFirstPage::class.java)
    }

    private fun thenIShouldSeeAFirstPageLoadingError() {
        assertThat(mockView.lastViewStateRendered)
            .isExactlyInstanceOf(FirstPageError::class.java)
    }

    private fun thenIShouldSeeTheseCharacters(characters: List<CharacterSummary>) {
        assertThat(mockView.lastViewStateRendered)
            .isEqualTo(Content(characters = characters, showLoadingMoreIndicator = false))
    }

    private fun thenISeeThatMoreCharactersAreLoading() {
        assertThat((mockView.lastViewStateRendered as? Content)?.showLoadingMoreIndicator)
            .isTrue
    }
}