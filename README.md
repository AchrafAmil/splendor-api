# splendor-api

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.achrafamil/splendor-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.achrafamil/splendor-api)

Kotlin library providing an API to play _Splendor_, the board game.

Useful for experimentation with different strategies/tactics or as a core engine wrapped in a graphical app.

Basically, you create a new game by instantiating an new `Game` and registering each player's implementation of the interface `Player`.
Then you start the game by calling `game.start(gameCallback)`.

Provide a `GameCallback` to get notified while the game is going on (on start, on turn, on end, etc).

Please note that `Game::start` is a blocking method that will only return once the game is finished.

The library do respect the same conventional game rules, for more details please refer to Splendor's Rulebook.

## How-to
First add dependency to your `build.gradle`.
```
repositories {
    mavenCentral()
    ...
}
```

```
dependencies {
    ...
    implementation "com.github.achrafamil:splendor-api:1.0.1"
}
```

### Implement your players
Define your own player or use one of the ready-to-use implementations defined in `/players`. For convenience those are named `Joe`, `Ryan`, `Johanna`... and do all extend `BasicPlayer`. Refer to their documentation for more details on their strategies.

You can either build your player from scratch by implementing `Player` interface or you can just extend `BasicPlayer` and override desired behavior.

`Player` interface requires two methods :
```

class MyPlayer() : Player {
    // give it a unique name
    override val name: String = "My player"

    override fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction {
        TODO("decide what to do with your turn and return your decision = transaction")
    }

    override fun chooseNoble(
        affordableNobles: List<Noble>,
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Noble {
        TODO("you're eligible for picking one noble, return your choice from $affordableNobles")
    }
}
```

#### Each turn's decision
Your player will be asked each turn to decide for a `Transaction`.
`Transaction` is a sealed class with 3 possible implementations :
- `TokensExchange` : you decide to collect tokens during this turn.
Negative values are allowed as far as they represent giving up some tokens.

    Valid `Transaction.TokensExchange` examples:
```
TokensExchange(colorMap(white = 1, green = 1, black = 1) // as long as user has < 8 tokens
TokensExchange(colorMap(white = 1, green = 1) // as long as user has < 9 tokens
TokensExchange(colorMap(white = 2) // as long as user has < 9 tokens
TokensExchange(colorMap(white = 1, green = 1, black = 1, red = -2) // as long as user has < 10 tokens
```
   More examples can be found in unit tests.

- `CardBuying` : you decide to exchange some of your tokens for a card in the board. You should reference one of the available cards in `boardState`.
- `CardReservation` : you book a card and get a golden token.

All in all your transaction should respect the game rules, please refer to Splendor's Rulebook.
If your player returns a transaction violating rules (eg. `TokensExchange` with 4 tokens or `CardBuying` without enough tokens...) the `Game` will throw an `IllegalTransactionException`.

So make sure your transaction does respect the rules by calling :
```
boardState.playerCanSubmitTransaction(selfState, myTransaction)
```

## Ready-to-use AI players
Some ready to use implementations of `Player` interface are provided inside `.players` subpackage.

Following are their respective strategies in a nutshell :

- *Joe*: I buy the best card I can afford, otherwise I collect the _3 most-needed_ important tokens.
- *Eve*: Will always try to _prevent opponent_ from getting their most interesting cards.
- *Ryan*: Makes _random choices_ when it comes to choosing which tokens to collect.
- *Johanna*: Like Joe but Johanna interest in cards is also _influenced by nobles_ in the game.

When played together in one-vs-one games, players on the left column have following chances of winning :

|         | Joe | Eve | Ryan | Johanna |
|---------|-----|-----|------|---------|
| Joe     | 50% | 55% | 55%  | 36%     |
| Eve     | 45% | 50% | 58%  | 43%     |
| Ryan    | 45% | 42% | 50%  | 34%     |
| Johanna | 64% | 57% | 66%  | 50%     |

Yes, Johanna rocks and taking nobles in consideration does in some circumstances considerably improve chances of winning. Now scientifically proven :-)

## Legal stuff
#### Disclaimer
This is an open source project for and by independent board game players community.

Even though the rules implemented here are quite similar to those of the official game, this project is not in any way related to the official Splendor board game company and does not copy any of its code or intellectual property.

 #### Contributions
 Contributions are more than welcome. Feel free to open issues for bugs or feature-requests. Fork and pull-request at will.
 
 #### License
 Splendor-API is released under the MIT license. [See LICENSE](LICENSE) for details.