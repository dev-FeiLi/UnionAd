package io.union.admin.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Administrator on 2017/4/10.
 */
@Configuration
public class DataSourceConfig {

    private Properties buildHibernateProperties(String showsql) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.ddl-auto", "nonoe");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        properties.setProperty("hibernate.show_sql", showsql);
        properties.setProperty("hibernate.format_sql", "true");
        //properties.setProperty("hibernate.cache.use_query_cache","true");
        //properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        //properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.EhCacheRegionFactory");
        //properties.setProperty("net.sf.ehcache.configurationResourceName", "ehcache.xml");
        properties.setProperty("hibernate.cache.use_structured_entries", "true");
        properties.setProperty("hibernate.generate_statistics", "true");
        properties.setProperty("hibernate.after_transaction", "after_transaction");
        return properties;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean(name = "datasource")
    @Primary
    public DataSource dataSource(@Value("${spring.datasource.url}") String url,
                                 @Value("${spring.datasource.username}") String username,
                                 @Value("${spring.datasource.password}") String password,
                                 @Value("${spring.datasource.driver-class-name}") String classname,
                                 @Value("${spring.datasource.max-active}") int maxactive) {
        DruidDataSource source = new DruidDataSource();
        source.setMaxActive(maxactive);
        source.setDriverClassName(classname);
        source.setPassword(password);
        source.setUsername(username);
        source.setUrl(url);
        source.setInitialSize(10);
        source.setTestOnBorrow(true);
        source.setTestOnReturn(false);
        source.setTestWhileIdle(true);
        source.setValidationQuery("select 1");
        source.setMaxWait(60000);
        source.setMinIdle(1);
        source.setTimeBetweenEvictionRunsMillis(60000);
        source.setMinEvictableIdleTimeMillis(300000);
        source.setKeepAlive(true);
        return source;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("datasource") DataSource dataSource,
                                                                       @Value("${hibernate.package-to-scan}") String packageToScan,
                                                                       @Value("${spring.jpa.show-sql}") String showsql) {
        String[] packages = {packageToScan};
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan(packages);
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factory.setJpaProperties(buildHibernateProperties(showsql));
        factory.afterPropertiesSet();
        return factory;
    }
}
