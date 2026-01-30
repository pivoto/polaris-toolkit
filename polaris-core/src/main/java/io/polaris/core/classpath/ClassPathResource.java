package io.polaris.core.classpath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.collection.Iterators;
import io.polaris.core.err.UncheckedPassException;
import io.polaris.core.io.Filenames;

/**
 * 表示类路径资源的类，用于访问类路径下的资源文件。
 * 支持通过指定的ClassLoader或系统默认ClassLoader加载资源。
 *
 * @author Qt
 * @since Jan 29, 2026
 */
public class ClassPathResource {

	/** 用于加载资源的类加载器 */
	private final ClassLoader classLoader;

	/** 资源路径 */
	private final String path;

	/**
	 * 构造函数，使用指定的ClassLoader和资源路径创建实例
	 *
	 * @param classLoader 类加载器，用于加载资源
	 * @param path        资源路径
	 */
	public ClassPathResource(ClassLoader classLoader, String path) {
		this.classLoader = classLoader;
		this.path = path;
	}

	/**
	 * 构造函数，使用系统默认ClassLoader和资源路径创建实例
	 *
	 * @param path 资源路径
	 */
	public ClassPathResource(String path) {
		this(null, path);
	}

	/**
	 * 获取用于加载资源的类加载器
	 *
	 * @return 类加载器实例
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * 获取资源路径
	 *
	 * @return 资源路径字符串
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 检查资源是否存在
	 *
	 * @return 如果资源存在返回true，否则返回false
	 */
	public boolean exists() {
		return (resolveUrl() != null);
	}

	/**
	 * 获取资源的URL
	 *
	 * @return 资源URL，如果资源不存在则返回null
	 * @throws IOException IO异常
	 */
	@Nullable
	public URL getUrl() throws IOException {
		return resolveUrl();
	}

	/**
	 * 解析资源URL
	 *
	 * @return 资源URL，如果找不到资源则返回null
	 */
	@Nullable
	private URL resolveUrl() {
		if (this.classLoader != null) {
			return this.classLoader.getResource(this.path);
		} else {
			return ClassLoader.getSystemResource(this.path);
		}
	}

	/**
	 * 获取资源的输入流
	 *
	 * @return 资源输入流
	 * @throws IOException 如果资源不存在或IO操作失败
	 */
	@Nonnull
	public InputStream getInputStream() throws IOException {
		InputStream is;
		if (this.classLoader != null) {
			is = this.classLoader.getResourceAsStream(this.path);
		} else {
			is = ClassLoader.getSystemResourceAsStream(this.path);
		}
		if (is == null) {
			throw new FileNotFoundException(path);
		}
		return is;
	}

	/**
	 * 获取所有匹配路径的资源URL迭代器
	 *
	 * @return URL迭代器
	 * @throws IOException IO异常
	 */
	@Nonnull
	public Iterator<URL> getAllUrls() throws IOException {
		Enumeration<URL> es;
		if (this.classLoader != null) {
			es = this.classLoader.getResources(this.path);
		} else {
			es = ClassLoader.getSystemResources(this.path);
		}
		return Iterators.iterator(es);
	}

	/**
	 * 获取所有匹配路径的资源输入流迭代器
	 *
	 * @return 输入流迭代器
	 * @throws IOException IO异常
	 */
	@Nonnull
	public Iterator<InputStream> getAllInputStreams() throws IOException {
		Enumeration<URL> es;
		if (this.classLoader != null) {
			es = this.classLoader.getResources(this.path);
		} else {
			es = ClassLoader.getSystemResources(this.path);
		}
		return Iterators.iterator(es, url -> {
			try {
				return url.openStream();
			} catch (IOException e) {
				throw new UncheckedPassException(e);
			}
		});
	}

	/**
	 * 创建相对于当前资源的新资源实例
	 *
	 * @param relativePath 相对路径
	 * @return 新的ClassPathResource实例
	 */
	public ClassPathResource createRelative(String relativePath) {
		return new ClassPathResource(this.classLoader, Filenames.relativePath(this.path, relativePath));
	}

	/**
	 * 获取资源文件名
	 *
	 * @return 文件名
	 */
	public String getFilename() {
		return Filenames.getName(this.path);
	}
}
