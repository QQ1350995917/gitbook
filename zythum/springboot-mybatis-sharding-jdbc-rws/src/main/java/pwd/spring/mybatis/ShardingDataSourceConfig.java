package pwd.spring.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * pwd.spring.mybatis@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-28 23:34
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Configuration
@EnableConfigurationProperties(ShardingMasterSlaveConfig.class)
@ConditionalOnProperty({"sharding.jdbc.data-sources.ds_master.url",
    "sharding.jdbc.master-slave-rule.master-data-source-name"})
public class ShardingDataSourceConfig {

  private Logger logger = LoggerFactory.getLogger(ShardingDataSourceConfig.class);

  @Autowired(required = false)
  private ShardingMasterSlaveConfig shardingMasterSlaveConfig;

  @Bean("dataSource")
  public DataSource masterSlaveDataSource() throws SQLException {
    shardingMasterSlaveConfig.getDataSources().forEach((k, v) -> configDataSource(v));
    Map<String, DataSource> dataSourceMap = Maps.newHashMap();
    dataSourceMap.putAll(shardingMasterSlaveConfig.getDataSources());
    DataSource dataSource = MasterSlaveDataSourceFactory
        .createDataSource(dataSourceMap, shardingMasterSlaveConfig.getMasterSlaveRule(),
            new Properties());
    logger.info("masterSlaveDataSource config complete");
    return dataSource;
  }

  private void configDataSource(DruidDataSource druidDataSource) {
    druidDataSource.setMaxActive(20);
    druidDataSource.setInitialSize(1);
    druidDataSource.setMaxWait(60000);
    druidDataSource.setMinIdle(1);
    druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
    druidDataSource.setMinEvictableIdleTimeMillis(300000);
    druidDataSource.setValidationQuery("select 'x'");
    druidDataSource.setTestWhileIdle(true);
    druidDataSource.setTestOnBorrow(false);
    druidDataSource.setTestOnReturn(false);
    druidDataSource.setPoolPreparedStatements(true);
    druidDataSource.setMaxOpenPreparedStatements(20);
    druidDataSource.setUseGlobalDataSourceStat(true);
    try {
      druidDataSource.setFilters("stat,wall,slf4j");
    } catch (SQLException e) {
      logger.error("druid configuration initialization filter", e);
    }
  }
}
