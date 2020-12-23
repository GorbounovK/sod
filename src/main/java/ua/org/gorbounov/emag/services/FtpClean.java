package ua.org.gorbounov.emag.services;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class FtpClean {

	@Value("${emag.ftp_server}")
	String server;
	@Value("${emag.ftp_port}")
	int port;
	@Value("${emag.ftp_user}")
	String user;
	@Value("${emag.ftp_password}")
	String password;

	@Async
	@Scheduled(cron="${emag.clean.cron}")
	public void ftpCleanScheduledTask() {
		log.info("------- Запущен поток FtpClean -------");
		try {

			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(server, port);
			ftpClient.login(user, password);
//		ftpClient.open();
//		ftpClient.showServerReply();
			log.trace("Открываем соединение");
			// ==============
			FTPFileFilter filter = new FTPFileFilter() {

				public boolean accept(final FTPFile file) {
					return (file.isFile() && file.getName().contains("processed_products"));
				}
			};

			log.trace("Получаем файлы");
			FTPFile[] files = ftpClient.listFiles("/", filter);
			if (files.length == 0) {
				log.debug("Нет processed_products* для удаления.");
			}

			for (FTPFile file : files) {
				String details = file.getName();
//			LOG.debug(details);
				boolean deleted = ftpClient.deleteFile(details);
				if (deleted) {
					log.info(details + " удален успешно.");
				} else {
					log.error(details + " удалить не получилось.");
				}
			}

			// ===============
			// отбираем jpg старше 120 минут
			FTPFileFilter filterJpg = new FTPFileFilter() {

				public boolean accept(final FTPFile file) {
					Calendar c1 = Calendar.getInstance(); // today
					c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday
					Date yes = c1.getTime();

					return (file.isFile() && file.getName().matches(".*(jpg|jpeg).*")
							&& file.getTimestamp().getTime().before(yes));
				}
			};
			FTPFile[] filesJpg = ftpClient.listFiles("/", filterJpg);
			if (filesJpg.length == 0) {
				log.debug("Нет изображений для удаления.");
			}
			for (FTPFile jpg : filesJpg) {
				String details = jpg.getName();

				boolean deleted = ftpClient.deleteFile(details);
				if (deleted) {
					log.info(details + " удален успешно.");
				} else {
					log.error(details + " удалить не получилось.");
				}
			}

			ftpClient.disconnect();
//		ftpClient.showServerReply();
			log.trace("Good bye.");
//		}
		} catch (Exception e) {
			log.error(e);
		}
		log.debug("------- Завершен поток FtpClean -------");

	}
}
