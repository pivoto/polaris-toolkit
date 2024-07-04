package io.polaris.mybatis.interceptor;

import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author Qt
 * @since Jul 04, 2024
 */
public class MybatisInterceptors {

	public static void addInterceptors(SqlSessionFactory sqlSessionFactory, Interceptor... interceptors) {
		addInterceptors(sqlSessionFactory, Arrays.asList(interceptors));
	}

	public static void addInterceptors(SqlSessionFactory sqlSessionFactory, List<Interceptor> interceptors) {
		Configuration configuration = sqlSessionFactory.getConfiguration();
		for (Interceptor interceptor : interceptors) {
			if (!containsInterceptor(configuration, interceptor)) {
				configuration.addInterceptor(interceptor);
			}
		}
	}

	public static void addInterceptors(List<SqlSessionFactory> sqlSessionFactoryList, Interceptor... interceptors) {
		for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
			addInterceptors(sqlSessionFactory, interceptors);
		}
	}

	public static void addInterceptors(List<SqlSessionFactory> sqlSessionFactoryList, List<Interceptor> interceptors) {
		for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
			addInterceptors(sqlSessionFactory, interceptors);
		}
	}

	/**
	 * 是否已经存在相同的拦截器
	 */
	public static boolean containsInterceptor(Configuration configuration, Interceptor interceptor) {
		try {
			return configuration.getInterceptors().stream().anyMatch(p -> interceptor.getClass().isAssignableFrom(p.getClass()));
		} catch (Exception e) {
			return false;
		}
	}
}
