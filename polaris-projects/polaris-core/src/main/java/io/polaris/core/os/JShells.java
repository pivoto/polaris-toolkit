package io.polaris.core.os;

import io.polaris.core.regex.Patterns;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8
 */
public class JShells {

	public static List<JMapHistoInfo> getJMapHistoInfo() {
		return getJMapHistoInfo(128);
	}

	public static List<JMapHistoInfo> getJMapHistoInfo(int maxSize) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[]{"jmap", "-histo", String.valueOf(OS.getPid())});
			InputStream stdin = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stdin, StandardCharsets.UTF_8));
			/*
			 num     #instances         #bytes  class name
			----------------------------------------------
			   1:        288111       40311136  [C
			   2:         29244       15318128  [B
			   3:        197913        4749912  java.lang.String
			   ...
			  1618:             1             16  sun.util.resources.LocaleData$LocaleDataResourceBundleControl
			  Total        969047       81279648
			 */
			List<JMapHistoInfo> list = new ArrayList<>(Math.min(Math.max(128, maxSize), 16384));
			int size = 0;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				line = line.trim();
				if (size >= maxSize) {
					continue;
				}
				Pattern pattern = Patterns.getPattern("(\\d+):\\s+(\\d+)\\s+(\\d+)\\s+([^\\s]+)");
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					JMapHistoInfo info = new JMapHistoInfo();
					info.setNum(Integer.parseInt(matcher.group(1)));
					info.setInstances(Integer.parseInt(matcher.group(2)));
					info.setBytes(Long.parseLong(matcher.group(3)));
					info.setClassName((matcher.group(4)));
					list.add(info);
					size++;
				}
			}
			process.waitFor();
			return list;
		} catch (Exception ignore) {
			return Collections.emptyList();
		} finally {
			if (process != null) {
				try {
					process.destroy();
				} catch (Exception ignore) {
				}
			}
		}
	}

	public static List<JStackThreadInfo> getJStackInfo() {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[]{"jstack", "-l", String.valueOf(OS.getPid())});
			InputStream stdin = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stdin, StandardCharsets.UTF_8));

			List<JStackThreadInfo> stackThreadInfoList = new ArrayList<>(128);
			StringBuilder traceHeadBuilder = new StringBuilder();
			StringBuilder traceBodyBuilder = new StringBuilder();
			boolean inTraceBody = false;
			JStackThreadInfo stackThreadInfo = null;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				line = line.trim();
				Pattern pattern = Patterns.getPattern("\"(.+)\"(?:\\s*#(\\d+))(?:\\s*(daemon))?(?:\\s*prio=(\\d+))(?:\\s*os_prio=(\\d+))(?:\\s*tid=([\\w]+))(?:\\s*nid=([\\w]+))");
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					if (stackThreadInfo != null) {
						stackThreadInfo.setStackTrace(traceBodyBuilder.toString());
						stackThreadInfo.setStackTraceHeader(traceHeadBuilder.toString());
					}
					stackThreadInfoList.add(stackThreadInfo = new JStackThreadInfo());
					inTraceBody = false;
					traceHeadBuilder.setLength(0);
					traceBodyBuilder.setLength(0);

					String threadName = matcher.group(1);
					stackThreadInfo.setThreadName(threadName);
					String threadId = matcher.group(2);
					if (threadId != null && threadId.length() > 0) {
						stackThreadInfo.setThreadId(Long.parseLong(threadId));
					}
					String daemon = matcher.group(3);
					if (daemon != null && daemon.length() > 0) {
						stackThreadInfo.setDaemon(true);
					}
					String prio = matcher.group(4);
					if (prio != null && prio.length() > 0) {
						stackThreadInfo.setPriority(Integer.parseInt(prio));
					}
					String osPrio = matcher.group(5);
					if (osPrio != null && osPrio.length() > 0) {
						stackThreadInfo.setOsPriority(Integer.parseInt(osPrio));
					}
					String tid = matcher.group(6);
					if (tid != null && tid.length() > 0) {
						stackThreadInfo.setTid(tid);
					}
					String nid = matcher.group(7);
					if (nid != null && nid.length() > 0) {
						stackThreadInfo.setNid(nid);
						stackThreadInfo.setPid(Integer.parseInt(nid.replace("0x", ""), 16));
					}

				} else if (line.startsWith("at")) {
					inTraceBody = true;
				}
				if (stackThreadInfo != null) {
					if (inTraceBody) {
						traceBodyBuilder.append(line).append("\n");
					} else {
						traceHeadBuilder.append(line).append("\n");
					}
				}
			}
			if (stackThreadInfo != null) {
				stackThreadInfo.setStackTrace(traceBodyBuilder.toString());
				stackThreadInfo.setStackTraceHeader(traceHeadBuilder.toString());
			}
			process.waitFor();
			return stackThreadInfoList;
		} catch (Exception ignore) {
			return Collections.emptyList();
		} finally {
			if (process != null) {
				try {
					process.destroy();
				} catch (Exception ignore) {
				}
			}
		}
	}

}
