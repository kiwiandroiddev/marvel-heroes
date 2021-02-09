package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummarySubList
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewIntent
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState.Content
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState.InitialCharactersError
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState.LoadingInitialCharacters
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks.MockCharacterListNavigator
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks.MockCharacterListView
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.mocks.MockGetCharacterSummaries
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class CharacterListPresenterTest {

    companion object {
        val SampleMarvelCharacters1 = listOf(
            CharacterSummary(id = CharacterId(1))
        )
        val SampleMarvelCharacters2 = listOf(
            CharacterSummary(id = CharacterId(2))
        )
    }

    lateinit var mockGetCharacterSummaries: MockGetCharacterSummaries
    lateinit var mockView: MockCharacterListView
    lateinit var mockNavigator: MockCharacterListNavigator
    lateinit var presenter: CharacterListPresenter

    var viewStateDisposable: Disposable? = null

    @Before
    fun setUp() {
        mockGetCharacterSummaries = MockGetCharacterSummaries()
        mockView = MockCharacterListView()
        mockNavigator = MockCharacterListNavigator()
        presenter = CharacterListPresenter(
            getCharacterSummaries = mockGetCharacterSummaries,
            navigator = mockNavigator,
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
    fun `show first page characters after retry`() {
        givenLoadingFirstPageWillFail()
        givenTheCharacterListScreenIsOpen()
        givenLoadingFirstPageWillSucceed(SampleMarvelCharacters1)

        whenIRetry()

        thenIShouldSeeTheseCharacters(SampleMarvelCharacters1)
    }

    @Test
    fun `shows updated first page on refresh`() {
        givenLoadingFirstPageWillSucceed(SampleMarvelCharacters1)
        givenTheCharacterListScreenIsOpen()
        givenLoadingFirstPageWillSucceed(SampleMarvelCharacters2)

        whenIRefresh()

        thenIShouldSeeTheseCharacters(SampleMarvelCharacters2)
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

    @Test
    fun `selecting a character triggers navigation`() {
        givenLoadingFirstPageWillSucceed(SampleMarvelCharacters1)
        givenTheCharacterListScreenIsOpen()

        whenISelectACharacter(id = SampleMarvelCharacters1.first().id)

        thenNavigationIsTriggered(targetCharacterId = SampleMarvelCharacters1.first().id)
    }

    @Test
    fun `can load more if all available characters are not loaded`() {
        givenLoadingFirstPageWillSucceed(SampleMarvelCharacters1, totalAvailable = 20)

        whenIOpenTheCharacterListScreen()

        thenThereAreMoreCharactersToLoad()
    }

    @Test
    fun `cannot load more once all available characters loaded`() {
        givenLoadingFirstPageWillSucceed(SampleMarvelCharacters1, totalAvailable = 1)

        whenIOpenTheCharacterListScreen()

        thenThereAreNoMoreCharactersToLoad()
    }

    private fun givenTheCharacterListScreenIsOpen() {
        whenIOpenTheCharacterListScreen()
    }

    private fun givenLoadingFirstPageWillFail() {
        mockGetCharacterSummaries.firstPageResult = Single.error(RuntimeException())
    }

    private fun givenLoadingFirstPageWillNotComplete() {
        mockGetCharacterSummaries.firstPageResult = Single.never()
    }

    private fun givenLoadingFirstPageWillSucceed(
        characters: List<CharacterSummary>,
        totalAvailable: Int = 20
    ) {
        mockGetCharacterSummaries.firstPageResult =
            Single.just(CharacterSummarySubList(characters, totalAvailable))
    }

    private fun givenLoadingNextPagesWillNotComplete() {
        mockGetCharacterSummaries.nextPagesResult = Single.never()
    }

    private fun givenLoadingNextPageWillSucceed(
        characters: List<CharacterSummary>,
        totalAvailable: Int = 20
    ) {
        mockGetCharacterSummaries.nextPagesResult =
            Single.just(CharacterSummarySubList(characters, totalAvailable))
    }

    private fun whenIOpenTheCharacterListScreen() {
        viewStateDisposable = presenter.attachView(mockView)
    }

    private fun whenIRetry() {
        mockView.emitViewIntent(ViewIntent.OnRetryFromError)
    }

    private fun whenIRefresh() {
        mockView.emitViewIntent(ViewIntent.OnRefresh)
    }

    private fun whenISelectACharacter(id: CharacterId) {
        mockView.emitViewIntent(ViewIntent.OnSelectCharacter(id))
    }

    private fun whenIRequestTheNextPage(currentCharacterCount: Int) {
        mockView.emitViewIntent(ViewIntent.OnLoadNextPage(currentCharacterCount))
    }

    private fun thenIShouldSeeTheFirstPageLoading() {
        assertThat(mockView.lastViewStateRendered)
            .isExactlyInstanceOf(LoadingInitialCharacters::class.java)
    }

    private fun thenIShouldSeeAFirstPageLoadingError() {
        assertThat(mockView.lastViewStateRendered)
            .isExactlyInstanceOf(InitialCharactersError::class.java)
    }

    private fun thenIShouldSeeTheseCharacters(
        characters: List<CharacterSummary>,
        canLoadMore: Boolean = true
    ) {
        assertThat(mockView.lastViewStateRendered)
            .isEqualTo(
                Content(
                    characters = characters,
                    showLoadingMoreIndicator = false,
                    canLoadMore = canLoadMore
                )
            )
    }

    private fun thenISeeThatMoreCharactersAreLoading() {
        assertThat((mockView.lastViewStateRendered as? Content)?.showLoadingMoreIndicator)
            .isTrue
    }

    private fun thenNavigationIsTriggered(targetCharacterId: CharacterId) {
        assertThat(mockNavigator.lastCalledWithCharacterId).isEqualTo(targetCharacterId)
    }

    private fun thenThereAreMoreCharactersToLoad() {
        assertThat((mockView.lastViewStateRendered as? Content)?.canLoadMore).isTrue
    }

    private fun thenThereAreNoMoreCharactersToLoad() {
        assertThat((mockView.lastViewStateRendered as? Content)?.canLoadMore).isFalse
    }
}