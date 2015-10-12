package sample.jooq;


import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class JooqConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public DataSourceConnectionProvider dataSourceConnectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(dataSource);
    }

    @Bean
    public DefaultConfiguration defaultConfiguration(DataSource dataSource) {
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
        String sqlDialect = environment.getRequiredProperty("spring.jooq.sqlDialect");
        SQLDialect dialect = SQLDialect.valueOf(sqlDialect);
        defaultConfiguration.setSQLDialect(dialect);
        defaultConfiguration.setConnectionProvider(dataSourceConnectionProvider(dataSource));
        defaultConfiguration.setExecuteListenerProvider(new DefaultExecuteListenerProvider(jooqExceptionTranslator()));
        return defaultConfiguration;
    }

    @Bean
    public DefaultDSLContext dsl(DataSource dataSource) {
        return new DefaultDSLContext(defaultConfiguration(dataSource));
    }

    @Bean
    public JooqExceptionTranslator jooqExceptionTranslator(){
        return new JooqExceptionTranslator();
    }

}
