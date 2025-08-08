package io.polaris.core.os;

import io.polaris.core.consts.CharConsts;
import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.io.IO;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;
import lombok.Value;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Qt
 * @since 1.8
 */
public class Shells {
	private static final ILogger log = Loggers.of(Shells.class);


	public static Result executeScriptContent(File workDir, String scriptFileName, String scriptContent) throws IOException {
		makeScriptFile(workDir, scriptFileName, scriptContent);
		return execute(workDir, wrapCmd(scriptFileName));
	}

	public static Result executeScriptContent(File workDir, String scriptFileName, String scriptContent, String cmdArgs) throws IOException {
		makeScriptFile(workDir, scriptFileName, scriptContent);
		return execute(workDir, wrapCmd(scriptFileName + SymbolConsts.SPACE + cmdArgs));
	}

	public static Result executeScriptContent(String charset, File workDir, String scriptFileName, String scriptContent) throws IOException {
		makeScriptFile(workDir, scriptFileName, scriptContent, charset);
		return execute(charset, workDir, wrapCmd(scriptFileName));
	}

	public static Result executeScriptContent(String charset, File workDir, String scriptFileName, String scriptContent, String cmdArgs) throws IOException {
		makeScriptFile(workDir, scriptFileName, scriptContent, charset);
		return execute(charset, workDir, wrapCmd(scriptFileName + SymbolConsts.SPACE + cmdArgs));
	}

	public static Result executeScriptContent(String charset, Map<String, String> env, File workDir, String scriptFileName, String scriptContent) throws IOException {
		makeScriptFile(workDir, scriptFileName, scriptContent, charset);
		return execute(charset, env, workDir, wrapCmd(scriptFileName));
	}

	public static Result executeScriptContent(String charset, Map<String, String> env, File workDir, String scriptFileName, String scriptContent, String cmdArgs) throws IOException {
		makeScriptFile(workDir, scriptFileName, scriptContent, charset);
		return execute(charset, env, workDir, wrapCmd(scriptFileName + SymbolConsts.SPACE + cmdArgs));
	}

	public static void makeScriptFile(String scriptFileName, String content) throws IOException {
		makeScriptFile(null, scriptFileName, content, StdConsts.UTF_8);
	}

	public static void makeScriptFile(String scriptFileName, String content, String charset) throws IOException {
		makeScriptFile(null, scriptFileName, content, charset);
	}

	public static void makeScriptFile(File workDir, String scriptFileName, String content) throws IOException {
		makeScriptFile(workDir, scriptFileName, content, StdConsts.UTF_8);
	}

	public static void makeScriptFile(File workDir, String scriptFileName, String content, String charset) throws IOException {
		File scriptFile = workDir != null ? new File(workDir, scriptFileName) : new File(scriptFileName);
		FileOutputStream fos = null;
		try {
			scriptFile.createNewFile();
			scriptFile.setExecutable(true);
			fos = new FileOutputStream(scriptFile);
			fos.write(content.getBytes(charset));
			fos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			IO.close(fos);
		}
	}

	public static String[] wrapCmd(String cmdStr) {
		OsType osType = OS.getOsType();
		switch (osType) {
			case WINDOWS:
				return new String[]{"cmd", "/c", cmdStr};
			case LINUX:
			case MAC:
			case SOLARIS:
				return new String[]{"/bin/sh", "-c", CharConsts.DOUBLE_QUOTATION
					+ cmdStr.replace("\\", "\\\\").replace("\"", "\\\"")
					+ CharConsts.DOUBLE_QUOTATION};
			case UNKNOWN:
			default:
				return parse(cmdStr);
		}
	}

