package ua.org.gorbounov.sod;

import java.util.concurrent.TimeUnit;

public class Utils {
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
}
