

package pacman.controllers.examples;

 

import java.util.Collections;

import java.util.ArrayList;

import pacman.controllers.Controller;

import pacman.game.Game;

 

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

       private static final int MIN_DISTANCE=8;      //if a ghost is this close, run away

       private static final int FC_DISTANCE=8;

 

       public MOVE getMove(Game game,long timeDue)

       {                  

             int current=game.getPacmanCurrentNodeIndex();

 

             //Strategy 1: if any non-edible ghost is too close (less than MIN_DISTANCE), run away

             for(GHOST ghost : GHOST.values())

                    if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)

                    if(game.getEuclideanDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE || game.getManhattanDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE || game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)

                                 return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.EUCLID);

 

 

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

 

             if(minGhost!=null)  //we found an edible ghost

                    return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(minGhost),DM.PATH);

 

             //Strategy 3: go after the pills and power pills

 

             int[] pills=game.getPillIndices();

             int[] powerPills=game.getPowerPillIndices();        

 

             ArrayList<Integer> targets=new ArrayList<Integer>();

             ArrayList<Integer> Distances_From_Ghosts=new ArrayList<Integer>();

            

             int[] targetsArray=new int[targets.size()];          //convert from ArrayList to array

            

            

             try {

 

                    for(GHOST ghost : GHOST.values()) {

                    Distances_From_Ghosts.add((int)game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost)));

                    }

                          

 

                    for(int i=0;i<powerPills.length;i++)                 //check with power pills are available

                           if(game.isPowerPillStillAvailable(i))

                                 targets.add(powerPills[i]);

 

                    for(int i=0;i<targetsArray.length;i++)

                           targetsArray[i]=targets.get(i);

 

 

                    if(Distances_From_Ghosts.get(0)>FC_DISTANCE && Distances_From_Ghosts.get(1)>FC_DISTANCE && Distances_From_Ghosts.get(2)>FC_DISTANCE && Distances_From_Ghosts.get(3)>FC_DISTANCE) {         

                           for(int i=0;i<pills.length;i++)                            //check which pills are available                   

                                 if(game.isPillStillAvailable(i))

                                        targets.add(pills[i]);

                    }

 

                    else

                           if (Collections.min(Distances_From_Ghosts) == (int)game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(GHOST.BLINKY))) {

                                 return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.BLINKY),DM.PATH);

                           }

                           else if (Collections.min(Distances_From_Ghosts) == (int)game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(GHOST.INKY))) {

                                 return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.INKY),DM.PATH);

                           }

                           else if (Collections.min(Distances_From_Ghosts) == (int)game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(GHOST.PINKY))) {

                                 return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.PINKY),DM.PATH);

                           }

                           else {

                                 return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.SUE),DM.PATH);

 

                           }

 

                    for(int i=0;i<targetsArray.length;i++)

                           targetsArray[i]=targets.get(i);

 

                          

                    //return the next direction once the closest target has been identified

 

                    return game.getNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current,targetsArray,DM.PATH),DM.PATH);

             }catch(ArrayIndexOutOfBoundsException a) {

                    targetsArray=new int[targets.size()];               

                   

                    for(int i=0;i<targetsArray.length;i++)

                           targetsArray[i]=targets.get(i);

                   

                    return game.getNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current,targetsArray,DM.PATH),DM.PATH);

             }

 

 

       }

}

 