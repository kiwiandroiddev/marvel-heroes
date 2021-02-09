package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import io.reactivex.disposables.Disposable
import nz.co.kiwiandroiddev.marvelheroes.R
import nz.co.kiwiandroiddev.marvelheroes.common.epoxy.error
import nz.co.kiwiandroiddev.marvelheroes.common.epoxy.loading
import nz.co.kiwiandroiddev.marvelheroes.common.widget.OnScrolledToBottomListener
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewIntent
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState

class CharacterListFragment : Fragment() {

    companion object {
        private const val GridColumns = 2
    }

    private var viewModel: CharacterListViewModel? = null
    private var previousViewState: ViewState? = null
    private var viewStateDisposable: Disposable? = null

    private var epoxyRecyclerView: EpoxyRecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var scrolledToBottomListener: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_character_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRecyclerView = view.findViewById(R.id.epoxy_recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView(requireActivity())
        setupSwipeRefreshLayout()
    }

    private fun setupRecyclerView(context: Context) {
        epoxyRecyclerView?.apply {
            layoutManager = GridLayoutManager(context, GridColumns)

            addOnScrollListener(object : OnScrolledToBottomListener() {
                override fun onScrolledToBottom() {
                    scrolledToBottomListener()
                }
            })
        }
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout!!.setOnRefreshListener {
            signalIntent(ViewIntent.OnRefresh)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(this).get(CharacterListViewModel::class.java)
        viewStateDisposable = viewModel?.viewStateSubject?.subscribe(::render)
    }

    override fun onStop() {
        super.onStop()

        viewModel = null
        if (viewStateDisposable?.isDisposed == false) {
            viewStateDisposable?.dispose()
        }
    }

    override fun onResume() {
        super.onResume()
        setActionBarTitle()
    }

    private fun setActionBarTitle() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.character_list_screen_title)
    }

    private fun signalIntent(intent: ViewIntent) {
        viewModel?.signalIntent(intent)
    }

    private fun render(viewState: ViewState) {
        println("ZZZ render viewstate: $viewState, previousViewState = $previousViewState")

        val didStopLoadingFirstPage = (previousViewState == ViewState.LoadingInitialCharacters &&
            viewState != ViewState.LoadingInitialCharacters)

        if (didStopLoadingFirstPage) {
            swipeRefreshLayout?.isRefreshing = false
        }

        if (viewState is ViewState.Content) {
            scrolledToBottomListener = {
                signalIntent(ViewIntent.OnLoadNextPage(
                    currentCharacterCount = viewState.characters.size
                ))
            }
        }

        epoxyRecyclerView!!.withModels {
            when (viewState) {
                is ViewState.LoadingInitialCharacters -> buildLoadingModel(previousViewState)
                is ViewState.InitialCharactersError -> buildErrorModel()
                is ViewState.Content -> buildCharacterModels(viewState.characters)
            }

            if (viewState.showLoadingMoreIndicator()) {
                inlineLoading {
                    spanSizeOverride { _, _, _ -> GridColumns }
                }
            }

            previousViewState = viewState
        }
    }

    private fun EpoxyController.buildLoadingModel(previousViewState: ViewState?) {
        println("ZZZ in building loading model, prev view state = $previousViewState")
        if (previousViewState?.haveAnyCharacters() == false) {
            loading {
                spanSizeOverride { _, _, _ -> GridColumns }
            }
        }
    }

    private fun EpoxyController.buildErrorModel() {
        error {
            errorTitle("Error fetching characters")
            onActionClickListener {
                signalIntent(ViewIntent.OnRetryFromError)
            }
            spanSizeOverride { _, _, _ -> GridColumns }
        }
    }

    private fun EpoxyController.buildCharacterModels(characters: List<CharacterSummary>) {
        characters.forEach { character ->
            characterSummary {
                id(character.id.toString())
                name(character.name)
                thumbnailPath(character.thumbnailImagePath)
                onClickListener { signalIntent(ViewIntent.OnSelectCharacter(character.id)) }
            }
        }
    }
}
