package ua.org.gorbounov.sod;

import java.util.concurrent.Executor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//import de.codecentric.boot.admin.server.config.EnableAdminServer;
import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@EnableAsync
public class SodApplication {
//	private static Logger LOG = LogManager.getLogger(OrdersTasks.class);

	public static void main(String[] args) {
		SpringApplication.run(SodApplication.class, args);
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
	    executor.setCorePoolSize(6);
	    executor.setMaxPoolSize(6);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("OrderTask-");
	    executor.initialize();
	    return executor;
	  }

}
