package pwd.spring.batch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

/**
 * pwd.spring.batch@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-15 15:20
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfigurer {

  @Override
  public void setDataSource(DataSource dataSource) {
    // initialize will use a Map based JobRepository (instead of database)
  }
}
