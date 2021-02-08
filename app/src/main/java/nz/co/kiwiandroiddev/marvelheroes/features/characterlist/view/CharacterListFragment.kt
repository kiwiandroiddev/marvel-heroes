package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import nz.co.kiwiandroiddev.marvelheroes.MarvelHeroesApplication
import nz.co.kiwiandroiddev.marvelheroes.R
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListPresenter
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListView
import javax.inject.Inject

class CharacterListFragment : Fragment(), CharacterListView {

    @Inject
    lateinit var presenter: CharacterListPresenter

    private val viewIntentSubject = PublishSubject.create<CharacterListView.ViewIntent>()

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
        textView = view.findViewById(R.id.textview_first)
        loadMoreButton = view.findViewById(R.id.button_first)
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

    override fun viewIntentStream(): Observable<CharacterListView.ViewIntent> {
        return viewIntentSubject
    }

    override fun render(viewState: CharacterListView.ViewState) {
        println("Got viewstate: $viewState")

        val vsText = when (viewState) {
            is CharacterListView.ViewState.Content -> {
                loadMoreButton?.setOnClickListener {
                    emitViewIntent(CharacterListView.ViewIntent.OnLoadNextPage(
                        currentCharacterCount = viewState.characters.size
                    ))
                }

                viewState.characters.map { it.name }.toString()
            }
            else -> viewState.toString()
        }

        textView?.setText(vsText)
    }

    private fun emitViewIntent(intent: CharacterListView.ViewIntent) {
        viewIntentSubject.onNext(intent)
    }
}