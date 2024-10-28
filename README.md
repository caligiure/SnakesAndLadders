This is a Snakes and Ladders game implemented in Java.
- To start the game, run the ScaleESerpenti.jar file (you can find it in the latest release).
- In the first menu of the game you have two options:
  - Start a new configuration of the game rules (recommended choice)
  - Load an old configuration from the file system (select this option only if you have already used this software)
- In the subsequent menus you'll be able to configure all the rules and settings of the game.
It's important to choose if you want to play in "Manual Mode", in which you'll have to press the button to roll the dice at every turn, or if you want to
play in "Automatic Mode", in which case you'll just press the start button and the system will automatically roll the dice and advance the game every few seconds.
If you check the "Automatically roll the dice and advance" setting the game will be set in Automatic Mode.
- At the end of the configuration you can save the configuration in a file.
- Use the "Start" button to start the game. The first thing you'll have to do is to insert a name for each one of the player,
but you can also choose to leave the default name.
- After choosing the names, the game board will appear on the left side of the screen, while on the right side you will see the
player's table, the legend of the colours and the game log with all the infos on what is happening in the game.
At the bottom of the screen you will find the "Rules" button which you can use to read the game rules and the "Roll Dice"/"Start" button,
depending on the game mode you have selected during the configuration.

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
    8.  If a player lands on a MAGENTA tile (BENCH), they must STOP ❌ for 1 turn.
        If a player lands on an ORANGE tile (TAVERN), they must STOP ❌ for 3 turns.
        A stopped player will have to wait ⏳ before being able to move again.
        A player can avoid getting stopped if they have a DENY STOP ✋ card.
    9.  If a player lands on a YELLOW tile, they must DRAW A CARD ♠♣♥♦.
        There are 4 types of cards: ROLL AGAIN ↺⚀⚅, MOVE AGAIN ⏩, STOP ❌ and DENY STOP ✋.
        The first 3 types of card have the same effect of the tile with the same name.
        The DENY STOP card is a special card that the player can hold in their hands until needed.
        When that player gets stopped, they will consume their DENY STOP ✋ card and avoid getting stopped.
    10. If a player rolled a DOUBLE SIX, at the end of their turn they will roll the dice a second time and move again.
