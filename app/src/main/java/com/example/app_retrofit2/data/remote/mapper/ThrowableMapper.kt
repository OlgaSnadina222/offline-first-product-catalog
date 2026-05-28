package com.example.app_retrofit2.data.remote.mapper

import com.example.app_retrofit2.domain.mapper.DomainErrorMapper
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.toDomainError(): DomainErrorMapper {
    return when (this) {
        is SocketTimeoutException -> DomainErrorMapper.Timeout
        is IOException -> DomainErrorMapper.Network
        is HttpException -> when (code()) {
            401 -> DomainErrorMapper.Unauthorized
            403 -> DomainErrorMapper.Forbidden
            404 -> DomainErrorMapper.NotFound
            500 -> DomainErrorMapper.Server
            else -> DomainErrorMapper.Unknown
        }
        else -> DomainErrorMapper.Unknown
    }
}