package pacman.controllers.examples;

import java.util.Collections;
import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.DM;

import static pacman.game.Constants.*;

/*
 * Pac-Man controller as part of the starter package - simply upload this file as a zip called
 * MyPacMan.zip and you will be entered into the rankings - as simple as that! Feel free to modify 
 * it or to start from scratch, using the classes supplied with the original software. Best of luck!
 *   
 * This controller utilises 3 tactics, in order of importance:
 * 1. Get away from any non-edible ghost that is in close proximity
 * 2. Go after the nearest edible ghost
 * 3. Go to the nearest pill/power pill
 */
public class CustomControllerPacman extends Controller<MOVE>
{      
	private static final int MIN_DISTANCE=8;      
	private static final int MAX_DISTANCE=20;
	private final static int PILL_PROXIMITY=15;

	public MOVE getMove(Game game,long timeDue)
	{                   
		int current=game.getPacmanCurrentNodeIndex();

		//Strategy 1: if any non-edible ghost is too close (less than MIN_DISTANCE), run away

		for(GHOST ghost : GHOST.values())
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
				if(game.getEuclideanDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.EUCLID);
		//				else if(game.getManhattanDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
		//					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.MANHATTAN);
		//				else if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
		//					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);

		//Strategy 2: find the nearest edible ghost and go after them 

		int minDistance=Integer.MAX_VALUE;
		GHOST minGhost=null;             

		for(GHOST ghost : GHOST.values())

			if(game.getGhostEdibleTime(ghost)>0)
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
			//Distance_From_Ghosts.add(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost)));
			//Distance_From_Ghosts.add(game.getManhattanDistance(current,game.getGhostCurrentNodeIndex(ghost)));
			Distance_From_Ghosts.add((int)game.getEuclideanDistance(current,game.getGhostCurrentNodeIndex(ghost)));
		}

		//Strategy 4: go after the pills and power pills
		for(GHOST ghost : GHOST.values()) {


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

				else if (Collections.min(Distance_From_Ghosts) > game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))) {
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
				}
		}
		int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array

		for(int i=0;i<targetsArray.length;i++)
			targetsArray[i]=targets.get(i);

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