package com.smartkdfarm.app.core.domain.exception

open class DairyDomainException(message: String) : Exception(message)

class ValidationException(message: String) : DairyDomainException(message)

class AuthorizationException(message: String) : DairyDomainException(message)

class AuthenticationException(message: String) : DairyDomainException(message)

class NotFoundException(message: String) : DairyDomainException(message)
