package com.example.app_retrofit2.presentation.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.app_retrofit2.R
import com.example.app_retrofit2.domain.mapper.DomainErrorMapper
import com.example.app_retrofit2.presentation.common.states.UiState

@Composable
fun DomainErrorMapper.toUiError(): UiState.Error {
    return when (this) {
        DomainErrorMapper.Network -> UiState.Error.Network()
        DomainErrorMapper.Timeout -> UiState.Error.Timeout()
        DomainErrorMapper.Unauthorized -> UiState.Error.Http(401, stringResource(R.string.unauthorized_access))
        DomainErrorMapper.Forbidden -> UiState.Error.Http(403, stringResource(R.string.access_forbidden))
        DomainErrorMapper.NotFound -> UiState.Error.Http(404, stringResource(R.string.product_not_found))
        DomainErrorMapper.Server -> UiState.Error.Http(500, stringResource(R.string.server_error_try_later))
        DomainErrorMapper.Unknown -> UiState.Error.Unknown()
    }
}