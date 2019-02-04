package com.mediusecho.particlehats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.type.DatabaseType;
import com.mediusecho.particlehats.managers.CommandManager;
import com.mediusecho.particlehats.managers.EventManager;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.stats.Metrics;
import com.mediusecho.particlehats.tasks.MenuTask;

public class Core extends JavaPlugin {

	public static Core instance;
	private static Logger logger;
	
	private Database database;
	private DatabaseType databaseType;
	
	// Managers
	private EventManager eventManager;
	private CommandManager commandManager;
	
	// Player State
	private Map<UUID, PlayerState> playerState;
	
	// Tasks
	private MenuTask menuTask;
	
	@Override
	public void onEnable ()
	{
		instance = this;	
		logger = getServer().getLogger();
		
		// Save default config
		saveDefaultConfig();
		
		log("Initializing");
		{		
			// Load our database
			databaseType = DatabaseType.fromAlias(SettingsManager.DATABASE_TYPE.getString());
			database = databaseType.getDatabase();
			
			// Initialize our player state map
			playerState = new HashMap<UUID, PlayerState>();
			
			// Create our managers
			eventManager   = new EventManager(this);
			commandManager = new CommandManager(this, "h");
			
			// Enable Metrics
			Metrics metrics = new Metrics(this);
			metrics.addCustomChart(new Metrics.SimplePie("database_type", () -> databaseType.toString().toLowerCase()));
			
			// Create our menu task
			menuTask = new MenuTask(this);
			menuTask.runTaskTimer(this, 0, 5);
		}
		log("" + this.getDescription().getVersion() + " loaded");
	}
	
	@Override
	public void onDisable () 
	{
		database.onDisable();
		
		menuTask.cancel();
	}
	
	/**
	 * Returns the Database this plugin is using
	 * @return
	 */
	public Database getDatabase () {
		return database;
	}
	
	/**
	 * Returns the type of database this server is using
	 * @return
	 */
	public DatabaseType getDatabaseType () {
		return databaseType;
	}
	
	/**
	 * Returns the PlayerState object that belongs to this player
	 * @param id
	 * @return
	 */
	public PlayerState getPlayerState (UUID id)
	{
		if (playerState.containsKey(id)) {
			return playerState.get(id);
		}
		
		else
		{
			PlayerState state = new PlayerState();
			playerState.put(id, state);
			
			return state;
		}
	}
	
	/**
	 * Logs a message to the server console
	 * @param obj
	 */
	public static void log (Object obj) {
		logger.log(Level.INFO, "[ParticleHats] " + obj.toString());
	}
}