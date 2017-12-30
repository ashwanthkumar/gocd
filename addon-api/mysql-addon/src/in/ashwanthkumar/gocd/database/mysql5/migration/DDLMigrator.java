package in.ashwanthkumar.gocd.database.mysql5.migration;

import in.ashwanthkumar.gocd.database.mysql5.tool.MySQLMigration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;

public class DDLMigrator {
    private final static String DEFAULT_DELTAS_OUTPUT = "/Users/ashwanthkumar/hacks/gocd/addon-api/mysql-addon/db/h2deltas";
    private final static String DEFAULT_DELTAS_INPUT = "db/h2deltas";

    public static void main(String[] args) throws IOException, SQLException {
        String inputDir = "/Users/ashwanthkumar/hacks/gocd/server/db/h2deltas/migrate";

        String outputDir = DEFAULT_DELTAS_OUTPUT;
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
