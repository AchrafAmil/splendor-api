package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.Transaction
import com.neogineer.splendor.api.data.mapToColorMap
import org.junit.Assert
import org.junit.Test

class RulesTest {

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
            Transaction.TokensExchange(mapToColorMap(green = 1)),
            Transaction.TokensExchange(mapToColorMap(white = 2)),
            Transaction.TokensExchange(mapToColorMap(white = 1, green = 1, black = 1)),
            Transaction.TokensExchange(mapToColorMap(white = 1, green = 1, black = 1, blue = -1, red = -1)),
            Transaction.TokensExchange(mapToColorMap(white = 1, green = 1, black = 1, blue = -2)),
            Transaction.TokensExchange(mapToColorMap(blue = 1, red = 1)),
            Transaction.TokensExchange(mapToColorMap())
        )

        internal val invalidTokensExchangeTransactions = listOf(
            Transaction.TokensExchange(mapToColorMap(green = 3)),
            Transaction.TokensExchange(mapToColorMap(white = 2, green = 2, black = 2, blue = 2)),
            Transaction.TokensExchange(mapToColorMap(white = 2, green = 2, black = 2, blue = 2, red = -8)),
            Transaction.TokensExchange(mapToColorMap(white = 1, green = 1, black = 1, blue = 1, red = -1)),
            Transaction.TokensExchange(mapToColorMap(blue = 2, red = 2))
        )
    }
}