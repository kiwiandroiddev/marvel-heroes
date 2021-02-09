package nz.co.kiwiandroiddev.marvelheroes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.add
import androidx.fragment.app.commit
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.view.CharacterDetailsFragment
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListNavigator
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view.CharacterListFragment
import nz.co.kiwiandroiddev.marvelheroes.navigation.MainActivityProvider
import javax.inject.Inject

class MainActivity : AppCompatActivity(), CharacterListNavigator {

    @Inject
    lateinit var mainActivityProvider: MainActivityProvider

    private var fragmentContainerView: FragmentContainerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        injectDependencies()

        fragmentContainerView = findViewById(R.id.fragment_container_view)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CharacterListFragment>(R.id.fragment_container_view)
            }
        }
    }

    private fun injectDependencies() {
        (applicationContext as MarvelHeroesApplication).appComponent.inject(this)
    }

    override fun onStart() {
        super.onStart()
        mainActivityProvider.register(this)
    }

    override fun onStop() {
        super.onStop()
        mainActivityProvider.unregister(this)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun navigateToCharacter(characterId: CharacterId) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            replace(R.id.fragment_container_view, CharacterDetailsFragment.newInstance(characterId))
        }
    }
}