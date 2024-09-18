package cosmeticsOG;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import cosmeticsOG.API.HatAPI;
import cosmeticsOG.API.ParticleHatsAPI;
import cosmeticsOG.database.Database;
import cosmeticsOG.database.type.DatabaseType;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.locale.Message;
import cosmeticsOG.managers.CommandManager;
import cosmeticsOG.managers.EventManager;
import cosmeticsOG.managers.HookManager;
import cosmeticsOG.managers.ParticleManager;
import cosmeticsOG.managers.ResourceManager;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.particles.renderer.ParticleRenderer;
import cosmeticsOG.particles.renderer.legacy.LegacyParticleRenderer;
import cosmeticsOG.particles.renderer.spigot.SpigotParticleRenderer;
import cosmeticsOG.player.EntityState;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.prompt.BukkitPrompt;
import cosmeticsOG.prompt.Prompt;
import cosmeticsOG.prompt.SpigotPrompt;
import cosmeticsOG.tasks.EntityTask;
import cosmeticsOG.tasks.MenuTask;
import cosmeticsOG.tasks.PromptTask;
import cosmeticsOG.ui.MenuManagerFactory;
import cosmeticsOG.util.YamlUtil;

@SuppressWarnings("unused")
public class CosmeticsOG extends JavaPlugin {

	// [Future] Are features that i plan on adding.
	// [?] Are ideas that may or may not get added.

	// TODO: #4 LuckPerms temp permission support.
	// This one kinda works, but only for player specific
	// temporary permissions. I'd like to get it working for
	// groups as well.

	// TODO: [?] Enjin economy support?
	// TODO: [?] Custom Player Head support? (Head Database, Heads Plugin)
	// TODO: [?] Type texture support? Lets built-in types display custom images (eg. capes)
	// TODO: [?] More flexible mode support?
	// Similar to tags, Have a list of tags that will trigger particles displaying / disabling
	// Display when: (running, walking, flying, etc)
	// Disable when: (pvp, swimming, etc)

	// TODO: [Future] Add update notifier
	// TODO: [Future] Allow adding custom types images as frames to an animation
	// TODO: [Future] Re-implement text particle type
	// TODO: [Future] Let particles be attached to blocks...
	// Have a separate block menu that shows nearby particles for the player to edit.

	public static CosmeticsOG instance;
	public static double serverVersion;
	private static Logger logger;
	private static ParticleHatsAPI hatAPI;

	private MenuManagerFactory menuManagerFactory;

	private Database database;
	private DatabaseType databaseType;

	private ParticleRenderer particleRenderer;

	// Managers
	private ResourceManager resourceManager;
	private EventManager eventManager;
	private CommandManager commandManager;
	private ParticleManager particleManager;
	private HookManager hookManager;

	// Lang
	private File langFile;
	private YamlConfiguration lang;

	// Update en_US.lang version as well.
	private final double LANG_VERSION = 1.14;

	private ConcurrentHashMap<UUID, EntityState> entityState;

	// Lets us know we can use the BaseComponent class from the bungee api
	private boolean supportsBaseComponent = true;
	private Prompt prompt;

	// Tasks
	private MenuTask menuTask;
	private PromptTask promptTask;
	private EntityTask entityTask;

	private boolean enabled = false;

	// Debugging
	public static final boolean debugging = false;

