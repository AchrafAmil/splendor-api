package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.CardCategory
import com.neogineer.splendor.api.data.ResourceLoader
import org.junit.Assert
import org.junit.Test

class InitialStateTest {

    @Test
    fun `loaded cards size should be 90 = 40 + 30 + 20`() {
        val cards = ResourceLoader().loadCards()
        val cardsByCategory = cards.groupBy { it.category }

        Assert.assertEquals(90, cards.size)
        Assert.assertEquals(40, cardsByCategory[CardCategory.FIRST]?.size)
        Assert.assertEquals(30, cardsByCategory[CardCategory.SECOND]?.size)
        Assert.assertEquals(20, cardsByCategory[CardCategory.THIRD]?.size)
    }

    @Test
    fun `loaded nobles size should be 10`() {
        val nobles = ResourceLoader().loadNobles()

        Assert.assertEquals(10, nobles.size)
    }
}