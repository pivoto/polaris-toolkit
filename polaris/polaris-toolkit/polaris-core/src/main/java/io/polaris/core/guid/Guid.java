package io.polaris.core.guid;



import io.polaris.core.clock.TimeMillisClock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Qt
 * @since 1.8
 */
public class Guid {
	private static final String ZERO_PADDING_64 = "0000000000000000000000000000000000000000000000000000000000000000";
	private static final String ZERO_PADDING_16 = "0000000000000000";
	private static final String ZERO_PADDING_20 = "00000000000000000000";

	/** 首位符号位不用 */
	private final long unusedBits = 1L;
	/** 时间戳位数, 41位可使用69年, 40位可使用34.9年 */
	private final long timestampBits/* = 41L*/;
	/** 工作节点位数 */
	private final long workerIdBits/* = 10L*/;
	/** 序列号位数 */
	private final long sequenceBits/* = 12L*/;
	/** 时间戳移位位数 */
	private final long timestampShift;
	/** 工作节点移位位数 */
	private final long workerIdShift;
	/** 序列号移位位数 */
	private final long sequenceShift;
	/** 时间戳起始值 2021-08-01 1627747200000L */
	private final long epoch = 1627747200000L;
	/** 工作节点 max: 2^5-1 range: [0,31] */
	private final long workerId;
	/** 序列号最大值  2^12-1 */
	private final long maxSequence;
	private final long startTimestamp;
	private final Lock lock = new ReentrantLock();
	private GuidNodeStrategy strategy;
	private long lastTimestamp = -1L;
	/** 序列号  max: 2^12-1 range: [0,4095] */
	private long sequence = 0L;

	Guid(GuidNodeStrategy strategy) {
		if (strategy == null) {
			final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
			final String app = traces[Integer.min(3, traces.length - 1)].getClassName();
			strategy = LocalNodeStrategy.getInstance(app);
		}
		this.strategy = strategy;

		timestampBits = 41L;
		workerIdBits = Long.max(strategy.bitSize(), 12L);
		sequenceBits = 63 - timestampBits - workerIdBits;

		timestampShift = workerIdBits + sequenceBits;
		sequenceShift = workerIdBits;
		workerIdShift = 0;
		workerId = strategy.nodeId();
		maxSequence = -1L ^ (-1L << sequenceBits);
		startTimestamp = System.currentTimeMillis();
	}


	public static Guid newInstance() {
		return new Guid(null);
	}

	public static Guid newInstance(String app) {
		return new Guid(LocalNodeStrategy.getInstance(app));
	}

	public static Guid newInstance(GuidNodeStrategy strategy) {
		return new Guid(strategy);
	}

	public static String bin(long d) {
		String s = Long.toBinaryString(d);
		return ZERO_PADDING_64.substring(s.length()) + s;
	}

	public static String dec(long d) {
		String s = Long.toString(d);
		return ZERO_PADDING_20.substring(s.length()) + s;
	}

	public static String hex(long d) {
		String s = Long.toHexString(d);
		return ZERO_PADDING_16.substring(s.length()) + s;
	}


	public long next() {
		long sequence;
		long currTimestamp = System.currentTimeMillis();
		lock.lock();
		try {
			// 允许时钟短暂回拨
			if (currTimestamp <= lastTimestamp) {
				currTimestamp = lastTimestamp;
				sequence = this.sequence = (this.sequence + 1) & maxSequence;
				if (sequence == 0) {
					currTimestamp = ++lastTimestamp;
				}
			} else {
				sequence = this.sequence = 0L;
				lastTimestamp = currTimestamp;
			}
		} finally {
			lock.unlock();
		}
		return ((currTimestamp - epoch) << timestampShift) | (workerId << workerIdShift) | (sequence << sequenceShift);
	}

	public String nextBin() {
		return bin(next());
	}

	public String nextDec() {
		return dec(next());
	}

	public String nextHex() {
		return hex(next());
	}

	public long getWorkerId() {
		return workerId;
	}

	public long getMaxSequence() {
		return maxSequence;
	}

	public long getSequence() {
		return sequence;
	}

	public long getEpoch() {
		return this.epoch;
	}

	public long[] parse(long id) {
		long[] arr = new long[4];
		arr[3] = (((~(-1L << timestampBits)) << timestampShift) & id) >> timestampShift;
		arr[0] = arr[3] + epoch;
		arr[1] = (((~(-1L << workerIdBits)) << workerIdShift) & id) >> workerIdShift;
		arr[2] = (((~(-1L << sequenceBits)) << sequenceShift) & id) >> sequenceShift;
		return arr;
	}

	public String format(long id) {
		long[] arr = parse(id);
		String tmf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(arr[0]));
		return String.format("%s, #%d, @%d", tmf, arr[2], arr[1]);
	}
}
