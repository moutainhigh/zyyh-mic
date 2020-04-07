package com.jkys.zyyh.mic.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.jkys.zyyh.mic.dao.handler.AutoEnumTypeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * <Description> <br>
 *
 * @author Rocky<br>
 * @version 1.0<br>
 * @createDate 2019/12/03 4:16 下午 <br>
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Autowired
    private GlobalConfig globalConfig;

    @Bean(name = "dataSource")
    public DataSource dataSource() throws Exception {
        String dbUrl = globalConfig.getDbUrl();
        //数据库tinyint(1) 禁止返回boolean
        if (dbUrl.indexOf("?") >= 0) {
            dbUrl = dbUrl + "&tinyInt1isBit=false";
        }
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(globalConfig.getUserName());
        dataSource.setPassword(globalConfig.getPassword());
        dataSource.setDriverClassName(globalConfig.getDriverClass());
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        dataSource.setMaxWait(60000);
        dataSource.setUseGlobalDataSourceStat(true);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        return dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // 注册默认枚举转换器
        configuration.setDefaultEnumTypeHandler(AutoEnumTypeHandler.class);
        //自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN 到经典 Java 属性名 aColumn 的类似映射。
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);

        return bean.getObject();
    }

    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
