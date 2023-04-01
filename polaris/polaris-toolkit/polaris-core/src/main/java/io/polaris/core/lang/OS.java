package io.polaris.core.lang;

import io.polaris.core.consts.StdConsts;

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

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("All")
public class OS {

	private static volatile int PID = -1;
	private static volatile String LOCAL_HOST_IP;
	private static volatile String CACHE_ONE_IP;
	private static volatile List<String> CACHE_ALL_IPS;
	private static volatile String OS_NAME;
	private static volatile String HOST_NAME;

	public static String getOsName() {
		if (OS_NAME == null) {
			OS_NAME = System.getProperty("os.name");
		}
		return OS_NAME;
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

	public static String getOneIp() {
		if (null != CACHE_ONE_IP) {
			return CACHE_ONE_IP;
		}
		try {
			String localIpAddress = null;
			Enumeration<NetworkInterface> es = NetworkInterface.getNetworkInterfaces();
			while (es.hasMoreElements()) {
				NetworkInterface ni = es.nextElement();
				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress ipAddress = inetAddresses.nextElement();
					System.out.println(ipAddress);
					if (!ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress)) {
						String publicIpAddress = ipAddress.getHostAddress();
						CACHE_ONE_IP = publicIpAddress;
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
			CACHE_ONE_IP = localIpAddress;
			return localIpAddress;
		} catch (Throwable e) {
			return null;
		}
	}

	public static String getFirstIp() {
		List<String> ips = getAllIps();
		if (ips.size() > 0) {
			return ips.get(0);
		}
		return null;
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
					byte b1 = bytes1[i];
					byte b2 = bytes2[i];
					int compared = Byte.compare(b1, b2);
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
		return ManagementFactory.getRuntimeMXBean().getStartTime();
	}

	public static long getVmUpTime() {
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}
}
