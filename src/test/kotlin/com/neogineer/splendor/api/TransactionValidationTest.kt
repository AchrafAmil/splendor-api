package com.github.achrafamil.splendor.api

import com.github.achrafamil.splendor.api.data.Transaction
import com.github.achrafamil.splendor.api.data.colorMap
import com.github.achrafamil.splendor.api.rules.isValid
import org.junit.Assert
import org.junit.Test

class TransactionValidationTest {

    @Test
    fun `TokensExchange#isValid() for valid transactions should return true`() {
        validTokensExchangeTransactions.forEach { transaction ->
            Assert.assertTrue("$transaction is valid but returned false", transaction.isValid())
        }
    }

    @Test
    fun `TokensExchange#isValid() for invalid transactions should return false`() {
        invalidTokensExchangeTransactions.forEach { transaction ->
            Assert.assertFalse("$transaction is invalid but returned true", transaction.isValid())
        }
    }

    companion object {
        internal val validTokensExchangeTransactions = listOf(
            Transaction.TokensExchange(colorMap(green = 1)),
            Transaction.TokensExchange(colorMap(white = 2)),
            Transaction.TokensExchange(colorMap(white = 1, green = 1, black = 1)),
            Transaction.TokensExchange(colorMap(white = 1, green = 1, black = 1, blue = -1, red = -1)),
            Transaction.TokensExchange(colorMap(white = 1, green = 1, black = 1, blue = -2)),
            Transaction.TokensExchange(colorMap(blue = 1, red = 1)),
            Transaction.TokensExchange(colorMap())
        )

        internal val invalidTokensExchangeTransactions = listOf(
            Transaction.TokensExchange(colorMap(green = 3)),
            Transaction.TokensExchange(colorMap(white = 2, green = 2, black = 2, blue = 2)),
            Transaction.TokensExchange(colorMap(white = 2, green = 2, black = 2, blue = 2, red = -8)),
            Transaction.TokensExchange(colorMap(white = 1, green = 1, black = 1, blue = 1, red = -1)),
            Transaction.TokensExchange(colorMap(blue = 2, red = 2))
        )
    }
}