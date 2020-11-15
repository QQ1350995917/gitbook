package pwd.spring.batch.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import pwd.spring.batch.BatchConfig;
import pwd.spring.batch.HelloWorldJobConfig;

/**
 * pwd.spring.batch.test@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-15 15:23
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBatchApplicationTests.BatchTestConfig.class})
public class SpringBatchApplicationTests {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Test
  public void testHelloWorldJob() throws Exception {
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    assert (jobExecution.getExitStatus().getExitCode().equals("COMPLETED"));
  }

  @Configuration
  @Import({BatchConfig.class, HelloWorldJobConfig.class})
  static class BatchTestConfig {

    @Autowired
    private Job helloWorlJob;

    @Bean
    JobLauncherTestUtils jobLauncherTestUtils()
        throws NoSuchJobException {
      JobLauncherTestUtils jobLauncherTestUtils =
          new JobLauncherTestUtils();
      jobLauncherTestUtils.setJob(helloWorlJob);

      return jobLauncherTestUtils;
    }
  }
}
