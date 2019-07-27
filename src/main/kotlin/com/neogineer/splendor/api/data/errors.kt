package com.neogineer.splendor.api.data

open class SplendorException(message: String) : Exception(message)

data class NameAlreadyTakenException(override val message: String) : SplendorException(message)

data class IllegalTransactionException(override val message: String) : SplendorException(message)
