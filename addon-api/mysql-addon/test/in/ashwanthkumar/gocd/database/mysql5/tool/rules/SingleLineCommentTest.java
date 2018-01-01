package in.ashwanthkumar.gocd.database.mysql5.tool.rules;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SingleLineCommentTest {
    private DDLPatch patcher = new SingleLineComment();

    @Test
    public void shouldPatchOnSingleLineComment() {
        assertThat(patcher.shouldPatch("// Copy the values from a temp table to the new column"), is(true));
    }

    @Test
    public void shouldPatchSingleLineComment() {
        assertThat(patcher.patch("// Copy the values from a temp table to the new column"), is("-- Copy the values from a temp table to the new column"));
    }


}