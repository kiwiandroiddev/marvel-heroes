package nz.co.kiwiandroiddev.marvelheroes.di

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import nz.co.kiwiandroiddev.marvelheroes.BuildConfig
import nz.co.kiwiandroiddev.marvelheroes.common.data.http.AuthorizationInterceptor
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.NetworkScheduler
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.PrivateApiKey
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.PublicApiKey
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.RenderingScheduler
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.data.MarvelCharacterDetailsApiClient
import nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.domain.usecase.GetCharacterDetails
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.data.MarvelCharactersApi
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.data.MarvelCharactersApiClient
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.domain.usecase.GetCharacterSummaries
import nz.co.kiwiandroiddev.marvelheroes.features.characterlist.presentation.CharacterListNavigator
import nz.co.kiwiandroiddev.marvelheroes.navigation.NavigationDispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class ApplicationModule {

    val baseUrl = "https://gateway.marvel.com/v1/public/"      // todo move to config

    @Provides
    @PublicApiKey
    fun provideDeveloperPublicApiKey() =
        "bb205b7c46c1f14b9a595a21ba502670"       // todo get from gradle properties

    @Provides
    @PrivateApiKey
    fun provideDeveloperPrivateApiKey() =
        "d7f9fd66fa0bdbec1e651772612a2068e626796e"       // todo get from gradle properties

    @Provides
    @Singleton
    fun provideCharacterListNavigator(navigationDispatcher: NavigationDispatcher): CharacterListNavigator =
        navigationDispatcher

    @Provides
    @Singleton
    fun provideGetCharacterSummaries(apiClient: MarvelCharactersApiClient): GetCharacterSummaries =
        apiClient

    @Provides
    @Singleton
    fun provideGetCharacterDetails(apiClient: MarvelCharacterDetailsApiClient): GetCharacterDetails =
        apiClient

    @Provides
    @Singleton
    fun provideMarvelCharactersApi(retrofit: Retrofit): MarvelCharactersApi =
        retrofit.create(MarvelCharactersApi::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(authorizationInterceptor: AuthorizationInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                addInterceptor(authorizationInterceptor)
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }

                    addInterceptor(loggingInterceptor)
                }
            }
            .build()
    }

    @Provides
    @Singleton
    @RenderingScheduler
    fun provideRenderingScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Singleton
    @NetworkScheduler
    fun provideNetworkScheduler(): Scheduler = Schedulers.io()
}
