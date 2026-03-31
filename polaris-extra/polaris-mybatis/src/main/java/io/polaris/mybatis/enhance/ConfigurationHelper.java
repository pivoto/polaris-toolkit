package io.polaris.mybatis.enhance;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.function.Function;

import io.polaris.core.reflect.Methods;
import io.polaris.core.reflect.Reflects;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

/**
 * @author Qt
 * @since Mar 28, 2026
 */
@SuppressWarnings("all")
public class ConfigurationHelper {
	private final Configuration configuration;
	private final Map<String, MappedStatement> mappedStatements;

	public static ConfigurationHelper of(Configuration configuration) {
		return new ConfigurationHelper(configuration);
	}

	private ConfigurationHelper(Configuration configuration) {
		this.configuration = configuration;
		Class<Map<String, MappedStatement>> fieldType = (Class) Map.class;
		Map<String, MappedStatement> mappedStatements = null;
		try {
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "mappedStatements", fieldType);
			mappedStatements = (Map<String, MappedStatement>) methodHandle.invokeExact(configuration);
		} catch (Throwable e) {
			mappedStatements = (Map<String, MappedStatement>) Reflects.getFieldValueQuietly(Configuration.class, "mappedStatements");
		}
		this.mappedStatements = mappedStatements;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void addMappedStatement(MappedStatement ms) {
		if (mappedStatements == null) {
			configuration.addMappedStatement( ms);
			return;
		}
		if (ms != null) {
			mappedStatements.put(ms.getId(), ms);
		}
	}

	public void putMappedStatement(MappedStatement ms) {
		if (mappedStatements == null) {
			configuration.addMappedStatement( ms);
			return;
		}
		String statementId = ms.getId();
		if (mappedStatements.containsKey(statementId)) {
			this.mappedStatements.remove(statementId);
		}
		this.mappedStatements.put(ms.getId(), ms);
	}

	public MappedStatement removeMappedStatement(String statementId) {
		if (mappedStatements == null) {
			throw new UnsupportedOperationException();
		}
		return this.mappedStatements.remove(statementId);
	}

	public void replaceMappedStatement(String statementId, MappedStatement newer) {
		if (mappedStatements == null) {
			throw new UnsupportedOperationException();
		}
		if (mappedStatements.containsKey(statementId)) {
			MappedStatement existed = mappedStatements.get(statementId);
			this.mappedStatements.remove(statementId);
			this.addMappedStatement(newer);
		}
	}

	public void replaceMappedStatement(String statementId, Function<MappedStatement, MappedStatement> replaced) {
		if (mappedStatements == null) {
			throw new UnsupportedOperationException();
		}
		if (mappedStatements.containsKey(statementId)) {
			MappedStatement existed = mappedStatements.get(statementId);
			MappedStatement newer = replaced.apply(existed);
			this.mappedStatements.remove(statementId);
			this.addMappedStatement(newer);
		}
	}

	public void addOrReplaceMappedStatement(String statementId, Function<MappedStatement, MappedStatement> replaced) {
		if (mappedStatements == null) {
			throw new UnsupportedOperationException();
		}
		if (mappedStatements.containsKey(statementId)) {
			MappedStatement existed = mappedStatements.get(statementId);
			MappedStatement newer = replaced.apply(existed);
			this.mappedStatements.remove(statementId);
			this.addMappedStatement(newer);
		} else {
			this.addMappedStatement(replaced.apply(null));
		}
	}
}
