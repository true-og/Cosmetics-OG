package cosmeticsOG.configuration;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.util.ResourceUtil;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig {

    private final CosmeticsOG core;

    private File file;
    private FileConfiguration config;

    private final String path;
    private final String fileName;
    private final String name;
    private final String directory;

    public CustomConfig(final CosmeticsOG core, final String path, final String name, boolean logOutput) {

        this.core = core;
        this.path = path;
        this.fileName = name;
        this.name = ResourceUtil.removeExtension(name);
        this.directory = core.getDataFolder() + File.separator + path;
        this.file = new File(directory + File.separator + name);
        this.config = new YamlConfiguration();

        // Load the configuration file.
        if (!file.exists()) {

            file = createFile(logOutput);

        } else if (logOutput) {

            Utils.logToConsole("Loading " + path + File.separator + fileName);

        }

        try {

            config.load(file);

        } catch (Exception error) {

            Utils.logToConsole("There was an error loading " + name + ", error: " + error.getClass().getSimpleName());

        }

    }

    public CustomConfig(final CosmeticsOG core, final String path, File file, boolean logOutput) {

        this.core = core;
        this.path = path;
        this.fileName = file.getName();
        this.name = ResourceUtil.removeExtension(file.getName());
        this.file = file;
        this.directory = core.getDataFolder() + File.separator + path;
        this.config = new YamlConfiguration();

        try {

            config.load(file);

        } catch (Exception error) {

            Utils.logToConsole("There was an error loading " + name + ", error: " + error.getClass().getSimpleName());

        }

    }

    /**
     * Saves any changes in this configuration file.
     */
    public void save() {

        try {

            config.save(file);

        } catch (Exception error) {

            Utils.logToConsole("There was an error saving the config file: " + error.getMessage());

        }

    }

    /**
     * Reloads this configuration file.
     */
    public void reload() {

        try {

            config = YamlConfiguration.loadConfiguration(file);

        } catch (Exception error) {

            Utils.logToConsole("There was an error loading " + name + ", error: " + error.getClass().getSimpleName());

        }

    }

    /**
     * Tries to delete this Configuration File.
     * 
     * @return
     */
    public boolean delete() {

        return file.delete();

    }

    public String getFileName() {

        return fileName;

    }

    /**
     * Returns the name of this file.
     * 
     * @return
     */
    public String getName() {

        return name;

    }

    public void set(String path, Object value) {

        config.set(path, value);

    }

    /**
     * Get this CustomConfig configuration file.
     * 
     * @return
     */
    public FileConfiguration getConfig() {

        return config;

    }

    /**
     * Creates a file. will try to load from an existing file first before creating
     * a blank file.
     * 
     * @param logOutput
     * @return
     */
    private File createFile(boolean logOutput) {

        file.getParentFile().mkdirs();
        file = new File(directory + File.separator + fileName);

        // Try to copy an existing .yml file into this one.
        if (core.getResource(fileName) != null) {

            try {

                ResourceUtil.copyFile(core.getResource(fileName), file);

            } catch (IOException error) {

                Utils.logToConsole("ERROR: Failed to copy resource: " + core.getResource(fileName) + "into file: "
                        + file.getAbsolutePath());

            }

        } else {

            if (!file.exists()) {

                try {

                    file.createNewFile();

                } catch (IOException error) {

                    Utils.logToConsole("ERROR: Failed to create file: " + file.getAbsolutePath());

                }

            }

        }

        if (logOutput) {

            Utils.logToConsole("Creating " + path + File.separator + fileName);

        }

        return file;

    }

}
