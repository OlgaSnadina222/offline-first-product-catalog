package com.example.app_retrofit2.domain.mapper

sealed class DomainErrorMapper {
    object Network: DomainErrorMapper()
    object Timeout: DomainErrorMapper()
    object Unauthorized: DomainErrorMapper()
    object Forbidden: DomainErrorMapper()
    object NotFound: DomainErrorMapper()
    object Server: DomainErrorMapper()
    object Unknown: DomainErrorMapper()
}
