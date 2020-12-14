package pacman.controllers.examples;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.DM;

import static pacman.game.Constants.*;

/*
 * This controller utilizes 5 tactics, in order of importance:
 * 1. Get away from any non-edible ghost if too close
 * 2. Go after the nearest edible ghost
 * 3. Get the distance from each ghost
 * 4. go after the pills and power pills if the distance from the ghost is in the range
 * 5: If Ms. PacMan is stuck at one position then move Ms. PacMan in another direction
 * 
 */
public class CustomControllerPacman extends Controller<MOVE>
{      
	private static final int MIN_DISTANCE=8; // minimum distance      
	private static final int MAX_DISTANCE=20; // maximum distance
	private final static int PILL_PROXIMITY=15; // pill proximity

	public MOVE getMove(Game game,long timeDue)
	{                   
		//Strategy 1: if any non-edible ghost is too close, run away from ghosts
		
		int current = game.getPacmanCurrentNodeIndex(); // current node index of Ms. PacMan

		//loop through each ghosts 
		for(GHOST ghost : GHOST.values())
			// if non-edible ghost and ghost liar time is 0 
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
				// if ghost is very close i.e. less than MIN_DISTANCE
				if(game.getEuclideanDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
					// then move away from ghosts
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.EUCLID);			
		//				else if(game.getManhattanDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
		//					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.MANHATTAN);
		//				else if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
		//					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
/*
 *  We have found that Euclidean Distance between ghosts and Ms.PacMan gives better score 
 *  compared to shortest path distance and Manhattan Distance 
 */
		
		//Strategy 2: find the nearest edible ghost and go after them 

		int minDistance=Integer.MAX_VALUE;
		GHOST minGhost=null;             

		for(GHOST ghost : GHOST.values())

			if(game.getGhostEdibleTime(ghost)>15)
			{
				int distance=game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));             
				//				double distance= game.getEuclideanDistance(current,game.getGhostCurrentNodeIndex(ghost));             
				//				int distance=game.getManhattanDistance(current,game.getGhostCurrentNodeIndex(ghost));             

				if(distance<minDistance)
				{
					minDistance=distance;
					minGhost=ghost;
				}
			}

		if(minGhost!=null)  //we found an edible ghost
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(minGhost),DM.PATH);

		//Strategy 3: Get the distance from each ghost
		ArrayList<Integer> Distance_From_Ghosts=new ArrayList<Integer>();
		ArrayList<Integer> targets=new ArrayList<Integer>();

		int[] pills=game.getPillIndices();
		int[] powerPills=game.getPowerPillIndices();

		for(GHOST ghost : GHOST.values()) {
			Distance_From_Ghosts.add(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost)));
			Distance_From_Ghosts.add(game.getManhattanDistance(current,game.getGhostCurrentNodeIndex(ghost)));
			Distance_From_Ghosts.add((int)game.getEuclideanDistance(current,game.getGhostCurrentNodeIndex(ghost)));
		}

		//Strategy 4: go after the pills and power pills if the distance from the ghost is in the range
		for (int s=0; s<=3; s++) 

			if(Distance_From_Ghosts.get(s)>MIN_DISTANCE && Distance_From_Ghosts.get(s)<MAX_DISTANCE)
			{          
				for(int i=0;i<pills.length;i++)					//check which pills are available			
					if(closeToPill(game, i))
						targets.add(pills[i]);

				for(int i=0;i<powerPills.length;i++)			//check with power pills are available
					if(closeToPower(game, i))
						targets.add(powerPills[i]);				
			}

			else if (Collections.min(Distance_From_Ghosts) == game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(GHOST.BLINKY))) {
				return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.BLINKY),DM.PATH);
			}
			else if (Collections.min(Distance_From_Ghosts) == game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(GHOST.INKY))) {
				return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.INKY),DM.PATH);
			}
			else if (Collections.min(Distance_From_Ghosts) == game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(GHOST.PINKY))) {
				return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.PINKY),DM.PATH);
			}
			else {
				return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.SUE),DM.PATH);

			}

		int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array

		for(int i=0;i<targetsArray.length;i++)
			targetsArray[i]=targets.get(i);

		//Strategy 5: If Ms. PacMan is stuck at one position then move Ms. PacMan in another direction

		int[] currentPosition = new int[2];

		if (game.getPacmanLastMoveMade() == MOVE.NEUTRAL) {

			currentPosition[0] = current;
			if (targetsArray.length > 0) {
				Arrays.copyOf(targetsArray, targetsArray.length-1);
				return game.getNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current,targetsArray,DM.PATH),DM.PATH);
			}
		}
		else if (game.getPacmanLastMoveMade() == MOVE.LEFT)
			return MOVE.RIGHT;
		else if (game.getPacmanLastMoveMade() == MOVE.RIGHT)
			return MOVE.LEFT;
		//		else if (game.getPacmanLastMoveMade() == MOVE.UP)
		//			return MOVE.DOWN;
		//		else if (game.getPacmanLastMoveMade() == MOVE.DOWN)
		//			return MOVE.UP;
		else {
			return game.getPacmanLastMoveMade();
		}
		//return the next direction once the closest target has been identified
		return game.getNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current,targetsArray,DM.PATH),DM.PATH);

	}

	//This helper function checks if Ms Pac-Man is close to an available power-pill
	private boolean closeToPower(Game game,int i)
	{
		int[] powerPills=game.getPowerPillIndices();

		if(game.isPowerPillStillAvailable(i) && game.getShortestPathDistance(powerPills[i],game.getPacmanCurrentNodeIndex())>PILL_PROXIMITY)
			return true;

		return false;
	}
	//This helper function checks if Ms Pac-Man is close to an available pill
	private boolean closeToPill(Game game,int i)
	{
		int[] Pills=game.getPillIndices();

		if(game.isPillStillAvailable(i) && game.getShortestPathDistance(Pills[i],game.getPacmanCurrentNodeIndex())>PILL_PROXIMITY)
			return true;

		return false;
	}
}