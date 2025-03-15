package com.example.mydevotional.di

import android.content.Context
import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.repositorie.BibleRepositoryImpl
import com.example.mydevotional.repositorie.FavoriteVersesRepository
import com.example.mydevotional.usecase.GetFavoriteVersesUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBibleRepository(
        firestore: FirebaseFirestore,
        httpClient: HttpClient
    ): BibleRepository {
        return BibleRepositoryImpl(firestore, httpClient)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                gson()
            }
        }
    }

    @Provides
    @Singleton
    fun provideFavoriteVersesRepository(@ApplicationContext context: Context): FavoriteVersesRepository {
        return FavoriteVersesRepository(context)
    }

    @Provides
    fun provideToggleFavoriteVerseUseCase(repository: FavoriteVersesRepository): ToggleFavoriteVerseUseCase {
        return ToggleFavoriteVerseUseCase(repository)
    }

    @Provides
    fun provideGetFavoriteVersesUseCase(repository: FavoriteVersesRepository): GetFavoriteVersesUseCase {
        return GetFavoriteVersesUseCase(repository)
    }
}