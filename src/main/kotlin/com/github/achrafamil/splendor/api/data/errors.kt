package com.github.achrafamil.splendor.api.data

open class SplendorException(message: String) : Exception(message)

data class NameAlreadyTakenException(override val message: String) : SplendorException(message)

data class IllegalTransactionException(override val message: String) : SplendorException(message)

data class TooManyTurnsException(override val message: String) : SplendorException(message)
