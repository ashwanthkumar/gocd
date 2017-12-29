package in.ashwanthkumar.gocd.database.mysql5.migration;

public class SingleLineComment implements DDLPatch {
    @Override
    public boolean shouldPatch(String line) {
        return line.startsWith("//");
    }

    @Override
    public String patch(String line) {
        return line.replaceAll("^//(.*)", "--$1");
    }
}
