This is a Snakes and Ladders game implemented in Java.
The game can be executed by running the Jar file (release version 1.0).

Snakes and Ladders Rules:
                
                1.  Each player starts at the bottom left corner (tile 1).
                2.  Players take turns rolling the dice and moving forward by the sum of the numbers rolled.
                3.  The first player to reach the last cell wins the game, but it must be reached with an exact number of steps.
                     If a player rolls a number that would take them beyond the last cell, they get to the last cell and then
                     retreat by the number of steps in excess.
                4.  If a player lands on the bottom of a ladder (GREEN tiles), they move up to the top of the ladder ⏫.
                5.  If a player lands on the head of a snake (RED tiles), they slide down to the tail of the snake ⏬.
                6.  If a player lands on a BLUE tile, they must ROLL AGAIN the dice ↺⚀⚅.
                7.  If a player lands on a CYAN tile, they must MOVE AGAIN ⏩ by the same number of steps they already moved.
                8.  If a player lands on a MAGENTA tile, they must STOP ❌.
                     A stopped player will have to wait ⏳ for a turn before being able to move again.
                     A player can avoid getting stopped if they have a DENY STOP ✋ card.
                9.  If a player lands on a YELLOW tile, they must DRAW A CARD ♠♣♥♦.
                     There are 4 types of cards: ROLL AGAIN ↺⚀⚅, MOVE AGAIN ⏩, STOP ❌ and DENY STOP ✋.
                     The first 3 types of card have the same effect of the tile with the same name.
                     The DENY STOP card is a special card that the player can hold in their hands until needed.
                     When that player gets stopped, they will consume their DENY STOP ✋ card and avoid getting stopped.
                10. If a player rolled a DOUBLE SIX, at the end of their turn they will roll the dice a second time and move again.
