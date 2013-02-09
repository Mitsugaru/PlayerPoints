package org.black_ixx.playerPoints.storage;

public interface IStorage {

   /**
    * Get the amount of points the given player has.
    * 
    * @param name
    *           - Name of player.
    * @return Points the player has.
    */
   int getPoints(String name);

   /**
    * Set the amount of points for the given player name.
    * 
    * @param name
    *           - Player name
    * @param points
    *           - Amount of points to set.
    * @return True if we were successful in editing, else false.
    */
   boolean setPoints(String name, int points);

   boolean playerInDatabase(String name);

}
