package pwd.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

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
public class HelloWorldJobConfig {
  @Bean
  public Job helloWorlJob(JobBuilderFactory jobBuilders,
      StepBuilderFactory stepBuilders) {
    return jobBuilders.get("helloWorldJob")
        .start(helloWorldStep(stepBuilders)).build();
  }

  @Bean
  public Step helloWorldStep(StepBuilderFactory stepBuilders) {
    return stepBuilders.get("helloWorldStep")
        .<Person, String>chunk(10).reader(reader())
        .processor(processor()).writer(writer()).build();
  }

  @Bean
  public FlatFileItemReader<Person> reader() {
    return new FlatFileItemReaderBuilder<Person>()
        .name("personItemReader")
        .resource(new ClassPathResource("csv/persons.csv"))
        .delimited().names(new String[] {"firstName", "lastName"})
        .targetType(Person.class).build();
  }

  @Bean
  public PersonItemProcessor processor() {
    return new PersonItemProcessor();
  }

  @Bean
  public FlatFileItemWriter<String> writer() {
    return new FlatFileItemWriterBuilder<String>()
        .name("greetingItemWriter")
        .resource(new FileSystemResource(
            "target/test-outputs/greetings.txt"))
        .lineAggregator(new PassThroughLineAggregator<>()).build();
  }
}
