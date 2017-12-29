package in.ashwanthkumar.gocd.database.mysql5.migration;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Iterator;

public class DDLMigrator {
    private final static String DEFAULT_DELTAS_OUTPUT = "/tmp/mysqldeltas";
    private final static String DEFAULT_DELTAS_INPUT = "db/h2deltas";

    public static void main(String[] args) throws IOException {
        String inputDir = "/Users/ashwanthkumar/hacks/gocd/server/db/h2deltas";
        String outputDir = DEFAULT_DELTAS_OUTPUT;
        String[] deltas = new File(inputDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }
        });
        for (String delta : deltas) {
            File output = new File(outputDir, delta);
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
    }
}
