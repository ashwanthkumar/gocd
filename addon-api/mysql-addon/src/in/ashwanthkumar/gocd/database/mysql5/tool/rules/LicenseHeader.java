package in.ashwanthkumar.gocd.database.mysql5.tool.rules;

// PATCH migration scripts because of MySQL's comment handling policy
// Ref - http://dev.mysql.com/doc/refman/5.7/en/ansi-diff-comments.html
public class LicenseHeader implements DDLPatch {
    @Override
    public boolean shouldPatch(String line) {
        return line.startsWith("--**");
    }

    @Override
    public String patch(String line) {
        return String.format("-- %s", line.substring(2));
    }
}
