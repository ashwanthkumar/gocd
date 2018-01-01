package in.ashwanthkumar.gocd.database.mysql5.tool.rules;

public class LongVarcharToText implements DDLPatch {
    @Override
    public boolean shouldPatch(String line) {
        return line.contains("LONGVARCHAR");
    }

    @Override
    public String patch(String line) {
        return line.replace("LONGVARCHAR", "TEXT");
    }
}
