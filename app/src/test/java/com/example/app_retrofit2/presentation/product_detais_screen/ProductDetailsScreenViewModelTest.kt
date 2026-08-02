package com.example.app_retrofit2.presentation.product_detais_screen

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.UserPreferences
import com.example.app_retrofit2.domain.usecase.preferences.GetUserPreferencesUseCase
import com.example.app_retrofit2.domain.usecase.product_details_screen.GetProductByIdUseCase
import com.example.app_retrofit2.domain.usecase.product_details_screen.ObserveProductByIdUseCase
import com.example.app_retrofit2.domain.usecase.product_details_screen.UpdateProductUseCase
import com.example.app_retrofit2.domain.usecase.product_details_screen.ValidateProductUseCase
import com.example.app_retrofit2.presentation.common.events.ProductDetailsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import com.example.app_retrofit2.presentation.product_details_screen.ProductDetailsScreenViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ProductDetailsScreenViewModelTest {
    @Mock
    lateinit var getProductByIdUseCase: GetProductByIdUseCase
    @Mock
    lateinit var getUserPreferencesUseCase: GetUserPreferencesUseCase
    @Mock
    lateinit var updateProductUseCase: UpdateProductUseCase
    @Mock
    lateinit var validateProductUseCase: ValidateProductUseCase
    @Mock
    lateinit var observeProductByIdUseCase: ObserveProductByIdUseCase
    private lateinit var viewModel: ProductDetailsScreenViewModel
    private val product = Product(
        id = 1,
        title = "Phone",
        description = "Description",
        category = "electronics",
        price = 999f,
        discountPercentage = 10f,
        rating = 4.5f,
        stock = 20,
        brand = "Samsung",
        images = listOf("image.jpg")
    )

    @Before
    fun setup() {
        whenever(getUserPreferencesUseCase())
            .thenReturn(
                flowOf(UserPreferences())
            )
        viewModel = ProductDetailsScreenViewModel(
            getProductByIdUseCase,
            getUserPreferencesUseCase,
            updateProductUseCase,
            validateProductUseCase,
            observeProductByIdUseCase
        )
    }

    @Test
    fun startEditing_sets_true() {
        viewModel.onEvent(
            ProductDetailsScreenUiEvents.OnStartEditing
        )
        assertTrue(viewModel.isEditing.value)
    }

    @Test
    fun stopEditing_sets_false() {
        viewModel.onEvent(
            ProductDetailsScreenUiEvents.OnStartEditing
        )
        viewModel.onEvent(
            ProductDetailsScreenUiEvents.OnStopEditing
        )
        assertFalse(viewModel.isEditing.value)
    }

    @Test
    fun productChanged_updates_editableProduct() {
        viewModel.onEvent(
            ProductDetailsScreenUiEvents.OnProductChanged(product)
        )
        assertEquals(
            product,
            viewModel.editableProduct.value
        )
    }

    @Test
    fun saveChanges_calls_updateUseCase() = runTest {
        whenever(
            validateProductUseCase(product)
        ).thenReturn(Result.success(Unit))
        whenever(
            updateProductUseCase(product)
        ).thenReturn(Result.success(product))
        viewModel.onEvent(
            ProductDetailsScreenUiEvents.OnProductChanged(product)
        )
        viewModel.onEvent(
            ProductDetailsScreenUiEvents.OnSaveChanges
        )
        advanceUntilIdle()
        verify(updateProductUseCase).invoke(product)
    }

    @Test
    fun preferences_are_loaded() {
        assertEquals(
            UserPreferences(),
            viewModel.preferences.value
        )
    }
}