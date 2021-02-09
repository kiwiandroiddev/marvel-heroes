package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import nz.co.kiwiandroiddev.marvelheroes.MarvelHeroesApplication
import nz.co.kiwiandroiddev.marvelheroes.R
import nz.co.kiwiandroiddev.marvelheroes.common.epoxy.error
import nz.co.kiwiandroiddev.marvelheroes.common.epoxy.loading
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.model.CharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsPresenter
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView.ViewIntent
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView.ViewState
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import javax.inject.Inject

class CharacterDetailsFragment : Fragment(), CharacterDetailsView {

    companion object {
        private const val ArgumentKeyCharacterId = "character_id"

        fun newInstance(characterId: CharacterId): CharacterDetailsFragment {
            return CharacterDetailsFragment().apply {
                val args = Bundle()
                args.putInt(ArgumentKeyCharacterId, characterId.value)
                arguments = args
            }
        }
    }

    @Inject
    lateinit var presenter: CharacterDetailsPresenter

    private val viewIntentSubject = PublishSubject.create<ViewIntent>()
    private var viewStateDisposable: Disposable? = null

    private var epoxyRecyclerView: EpoxyRecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies()
    }

    private fun injectDependencies() {
        (requireContext().applicationContext as MarvelHeroesApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_character_details, container, false)
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
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout!!.setOnRefreshListener {
            signalIntent(ViewIntent.OnRefresh(getCharacterId()))
        }
    }

    override fun onResume() {
        super.onResume()
        viewStateDisposable = presenter.attachView(this)
        signalIntent(ViewIntent.OnViewReady(getCharacterId()))
    }

    private fun getCharacterId(): CharacterId {
        return CharacterId(requireArguments().getInt(ArgumentKeyCharacterId))
    }

    override fun onPause() {
        super.onPause()
        if (viewStateDisposable?.isDisposed == false) {
            viewStateDisposable?.dispose()
        }
    }

    override fun viewIntentStream(): Observable<ViewIntent> = viewIntentSubject
        .doOnNext { intent ->
            println("ZZZ emitting intent: $intent")
        }

    private fun signalIntent(intent: ViewIntent) {
        viewIntentSubject.onNext(intent)
    }

    override fun render(viewState: ViewState) {
        println("ZZZ render viewstate: $viewState")

        epoxyRecyclerView!!.withModels {
            when (viewState) {
                is ViewState.Loading -> loading {  }
                is ViewState.Error -> error {
                    errorTitle("Error fetching character")
                }
                is ViewState.Content -> buildCharacterDetailsModel(viewState.characterDetails)
            }
        }
    }

    private fun EpoxyController.buildCharacterDetailsModel(characterDetails: CharacterDetails) {
        characterDetails {
            id(characterDetails.id.value)
            name(characterDetails.name)
            description(characterDetails.description)
            comicsAppearedIn(characterDetails.comicsAppearedIn)
            imagePath(characterDetails.imagePath)
        }
    }
}