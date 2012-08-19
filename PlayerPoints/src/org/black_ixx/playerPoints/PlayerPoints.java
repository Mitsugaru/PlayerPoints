package org.black_ixx.playerPoints;

import org.black_ixx.playerPoints.config.LocalizeConfig;
import org.black_ixx.playerPoints.config.RootConfig;
import org.black_ixx.playerPoints.listeners.VotifierListener;
import org.black_ixx.playerPoints.storage.StorageHandler;
import org.black_ixx.playerPoints.update.Update;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerPoints extends JavaPlugin{
   public static final String TAG = "[PlayerPoints]";
   private RootConfig rootConfig;
   private StorageHandler storage;

   @Override
   public void onEnable(){
      // Initialize localization
      LocalizeConfig.init(this);
      // Initialize config
      rootConfig = new RootConfig(this);
      // Intialize storage handler
      storage = new StorageHandler(this);
      // Initialize API
      PlayerPointsAPI.init(this);
      // Initialize updater
      Update.init(this);
      Update.checkUpdate();
      // Register commands
      final Commander commander = new Commander();
      getCommand("points").setExecutor(commander);
      getCommand("p").setExecutor(commander);
      // Register votifier listener, if applicable
      if(rootConfig.voteEnabled){
         final PluginManager pm = getServer().getPluginManager();
         final Plugin votifier = pm.getPlugin("Votifier");
         if(votifier != null){
            pm.registerEvents(new VotifierListener(this), this);
         }else{
            getLogger().warning("Could not hook into Votifier!");
         }
      }
   }

   public RootConfig getRootConfig(){
      return rootConfig;
   }

   public StorageHandler getStorageHandler(){
      return storage;
   }
}