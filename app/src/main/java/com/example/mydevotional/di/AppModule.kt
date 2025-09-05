package com.example.mydevotional.di

import android.content.Context
import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.repositorie.BibleRepositoryImpl
import com.example.mydevotional.repositorie.CompletedReadingsRepository
import com.example.mydevotional.repositorie.CompletedReadingsRepositoryImpl
import com.example.mydevotional.repositorie.FavoriteVersesRepository
import com.example.mydevotional.repositorie.TranslationPreferenceRepository
import com.example.mydevotional.usecase.FavoriteVerseUseCase
import com.example.mydevotional.usecase.GetSelectedTranslationUseCase
import com.example.mydevotional.usecase.SetSelectedTranslationUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
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
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideBibleRepository(
        firestore: FirebaseFirestore,
        httpClient: HttpClient,
        gson: Gson,
        getSelectedTranslationUseCase: GetSelectedTranslationUseCase
    ): BibleRepository {
        return BibleRepositoryImpl(firestore, httpClient, gson, getSelectedTranslationUseCase)
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
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideFavoriteVersesRepository(
        @ApplicationContext context: Context,
        gson: Gson
    ): FavoriteVersesRepository {
        return FavoriteVersesRepository(context, gson)
    }

    @Provides
    @Singleton
    fun provideTranslationPreferenceRepository(@ApplicationContext context: Context): TranslationPreferenceRepository {
        return TranslationPreferenceRepository(context)
    }

    @Provides
    fun provideToggleFavoriteVerseUseCase(repository: FavoriteVersesRepository): ToggleFavoriteVerseUseCase {
        return ToggleFavoriteVerseUseCase(repository)
    }

    @Provides
    fun provideGetFavoriteVersesUseCase(repository: FavoriteVersesRepository): FavoriteVerseUseCase {
        return FavoriteVerseUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCompletedReadingsRepository(
        @ApplicationContext context: Context,
    ): CompletedReadingsRepository {
        return CompletedReadingsRepositoryImpl(context)
    }

    @Provides
    fun provideGetSelectedTranslationUseCase(repository: TranslationPreferenceRepository): GetSelectedTranslationUseCase {
        return GetSelectedTranslationUseCase(repository)
    }

    @Provides
    fun provideSetSelectedTranslationUseCase(repository: TranslationPreferenceRepository): SetSelectedTranslationUseCase {
        return SetSelectedTranslationUseCase(repository)
    }

}

