package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import nz.co.kiwiandroiddev.marvelheroes.MarvelHeroesApplication
import nz.co.kiwiandroiddev.marvelheroes.R
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsPresenter
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.presentation.CharacterDetailsView
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

    private val viewIntentSubject = PublishSubject.create<CharacterDetailsView.ViewIntent>()
    private var viewStateDisposable: Disposable? = null

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

    override fun onResume() {
        super.onResume()
        viewStateDisposable = presenter.attachView(this)
        signalIntent(CharacterDetailsView.ViewIntent.OnViewReady(getCharacterId()))
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

    override fun viewIntentStream(): Observable<CharacterDetailsView.ViewIntent> = viewIntentSubject
        .doOnNext { intent ->
            println("ZZZ emitting intent: $intent")
        }

    private fun signalIntent(intent: CharacterDetailsView.ViewIntent) {
        viewIntentSubject.onNext(intent)
    }

    override fun render(viewState: CharacterDetailsView.ViewState) {
        println("ZZZ render viewstate: $viewState")
        // todo
    }
}