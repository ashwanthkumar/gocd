package in.ashwanthkumar.gocd.database.mysql5.migration;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AlterAddNonNullConstraintTest {
    DDLPatch patcher = new AlterAddNonNullConstraint();

    @Test
    public void shouldMatchSetNotNullStatement() {
        assertThat(patcher.shouldPatch("SET NOT NULL;"), is(true));
        assertThat(patcher.shouldPatch("ALTER TABLE materials ALTER COLUMN fingerprint SET NOT NULL;"), is(true));
    }

    @Test
    public void shouldPAtchSetNotNullStatement() {
        assertThat(patcher.patch("SET NOT NULL;"), is("NOT NULL;"));
            assertThat(patcher.patch("ALTER TABLE materials ALTER COLUMN fingerprint SET NOT NULL;"), is("ALTER TABLE materials ALTER COLUMN fingerprint NOT NULL;"));
    }

}