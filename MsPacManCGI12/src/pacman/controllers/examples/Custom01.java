package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class Custom01 extends Controller<MOVE>{
	public MOVE getMove(Game game,long timeDue) {
		int current = game.getPacmanCurrentNodeIndex(); // current node index of Ms. PacMan

		System.out.println(current);
		System.out.println(game.getPacmanLastMoveMade());
		return MOVE.RIGHT;
	}
}
