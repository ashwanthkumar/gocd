/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.ashwanthkumar.gocd.database.mysql5.tool;

import com.thoughtworks.go.util.SystemEnvironment;
import in.ashwanthkumar.gocd.database.mysql5.MySQL5Database;
import in.ashwanthkumar.gocd.database.mysql5.MySQLDbDeployMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class MySQLMigration {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLMigration.class);

    public void migrate() throws SQLException {
        SystemEnvironment systemEnvironment = new SystemEnvironment();
        MySQL5Database database = new MySQL5Database(systemEnvironment);
        LOG.info("Starting MySQLDeployMigration");
        MySQLDbDeployMigration migrateSchema = new MySQLDbDeployMigration(database.getDataSource(), systemEnvironment);
        migrateSchema.migrate("mysql");
        LOG.info("MySQLDeployMigration is complete");
    }

    public static void main(String[] args) throws SQLException {
        new MySQLMigration().migrate();
    }
}
