/**
 * 
 */
package ua.org.gorbounov.sod.prom.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gk
 *
 */

@Component
public class SchedulerService {
	@Autowired
	OrdersService orderService;
	@Autowired
	ExportService exportService;
	@Autowired
	ImagesService imagesService;
	
	@Value("${prom.ua.orders.download.cron}")
	String promOrdersDownloadCron;

	
	@Scheduled(cron = "${prom.ua.orders.download.cron}")
	public void getOrdersSheduledTask() {
		orderService.getOrdersSheduledTask();
	}

	@Scheduled(cron = "${prom.ua.products.upload.cron}")
	public void exportProductSheduledTask() {
		exportService.exportProductSheduledTask();
	}

	@Scheduled(cron = "${prom.ua.products.images.cron}")
	public void exportImagesSheduledTask() {
		imagesService.imagesScheduledTask();
	}
}
