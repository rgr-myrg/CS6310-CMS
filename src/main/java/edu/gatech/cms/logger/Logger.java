package edu.gatech.cms.logger;

public final class Logger {
	public static void debug(final String tag, final Object object) {
		if (object == null) {
			return;
		}

		System.out.println(String.format("[%s] %s", tag, String.valueOf(object)));
	}
}
