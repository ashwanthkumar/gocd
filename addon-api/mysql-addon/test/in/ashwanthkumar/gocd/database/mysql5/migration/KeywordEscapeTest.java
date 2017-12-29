package in.ashwanthkumar.gocd.database.mysql5.migration;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class KeywordEscapeTest {

    private DDLPatch patcher = new ReservedWordWrapper();;

    @Test
    public void shouldPatchLineOnKeyword() {
        assertThat(patcher.shouldPatch("key VARCHAR(255),"), is(true));
        assertThat(patcher.shouldPatch("UNIQUE (buildId, key)"), is(true));
        assertThat(patcher.shouldPatch("CREATE INDEX idx_properties_key ON properties (key);"), is(true));
        assertThat(patcher.shouldPatch("MERGE INTO stages (id, completedByTransitionId) key(id) select stageId id, id completedByTransitionId from latest_stage_bst;"), is(false));
        assertThat(patcher.shouldPatch("UPDATE materials SET submoduleFolder = (SELECT props.value FROM MaterialProperties props WHERE props.materialId = materials.id AND props.key = 'submoduleFolder');"), is(true));
    }

    @Test
    public void shouldEscapeKeyword() {
        assertThat(patcher.patch("key VARCHAR(255),"), is("`key` VARCHAR(255),"));
        assertThat(patcher.patch("UNIQUE (buildId, key)"), is("UNIQUE (buildId, `key`)"));
        assertThat(patcher.patch("CREATE INDEX idx_properties_key ON properties (key);"), is("CREATE INDEX idx_properties_key ON properties (`key`);"));
        assertThat(patcher.patch("UPDATE materials SET submoduleFolder = (SELECT props.value FROM MaterialProperties props WHERE props.materialId = materials.id AND props.key = 'submoduleFolder');"), is("UPDATE materials SET submoduleFolder = (SELECT props.value FROM MaterialProperties props WHERE props.materialId = materials.id AND props.`key` = 'submoduleFolder');"));
    }


}