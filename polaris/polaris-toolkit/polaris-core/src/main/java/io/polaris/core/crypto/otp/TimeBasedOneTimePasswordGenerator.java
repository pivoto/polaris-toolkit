package io.polaris.core.crypto.otp;

import java.security.InvalidKeyException;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;

/**
 * @author Qt
 * @since 1.8
 */
public class TimeBasedOneTimePasswordGenerator {
	private final OneTimePasswordGenerator otp;
	private final Duration timeStep;

	public TimeBasedOneTimePasswordGenerator() {
		this(Duration.ofSeconds(30));
	}

	public TimeBasedOneTimePasswordGenerator(Duration timeStep) {
		this(timeStep, 6);
	}

	public TimeBasedOneTimePasswordGenerator(Duration timeStep, int length) {
		this(timeStep, length, HmacHash.HmacSHA1);
	}

	public TimeBasedOneTimePasswordGenerator(Duration timeStep, int length, HmacHash algorithm) {
		this.otp = new OneTimePasswordGenerator(length, algorithm);
		this.timeStep = timeStep;
	}

	private long toStepData(Instant timestamp) {
		return timestamp.toEpochMilli() / this.timeStep.toMillis();
	}

	private long toStepData(long timestamp) {
		return timestamp / this.timeStep.toMillis();
	}

	public boolean verify(Key key, Instant timestamp, int code) throws InvalidKeyException {
		return verify(key, timestamp.toEpochMilli(), code);
	}

	public boolean verify(Key key, long timestamp, int code) throws InvalidKeyException {
		long data = toStepData(timestamp);
		for (int i = -1; i <= 1; i++) {
			int generated = otp.generate(key, data + i);
			if (generated == code) {
				return true;
			}
		}
		return false;
	}

	public boolean verify(String key, Instant timestamp, int code) throws InvalidKeyException {
		return verify(key, timestamp.toEpochMilli(), code);
	}

	public boolean verify(String key, long timestamp, int code) throws InvalidKeyException {
		long data = toStepData(timestamp);
		for (int i = -1; i <= 1; i++) {
			int generated = otp.generate(key, data + i);
			if (generated == code) {
				return true;
			}
		}
		return false;
	}

	public int generate(Key key, Instant timestamp) throws InvalidKeyException {
		return otp.generate(key, toStepData(timestamp));
	}

	public int generate(Key key, long timestamp) throws InvalidKeyException {
		return otp.generate(key, toStepData(timestamp));
	}

	public int generate(String key, Instant timestamp) throws InvalidKeyException {
		return otp.generate(key, toStepData(timestamp));
	}

	public int generate(String key, long timestamp) throws InvalidKeyException {
		return otp.generate(key, toStepData(timestamp));
	}

	public String generateString(Key key, Instant timestamp) throws InvalidKeyException {
		return otp.generateString(key, toStepData(timestamp));
	}

	public String generateString(Key key, long timestamp) throws InvalidKeyException {
		return otp.generateString(key, toStepData(timestamp));
	}

	public String generateString(String key, Instant timestamp) throws InvalidKeyException {
		return otp.generateString(key, toStepData(timestamp));
	}

	public String generateString(String key, long timestamp) throws InvalidKeyException {
		return otp.generateString(key, toStepData(timestamp));
	}

	public String generateKeyString() {
		return otp.generateKeyString();
	}

	public String generateKeyString(int size) {
		return otp.generateKeyString(size);
	}

	public Duration getTimeStep() {
		return timeStep;
	}

	public int getLength() {
		return otp.getLength();
	}

	public String getAlgorithm() {
		return otp.getAlgorithm();
	}
}
