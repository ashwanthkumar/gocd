package in.ashwanthkumar.gocd.database.mysql5;

import com.thoughtworks.go.database.Database;
import com.thoughtworks.go.database.QueryExtensions;
import com.thoughtworks.go.util.SystemEnvironment;
import in.ashwanthkumar.gocd.database.SqlUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class MySQL5Database implements Database {
    private static final Logger LOG = LoggerFactory.getLogger(MySQL5Database.class);
    static final String DIALECT_MYSQL = "org.hibernate.dialect.MySQLDialect";
    private final MySQL5Configuration configuration;
    private final SystemEnvironment systemEnvironment;
    private BasicDataSource dataSource;

    public static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";

    public MySQL5Database(SystemEnvironment systemEnvironment) {
        this.systemEnvironment = systemEnvironment;
        this.configuration = new MySQL5Configuration(systemEnvironment);
    }

    @Override
    public String dialectForHibernate() {
        return DIALECT_MYSQL;
    }

    @Override
    public String getType() {
        return "mysql5";
    }

    @Override
    public void startDatabase() {
        // Nothing to be done
    }

    @Override
    public DataSource createDataSource() {
        return getDataSource();
    }

    @Override
    public void upgrade() throws SQLException {
        LOG.info("[db] Running upgrade");
        BasicDataSource dataSource = getDataSource();
        if (systemEnvironment.inDbDebugMode()) {
            LOG.info("In debug mode - not upgrading database");
            //don't upgrade
        } else {
            LOG.info("Starting MySQLDeployMigration");
            DbDeployMigration migrateSchema = new DbDeployMigration(dataSource, systemEnvironment);
            migrateSchema.migrate("mysql");
            LOG.info("MySQLDeployMigration is complete");
        }
        LOG.info("[db] upgrade is complete");
    }

    @Override
    public void shutdown() throws SQLException {
        // Nothing to be done
    }

    @Override
    public void backup(File file) {
        // TODO - Implement backup method
    }

    @Override
    public String getIbatisConfigXmlLocation() {
        return null;
    }

    @Override
    public QueryExtensions getQueryExtensions() {

        return new QueryExtensions() {
            @Override
            public String queryFromInclusiveModificationsForPipelineRange(String pipelineName, Integer fromCounter, Integer toCounter) {
                return "WITH LINK(id) AS ( "
                        + "  SELECT id "
                        + "     FROM pipelines "
                        + "     WHERE name = " + SqlUtil.escape(
                        pipelineName) // using string concatenation because Hibernate does not seem to be able to replace named or positional parameters here
                        + "         AND counter >= " + fromCounter
                        + "         AND counter <= " + toCounter
                        + "  UNION ALL "
                        + "  SELECT mod.pipelineId "
                        + "     FROM link "
                        + "         INNER JOIN pipelineMaterialRevisions pmr ON link.id = pmr.pipelineId "
                        + "         INNER JOIN modifications mod ON pmr.toRevisionId >= mod.id and pmr.actualFromRevisionId <= mod.id AND pmr.materialId = mod.materialId "
                        + "     WHERE mod.pipelineId IS NOT NULL"
                        + ")"
                        + "SELECT DISTINCT id FROM link WHERE id IS NOT NULL";

            }

            @Override
            public String queryRelevantToLookedUpDependencyMap(List<Long> pipelineIds) {
                return "WITH LINK(id, name, lookedUpId) AS ( "
                        + "  SELECT id, name, id as lookedUpId"
                        + "     FROM pipelines "
                        + "     WHERE id in (" + SqlUtil.joinWithQuotesForSql(pipelineIds.toArray()) + ") "
                        + "  UNION ALL "
                        + "  SELECT mod.pipelineId as id, p.name as name, link.lookedUpId as lookedUpId "
                        + "     FROM link "
                        + "         INNER JOIN pipelineMaterialRevisions pmr ON link.id = pmr.pipelineId "
                        + "         INNER JOIN modifications mod ON pmr.toRevisionId >= mod.id and pmr.actualFromRevisionId <= mod.id AND pmr.materialId = mod.materialId "
                        + "         INNER JOIN pipelines p ON mod.pipelineId = p.id "
                        + "     WHERE mod.pipelineId IS NOT NULL"
                        + ")"
                        + "SELECT id, name, lookedUpId FROM link";
            }

            @Override
            public String retrievePipelineTimeline() {
                return "SELECT CAST(p.name AS VARCHAR), p.id AS p_id, p.counter, m.modifiedtime, "
                        + " (SELECT CAST(materials.fingerprint AS VARCHAR) FROM materials WHERE id = m.materialId), naturalOrder, m.revision, pmr.folder, pmr.toRevisionId AS mod_id, pmr.Id as pmrid "
                        + "FROM pipelines p, pipelinematerialrevisions pmr, modifications m "
                        + "WHERE p.id = pmr.pipelineid "
                        + "AND pmr.torevisionid = m.id "
                        + "AND p.id > ?";
            }
        };

    }

    private BasicDataSource getDataSource() {
        if (this.dataSource == null) {
            // TODO - Add support for DBDebug mode?
            BasicDataSource source = new BasicDataSource();
            configureDataSource(source, configuration.dbUrl());
            LOG.info("Creating data source on port=" + configuration.getPort());
            this.dataSource = source;
        }
        return dataSource;
    }

    private void configureDataSource(BasicDataSource source, String url) {
        String databaseUsername = configuration.getUser();
        String databasePassword = configuration.getPassword();
        LOG.info(String.format("[db] Using connection configuration %s [User: %s]", url, databaseUsername));
        source.setDriverClassName(MYSQL_JDBC_DRIVER);
        source.setUrl(url);
        source.setUsername(databaseUsername);
        source.setPassword(databasePassword);
        source.setMaxActive(configuration.getMaxActive());
        source.setMaxIdle(configuration.getMaxIdle());
    }
}
