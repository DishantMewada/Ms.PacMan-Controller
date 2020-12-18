package pacman.controllers.examples;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;

import static pacman.game.Constants.*;
import pacman.controllers.Controller;

/* 
 * This controller utilizes 5 tactics which are as follows:
 * 1. Get away from any non-edible ghost if too close.
 * 2. Go after the nearest edible ghost.
 * 3. Get the distance from each ghost, if the distance from each ghost is more than max distance move away from ghost.
 * 4. Go after the pills and power pills, if the distance from the ghost is in the range.
 * 5: If Ms PacMan is stuck at one position then move Ms PacMan in another direction.
 * 
 * 
 * if distance of power pill is less compared to ghost then go to PP
 */


public class CustomControllerPacman extends Controller<MOVE>

{      
	private static final int MIN_DISTANCE=8; // minimum distance      
	private static final int MAX_DISTANCE=20; // maximum distance
	private final static int POWER_PILL_PROXIMITY=15; // pill proximity

	public MOVE getMove(Game game,long timeDue)

	{                   

		// Strategy 1: If any non-edible ghost is too close, run away from ghosts.		
		int current = game.getPacmanCurrentNodeIndex(); // current node index of Ms. PacMan

		// loop through each ghosts. 
		for(GHOST ghost : GHOST.values())

			// If non-edible ghost and ghost liar time is 0. 
			if(game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost)==0) {

				// If ghost is very close (less than MIN_DISTANCE), then move away from ghosts.
				if(game.getEuclideanDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)

					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.EUCLID);		
			}

		/*
		 *  We have found that Euclidean Distance between ghosts and Ms.PacMan gives better score compared to shortest path distance and Manhattan Distance.
		 */

		// Strategy 2: Find the nearest edible ghost and go after them.
		double minDistance=Integer.MAX_VALUE; // maximum possible value for minDistance variable.
		GHOST minGhost=null; // instantiating minGhost as null.            

		// Loop through each ghost.
		for(GHOST ghost : GHOST.values())

			// If the ghost edible time is greater than 0.
			if(game.getGhostEdibleTime(ghost)>0)

			{	
				// distance is the shortest path distance between current node index and ghost's index.
				double distance = game.getEuclideanDistance(current,game.getGhostCurrentNodeIndex(ghost));             

				// If distance is less than minDistance, we update the minDistance and minGhost.
				if(distance<minDistance)

				{
					minDistance=distance;
					minGhost=ghost;
				}
			}

		// If edible ghost is found, then move towards the ghost with minimum distance.
		if(minGhost!=null) {

			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(minGhost),DM.PATH);
		}
		// Strategy 3: Get the distance from each ghost
		ArrayList<Integer> Distance_From_Ghosts=new ArrayList<Integer>();

		for(GHOST ghost : GHOST.values()) {
			//System.out.println(game.getGhostCurrentNodeIndex(ghost));
			Distance_From_Ghosts.add(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost)));
			Distance_From_Ghosts.add(game.getManhattanDistance(current,game.getGhostCurrentNodeIndex(ghost)));
			Distance_From_Ghosts.add((int)game.getEuclideanDistance(current,game.getGhostCurrentNodeIndex(ghost)));
		}
		for(GHOST ghost : GHOST.values()) {

			for (int s=0; s<=11; s++)

				if(Distance_From_Ghosts.get(s)<MAX_DISTANCE && game.getGhostLairTime(ghost)!=0)
				{
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);

				}
		}
		//Strategy 4: go after the pills and power pills if the distance from the ghost is in the range
		ArrayList<Integer> targets=new ArrayList<Integer>();

		int[] pills=game.getActivePillsIndices();
		int[] powerPills=game.getActivePowerPillsIndices();

		for (int s=0; s<=11; s++)

			for(GHOST ghost : GHOST.values())

				if(Distance_From_Ghosts.get(s)>MIN_DISTANCE && game.getGhostLairTime(ghost)>0)

				{   	    
					for(int i=0;i<powerPills.length;i++) 			//check with power pills are available
						if(closeToPower(game, i)) {
							//							System.out.println(powerPills[i]);
							targets.add(powerPills[i]);
						}
						else 										//check which pills are available			
							if(!closeToPower(game, i) && game.isPillStillAvailable(i)) {
								targets.add(pills[i]);
							}

				}
				else 
					if (Collections.min(Distance_From_Ghosts) == game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost)) && game.getGhostLairTime(ghost)>0) {
						return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
					}

		int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array

		for(int i=0;i<targets.size();i++)
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
		else {
			return game.getPacmanLastMoveMade();
		}

		//return the next direction once the closest target has been identified
		return game.getNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current,targetsArray,DM.PATH),DM.PATH);

	}

	//This helper function checks if Ms Pac-Man is close to an available power-pill
	private boolean closeToPower(Game game,int i)
	{
		int[] powerPills=game.getActivePowerPillsIndices();

		if(game.isPowerPillStillAvailable(i) && game.getShortestPathDistance(powerPills[i],game.getPacmanCurrentNodeIndex())<POWER_PILL_PROXIMITY)
			return true;

		return false;
	}

}