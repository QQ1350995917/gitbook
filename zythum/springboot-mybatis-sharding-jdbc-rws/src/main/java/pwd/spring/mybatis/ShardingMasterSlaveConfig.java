package pwd.spring.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import java.util.HashMap;
import java.util.Map;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * pwd.spring.mybatis@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-28 23:33
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@ConfigurationProperties(prefix = "sharding.jdbc")
public class ShardingMasterSlaveConfig {

  private Map<String, DruidDataSource> dataSources = new HashMap<>();

  private MasterSlaveRuleConfiguration masterSlaveRule;

  public Map<String, DruidDataSource> getDataSources() {
    return dataSources;
  }

  public void setDataSources(Map<String, DruidDataSource> dataSources) {
    this.dataSources = dataSources;
  }

  public MasterSlaveRuleConfiguration getMasterSlaveRule() {
    return masterSlaveRule;
  }

  public void setMasterSlaveRule(MasterSlaveRuleConfiguration masterSlaveRule) {
    this.masterSlaveRule = masterSlaveRule;
  }
}
