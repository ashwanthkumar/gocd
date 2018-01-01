/*
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.ashwanthkumar.gocd.database.mysql5.tool.rules;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AlterColumnTest {
    DDLPatch patcher = new AlterColumn();

    @Test
    public void shouldMatchAlterColumnStatement() {
        assertTrue(patcher.shouldPatch("ALTER TABLE stages\n" +
                "ALTER COLUMN result\n" +
                "SET DEFAULT 'Unknown';\n"));
    }

    @Test
    public void shouldPatchAlterColumnStatements() {
        String patched = patcher.patch("ALTER TABLE stages\n" +
                "ALTER COLUMN result\n" +
                "SET DEFAULT 'Unknown';\n");

        String expected = patcher.patch("ALTER TABLE stages\n" +
                "MODIFY COLUMN result\n" +
                "SET DEFAULT 'Unknown';\n");

        assertThat(patched, is(expected));
    }


}