package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import nz.co.kiwiandroiddev.marvelheroes.MarvelHeroesApplication
import nz.co.kiwiandroiddev.marvelheroes.R
import nz.co.kiwiandroiddev.marvelheroes.common.epoxy.error
import nz.co.kiwiandroiddev.marvelheroes.common.epoxy.loading
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListPresenter
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewIntent
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView.ViewState
import javax.inject.Inject

class CharacterListFragment : Fragment(), CharacterListView {

    companion object {
        private const val GridColumns = 2
    }

    @Inject
    lateinit var presenter: CharacterListPresenter

    private val viewIntentSubject = PublishSubject.create<ViewIntent>()

    private var epoxyRecyclerView: EpoxyRecyclerView? = null
    private var textView: TextView? = null
    private var loadMoreButton: View? = null

    private var viewStateDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies()
    }

    private fun injectDependencies() {
        (requireContext().applicationContext as MarvelHeroesApplication).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_character_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews(view)
    }

    private fun bindViews(view: View) {
        epoxyRecyclerView = view.findViewById(R.id.epoxy_recycler_view)
        textView = view.findViewById(R.id.textview_first)
        loadMoreButton = view.findViewById(R.id.button_first)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView(activity!!)
    }

    private fun setupRecyclerView(context: Context) {
        epoxyRecyclerView!!.layoutManager = GridLayoutManager(context, GridColumns)
    }

    override fun onResume() {
        super.onResume()
        viewStateDisposable = presenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        if (viewStateDisposable?.isDisposed == false) {
            viewStateDisposable?.dispose()
        }
    }

    override fun viewIntentStream(): Observable<ViewIntent> {
        return viewIntentSubject
    }

    override fun render(viewState: ViewState) {
        println("Got viewstate: $viewState")

        epoxyRecyclerView!!.withModels {
            when (viewState) {
                is ViewState.Uninitialized -> { /* show nothing */ }
                is ViewState.LoadingFirstPage -> loading {
                    spanSizeOverride { _, _, _ -> GridColumns }
                }
                is ViewState.FirstPageError -> error {
                    errorTitle("Error fetching characters")
                    onActionClickListener {
                        signalIntent(ViewIntent.OnRetryFromError)
                    }
                    spanSizeOverride { _, _, _ -> GridColumns }
                }
                is ViewState.Content -> buildCharacterModels(viewState.characters)
            }
        }

        val vsText = when (viewState) {
            is ViewState.Content -> {
                loadMoreButton?.setOnClickListener {
                    signalIntent(
                        ViewIntent.OnLoadNextPage(
                            currentCharacterCount = viewState.characters.size
                        )
                    )
                }

                viewState.characters.map { it.name }.toString()
            }
            else -> viewState.toString()
        }

        textView?.setText(vsText)
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

    private fun signalIntent(intent: ViewIntent) {
        viewIntentSubject.onNext(intent)
    }
}
