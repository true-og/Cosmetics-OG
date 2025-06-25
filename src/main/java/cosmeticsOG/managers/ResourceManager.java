package cosmeticsOG.managers;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.configuration.CustomConfig;
import cosmeticsOG.util.ResourceUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class ResourceManager {

    private final CosmeticsOG core;
    private final Map<String, BufferedImage> images;
    private final Map<String, CustomConfig> menus;

    public ResourceManager(final CosmeticsOG core) {
        this.core = core;
        images = new HashMap<String, BufferedImage>();
        menus = new HashMap<String, CustomConfig>();

        loadResources();
    }

    public void onReload() {
        images.clear();
        menus.clear();

        loadResources();
    }

    /**
     * Get all images stored locally on this server
     * @return
     */
    public Map<String, BufferedImage> getImages() {
        return new HashMap<String, BufferedImage>(images);
    }

    /**
     * Checks to see if an images exists with the given name
     * @param name
     * @return
     */
    public boolean imageExists(String name) {
        return images.containsKey(name);
    }

    /**
     * Gets a BufferedImage with the give name
     * @param name
     * @return
     */
    public BufferedImage getImage(String name) {
        return images.get(name);
    }

    /**
     * Get all menus stored locally on this server
     * @return
     */
    public List<String> getMenus() {
        return new ArrayList<String>(menus.keySet());
    }

    public CustomConfig getConfig(String menuName) {
        if (menus.containsKey(menuName)) {
            return menus.get(menuName);
        }
        return null;
    }

    private void loadResources() {
        // Images
        File resourceDirectory = new File(core.getDataFolder() + File.separator + "types");
        if (resourceDirectory.isDirectory()) {
            File[] imageFiles = resourceDirectory.listFiles();
            for (int i = 0; i < imageFiles.length; i++) {
                if (imageFiles[i].isFile()) {
                    try {
                        File imageFile = imageFiles[i];
                        BufferedImage image = ImageIO.read(imageFile);
                        images.put(ResourceUtil.removeExtension(imageFile.getName()), image);

                    } catch (IOException e) {
                    }
                }
            }
        }

        // Menus
        File menuDirectory = new File(core.getDataFolder() + File.separator + "menus");
        if (menuDirectory.isDirectory()) {
            File[] menuFiles = menuDirectory.listFiles();
            for (int i = 0; i < menuFiles.length; i++) {
                if (menuFiles[i].isFile()) {
                    File menuFile = menuFiles[i];
                    if (menuFile.getName().endsWith("yml")
                            && !menuFile.getName().startsWith(".")) {
                        CustomConfig menu = new CustomConfig(core, "menus", menuFile, true);
                        menus.put(menu.getName(), menu);
                    }
                }
            }
        }
    }
}
