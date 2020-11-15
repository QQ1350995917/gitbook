package pwd.spring.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * pwd.spring.batch@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-15 15:22
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class PersonItemProcessor implements ItemProcessor<Person, String> {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PersonItemProcessor.class);

  @Override
  public String process(Person person) throws Exception {
    String greeting = "Hello " + person.getFirstName() + " "
        + person.getLastName() + "!";

    LOGGER.info("converting '{}' into '{}'", person, greeting);
    return greeting;
  }
}
