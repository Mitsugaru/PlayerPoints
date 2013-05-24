package org.black_ixx.playerpoints.storage;

/**
 * Represents a storage model object.
 * 
 * @author Mitsugaru
 */
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

   /**
    * Check whether the player already exists in the storage medium.
    * 
    * @param name
    *           - Player name.
    * @return True if player is in storage, else false.
    */
   boolean playerEntryExists(String name);

}
