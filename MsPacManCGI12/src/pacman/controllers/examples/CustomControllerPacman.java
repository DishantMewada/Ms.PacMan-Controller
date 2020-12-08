package pacman.controllers.examples;

import java.util.ArrayList;

//import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
//import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

import static pacman.game.Constants.*;
//import static pacman.game.Constants.*;


public class CustomControllerPacman extends Controller<MOVE>
{	
	private static final int MIN_DISTANCE=20;	//if a ghost is this close, run away
	//private static final int Min_Distance_Ghost = 1111111;  //change this later accordingly
	//private static final int Min_Distance_PP;
	
	
	
	public MOVE getMove(Game game,long timeDue)
		{		
			
			int current=game.getPacmanCurrentNodeIndex();
			//int currentNodeIndex=game.getPacmanCurrentNodeIndex();
			
			//get all active pills
			int[] activePills=game.getActivePillsIndices();
			
			//get all active power pills
			int[] activePowerPills=game.getActivePowerPillsIndices();
			
			//create a target array that includes all ACTIVE pills and power pills
			int[] targetNodeIndices=new int[activePills.length+activePowerPills.length];
			
			for(int i=0;i<activePills.length;i++)
				targetNodeIndices[i]=activePills[i];
			
			for(int i=0;i<activePowerPills.length;i++)
				targetNodeIndices[activePills.length+i]=activePowerPills[i];		
			
			//return the next direction once the closest target has been identified
			//return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getClosestNodeIndexFromNodeIndex(currentNodeIndex,targetNodeIndices,DM.PATH),DM.PATH);	
		
			
			
			//Strategy 1: if any non-edible ghost is too close (less than MIN_DISTANCE), run away
			for(GHOST ghost : GHOST.values())
				if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
					if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
						return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
			
			//Strategy 2: find the nearest edible ghost and go after them 
			int minDistance=Integer.MAX_VALUE;
			GHOST minGhost=null;		
			
			for(GHOST ghost : GHOST.values())
				if(game.getGhostEdibleTime(ghost)>0)
				{
					int distance=game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));
					
					if(distance<minDistance)
					{
						minDistance=distance;
						minGhost=ghost;
					}
				}
			
			if(minGhost!=null)	//we found an edible ghost
				return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(minGhost),DM.PATH);
			
			minDistance=Integer.MAX_VALUE;
			//GHOST minGhost=null;		
			
			for(GHOST ghost : GHOST.values())
				if(game.getGhostEdibleTime(ghost)>0)
				{
					int distance=game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));
					
					if(distance<minDistance)
					{
						minDistance=distance;
						minGhost=ghost;
					}
				}
			
			if(minGhost!=null)	//we found an edible ghost
				return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(minGhost),DM.PATH);
			int[] pills=game.getPillIndices();
			int[] powerPills=game.getPowerPillIndices();		
			
			ArrayList<Integer> targets=new ArrayList<Integer>();
			
			for(int i=0;i<pills.length;i++)					//check which pills are available			
				if(game.isPillStillAvailable(i))
					targets.add(pills[i]);
			
			for(int i=0;i<powerPills.length;i++)			//check with power pills are available
				if(game.isPowerPillStillAvailable(i))
					targets.add(powerPills[i]);				
			
			int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array
			
			for(int i=0;i<targetsArray.length;i++)
				targetsArray[i]=targets.get(i);
			
			//return the next direction once the closest target has been identified
			return game.getNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current,targetsArray,DM.PATH),DM.PATH);
			
		
		
		}
	
	
	
	
	
	
}