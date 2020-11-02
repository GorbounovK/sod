package ua.org.gorbounov;

import java.util.concurrent.Executor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class DemoApplication {
//	private static Logger LOG = LogManager.getLogger(OrdersTasks.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
        log.info("=================================");
        log.info("Helo SOD!");
        log.info("------- Initialization Complete -----------");
        log.debug("-------debug Initialization Complete -----------");
        log.trace("-------trace Initialization Complete -----------");
        log.error("-------error Initialization Complete -----------");
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

//			LOG.info("Let's inspect the beans provided by Spring Boot:");

//			String[] beanNames = ctx.getBeanDefinitionNames();
//			Arrays.sort(beanNames);
//			for (String beanName : beanNames) {
//				LOG.info(beanName);
//			}

		};
	}
	
	  @Bean
	  public Executor taskExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(2);
	    executor.setMaxPoolSize(2);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("OrderTask-");
	    executor.initialize();
	    return executor;
	  }

}
