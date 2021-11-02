/**
 * 
 */
package ua.org.gorbounov.sod.prom.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.NoRouteToHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.Utils;
import ua.org.gorbounov.sod.models.ImageEntity;
import ua.org.gorbounov.sod.prom.repositories.ImageRepositories;

/**
 * @author gk Реализует непосредственную работу с изображениями
 */
@Log4j2
@Service
public class ImagesService {
	@Value("${prom.ua.images.local.path}")
	String pathImages;
	@Value("${prom.ua.images.ftp.server}")
	String server;
	@Value("${prom.ua.images.ftp.port}")
	int port;
	@Value("${prom.ua.images.ftp.user}")
	String user;
	@Value("${prom.ua.images.ftp.password}")
	String password;

	@Value("${prom.ua.products.images.cron}")
	String promImagesDownloadCron;

	@Autowired
	private ImageRepositories repository;
	ImageEntity imageEntity;
	/**
	 * 
	 */
	@Async("threadPoolTaskExecutor")
	public void imagesScheduledTask() {
		log.debug("------- imagesScheduledTask start -----------");
		long startTime = System.currentTimeMillis();
		imageEntity = new ImageEntity();
		imageEntity.setLastUpload(new Date());
		String resStringTotal="";
		String resString="";
		try {
//		StopWatch timer = new StopWatch();
			// получаем время последнего запуска
			// временно, СЕГОДНЯ

			// получаем список файлов, измененых со времени последнего запуска
			List<Path> filesInFolder = getModifcatedImages();

			// выгружаем эти файлы на ФТП и регистрируем событие в БД
			FTPClient ftpClient = createFtpConnect();
			for (Path fileEntry : filesInFolder) {
				BasicFileAttributes attrs;
				attrs = Files.readAttributes(fileEntry, BasicFileAttributes.class);
				log.debug("name {}, modificated {}", fileEntry.getFileName(), attrs.lastModifiedTime());
				resString = uploadImageToFtp(ftpClient, fileEntry.toFile(), fileEntry.getFileName().toString());
				resStringTotal = resStringTotal+resString+"<br>";
//				imageEntity.setResultExecution(imageEntity.getResultExecution()+resString);
				log.trace("resStringTotal {}",resStringTotal);
			}
			ftpClient.logout();
			ftpClient.disconnect();
			log.debug("ftpClient.disconnect");
		} catch (NoRouteToHostException e) {
			log.error("Сервер {} не доступен",server);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Выгрузка изображений завершилась успешно");
		long endTime = System.currentTimeMillis();
		log.debug("endTime {} - startTime {}", endTime, startTime);
		long executionTime = endTime - startTime;
		String durationString = Utils.millisToShortDHMS(executionTime);
		log.info("Total execution time: " + durationString);
		imageEntity.setExecutionTime(durationString);
		imageEntity.setResultExecution(resStringTotal);
		repository.save(imageEntity);
		log.trace("imageEntity={}",imageEntity.toString());
		log.debug("------- imagesScheduledTask end -----------");
		//		repository.
	}

	private FTPClient createFtpConnect() throws IOException {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.connect(server, port);
		int reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// не получилось
			ftpClient.disconnect();
			throw new IOException("Не удалось подключиться к серверу.");
		}

		ftpClient.login(user, password);
		String[] replyes = ftpClient.getReplyStrings();
		if (replyes != null && replyes.length > 0) {
			for (String aReply : replyes) {
				log.debug(aReply);
			}
		}

		log.trace("К серверу успешло залогинились соединение");
		ftpClient.enterLocalPassiveMode();
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		log.trace("Открываем соединение");
		return ftpClient;
	}

	/**
	 * 
	 */
	public List<Path> getModifcatedImages() throws IOException {
		LocalDateTime lastRun = LocalDateTime.of(2020, 8, 20, 0, 0, 0);
		List<Path> filesInFolder = new ArrayList<>();
		try {
			// Instant instant = lastRun.atZone(ZoneId.of("Europe/Paris")).toInstant();
			Instant instant = Instant.now().minus(24, ChronoUnit.HOURS); // проверяем файлы за сутки
			log.debug("prom.ua.images.local.path={}", pathImages);
			filesInFolder = findByLastModifiedTime(Paths.get(pathImages), instant); // Instant.now().minus(1,
																					// ChronoUnit.DAYS));

//			for (Path fileEntry : filesInFolder) {
//				BasicFileAttributes attrs = Files.readAttributes(fileEntry, BasicFileAttributes.class);
//				log.debug("name {}, modificated {}", fileEntry.getFileName(), attrs.lastModifiedTime());
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filesInFolder;
	}

	public static List<Path> findByLastModifiedTime(Path path, Instant instant) throws IOException {

		List<Path> result;
		try (Stream<Path> pathStream = Files.find(path, Integer.MAX_VALUE, (p, basicFileAttributes) -> {

			if (Files.isDirectory(p) || !Files.isReadable(p)) {
				return false;
			}

			FileTime fileTime = basicFileAttributes.lastModifiedTime();
			// negative if less, positive if greater
			// 1 means fileTime equals or after the provided instant argument
			// -1 means fileTime before the provided instant argument
			int i = fileTime.toInstant().compareTo(instant);
			return i > 0;
		}

		)) {
			result = pathStream.collect(Collectors.toList());
		}
		return result;

	}

	/**
	 * выгружает изображени на удаленный ftp сервер
	 */
	public String uploadImageToFtp(FTPClient ftpClient, File imageFile, String remoteName)
			throws FileNotFoundException, IOException {
		String resString="";
		InputStream is = new FileInputStream(imageFile);
		String remoteDirectory = "/images/" + remoteName.substring(3, 6);
		log.trace("remoteDirectory = {}", remoteDirectory);
		if (checkDirectoryExists(ftpClient, remoteDirectory)) {
			log.trace("папка {} существут", remoteDirectory);
		} else {
			boolean success = ftpClient.makeDirectory(remoteDirectory);
			if (success) {
				log.trace("папка {} создана", remoteDirectory);
			}
		}
		log.trace("выгружаем {}",remoteDirectory+"/"+remoteName);
		boolean res = ftpClient.storeFile(remoteDirectory+"/"+remoteName, is);
		if (res) {
			log.info("файл " + remoteDirectory+"/"+remoteName + " выгружен успешно");
			resString = remoteDirectory+"/"+remoteName + " выгружен";
		} else {
			log.error("не получилось выгрузить");
			resString = "не получилось выгрузить "+remoteName;
			String[] replyes = ftpClient.getReplyStrings();
			if (replyes != null && replyes.length > 0) {
				for (String aReply : replyes) {
					log.debug(aReply);
				}
			}
		}
		is.close();
		return resString;
	}

	boolean checkDirectoryExists(FTPClient ftpClient, String dirPath) throws IOException {
		ftpClient.changeWorkingDirectory(dirPath);
		int returnCode = ftpClient.getReplyCode();
		if (returnCode == 550) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return "imagesScheduledTask " + System.lineSeparator() + "prom.ua.products.images.cron = " + promImagesDownloadCron
				+ System.lineSeparator() + "prom.ua.images.local.path =" + pathImages + System.lineSeparator()
				+ "----------------";
	}
	
	@PostConstruct
	public void init() {
		log.info(toString());
	}
}
