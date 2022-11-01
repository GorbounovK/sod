package ua.org.gorbounov.sod;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.prom.PromControllers;

@Log4j2
public class SodUtils {
	/**
	 * @param duration
	 * @return
	 */
	public static String millisToShortDHMS(long duration) {
		String res = ""; // java.util.concurrent.TimeUnit;
		long days = TimeUnit.MILLISECONDS.toDays(duration);
		long hours = TimeUnit.MILLISECONDS.toHours(duration)
				- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
//		long millis = TimeUnit.MILLISECONDS.toMillis(duration)
//				- TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration));

		if (days == 0)
//			res = String.format("%02d:%02d:%02d.%04d", hours, minutes, seconds, millis);
			res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		else
//			res = String.format("%dd %02d:%02d:%02d.%04d", days, hours, minutes, seconds, millis);
			res = String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);
		return res;
	}

	/**
	 * @param startTime
	 * @param endTime
	 * @return Интервал между событиями в отформатированной строке
	 */
	public static String getDurationToString(long startTime, long endTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
		Instant instStart = Instant.ofEpochMilli(startTime);
		Instant instEnd = Instant.ofEpochMilli(endTime);
		String resStr = LocalTime.ofSecondOfDay(Duration.between(instStart, instEnd).getSeconds()).format(formatter);
		return resStr;
	}

	public static int[] paginatorView(int totalPages, int pageNumber) {
		int[] body;
		if (totalPages > 7) {
//			int totalPages = page.getTotalPages();
//			int pageNumber = page.getNumber() + 1;
			int[] head = (pageNumber > 4) ? new int[] { 1, -1 } : new int[] { 1, 2, 3 };
			int[] bodyBefore = (pageNumber > 4 && pageNumber < totalPages - 1)
					? new int[] { pageNumber - 2, pageNumber - 1 }
					: new int[] {};
			int[] bodyCenter = (pageNumber > 3 && pageNumber < totalPages - 2) ? new int[] { pageNumber }
					: new int[] {};
			int[] bodyAfter = (pageNumber > 2 && pageNumber < totalPages - 3)
					? new int[] { pageNumber + 1, pageNumber + 2 }
					: new int[] {};
			int[] tail = (pageNumber < totalPages - 3) ? new int[] { -1, totalPages }
					: new int[] { totalPages - 2, totalPages - 1, totalPages };
			body = merge(head, bodyBefore, bodyCenter, bodyAfter, tail);
		} else {
			body = new int[totalPages];
			for (int i = 0; i < totalPages; i++) {
				body[i] = 1 + i;
			}
		}
		log.debug("pages view = "+Arrays.toString(body));
		return body;

	}
	
	public static int[] merge(int[]... intarrays) {
	    return Arrays.stream(intarrays).flatMapToInt(Arrays::stream)
	            .toArray();
	}
}
