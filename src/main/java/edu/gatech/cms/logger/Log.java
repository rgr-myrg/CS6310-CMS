package edu.gatech.cms.logger;

public final class Log {
	private static boolean debug = false;

	public static final boolean isDebug() {
		return debug;
	}

	public static final void setDebug(boolean debug) {
		Log.debug = debug;
	}
}
