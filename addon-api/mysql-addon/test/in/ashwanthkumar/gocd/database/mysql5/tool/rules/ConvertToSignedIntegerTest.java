package in.ashwanthkumar.gocd.database.mysql5.tool.rules;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ConvertToSignedIntegerTest {
    DDLPatch patcher = new ConvertToSignedInteger();

    @Test
    public void shouldMatchConvertStatement() {
        assertThat(patcher.shouldPatch("UPDATE stages SET orderId = CONVERT(id, INTEGER);"), is(true));
    }

    @Test
    public void shouldPatchConvertStatement() {
        assertThat(patcher.patch("UPDATE stages SET orderId = CONVERT(id, INTEGER);"), is("UPDATE stages SET orderId = CONVERT(id, UNSIGNED INTEGER);"));
    }


}