	@Override
	public void onEnable ()
	{
		instance = this;	
		serverVersion = getServerVersion();
		logger = instance.getLogger();
		hatAPI = new HatAPI(this);

		// Make sure we're running on a supported version
		if (serverVersion < 13)
		{
			particleRenderer = new LegacyParticleRenderer();

			if (serverVersion < 8)
			{
				Utils.logToConsole("-----------------------------------------------------------------------");
				Utils.logToConsole("This version of ParticleHats is not compatible with your server version");
				Utils.logToConsole("Download version 3.7.5 if your server is on a version older than 1.8");
				Utils.logToConsole("-----------------------------------------------------------------------");

				getServer().getPluginManager().disablePlugin(this);
				return;	
			}
		} else {
			particleRenderer = new SpigotParticleRenderer();
		}

		// Check to see if we're running on Spigot
		try {
			Class.forName("net.md_5.bungee.api.chat.BaseComponent");
		} catch (ClassNotFoundException e) {
			supportsBaseComponent = false;
		}

		// Save default config
		saveDefaultConfig();

		Utils.logToConsole("Loading ParticleHats v" + getPluginMeta().getVersion());
		Utils.logToConsole("");
		{		
			// Create our menu manager factory
			menuManagerFactory = new MenuManagerFactory(this);

			if (YamlUtil.checkConfigForUpdates(getConfig()))
			{
				if (SettingsManager.CONFIG_AUTO_UPDATE.getBoolean())
				{
					Utils.logToConsole("Updating config.yml");
					YamlUtil.updateConfig(this, getConfig());
				}

				else {
					Utils.logToConsole("There is an update for config.yml, auto updates are disabled.");
				}
			}

			// Load our database
			databaseType = DatabaseType.fromAlias(SettingsManager.DATABASE_TYPE.getString());
			database = databaseType.getDatabase(this);

			// yaml always returns true, mysql will return false if the connection could not be made
			if (! database.isEnabled())
			{
				Utils.logToConsole("---------------------------------------------------");
				Utils.logToConsole("There was an error connecting to the MySQL database");

				if (database.getException() != null) {
					Utils.logToConsole("Error: " + database.getException().getClass().getSimpleName());
					if (debugging) {
						database.getException().printStackTrace();
					}
				}

				Utils.logToConsole("Switching to yaml");
				Utils.logToConsole("---------------------------------------------------");
				Utils.logToConsole("");

				databaseType = DatabaseType.YAML;
				database = databaseType.getDatabase(this);
			}

			// Initialize our player state map
			entityState = new ConcurrentHashMap<UUID, EntityState>();

			//log("");
			checkDefaultLang();
			loadLang();

			// Create our managers
			resourceManager = new ResourceManager(this);
			eventManager = new EventManager(this);
			commandManager = new CommandManager(this, "h");
			particleManager = new ParticleManager(this);
			hookManager = new HookManager(this);

			if (SettingsManager.EDITOR_USE_ACTION_BAR.getBoolean() && supportsBaseComponent) {
				prompt = new SpigotPrompt();
			} else {
				prompt = new BukkitPrompt();
			}

			// Handles menu updates
			menuTask = new MenuTask(this);
			menuTask.runTaskTimer(this, 0, SettingsManager.LIVE_MENU_UPDATE_FREQUENCY.getInt());

			// Handles meta editing prompts
			promptTask = new PromptTask(this);
			promptTask.runTaskTimer(this, 0, 40);

			// Handles displaying particles
			entityTask = new EntityTask(this);
			entityTask.runTaskTimer(this, 0, 4);
		}
		Utils.logToConsole("");
		Utils.logToConsole("Done :)");

		enabled = true;
	}

	public static CosmeticsOG getInstance() {
		return instance;
	}

	@Override
	public void onDisable () 
	{
		if (enabled)
		{
			database.onDisable();

			menuTask.cancel();
			promptTask.cancel();
			entityTask.cancel();
		}
	}

	public void onReload ()
	{
		//locale.reload();
		reloadConfig();

		SettingsManager.onReload();

		if (langFile == null || !(langFile.getName().equals(SettingsManager.LANG.getString()))) {
			loadLang();
		}
		lang = YamlConfiguration.loadConfiguration(langFile);

		Message.onReload();

		if (SettingsManager.EDITOR_USE_ACTION_BAR.getBoolean() && supportsBaseComponent) {
			prompt = new SpigotPrompt();
		} else {
			prompt = new BukkitPrompt();
		}

		eventManager.onReload();
		database.onReload();
		entityTask.onReload();
		resourceManager.onReload();
		hookManager.onReload();

		// Reload each equipped hats async task
		entityState.values().forEach(EntityState::reload);
	}

