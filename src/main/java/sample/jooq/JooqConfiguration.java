package sample.jooq;


import org.apache.commons.dbcp2.BasicDataSource;
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
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class JooqConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() throws URISyntaxException {

        URI dbUrl = new URI(System.getenv("DATABASE_URL"));

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(getDatabaseUrl(dbUrl));
        dataSource.setUsername(getUsername(dbUrl));
        dataSource.setPassword(getPassword(dbUrl));
        dataSource.setMinEvictableIdleTimeMillis(30L * 60L * 1000L);
        dataSource.setTimeBetweenEvictionRunsMillis(30L * 60L * 1000L);
        dataSource.setNumTestsPerEvictionRun(3);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(false);
        dataSource.setTestOnReturn(false);
        dataSource.setValidationQuery("SELECT 1");
        return dataSource;
    }

    @Bean
    public DataSourceConnectionProvider dataSourceConnectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(dataSource);
    }

    @Bean
    public DefaultConfiguration defaultConfiguration(DataSource dataSource) {
        System.out.println("*************************here we  go****************");
        System.out.println(System.getenv("PISSU_PUSA"));
        System.out.println("*************************here we end****************");
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

    private String getDatabaseUrl(URI dbUrl) {
        StringBuilder sb = new StringBuilder("jdbc:postgresql://")
                .append(dbUrl.getHost())
                .append(dbUrl.getPath());
        String query = dbUrl.getQuery();
        if (query != null) {
            sb.append("?")
                    .append(dbUrl.getQuery());
        }
        return sb.toString();
    }

    private String getUsername(URI dbUrl) {
        return dbUrl.getUserInfo().split(":")[0];
    }

    private String getPassword(URI dbUrl) {
        return dbUrl.getUserInfo().split(":")[1];
    }

}
