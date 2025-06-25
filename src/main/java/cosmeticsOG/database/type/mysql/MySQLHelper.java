package cosmeticsOG.database.type.mysql;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.configuration.CustomConfig;
import cosmeticsOG.database.type.mysql.MySQLDatabase.Table;
import cosmeticsOG.database.type.mysql.MySQLDatabase.TableInfo;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.ParticleAction;
import cosmeticsOG.util.ResourceUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import org.bukkit.Material;

public class MySQLHelper {

    private final MySQLDatabase database;
    private int menuTableVersion = 1;

    public MySQLHelper(final MySQLDatabase database) {
        this.database = database;
    }

    /**
     * Creates our initial database
     *
     * @throws SQLException
     */
    public void initDatabase(CosmeticsOG core) throws SQLException {
        database.async(() -> {
            database.connect((connection) -> {
                try (Statement initStatement = connection.createStatement()) {
                    // Version Table
                    String versionTable = "CREATE TABLE IF NOT EXISTS " + TableInfo.VERSION.getFormat() + "("
                            + "type VARCHAR(32) PRIMARY KEY," + "version DECIMAL(4,2)" + ")";
                    initStatement.addBatch(versionTable);

                    // Menus Table
                    String menuTable = "CREATE TABLE IF NOT EXISTS " + TableInfo.MENUS.getFormat() + " ("
                            + "name VARCHAR(128) PRIMARY KEY," + "title VARCHAR(40) NOT NULL DEFAULT '',"
                            + "size TINYINT(3) NOT NULL DEFAULT 6," + "alias VARCHAR(64)" + ")";
                    initStatement.addBatch(menuTable);

                    // Groups Table
                    String groupTable = "CREATE TABLE IF NOT EXISTS " + TableInfo.GROUPS.getFormat() + " ("
                            + "name VARCHAR(128) PRIMARY KEY," + "menu VARCHAR(128) NOT NULL," + "weight INT NOT NULL"
                            + ")";
                    initStatement.addBatch(groupTable);

                    // Default Group Data
                    String defaultGroupQuery = "INSERT IGNORE INTO " + TableInfo.GROUPS.getFormat()
                            + " VALUES ('default', 'particles', 0)";
                    initStatement.addBatch(defaultGroupQuery);

                    // Images Table
                    String imageTable = "CREATE TABLE IF NOT EXISTS " + TableInfo.IMAGES.getFormat() + "("
                            + "name VARCHAR(64) PRIMARY KEY," + "image BLOB" + ")";
                    initStatement.addBatch(imageTable);

                    // Equipped Table
                    String equippedTable = "CREATE TABLE IF NOT EXISTS " + TableInfo.EQUIPPED.getFormat() + " ("
                            + "id VARCHAR(36)," + "name VARCHAR(128)," + "slot TINYINT," + "hidden BOOLEAN,"
                            + "PRIMARY KEY(id, name, slot)" + ")";
                    initStatement.addBatch(equippedTable);

                    // Purchases Table
                    String purchasedTable = "CREATE TABLE IF NOT EXISTS " + TableInfo.PURCHASED.getFormat() + " ("
                            + "id VARCHAR(36)," + "name VARCHAR(128)," + "slot TINYINT," + "PRIMARY KEY(id, name, slot)"
                            + ")";
                    initStatement.addBatch(purchasedTable);

                    // Execute batch
                    initStatement.executeBatch();
                }

                // Insert our versions
                String versionQuery = "INSERT IGNORE INTO " + TableInfo.VERSION.getFormat() + " VALUES (?,?)";
                try (PreparedStatement verStatement = connection.prepareStatement(versionQuery)) {
                    for (TableInfo table : TableInfo.values()) {
                        verStatement.setString(1, table.getName());
                        verStatement.setDouble(2, table.getVersion());
                        verStatement.addBatch();
                    }
                    verStatement.executeBatch();
                }

                // Try to add our included custom types to the database
                if (SettingsManager.LOAD_INCLUDED_CUSTOM_TYPES.getBoolean()) {
                    String imageInsertQuery = "INSERT IGNORE INTO " + TableInfo.IMAGES.getFormat() + " VALUES(?,?)";
                    try (PreparedStatement imageInsertStatement = connection.prepareStatement(imageInsertQuery)) {
                        InputStream vampireWingsStream = core.getResource("types/vampire_wings.png");
                        if (vampireWingsStream != null) {
                            try {
                                InputStream vampireStream = getTrimmedStream(vampireWingsStream);
                                imageInsertStatement.setString(1, "vampire_wings");
                                imageInsertStatement.setBlob(2, vampireStream);
                                imageInsertStatement.addBatch();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        InputStream butterflyWingsStream = core.getResource("types/butterfly_wings_colorable.png");
                        if (butterflyWingsStream != null) {
                            try {
                                InputStream butterflyStream = getTrimmedStream(butterflyWingsStream);
                                imageInsertStatement.setString(1, "butterfly_wings_colorable");
                                imageInsertStatement.setBlob(2, butterflyStream);
                                imageInsertStatement.addBatch();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        imageInsertStatement.executeBatch();
                    }
                }

                if (SettingsManager.LOAD_INCLUDED_MENUS.getBoolean()) {
                    String menuQuery =
                            "SELECT COUNT(*) AS count FROM " + TableInfo.MENUS.getFormat() + " WHERE name = ?";
                    try (PreparedStatement menuStatement = connection.prepareStatement(menuQuery)) {
                        menuStatement.setString(1, "particles");
                        ResultSet set = menuStatement.executeQuery();

                        while (set.next()) {
                            if (set.getInt("count") == 0) {
                                File tempFolder = new File(core.getDataFolder() + File.separator + "temp");
                                if (!tempFolder.exists()) {
                                    tempFolder.mkdirs();
                                }

                                InputStream particleStream = ResourceUtil.getMostCompatibleParticlesMenu();
                                if (particleStream != null) {
                                    File particlesFile = new File(core.getDataFolder() + File.separator + "temp"
                                            + File.separator + "particles.yml");
                                    try {
                                        ResourceUtil.copyFile(particleStream, particlesFile);
                                        CustomConfig menuConfig =
                                                new CustomConfig(core, "temp", "particles.yml", false);

                                        if (menuConfig != null) {
                                            database.importMenu(menuConfig, connection);
                                            menuConfig.delete();
                                            tempFolder.delete();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            });
        });
    }

    public String getItemTableQuery(String menuName) {
        return "CREATE TABLE IF NOT EXISTS " + Table.ITEMS.format(menuName) + " ("
                + "slot TINYINT PRIMARY KEY,"
                + "ver SMALLINT NOT NULL DEFAULT " + menuTableVersion + ","
                + "id VARCHAR(64),"
                + "durability INT,"
                + "title VARCHAR(128),"
                + "permission VARCHAR(64),"
                + "permission_denied VARCHAR(128),"
                + "type TINYINT,"
                + "custom_type VARCHAR(64),"
                + "location TINYINT,"
                + "mode TINYINT,"
                + "animation TINYINT,"
                + "tracking TINYINT,"
                + "label VARCHAR(128),"
                + "equip_message VARCHAR(128),"
                + "offset_x DECIMAL(3,1),"
                + "offset_y DECIMAL(3,1),"
                + "offset_z DECIMAL(3,1),"
                + "random_offset_x DECIMAL(3,1),"
                + "random_offset_y DECIMAL(3,1),"
                + "random_offset_z DECIMAL(3,1),"
                + "angle_x DECIMAL(4,1),"
                + "angle_y DECIMAL(4,1),"
                + "angle_z DECIMAL(4,1),"
                + "update_frequency TINYINT DEFAULT 2,"
                + "icon_update_frequency TINYINT DEFAULT 2,"
                + "speed TINYINT,"
                + "count TINYINT,"
                + "price INT,"
                + "sound VARCHAR(64),"
                + "volume DECIMAL(2,1) DEFAULT 1.0,"
                + "pitch DECIMAL(2,1) DEFAULT 1.0,"
                + "left_action TINYINT,"
                + "right_action TINYINT DEFAULT 12," // Mimic Left Click
                + "left_argument VARCHAR(128),"
                + "right_argument VARCHAR(128),"
                + "duration MEDIUMINT DEFAULT 200,"
                + "display_mode TINYINT,"
                + "scale DECIMAL(3,1) DEFAULT 1.0,"
                + "potion VARCHAR(64),"
                + "potion_strength TINYINT"
                + ")";
    }

    public String getNodeTableQuery(String menuName) {
        return "CREATE TABLE IF NOT EXISTS " + Table.NODES.format(menuName) + " ("
                + "slot TINYINT,"
                + "node_index INT,"
                + "ver SMALLINT,"
                + "type TINYINT,"
                + "custom_type VARCHAR(64),"
                + "location TINYINT,"
                + "mode TINYINT,"
                + "animation TINYINT,"
                + "tracking TINYINT,"
                + "offset_x DECIMAL(3,1),"
                + "offset_y DECIMAL(2,1),"
                + "offset_z DECIMAL(3,1),"
                + "random_offset_x DECIMAL(3,1),"
                + "random_offset_y DECIMAL(3,1),"
                + "random_offset_z DECIMAL(3,1),"
                + "angle_x DECIMAL(4,1),"
                + "angle_y DECIMAL(4,1),"
                + "angle_z DECIMAL(4,1),"
                + "update_frequency TINYINT,"
                + "speed TINYINT,"
                + "count TINYINT,"
                + "scale DECIMAL(3,1),"
                + "PRIMARY KEY(slot, node_index),"
                + "FOREIGN KEY(slot) REFERENCES " + Table.ITEMS.format(menuName)
                + "(slot) ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
    }

    public String getMetaTableQuery(String menuName) {
        return "CREATE TABLE IF NOT EXISTS " + Table.META.format(menuName) + " ("
                + "slot TINYINT,"
                + "type TINYINT,"
                + "line TINYINT,"
                + "line_ex TINYINT,"
                + "node_index INT,"
                + "value VARCHAR(256),"
                + "PRIMARY KEY(slot, type, line, line_ex, node_index),"
                + "FOREIGN KEY(slot) REFERENCES " + Table.ITEMS.format(menuName)
                + "(slot) ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
    }

    public String getParticleTableQuery(String menuName) {
        return "CREATE TABLE IF NOT EXISTS " + Table.PARTICLES.format(menuName) + " ("
                + "slot TINYINT,"
                + "particle_index TINYINT,"
                + "node_index INT,"
                + "particle_id TINYINT,"
                + "color INT NOT NULL DEFAULT 16711680," // Red
                + "random BOOLEAN,"
                + "scale DECIMAL(4,2),"
                + "item_data VARCHAR(64),"
                + "block_data VARCHAR(64),"
                + "duration SMALLINT,"
                + "gravity BOOLEAN,"
                + "velocity_x DECIMAL(3,1),"
                + "velocity_y DECIMAL(3,1),"
                + "velocity_z DECIMAL(3,1),"
                + "PRIMARY KEY(slot, particle_index, node_index),"
                + "FOREIGN KEY(slot) REFERENCES " + Table.ITEMS.format(menuName)
                + "(slot) ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
    }

    /**
     * Creates a valid insert SQL statement based on the hats modified properties
     * @param menuName
     * @param hat
     * @param particleIndex
     * @return
     */
    public String getParticleInsertQuery(String menuName, Hat hat, int particleIndex) {
        StringBuilder propertyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        StringBuilder updateBuilder = new StringBuilder();

        propertyBuilder.append("slot,particle_index,node_index");
        valueBuilder
                .append(hat.getSlot())
                .append(",")
                .append(particleIndex)
                .append(",")
                .append(hat.getIndex());

        String insertQuery =
                "INSERT INTO " + Table.PARTICLES.format(menuName) + " ({1}) VALUES ({2}) ON DUPLICATE KEY UPDATE {3}";

        Map<String, String> editedProperties =
                hat.getParticleData(particleIndex).getPropertyChanges();
        for (Entry<String, String> value : editedProperties.entrySet()) {
            propertyBuilder.append(",").append(value.getKey());
            valueBuilder.append(",").append(value.getValue());
            updateBuilder.append(",").append(value.getKey()).append("=").append(value.getValue());
        }

        String properties = propertyBuilder.toString();
        String values = valueBuilder.toString();
        String updates = "";
        if (updateBuilder.length() > 0) {
            updates = updateBuilder.deleteCharAt(0).toString();
        }

        return insertQuery.replace("{1}", properties).replace("{2}", values).replace("{3}", updates);
    }

    /**
     * Creates a valid insert SQL statement based on the hats modified properties
     * @param menuName
     * @param hat
     * @param slot
     * @param index
     * @return
     */
    public String getNodeInsertQuery(String menuName, Hat hat, int slot, int index) {
        StringBuilder propertyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        StringBuilder updateBuilder = new StringBuilder();

        propertyBuilder.append("slot,node_index");
        valueBuilder.append(slot).append(",").append(index);
        updateBuilder.append("node_index=").append(index);

        String query =
                "INSERT INTO " + Table.NODES.format(menuName) + " ({1}) VALUES ({2}) ON DUPLICATE KEY UPDATE {3}";

        Map<String, String> editedProperties = hat.getPropertyChanges();
        for (Entry<String, String> value : editedProperties.entrySet()) {
            propertyBuilder.append(",").append(value.getKey());
            valueBuilder.append(",").append(value.getValue());
            updateBuilder.append(",").append(value.getKey()).append("=").append(value.getValue());
        }

        return query.replace("{1}", propertyBuilder.toString())
                .replace("{2}", valueBuilder.toString())
                .replace("{3}", updateBuilder.toString());
    }

    public String getImportQuery(String menuName) {
        return "INSERT INTO " + Table.ITEMS.format(menuName) + " ("
                + "slot,"
                + "ver,"
                + "id,"
                + "durability,"
                + "title,"
                + "permission,"
                + "permission_denied,"
                + "type,"
                + "custom_type,"
                + "location,"
                + "mode,"
                + "animation,"
                + "tracking,"
                + "label,"
                + "equip_message,"
                + "offset_x,"
                + "offset_y,"
                + "offset_z,"
                + "random_offset_x,"
                + "random_offset_y,"
                + "random_offset_z,"
                + "angle_x,"
                + "angle_y,"
                + "angle_z,"
                + "update_frequency,"
                + "icon_update_frequency,"
                + "speed,"
                + "count,"
                + "price,"
                + "sound,"
                + "volume,"
                + "pitch,"
                + "left_action,"
                + "right_action,"
                + "left_argument,"
                + "right_argument,"
                + "duration,"
                + "display_mode,"
                + "scale,"
                + "potion,"
                + "potion_strength"
                + ") VALUES {1}";
    }

    public String getParticleImportQuery(String menuName) {
        return "INSERT INTO " + Table.PARTICLES.format(menuName) + " ("
                + "slot,"
                + "particle_index,"
                + "node_index,"
                + "particle_id,"
                + "color,"
                + "random,"
                + "scale,"
                + "item_data,"
                + "block_data,"
                + "duration,"
                + "gravity,"
                + "velocity_x,"
                + "velocity_y,"
                + "velocity_z"
                + ") VALUES {1}";
    }

    public String getMetaImportQuery(String menuName) {
        return "INSERT INTO " + Table.META.format(menuName) + " ("
                + "slot,"
                + "type,"
                + "line,"
                + "line_ex,"
                + "node_index,"
                + "value"
                + ") VALUES {1}";
    }

    public String getNodeImportQuery(String menuName) {
        return "INSERT INTO " + Table.NODES.format(menuName) + " ("
                + "slot,"
                + "node_index,"
                + "ver,"
                + "type,"
                + "custom_type,"
                + "location,"
                + "mode,"
                + "animation,"
                + "tracking,"
                + "offset_x,"
                + "offset_y,"
                + "offset_z,"
                + "random_offset_x,"
                + "random_offset_y,"
                + "random_offset_z,"
                + "angle_x,"
                + "angle_y,"
                + "angle_z,"
                + "update_frequency,"
                + "speed,"
                + "count,"
                + "scale"
                + ") VALUES {1}";
    }

    public void populatePurchaseMenu(Connection connection) throws SQLException {
        String insertQuery = "INSERT INTO " + Table.ITEMS.format("purchase")
                + " (slot,id,durability,title,left_action) VALUES (?,?,?,?,?)";
        try (PreparedStatement populateStatement = connection.prepareStatement(insertQuery)) {
            // Purchase Item
            populateStatement.setInt(1, 13);
            populateStatement.setString(2, Material.STONE.toString());
            populateStatement.setInt(3, 0);
            populateStatement.setString(4, "&fTemporary Hat");
            populateStatement.setInt(5, ParticleAction.PURCHASE_ITEM.getID());
            populateStatement.addBatch();

            // Purchase Confirm
            populateStatement.setInt(1, 30);
            populateStatement.setString(2, Material.DIAMOND.toString());
            populateStatement.setInt(3, 0);
            populateStatement.setString(4, "&2Unlock this Hat");
            populateStatement.setInt(5, ParticleAction.PURCHASE_CONFIRM.getID());
            populateStatement.addBatch();

            // Purchase Deny
            populateStatement.setInt(1, 32);
            populateStatement.setString(2, Material.COAL.toString());
            populateStatement.setInt(3, 0);
            populateStatement.setString(4, "&cCancel");
            populateStatement.setInt(5, ParticleAction.PURCHASE_DENY.getID());
            populateStatement.addBatch();

            populateStatement.executeBatch();
        }
    }

    /**
     * Creates a new InputStream from an existing InputStream removing comments
     * @param stream
     * @return
     */
    private InputStream getTrimmedStream(InputStream stream) throws IOException {
        BufferedImage image = ImageIO.read(stream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