	public static String[] parse(String cmdStr) {
		List<String> list = new ArrayList<>();
		char[] chs = cmdStr.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean escape = false;
		boolean singleQuota = false;
		boolean doubleQuota = false;
		for (char ch : chs) {
			if (ch == CharConsts.BACKSLASH) {
				escape = !escape;
				sb.append(ch);
				continue;
			}
			if (escape) {
				sb.append(ch);
				escape = false;
				continue;
			}
			if (ch == CharConsts.DOUBLE_QUOTATION) {
				if (singleQuota) {
					sb.append(ch);
					continue;
				}
				if (doubleQuota) {
					doubleQuota = false;
					if (sb.length() > 0) {
						list.add(sb.toString());
						sb.setLength(0);
					}
					continue;
				}
				doubleQuota = true;
				continue;
			}
			if (ch == CharConsts.SINGLE_QUOTATION) {
				if (doubleQuota) {
					sb.append(ch);
					continue;
				}
				if (singleQuota) {
					singleQuota = false;
					if (sb.length() > 0) {
						list.add(sb.toString());
						sb.setLength(0);
					}
					continue;
				}
				singleQuota = true;
				continue;
			}
			if (doubleQuota || singleQuota) {
				sb.append(ch);
			} else {
				if (Character.isWhitespace(ch)) {
					if (sb.length() > 0) {
						list.add(sb.toString());
						sb.setLength(0);
					}
				} else {
					sb.append(ch);
				}
			}
		}
		if (sb.length() > 0) {
			list.add(sb.toString());
			sb.setLength(0);
		}
		return list.toArray(new String[0]);
	}

	public static Result execute(String charset, File workDir, String... cmdLine) {
		return execute(charset, null, workDir, cmdLine);
	}

	public static Result execute(File workDir, String... cmdLine) {
		return execute(StdConsts.UTF_8, null, workDir, cmdLine);
	}

	public static Result execute(String charset, Map<String, String> env, File workDir, String... cmdLine) {
		if (cmdLine.length == 0) {
			throw new IllegalArgumentException();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (workDir != null) {
			processBuilder.directory(workDir);
		}
		if (env != null) {
			processBuilder.environment().putAll(env);
		}
		processBuilder.command(cmdLine);
		Process process = null;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			return toErrorResult(bos, e);
		}
		try {
			return waitFor(bos, process, charset);
		} catch (InterruptedException e) {
			return toErrorResult(bos, e);
		}
	}

	public static Result waitFor(Process process) throws InterruptedException {
		return waitFor(new ByteArrayOutputStream(), process, StdConsts.UTF_8);
	}

	public static Result waitFor(Process process, String charset) throws InterruptedException {
		return waitFor(new ByteArrayOutputStream(), process, charset);
	}

	private static Result waitFor(ByteArrayOutputStream bos, Process process, String charset) throws InterruptedException {
		StreamCollector outputCollector = new StreamCollector(charset, process.getInputStream(), bos);
		StreamCollector errorCollector = new StreamCollector(charset, process.getErrorStream(), bos);
		outputCollector.start();
		errorCollector.start();
		int code = process.waitFor();
		outputCollector.waitFor();
		errorCollector.waitFor();
		return new Result(code, bos.toString());
	}

	private static Result toErrorResult(ByteArrayOutputStream bos, Exception ex) {
		ex.printStackTrace(new PrintStream(bos));
		return new Result(-1, bos.toString());
	}

	@Value
	public static class Result {
		private int exitCode;
		private String output;

		public Result(int exitCode, String output) {
			this.output = output;
			this.exitCode = exitCode;
		}
	}

	static class StreamCollector {

		private final InputStream in;
		private final OutputStream out;
		private final String charset;
		private final Lock lock;
		private final Condition condition;
		private volatile boolean completed = false;
		private Thread thread;

		public StreamCollector(InputStream in, OutputStream out) {
			this(StdConsts.UTF_8, in, out);
		}

		public StreamCollector(String charset, InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
			this.charset = charset;
			this.lock = new ReentrantLock();
			this.condition = lock.newCondition();

			this.thread = new Thread(() -> {
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(in, charset));
					String line;
					while ((line = br.readLine()) != null) {
						if (out != null) {
							out.write(line.getBytes());
							out.write(SymbolConsts.LF.getBytes());
						}
					}
					out.flush();
				} catch (IOException e) {
					if (out != null) {
						e.printStackTrace(new PrintStream(out));
					}
				} finally {
					completed = true;
					IO.close(br);
				}
				lock.lock();
				try {
					condition.signalAll();
				} finally {
					lock.unlock();
				}
			});
		}


		public boolean isCompleted() {
			return completed;
		}

		public void waitFor() {
			if (completed) {
				return;
			}
			lock.lock();
			try {
				condition.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				lock.unlock();
			}
		}

		public void start() {
			thread.start();
		}
	}
}
