package in.ashwanthkumar.gocd.database.mysql5.tool.rules;

import java.util.ArrayList;
import java.util.List;

public class Patcher {
    private static List<DDLPatch> availablePatches = new ArrayList<DDLPatch>() {{
        add(new LicenseHeader());
        add(new IdentityColumns());
        add(new LongVarcharToText());
        add(new ReservedWordWrapper());
        add(new SingleLineComment());
        add(new ConvertToSignedInteger());
        add(new AlterColumn());
        add(new VarcharIgnoreCaseToTextCollate());
    }};

    public static String applyPatch(String line) {
        String patchedSoFar = line;
        for (DDLPatch patcher : availablePatches) {
            if (patcher.shouldPatch(line)) {
                patchedSoFar = patcher.patch(patchedSoFar);
            }
        }
        return patchedSoFar;
    }
}
