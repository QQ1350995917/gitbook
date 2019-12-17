package pwd.mysql.rw;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

/**
 * pwd.mysql.rw@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-17 18:53
 *
 * @author DingPengwei[dingpengwei@eversec.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class MyRoutingDataSource extends AbstractRoutingDataSource {
  @Nullable
  @Override
  protected Object determineCurrentLookupKey() {
    return DBContextHolder.get();
  }
}
