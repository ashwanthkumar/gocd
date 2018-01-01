package in.ashwanthkumar.gocd.database.mysql5;

/**
 * GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END
 * <p/>
 * Originally adopted from https://github.com/gocd/gocd/blob/master/server/src/com/thoughtworks/go/server/database/DbDeployMigration.java
 * for MySQL5 database
 */

import com.dbdeploy.Controller;
import com.dbdeploy.appliers.TemplateBasedApplier;
import com.dbdeploy.appliers.UndoTemplateBasedApplier;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.scripts.ChangeScriptRepository;
import com.dbdeploy.scripts.DirectoryScanner;
import com.thoughtworks.go.util.SystemEnvironment;
import in.ashwanthkumar.gocd.database.mysql5.tool.rules.Patcher;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.sql.SQLException;

import static com.thoughtworks.go.util.ExceptionUtils.bomb;
import static com.thoughtworks.go.util.ExceptionUtils.bombUnless;

public class MySQLDbDeployMigration {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLDbDeployMigration.class);
    public static final String CHANGELOG_TABLE_NAME = "changelog";

    private final SystemEnvironment env;
    private final BasicDataSource dataSource;

    public MySQLDbDeployMigration(BasicDataSource dataSource, SystemEnvironment env) {
        this.dataSource = dataSource;
        this.env = env;
    }

    public void migrate(String dbType) throws SQLException {
        upgradeWithDbDeploy(dbType);
    }

    private void upgradeWithDbDeploy(String dbType) throws SQLException {
        LOG.info("Upgrading database at " + dataSource + ". This might take a while depending on the size of the database.");
        final File upgradePath = env.getDBDeltasPath();
        bombUnless(upgradePath.exists(), "Database upgrade scripts do not exist in directory " + upgradePath.getAbsolutePath());
        ByteArrayOutputStream sqlOutput = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(sqlOutput);

        try {
            Controller controller = new Controller(
                    new ChangeScriptRepository(new DirectoryScanner("UTF-8").getChangeScriptsForDirectory(upgradePath)),
                    new DatabaseSchemaVersionManager(new QueryExecuter(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword()), CHANGELOG_TABLE_NAME),
                    new TemplateBasedApplier(printWriter, dbType, CHANGELOG_TABLE_NAME, null),
                    new UndoTemplateBasedApplier(printWriter, dbType, CHANGELOG_TABLE_NAME, null)
            );

            controller.processChangeScripts(Long.MAX_VALUE);
            printWriter.close();

            StringBuilder builder = new StringBuilder();
            String[] lines = sqlOutput.toString().split("\n");
            for (String line : lines) {
                builder.append(Patcher.applyPatch(line));
                builder.append("\n");
            }
            String finalSQL = builder.toString();
            if (StringUtils.isNotBlank(finalSQL)) {
                System.out.println("Changes to apply are " + finalSQL);
                new JdbcTemplate(dataSource).execute(finalSQL);
            } else {
                System.out.println("No changes to apply, skipping and moving on.");
            }
        } catch (Exception e) {
            String message = "Unable to create database upgrade script for database " + dataSource.getUrl() + ". The problem was: " + e.getMessage();
            if (e.getCause() != null) {
                message += ". The cause was: " + e.getCause().getMessage();
            }
            LOG.error(message, e);
            throw bomb(message, e);
        }
        LOG.info("Database upgraded");
    }


}
