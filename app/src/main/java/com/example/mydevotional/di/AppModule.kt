package com.example.mydevotional.di

import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.repositorie.BibleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindBibleRepository(impl: BibleRepositoryImpl): BibleRepository
}