	/**
	 * Get the ParticleHats Hat API
	 * @return
	 */
	public ParticleHatsAPI getAPI () {
		return hatAPI;
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
	 * Returns the MenuManagerFactory class
	 * @return
	 */
	public MenuManagerFactory getMenuManagerFactory () {
		return menuManagerFactory;
	}

	/**
	 * Get the ParticleRenderer for this server version
	 * @return
	 */
	public ParticleRenderer getParticleRenderer () {
		return particleRenderer;
	}

	/**
	 * Get the ResourceManager class
	 * @return
	 */
	public ResourceManager getResourceManager () {
		return resourceManager;
	}

	/**
	 * Get the ParticleManager class
	 * @return
	 */
	public ParticleManager getParticleManager () {
		return particleManager;
	}

	/**
	 * Get the HookManager class
	 * @return
	 */
	public HookManager getHookManager () {
		return hookManager;
	}

	/**
	 * Returns the PlayerState object that belongs to this player
	 * @return
	 */
	public PlayerState getPlayerState (Player player) {
		return (PlayerState)getEntityState(player);
	}

	public PlayerState getNewPlayerState (Player player)
	{
		UUID id = player.getUniqueId();
		removePlayerState(id);

		return (PlayerState)getEntityState(player);
	}

	public EntityState getEntityState (Entity entity, int entityID)
	{
		UUID id = entity.getUniqueId();

		if (entityState.containsKey(id)) {
			return entityState.get(id);
		}

		if (entity instanceof Player)
		{
			EntityState eState;

			if (entity.hasMetadata("NPC")) {
				eState = new EntityState(entity, entityID);
			} else {
				eState = new PlayerState((Player)entity);
			}

			entityState.put(id, eState);
			return eState;
		}

		EntityState eState = new EntityState(entity, entityID);

		entityState.put(id, eState);
		return eState;
	}

	public EntityState getEntityState (Entity entity) {
		return getEntityState(entity, -1);
	}

	/**
	 * Checks to see if this entity has an EntityState object loaded
	 * @param entity
	 * @return
	 */
	public boolean hasEntityState (Entity entity) {
		return entityState.containsKey(entity.getUniqueId());
	}

	/**
	 * Returns all currently active player states
	 * @return
	 */
	public Collection<EntityState> getEntityStates () {
		return entityState.values();
	}

	public void removePlayerState (UUID id)
	{
		EntityState es = entityState.remove(id);
		if (es != null) {
			es.clearActiveHats();
		}
	}

	/**
	 * Check to see if we can use the bungee BaseComponent class
	 * @return
	 */
	public Boolean canUseBungee () {
		return supportsBaseComponent;
	}

	/**
	 * Gets the current server version
	 * @return
	 */
	public double getServerVersion()
	{
		String version = Bukkit.getBukkitVersion().split("-")[0].substring(2);
		return Double.parseDouble(version);
	}

	/**
	 * Get this plugins CommandManager
	 * @return
	 */
	public CommandManager getCommandManager () {
		return commandManager;
	}

	public YamlConfiguration getLocale () {
		return lang;
	}

	/**
	 * Sends the player a message using their Action Bar
	 * @param player
	 * @param message
	 */
	public void prompt (Player player, MetaState message) {
		prompt.prompt(player, message);
	}

	public Prompt getPrompt () {
		return prompt;
	}

	/**
	 * Checks to see if a file exists with this name in the given folder
	 * @param folderName
	 * @param fileName
	 * @return
	 */
	public boolean fileExists (String folderName, String fileName)
	{
		String directory = getDataFolder() + File.separator + folderName + File.separator + fileName;
		File file = new File(directory);

		return file.exists();
	}

	/**
	 * Logs a debug message to the server console if debugging is enabled
	 * @param obj
	 */
	public static void debug (Object obj) 
	{
		if (debugging) {
			logger.log(Level.INFO, "[ParticleHats Debug] " + obj.toString());
		}
	}

	private void loadLang ()
	{
		String targetLang = SettingsManager.LANG.getString();

		File langFile = new File(getDataFolder() + File.separator + "lang" + File.separator + targetLang);
		if (!langFile.exists()) 
		{
			if (targetLang.equals("en_US.lang")) {
				Utils.logToConsole("Creating en_US.lang");
			} else {
				Utils.logToConsole("Could not find locale " + targetLang + ", switching to en_US.lang");
			}

			// Create our default .lang file since the specified one doesn't exist
			createDefaultLang();
			targetLang = "en_US.lang";
		}

		Utils.logToConsole("Loaded locale " + targetLang);
		this.langFile = new File(getDataFolder() + File.separator + "lang" + File.separator + targetLang);
		this.lang = YamlConfiguration.loadConfiguration(this.langFile);
	}

	private void createDefaultLang ()
	{
		File langFolder = new File(getDataFolder() + File.separator + "lang");
		if (!langFolder.exists()) {
			langFolder.mkdirs();
		}

		InputStream langStream = getResource("lang/en_US.lang");
		if (langStream != null)
		{
			File langFile = new File(getDataFolder() + File.separator + "lang" + File.separator + "en_US.lang");

			if (langFile.exists()) {
				langFile.delete();
			}

			try {
				Files.copy(langStream, Paths.get(langFile.getPath()));
			} catch (IOException e) {}
		}
	}

	private void checkDefaultLang ()
	{
		File langFile = new File(getDataFolder() + File.separator + "lang" + File.separator + "en_US.lang");
		if (! langFile.exists()) {
			createDefaultLang();
		}

		else
		{
			YamlConfiguration tempLangConfig = YamlConfiguration.loadConfiguration(langFile);
			if (tempLangConfig.getDouble("version", 1.0) != LANG_VERSION) 
			{
				Utils.logToConsole("Updating en_US.lang");
				createDefaultLang();
			}
		}
	}

}