package io.polaris.core.os;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.StdKeys;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.regex.Patterns;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("All")
public class OS {

	public static final String KEY_IP_REGEX = "ip.regex";
	private static volatile int PID = -1;
	private static volatile String LOCAL_HOST_IP;
	private static volatile List<String> CACHE_ALL_IPS;
	private static volatile String CACHE_IP;
	private static volatile String CACHE_FIRST_IP;
	private static volatile String OS_NAME;
	private static volatile String HOST_NAME;
	private static volatile long VM_START_TIME = -1;

	public static String getOsName() {
		if (OS_NAME == null) {
			OS_NAME = System.getProperty(StdKeys.OS_NAME);
		}
		return OS_NAME;
	}

	public static OsType getOsType() {
		String osName = getOsName().toLowerCase();
		if (osName.contains("win"))
			return OsType.WINDOWS;
		else if (osName.contains("mac"))
			return OsType.MAC;
		else if ((osName.contains("nix")) || (osName.contains("nux")))
			return OsType.LINUX;
		else if (osName.contains("sunos"))
			return OsType.SOLARIS;
		else
			return OsType.UNKOWN;
	}

	public static String getHostName() {
		if (HOST_NAME == null) {
			try {
				InetAddress host = InetAddress.getLocalHost();
				HOST_NAME = host.getHostName();
			} catch (UnknownHostException e) {
				HOST_NAME = StdConsts.UNKNOWN;
			}
		}
		return HOST_NAME;
	}

	public static String getLocalHostIp() {
		if (null != LOCAL_HOST_IP) {
			return LOCAL_HOST_IP;
		}
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			ip = StdConsts.UNKNOWN;
		}
		LOCAL_HOST_IP = ip;
		return ip;
	}

	public static String getIp() {
		if (null != CACHE_IP) {
			return CACHE_IP;
		}
		String ipRegex = GlobalStdEnv.get(KEY_IP_REGEX);
		if (Strings.isNotBlank(ipRegex)) {
			// 从系统属性中取优先IP范式，存在则使用
			String[] arr = Strings.delimitedToArray(ipRegex, ",");
			if (arr != null && arr.length > 0) {
				String ip = getPriorIp(arr);
				if (Strings.isNotBlank(ip)) {
					CACHE_IP = ip;
					return ip;
				}
			}
		}
		try {
			String localIpAddress = null;
			Enumeration<NetworkInterface> es = NetworkInterface.getNetworkInterfaces();
			while (es.hasMoreElements()) {
				NetworkInterface ni = es.nextElement();
				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress ipAddress = inetAddresses.nextElement();
					if (!ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress)) {
						String publicIpAddress = ipAddress.getHostAddress();
						CACHE_IP = publicIpAddress;
						return publicIpAddress;
					}
					if (ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress)) {
						localIpAddress = ipAddress.getHostAddress();
					}
				}
			}
			if (localIpAddress == null) {
				localIpAddress = InetAddress.getLocalHost().getHostAddress();
			}
			CACHE_IP = localIpAddress;
			return localIpAddress;
		} catch (Throwable e) {
			return null;
		}
	}

	public static String getFirstIp() {
		if (CACHE_FIRST_IP != null) {
			return CACHE_FIRST_IP;
		}
		List<String> ips = getAllIps();
		if (ips.size() > 0) {
			CACHE_FIRST_IP = ips.get(0);
		}
		return CACHE_FIRST_IP;
	}

	public static String getPriorIp(String... regex) {
		List<String> ips = getAllIps();
		for (String re : regex) {
			Pattern pattern = Patterns.getPattern(re);
			for (String ip : ips) {
				if (pattern.matcher(ip).find()) {
					return ip;
				}
			}
		}
		return null;
	}

	public static String getPriorOrFirstIp(String... regex) {
		List<String> ips = getAllIps();
		for (String re : regex) {
			Pattern pattern = Patterns.getPattern(re);
			for (String ip : ips) {
				if (pattern.matcher(ip).find()) {
					return ip;
				}
			}
		}
		if (ips.size() > 0) {
			return ips.get(0);
		}
		return null;
	}

	public static List<String> getAllIps() {
		if (CACHE_ALL_IPS != null) {
			return CACHE_ALL_IPS;
		}
		List<String> ips = new ArrayList<>();
		try {
			Comparator<InetAddress> comparator = (o1, o2) -> {
				byte[] bytes1 = o1.getAddress();
				byte[] bytes2 = o2.getAddress();
				if (bytes1.length < bytes2.length) {
					return -1;
				}
				for (int i = 0; i < bytes1.length; i++) {
					// 用int优化排序，byte排序不适合大于127的值
					int b1 = 0xFF & bytes1[i];
					int b2 = 0xFF & bytes2[i];
					int compared = Integer.compare(b1, b2);
					if (compared != 0) {
						return compared;
					}
				}
				return 0;
			};
			Set<InetAddress> publicIpv4Set = new TreeSet<>(comparator);
			Set<InetAddress> localIpV4Set = new TreeSet<>(comparator);
			Enumeration<NetworkInterface> es = NetworkInterface.getNetworkInterfaces();
			while (es.hasMoreElements()) {
				NetworkInterface ni = es.nextElement();
				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress ipAddress = inetAddresses.nextElement();
					if (!ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress)) {
						publicIpv4Set.add(ipAddress);
					}
					if (ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress)) {
						localIpV4Set.add(ipAddress);
					}
				}
			}
			if (localIpV4Set.isEmpty()) {
				localIpV4Set.add(InetAddress.getLocalHost());
			}
			for (InetAddress inetAddress : publicIpv4Set) {
				ips.add(inetAddress.getHostAddress());
			}
			for (InetAddress inetAddress : localIpV4Set) {
				ips.add(inetAddress.getHostAddress());
			}
		} catch (Throwable ignored) {
		}
		CACHE_ALL_IPS = ips;
		return ips;
	}


	private static boolean isV6IpAddress(final InetAddress ipAddress) {
		return ipAddress instanceof Inet6Address || ipAddress.getHostAddress().contains(":");
	}

	public static int getPid() {
		if (PID == -1) {
			synchronized (OS.class) {
				if (PID == -1) {
					final String pidStr = getPidStr();
					try {
						PID = Integer.parseInt(pidStr);
					} catch (NumberFormatException e) {
						PID = 0;
					}
				}
			}
		}
		return PID;
	}

	public static String getPidStr() {
		try {
			return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		} catch (final Exception e) {
			try {
				return new File("/proc/self").getCanonicalFile().getName();
			} catch (final IOException ignored) {
			}
		}
		return "-";
	}


	public static long getVmStartTime() {
		if (VM_START_TIME == -1) {
			try {
				VM_START_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
			} catch (Exception e) {
				VM_START_TIME = 0;
			}
		}
		return VM_START_TIME;
	}

	public static long getVmUpTime() {
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}
}
