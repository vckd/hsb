/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.jooq;

import org.hamcrest.Matcher;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.TransactionalRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.boot.test.OutputCapture;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import sample.jooq.repository.PlaygroundRepository;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Integration tests for {@link SampleJooqApplication}.
 */
public class SampleJooqApplicationTests {

	private static final String[] NO_ARGS = {};

	@Rule
	public OutputCapture out = new OutputCapture();
    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();


    @Before
    public void init() {
        EnvironmentTestUtils.addEnvironment(this.context,
                "spring.datasource.name:postgres");
        EnvironmentTestUtils.addEnvironment(this.context, "spring.jooq.sql-dialect:Postgres");
    }

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }


//    @Test
//    public void relaxedBindingOfSqlDialect() {
//        EnvironmentTestUtils.addEnvironment(this.context,"spring.jooq.sql-dialect:PoSTGrES");
//        registerAndRefresh(JooqDataSourceConfiguration.class, JooqAutoConfiguration.class);
//        assertThat(this.context.getBean(org.jooq.Configuration.class).dialect(),
//                is(equalTo(SQLDialect.POSTGRES)));
//    }
//
    @Test
    public void jooqWithoutTx() throws Exception {
        registerAndRefresh(JooqConfiguration.class);
        DSLContext dsl = this.context.getBean(DSLContext.class);

        dsl.execute("create table jooqtest (name varchar(255) primary key);");
        dsl.transaction(new AssertFetch(dsl, "select count(*) as total from jooqtest;",
                equalTo("0")));
        dsl.transaction(new ExecuteSql(dsl, "insert into jooqtest (name) values ('foo');"));
        dsl.transaction(new AssertFetch(dsl, "select count(*) as total from jooqtest;",
                equalTo("1")));
        dsl.transaction(new ExecuteSql(dsl, "delete from jooqtest;"));
        dsl.transaction(new AssertFetch(dsl, "select count(*) as total from jooqtest;",
                equalTo("0")));
        dsl.execute("drop table jooqtest;");

    }
//
//
//    private String[] getBeanNames(Class<?> type) {
//        return this.context.getBeanNamesForType(type);
//    }
//
    @Test
	public void outputResults() throws Exception {
//		SampleJooqApplication.main(NO_ARGS);


//		assertThat(this.out.toString(), containsString("jOOQ Fetch 1 Greg Turnquest"));
//		assertThat(this.out.toString(), containsString("jOOQ Fetch 2 Craig Walls"));
//		assertThat(this.out.toString(), containsString("jOOQ SQL "
//				+ "[Learning Spring Boot : Greg Turnquest, "
//				+ "Spring Boot in Action : Craig Walls]"));
	}




    private void registerAndRefresh(Class<?>... annotatedClasses) {
        this.context.register(annotatedClasses);
        this.context.refresh();
    }

    private static class AssertFetch implements TransactionalRunnable {

        private final DSLContext dsl;

        private final String sql;

        private final Matcher<? super String> matcher;

        public AssertFetch(DSLContext dsl, String sql, Matcher<? super String> matcher) {
            this.dsl = dsl;
            this.sql = sql;
            this.matcher = matcher;
        }

        @Override
        public void run(org.jooq.Configuration configuration) throws Exception {
            assertThat(this.dsl.fetch(this.sql).getValue(0, 0).toString(), this.matcher);
        }

    }

    private static class ExecuteSql implements TransactionalRunnable {

        private final DSLContext dsl;

        private final String[] sql;

        public ExecuteSql(DSLContext dsl, String... sql) {
            this.dsl = dsl;
            this.sql = sql;
        }

        @Override
        public void run(org.jooq.Configuration configuration) throws Exception {
            for (String statement : this.sql) {
                this.dsl.execute(statement);
            }
        }

    }

}
