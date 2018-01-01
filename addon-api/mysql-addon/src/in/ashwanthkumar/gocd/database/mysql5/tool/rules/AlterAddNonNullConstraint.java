package in.ashwanthkumar.gocd.database.mysql5.tool.rules;

public class AlterAddNonNullConstraint implements DDLPatch {
    @Override
    public boolean shouldPatch(String line) {
        return line.contains("SET NOT NULL;");
    }

    @Override
    public String patch(String line) {
        return line.replace("SET NOT NULL;", "NOT NULL;");
    }
}
