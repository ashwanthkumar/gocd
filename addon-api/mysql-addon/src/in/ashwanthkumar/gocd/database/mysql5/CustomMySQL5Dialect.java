/*
 * Copyright 2017 ThoughtWorks, Inc.
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

package in.ashwanthkumar.gocd.database.mysql5;

import org.hibernate.dialect.MySQL5Dialect;

import java.sql.Types;

import org.hibernate.Hibernate;


public class CustomMySQL5Dialect extends MySQL5Dialect {
    public CustomMySQL5Dialect() {
        super();
        // register additional hibernate types for default use in scalar sqlquery type auto detection
        // http://opensource.atlassian.com/projects/hibernate/browse/HHH-1483
        registerHibernateType(Types.LONGVARCHAR, Hibernate.TEXT.getName());
    }
}
