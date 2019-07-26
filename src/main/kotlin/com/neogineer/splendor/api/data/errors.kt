package com.neogineer.splendor.api.data

import java.lang.Exception

data class NameAlreadyTakenException(override val message: String) : Exception(message)