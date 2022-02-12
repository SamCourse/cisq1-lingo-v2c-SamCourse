Feature: Using the Lingo Trainer
  As a User,
  I want to be able to train my Lingo skills using the Lingo Trainer,
  In order to get better at Lingo.

  Scenario: Start new game
    When I start a new game
    Then I should be shown the first letter and the length of the Lingo word

  Scenario Outline: Start a new round
    Given I am playing a game
    And the round was won
    And the last word had "<previous length>" letters
    When I start a new round
    Then the word to guess has "<next length>" letters

    Examples:
      | previous length | next length |
      | 5               | 6           |
      | 6               | 7           |
      | 7               | 5           |

    # Exception paths
  Scenario: an eliminated player cannot start a new round
    Given I am playing a game
    And the round was lost
    Then I cannot start a new round

  Scenario: a round cannot be started if a player is still playing
    Given I am playing a game
    Then I cannot start a new round


  Scenario: Guessing a word
    Given The game has not ended yet
    Then I can guess a word

  Scenario Outline: Guessing a word
    Given I am guessing a word
    And the game has not ended yet
    And <guess> is not <word>
    Then process <feedback>

    Examples:
    | word      | hint  | guess   | feedback                                              |
    | GROEP     | G.... | GEGROET | INVALID, INVALID, INVALID, INVALID, INVALID (te lang) |
    | GROEP     | G.... | GERST   | CORRECT, PRESENT, PRESENT, ABSENT, ABSENT             |
    | GROEP     | G.... | GENEN   | CORRECT, ABSENT, ABSENT, CORRECT, ABSENT              |
    | GROEP     | G..E. | GEDOE   | CORRECT, PRESENT, ABSENT, PRESENT, ABSENT             |
    | GROEP     | G..E. | GROEP   | CORRECT, CORRECT, CORRECT, CORRECT, CORRECT           |