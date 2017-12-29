package in.ashwanthkumar.gocd.database.mysql5.migration;

public class ConvertToSignedInteger implements DDLPatch{
    @Override
    public boolean shouldPatch(String line) {
        return line.contains("CONVERT(id, INTEGER)");
    }

    @Override
    public String patch(String line) {
        return line.replace("INTEGER", "UNSIGNED INTEGER");
    }
}
