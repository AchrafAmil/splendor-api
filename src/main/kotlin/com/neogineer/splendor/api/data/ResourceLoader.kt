package com.neogineer.splendor.api.data

class ResourceLoader {

    fun loadCards(): Set<Card> {
        val text = javaClass.getResource("/cards/cards.txt").readText()
        val cardsTextLines = text
            .split("\n")
            .filter { it.contains(',') && !it.contains('#') }

        return cardsTextLines
            .map { cardTextLine ->
                val cardParts = cardTextLine.split(',').map { it.trim() }
                Card(
                    id = cardParts[0].toInt(),
                    cost = mapToColorMap(
                        white = cardParts[1].toInt(),
                        blue = cardParts[2].toInt(),
                        green = cardParts[3].toInt(),
                        red = cardParts[4].toInt(),
                        black = cardParts[5].toInt()
                    ),
                    category = mapToCardCategory(cardParts[6].toInt()),
                    color = mapToColor(cardParts[7]),
                    points = cardParts[8].toInt()
                )
            }
            .toSet()
    }

    fun loadNobles(): Set<Noble> {
        val text = javaClass.getResource("/cards/nobles.txt").readText()
        val noblesTextLines = text
            .split("\n")
            .filter { it.contains(',') && !it.contains('#') }

        return noblesTextLines
            .map { nobleTextLine ->
                val nobleParts = nobleTextLine.split(',').map { it.trim() }
                Noble(
                    id = nobleParts[0].toInt(),
                    cost = mapToColorMap(
                        white = nobleParts[1].toInt(),
                        blue = nobleParts[2].toInt(),
                        green = nobleParts[3].toInt(),
                        red = nobleParts[4].toInt(),
                        black = nobleParts[5].toInt()
                    ),
                    points = nobleParts[6].toInt()
                )
            }
            .toSet()
    }
}