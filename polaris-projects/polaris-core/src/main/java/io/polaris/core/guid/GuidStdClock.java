//package io.polaris.core.guid;
//
//
///**
// * @author Qt
// * @since 1.8
// */
//public class GuidStdClock implements GuidClock {
//
//	@Override
//	public long currentTimestamp() {
//		return System.currentTimeMillis();
//	}
//
//	@Override
//	public long nextTimestamp(long lastTimestamp) {
//		long currTimestamp = lastTimestamp;
//		while (currTimestamp <= lastTimestamp) {
//			currTimestamp = currentTimestamp();
//		}
//		return currTimestamp;
//	}
//}
