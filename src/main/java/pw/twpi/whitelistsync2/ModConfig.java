package pw.twpi.whitelistsync2;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import java.nio.file.Path;

@Config(name = WhitelistSync2.MODID)
public class ModConfig implements ConfigData {

    public enum DatabaseMode {
        MYSQL,
        SQLITE,
        POSTGRESQL
    }

    public DatabaseMode databaseMode = DatabaseMode.SQLITE;
    public int syncTimer;
    public boolean syncOpList = false;

    public MySqlConfig mysql;
    public SqliteConfig sqlite;
    public PostgreSqlConfig postgresql;

    public static class MySqlConfig {
        public String host;
        public short port;
        public String username;
        public String password;
        public String database;
    }

    public static class SqliteConfig {
        public String path;
    }

    public static class PostgreSqlConfig {
        public String host;
        public short port;
        public String username;
        public String password;
        public String database;
    }
}
