package in.ashwanthkumar.gocd.database.mysql5;

import com.thoughtworks.go.configuration.DatabaseConfiguration;
import com.thoughtworks.go.util.SystemEnvironment;

import java.util.Properties;

public class MySQL5Configuration extends DatabaseConfiguration {
    public MySQL5Configuration(SystemEnvironment systemEnvironment) {
        super(systemEnvironment);
    }

    @Override
    public Properties loadConfiguration() {
        // Nothing to load as configuration is fetched by system environment
        return null;
    }

    public String getHost() {
        return systemEnvironment.getPropertyImpl(SystemEnvironment.GO_DATABASE_HOST.propertyName());
    }

    public int getPort() {
        return systemEnvironment.getDatabaseSeverPort();
    }

    public String getName() {
        return systemEnvironment.get(SystemEnvironment.GO_DATABASE_NAME);
    }

    public String getUser() {
        return systemEnvironment.get(SystemEnvironment.GO_DATABASE_USER);
    }

    public String getPassword() {
        return systemEnvironment.get(SystemEnvironment.GO_DATABASE_PASSWORD);
    }

    public int getMaxActive() {
        return systemEnvironment.get(SystemEnvironment.GO_DATABASE_MAX_ACTIVE);
    }

    public int getMaxIdle() {
        return systemEnvironment.get(SystemEnvironment.GO_DATABASE_MAX_IDLE);
    }

    public String dbUrl() {
        return String.format("jdbc:mysql://%s:%s/%s?allowMultiQueries=true", this.getHost(), this.getPort(), this.getName());
    }
}
