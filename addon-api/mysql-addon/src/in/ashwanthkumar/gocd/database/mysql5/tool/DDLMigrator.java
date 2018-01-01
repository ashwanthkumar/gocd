/*
 * Copyright 2018 ThoughtWorks, Inc.
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

import in.ashwanthkumar.gocd.database.mysql5.tool.rules.Patcher;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Wrapper to attempt at automatic conversion of H2 based DB migration
 * to be MySQL compatible.
 */
public class DDLMigrator {
    public static void main(String[] args) throws IOException, SQLException {
        // should be gocd/server/db/h2deltas/migrate
        String inputDir = args[0];
        // should be gocd/addon-api/mysql-addon/db/h2deltas
        String outputDir = args[1];

        //        File outputFile = new File(outputDir);
//        if (outputFile.exists()) {
//            FileUtils.deleteDirectory(outputFile);
//        }
//        outputFile.mkdirs();

        File input = new File(inputDir);
        System.out.println("Starting to patch files in " + input.getAbsolutePath());
        String[] deltas = input.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }
        });
        for (String delta : deltas) {
            File output = new File(outputDir, delta);
            String deltaVersion = extractVersion(delta);
            System.out.println("Patching the file - " + delta + " into " + output);
            PrintWriter writer = new PrintWriter(output);
            Iterator<String> lines = IOUtils.lineIterator(new FileReader(new File(inputDir, delta)));
            while (lines.hasNext()) {
                String patchedLine = Patcher.applyPatch(lines.next());
                writer.println(patchedLine);
            }
            writer.flush();
            writer.close();
        }

        // Attempt to do the migration once we've patched the files
        new MySQLMigration().migrate();
    }

    private static String extractVersion(String delta) {
        String[] parts = delta.split("_");
        return parts[0];
    }
}
