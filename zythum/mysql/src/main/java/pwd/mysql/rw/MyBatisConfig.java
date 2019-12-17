package pwd.mysql.rw;

import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * pwd.mysql.rw@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-17 18:49
 *
 * @author DingPengwei[dingpengwei@eversec.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@EnableTransactionManagement
@Configuration
public class MyBatisConfig {
  @Resource(name = "myRoutingDataSource")
  private DataSource myRoutingDataSource;

  @Bean
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(myRoutingDataSource);
    sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
    return sqlSessionFactoryBean.getObject();
  }

  @Bean
  public PlatformTransactionManager platformTransactionManager() {
    return new DataSourceTransactionManager(myRoutingDataSource);
  }
}
