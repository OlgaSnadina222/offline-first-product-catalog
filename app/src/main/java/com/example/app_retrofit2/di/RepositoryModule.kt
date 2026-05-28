package com.example.app_retrofit2.di

import com.example.app_retrofit2.data.remote.repository.CategoryRepoImpl
import com.example.app_retrofit2.data.remote.repository.ProductRepoImpl
import com.example.app_retrofit2.domain.repositoty.CategoryRepo
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindProductRepository (impl: ProductRepoImpl): ProductRepo

    @Binds
    abstract fun bindCategoryRepository (impl: CategoryRepoImpl): CategoryRepo
}