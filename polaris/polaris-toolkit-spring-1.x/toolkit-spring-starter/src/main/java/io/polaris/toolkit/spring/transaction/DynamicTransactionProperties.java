package io.polaris.toolkit.spring.transaction;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @version Dec 29, 2021
 * @since 1.8
 */
@ConfigurationProperties(prefix = ToolkitConstants.TOOLKIT_DYNAMIC_TRANSACTION, ignoreUnknownFields = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DynamicTransactionProperties {

	/** 是否启用 */
	private boolean enabled = false;
	/** 是否启用AspectJ */
	private boolean enableAspectj = true;

	/** 是否启用Repository注解拦截 */
	private boolean enableRepositoryAspect = false;
	/** 是否启用Service注解拦截 */
	private boolean enableServiceAspect = true;
	/** 是否启用Transactional注解拦截 */
	private boolean enableTransactionalAspect = true;

	/**
	 * 事务拦截类匹配模式(AspectJ类匹配语法，支持 and/or/not 逻辑操作符)
	 * <ul>Examples include:
	 * <li>
	 * <code class="code">
	 * org.springframework.beans.*
	 * </code>
	 * This will match any class or interface in the given package.
	 * </li>
	 * <li>
	 * <code class="code">
	 * org.springframework.beans.ITestBean+
	 * </code>
	 * This will match the {@code ITestBean} interface and any class
	 * that implements it.
	 * </li>
	 * </ul>
	 */
	private String classPattern = "";

	/** 标准事务方法名 (REQUIRED,READ_COMMITTED)，多种方法以逗号分隔 */
	private String stdTransactionalMethods = "*";
	/** 异步操作、日志记录等独立事务方法名 (REQUIRED_NEW,READ_COMMITTED)，多种方法以逗号分隔 */
	private String newTransactionalMethods = "log*,doLog*,async*,doAsync*";
	/** 只读事务方法名 (REQUIRED,ReadOnly)，多种方法以逗号分隔 */
	private String readonlyTransactionalMethods = "get*,count*,find*,list*,query*,select*,is*,has*,exist*";
	/** 触发事务回滚的异常类名，多种方法以逗号分隔 */
	private String rollbackExceptions = "";
	/** 不需要事务回滚的异常类名，多种方法以逗号分隔 */
	private String noRollbackExceptions = "";
	/** 自定义事务方法名匹配规则 */
	private List<TransactionalMethodsRule> rules = new ArrayList<>();

	@Getter
	@Setter
	@ToString
	@EqualsAndHashCode
	public static class TransactionalMethodsRule {
		/** 事务方法名，多种方法以逗号分隔 */
		private String methods;
		/** 事务传播方式 */
		private TransactionAttributeName.Propagation propagation = TransactionAttributeName.DEFAULT_PROPAGATION;
		/** 事务隔离级别 */
		private TransactionAttributeName.Isolation isolation = TransactionAttributeName.DEFAULT_ISOLATION;
		/** 事务超时时间(秒) */
		private int timeout = TransactionAttributeName.DEFAULT_TIMEOUT;
		/** 事务是否只讯 */
		private boolean readOnly = TransactionAttributeName.DEFAULT_READONLY;
		/** 触发事务回滚的异常类名，多种方法以逗号分隔 */
		private String rollbackExceptions = "";
		/** 不需要事务回滚的异常类名，多种方法以逗号分隔 */
		private String noRollbackExceptions = "";

	}


}
