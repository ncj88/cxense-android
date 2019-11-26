package com.cxense.cxensesdk

/**
 * Base class for SDK exceptions
 *
 */
open class BaseException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class ConsentRequiredException : BaseException {
    constructor() : super("Required user consent wasn't provided.")
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

/**
 * Exception that is thrown for HTTP 400 Bad Request responses
 *
 */
class BadRequestException : BaseException {
    constructor() : super("Request failed! Please make sure that all the request parameters are valid.")
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

/**
 * Exception that is thrown for HTTP 403 Forbidden responses
 *
 */
class ForbiddenException : BaseException {
    constructor() : super("Request failed! Please make sure that all the request parameters are valid and uses authorized values.")
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

/**
 * Exception that is thrown for HTTP 401 Not Authorized responses
 *
 */
class NotAuthorizedException : BaseException {
    constructor() : super("Request failed! Please make sure that all the request parameters are valid and uses authorized values.")
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}
