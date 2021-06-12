package ua.org.gorbounov.sod.emag.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockService {
	@Value("${emag.enabled:false}")
	private Boolean emagEnabled;

	@Value("${emag.path:./}")
	private String mPath;

	@Value("${emag.ftp_server}")
	String server;
	@Value("${emag.ftp_port}")
	int port;
	@Value("${emag.ftp_user}")
	String user;
	@Value("${emag.ftp_password}")
	String password;

	@Async
	@Scheduled(cron = "${emag.stockDiff.cron}")
	public void createAndUploadStockDiffSheduledTask() {
		if (emagEnabled) {
			if (stockFileIsExist()) {
				if (renameStockNewToOld()) {
					log.debug("stock_new в stock_old успешно переименован");
				}else {
					log.error("ошибка при переименовании stock_new в stock_old");
					return;
				}

				if (renameStockToStockNew()) {
					log.debug("stock в stock_new успешно переименован");
				}else {
					log.error("ошибка при переименовании stock в stock_new");
					return;
				}
				diffStocks();
				uploadFileToFtp("stock_dif.csv");
			}
		}
	}

	@Async
	@Scheduled(cron = "${emag.stockNew.cron}")
	public void uploadStockNewScheduledTask() {
		if (emagEnabled) {
			try {
				uploadFileToFtp("stock_new.csv");
			} catch (Exception e) {
				log.error(e);
			}

		}

	}

	/**
	 * Ищет есть ли файл stock.csv.
	 * 
	 * @return File
	 */
	public boolean stockFileIsExist() {
		File f = new File(mPath + "stock.csv");
		if (f.exists()) {
			log.debug(mPath + "stock.csv найден");
			return true;
		} else {
			log.debug(mPath + "stock.csv не найден");
			return false;
		}
	}

	/**
	 * Переименовывает stock_new.csv to stock_old.csv.
	 * 
	 * @return успешно ли прошло переименование
	 */
	public boolean renameStockNewToOld() {
		File f = new File(mPath + "stock_new.csv");
		File fOld = new File(mPath + "stock_old.csv");
		if (f.exists()) {
			log.debug(mPath + "stock_new.csv найден");
			fOld.delete();
			// пытаемся 20 раз переименовать, решаем проблему не понятной блокировки в windows
			boolean res = false;
			for (int i = 0; i < 20; i++) {
			    if (f.renameTo(fOld)) {
			    	res = true;
			        break;}
			    System.gc();
			    Thread.yield();
			}
			if (res) {
				log.debug("stock_new переименован в stock_old успешно");
				return true;
			} else {
				log.debug("stock_new переименовать в stock_old не удалось");
				return false;
			}
		} else {
			log.debug(mPath + "stock_new.csv не найден");
			return false;
		}
	}

	/**
	 * Переименовывает stock.csv to stock_new.csv.
	 * 
	 * @return успешно ли прошло переименование
	 */
	public boolean renameStockToStockNew() {
		File f = new File(mPath + "stock.csv");
		if (f.exists()) {
			log.debug(mPath + "stock.csv найден");
			f.renameTo(new File(mPath + "stock_new.csv"));
			return true;
		} else {
			log.debug(mPath + "stock.csv не найден");
			return false;
		}
	}

	/**
	 * 
	 * 
	 */
	public void diffStocks() {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(mPath + "stock_dif.csv"), ';',
					CSVWriter.NO_QUOTE_CHARACTER);

			CSVReader readerOld = new CSVReader(new FileReader(mPath + "stock_old.csv"), ';', '"', 1);
			CSVReader readerNew = new CSVReader(new FileReader(mPath + "stock_new.csv"), ';', '"', 1);
			// Read all rows at once
			List<String[]> allRowsOld = readerOld.readAll();
			List<String[]> allRowsNew = readerNew.readAll();

			for (String[] lineNew : allRowsNew) {
				boolean findInOld = false;
				for (String[] lineOld : allRowsOld) {
					if (lineOld[0].equals(lineNew[0])) {
						// нашли совпадающие коды, проверяем остаток
						findInOld = true;
						if (lineOld[1].equals(lineNew[1])) {
							// полное совпадение - пропускаем
							log.trace("code=" + lineOld[0] + ", old=" + lineOld[1] + ", new=" + lineNew[1]
									+ "- пропускаем");
						} else {
							// коды совпали, остаток разный
							log.debug("code=" + lineOld[0] + ", old=" + lineOld[1] + ", new=" + lineNew[1]);
							log.trace(lineNew.toString());
							writer.writeNext(lineNew);
						}
					}
				}
				// прошли весь new файл
				if (!findInOld) {
					log.debug("code=" + lineNew[0] + ", ост.=" + lineNew[1]
							+ " - в старом не найден, добавляем из нового");
					writer.writeNext(lineNew);
				}
			}
			readerOld.close();
			readerNew.close();
			writer.close();
			log.trace("stock_diff - записан");
//			return true;
		} catch (FileNotFoundException e) {
			log.error("Файл не обнаружен."+ e);
			log.error(e.getLocalizedMessage());
		}catch (Exception e) {
			log.error(e);
//			return false;
		}
	}

	/**
	 * Выгружает сформированный stock_dif.csv на аез сервер.
	 */
	public void uploadFileToFtp(String mFileName) {
		// todo: установить соединение
		// выгрузить с правильным именем
		// закрыть соединения
		log.trace("начинаем выгрузку "+mFileName+" на сервер");
		try {
			File stockDif = new File(mPath + mFileName);
			if (stockDif.length() == 0) {
				log.trace(mPath + mFileName + ", length="+ stockDif.length() + ", uploaded aborted.");
				return;
			}
			// =======================
//			String server = mProps.getProperty("ftp_server");
//			final int port = 21;
//			String user = mProps.getProperty("ftp_user");
//			String password = mProps.getProperty("ftp_password");

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
			log.trace("Открываем соединение");
			// ================================
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date now = new Date();
			InputStream input = new FileInputStream(mPath + mFileName);
			String outName = "stock_" + formatter.format(now) + ".csv";
			log.trace("outName="+outName);
			boolean res = ftpClient.storeFile(outName, input);
			if (res) {
				log.info("файл " + outName + " выгружен успешно");
			} else {
				log.error("не получилось выгрузить");
				replyes = ftpClient.getReplyStrings();
				if (replyes != null && replyes.length > 0) {
					for (String aReply : replyes) {
						log.debug(aReply);
					}
				}
			}
			// ================================
			ftpClient.disconnect();
		} catch (Exception e) {
			log.error(e);
		}
	}

	@Override
	public String toString() {
		return "StockService [emagEnabled=" + emagEnabled + ", mPath=" + mPath + ", server=" + server + ", port=" + port
				+ ", user=" + user + ", password=" + password + "]";
	}
	@PostConstruct
	public void init() {
		log.info(toString());
	}

}
