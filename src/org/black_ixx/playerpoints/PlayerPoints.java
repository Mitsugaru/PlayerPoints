package org.black_ixx.playerpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.black_ixx.playerpoints.commands.Commander;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.listeners.VotifierListener;
import org.black_ixx.playerpoints.services.IModule;
import org.black_ixx.playerpoints.storage.StorageHandler;
import org.black_ixx.playerpoints.storage.imports.Importer;
import org.black_ixx.playerpoints.update.UpdateManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for PlayerPoints.
 */
public class PlayerPoints extends JavaPlugin {

    /**
     * Plugin tag.
     */
    public static final String TAG = "[PlayerPoints]";

    /**
     * Root config reference.
     */
    private RootConfig rootConfig;

    /**
     * Storage handler.
     */
    private StorageHandler storage;

    /**
     * API instance.
     */
    private PlayerPointsAPI api;

    /**
     * Modules.
     */
    private final Map<Class<? extends IModule>, IModule> modules = new HashMap<Class<? extends IModule>, IModule>();

    @Override
    public void onEnable() {
        // Initialize localization
        LocalizeConfig.init(this);
        // Initialize config
        rootConfig = new RootConfig(this);
        // Do imports
        Importer importer = new Importer(this);
        importer.checkImport();
        // Intialize storage handler
        storage = new StorageHandler(this);
        // Initialize API
        api = new PlayerPointsAPI(this);
        // Initialize updater
        UpdateManager update = new UpdateManager(this);
        update.checkUpdate();
        // Register commands
        final Commander commander = new Commander(this);
        getCommand("points").setExecutor(commander);
        getCommand("p").setExecutor(commander);
        // Register votifier listener, if applicable
        if(rootConfig.voteEnabled) {
            final PluginManager pm = getServer().getPluginManager();
            final Plugin votifier = pm.getPlugin("Votifier");
            if(votifier != null) {
                pm.registerEvents(new VotifierListener(this), this);
            } else {
                getLogger().warning("Could not hook into Votifier!");
            }
        }
        // Vault module
        if(rootConfig.vault) {
            registerModule(PlayerPointsVaultLayer.class,
                    new PlayerPointsVaultLayer(this));
        }
    }

    @Override
    public void onDisable() {
        // Deregister all modules.
        List<Class<? extends IModule>> clazzez = new ArrayList<Class<? extends IModule>>();
        clazzez.addAll(modules.keySet());
        for(Class<? extends IModule> clazz : clazzez) {
            this.deregisterModuleForClass(clazz);
        }
    }

    /**
     * Get the plugin's API.
     * 
     * @return API instance.
     */
    public PlayerPointsAPI getAPI() {
        return api;
    }

    /**
     * Get the root plugin config.yml handler.
     * 
     * @return Root config handler.
     */
    public RootConfig getRootConfig() {
        return rootConfig;
    }

    /**
     * Get the storage handler for player points data.
     * 
     * @return Storage handler.
     */
    public StorageHandler getStorageHandler() {
        return storage;
    }

    /**
     * Register a module to the API.
     * 
     * @param clazz
     *            - Class of the instance.
     * @param module
     *            - Module instance.
     * @throws IllegalArgumentException
     *             - Thrown if an argument is null.
     */
    public <T extends IModule> void registerModule(Class<T> clazz, T module) {
        // Check arguments.
        if(clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        } else if(module == null) {
            throw new IllegalArgumentException("Module cannot be null");
        } else if(modules.containsKey(clazz)) {
            this.getLogger().warning(
                    "Overwriting module for class: " + clazz.getName());
        }
        // Add module.
        modules.put(clazz, module);
        // Tell module to start.
        module.starting();
    }

    /**
     * Unregister a module from the API.
     * 
     * @param clazz
     *            - Class of the instance.
     * @return Module that was removed from the API. Returns null if no instance
     *         of the module is registered with the API.
     */
    public <T extends IModule> T deregisterModuleForClass(Class<T> clazz) {
        // Check arguments.
        if(clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        // Grab module and tell it its closing.
        T module = clazz.cast(modules.get(clazz));
        if(module != null) {
            module.closing();
        }
        return module;
    }

    /**
     * Retrieve a registered CCModule.
     * 
     * @param clazz
     *            - Class identifier.
     * @return Module instance. Returns null is an instance of the given class
     *         has not been registered with the API.
     */
    public <T extends IModule> T getModuleForClass(Class<T> clazz) {
        return clazz.cast(modules.get(clazz));
    }
}