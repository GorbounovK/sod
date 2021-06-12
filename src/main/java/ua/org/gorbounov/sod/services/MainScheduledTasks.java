package ua.org.gorbounov.sod.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class MainScheduledTasks {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(cron = "${cron.expression}")
	public void scheduleTaskUsingCronExpression() {

		log.info("The time is now {}", dateFormat.format(new Date()));
	}
}
