package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.view

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import io.reactivex.disposables.Disposable
import nz.co.kiwiandroiddev.marvelheroes.R
import nz.co.kiwiandroiddev.marvelheroes.common.epoxy.error
import nz.co.kiwiandroiddev.marvelheroes.common.epoxy.loading
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.model.CharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView.ViewIntent
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView.ViewState
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId

class CharacterDetailsFragment : Fragment() {

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

    private var viewModel: CharacterDetailsViewModel? = null
    private var viewStateDisposable: Disposable? = null

    private var epoxyRecyclerView: EpoxyRecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

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

    override fun onStart() {
        super.onStart()

        val app = requireContext().applicationContext as Application
        viewModel =
            ViewModelProvider(this, CharacterDetailsViewModelFactory(app, getCharacterId())).get(
                CharacterDetailsViewModel::class.java
            )

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
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.character_details_screen_title)
    }

    private fun getCharacterId(): CharacterId {
        return CharacterId(requireArguments().getInt(ArgumentKeyCharacterId))
    }

    private fun signalIntent(intent: ViewIntent) {
        viewModel?.signalIntent(intent)
    }

    fun render(viewState: ViewState) {
        println("ZZZ render viewstate: $viewState")

        epoxyRecyclerView!!.withModels {
            when (viewState) {
                is ViewState.Loading -> loading { }
                is ViewState.Error -> error {
                    errorTitle("Error fetching character")
                }
                is ViewState.Content -> buildCharacterDetailsModel(viewState.characterDetails)
            }
        }

        if (viewState !is ViewState.Loading) {
            swipeRefreshLayout?.isRefreshing = false
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