package in.ashwanthkumar.gocd.database.mysql5.migration;

public interface DDLPatch {
    /**
     * Should the current patch be applied to the line?
     *
     * @param line
     * @return true if the current Patch has to be applied, false otherwise
     */
    boolean shouldPatch(String line);

    /**
     * Apply the patch by transforming the given line
     *
     * @param line
     * @return Patched up line
     */
    String patch(String line);
}
