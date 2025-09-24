package io.polaris.core.clock;

/**
 * @author Qt
 */
public interface IdClock {

	long setCurrentTimestamp();

	long setCurrentTimestamp(long timestamp);

	long getCurrentTimestamp();

	long nextTimestamp(long lastTimestamp);
}

