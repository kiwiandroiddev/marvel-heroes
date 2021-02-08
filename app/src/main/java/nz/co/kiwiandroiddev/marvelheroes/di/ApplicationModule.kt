package nz.co.kiwiandroiddev.marvelheroes.di

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.RenderingScheduler
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterId
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.model.CharacterSummary
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase.GetCharacterSummaries
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideGetCharacterSummaries(): GetCharacterSummaries =
        object : GetCharacterSummaries {
            override fun getCharacters(offset: Int, limit: Int): Single<List<CharacterSummary>> {
                return Single.just(
                    listOf(
                        CharacterSummary(
                            id = CharacterId(1),
                            name = "Wolverine"
                        ),
                        CharacterSummary(
                            id = CharacterId(2),
                            name = "Charles Xavier"
                        ),
                    )
                )
            }
        }

    @Provides
    @Singleton
    @RenderingScheduler
    fun provideRenderingScheduler(): Scheduler = AndroidSchedulers.mainThread()

}
