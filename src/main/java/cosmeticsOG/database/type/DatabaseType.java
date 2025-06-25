package cosmeticsOG.database.type;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.database.Database;
import cosmeticsOG.database.type.mysql.MySQLDatabase;
import cosmeticsOG.database.type.yaml.YamlDatabase;
import java.util.Arrays;
import java.util.List;

public enum DatabaseType {
    YAML("yml, yaml"),
    MYSQL("mysql", "MySQL");

    private List<String> aliases;

    private DatabaseType(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }

    /**
     * Returns the Database object of this type
     * @return
     */
    public Database getDatabase(CosmeticsOG core) {
        switch (this) {
            case MYSQL:
                return new MySQLDatabase(core);
            default:
                return new YamlDatabase(core);
        }
    }

    /**
     * Returns the DatabaseType that uses this alias, or YAML if none are found
     * @param alias
     * @return
     */
    public static DatabaseType fromAlias(String alias) {
        for (DatabaseType type : values()) {
            if (type.aliases.contains(alias.toLowerCase())) {
                return type;
            }
        }
        return YAML;
    }

    @FunctionalInterface
    public interface DatabaseConnectionCallback {
        public void onTimeout(Exception e);
    }
}
