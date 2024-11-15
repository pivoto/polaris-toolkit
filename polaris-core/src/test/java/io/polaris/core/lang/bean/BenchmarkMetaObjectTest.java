package io.polaris.core.lang.bean;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import io.polaris.core.io.Consoles;
import io.polaris.core.time.Dates;
import io.polaris.core.json.Jsons;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author Qt
 * @since  Apr 12, 2024
 */

@Fork(1)
@Threads(8)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@Warmup(iterations = 1)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkMetaObjectTest {

	public static void main(String[] args) throws RunnerException {
		for (int i = 1; i <= 3; i++) {
			String time = Dates.YYYYMMDDHHMMSSSSS.format(Instant.now());
			String fileName = "/data/benchmark/BenchmarkMetaObjectTest." + i + "." + time;
			Options options = new OptionsBuilder().include(BenchmarkMetaObjectTest.class.getSimpleName())
				//.syncIterations(true)
				.forks(1)
				.threads(8 * i)
				.resultFormat(ResultFormatType.CSV)
				.result(fileName + ".csv")
				.output(fileName + ".log")
				.build();
			new Runner(options).run();
		}
	}

	IndexedMetaObject<MetaObjectTestBean> indexedMeta = IndexedMetaObject.of(MetaObjectTestBean.class);
	LambdaMetaObject<MetaObjectTestBean> lambdaMeta = LambdaMetaObject.of(MetaObjectTestBean.class);

	@State(Scope.Thread)
	public static class ThreadState {
		public MetaObjectTestBean source = new MetaObjectTestBean();
	}

	@Benchmark
	public Object Origin_setProperty(ThreadState state) {
		MetaObjectTestBean bean = state.source;
		bean.setPrivateStringVal("test");
		return bean.getPrivateStringVal();
	}

	@Benchmark
	public Object Reflect_setProperty(ThreadState state) throws ReflectiveOperationException {
		MetaObjectTestBean bean = state.source;
		Method setPrivateStringVal = Reflects.getMethod(bean.getClass(), "setPrivateStringVal", String.class);
		Method getPrivateStringVal = Reflects.getMethod(bean.getClass(),"getPrivateStringVal");
		Reflects.invoke(bean, setPrivateStringVal, "test");
		return Reflects.invoke(bean, getPrivateStringVal);
	}

	@Benchmark
	public Object IndexedMetaObject_setProperty(ThreadState state) {
		MetaObjectTestBean bean = state.source;
		MetaObject meta = indexedMeta;

		meta.setProperty(bean, "privateStringVal", "test");
		return meta.getProperty(bean, "privateStringVal");
	}

	@Benchmark
	public Object LambdaMetaObject_setProperty(ThreadState state) {
		MetaObjectTestBean bean = state.source;
		MetaObject meta = lambdaMeta;

		meta.setProperty(bean, "privateStringVal", "test");
		return meta.getProperty(bean, "privateStringVal");
	}

	@Benchmark
	public Object IndexedMetaObject_setPathProperty(ThreadState state) {
		MetaObjectTestBean bean = state.source;
		MetaObject meta = indexedMeta;

		meta.setPathProperty(bean, "privateMetaObjectTestBeanVal", new MetaObjectTestBean());
		meta.setPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal", "test");
		return meta.getPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal");
	}

	@Benchmark
	public Object LambdaMetaObject_setPathProperty(ThreadState state) {
		MetaObjectTestBean bean = state.source;
		MetaObject meta = lambdaMeta;

		meta.setPathProperty(bean, "privateMetaObjectTestBeanVal", new MetaObjectTestBean());
		meta.setPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal", "test");
		return meta.getPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal");
	}

	@Test
	void testIndexedMetaObject() {
		IndexedMetaObject<MetaObjectTestBean> meta = IndexedMetaObject.of(MetaObjectTestBean.class);
		MetaObjectTestBean bean = new MetaObjectTestBean();

		meta.setProperty(bean, "privateStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "privateStringVal"));
		Object[] args7 = new Object[]{meta.getProperty(bean, "privateStringVal")};
		Consoles.log("$.privateStringVal: {}", args7);

		meta.setProperty(bean, "publicStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "publicStringVal"));
		Object[] args6 = new Object[]{meta.getProperty(bean, "publicStringVal")};
		Consoles.log("$.publicStringVal: {}", args6);

//		meta.setProperty(bean, "privateIntVal", "test"); //error
//		Assertions.assertEquals(0, meta.getProperty(bean, "privateIntVal"));
		meta.setProperty(bean, "privateIntVal", "123");
		Assertions.assertEquals(123, meta.getProperty(bean, "privateIntVal"));
		Object[] args5 = new Object[]{meta.getProperty(bean, "privateIntVal")};
		Consoles.log("$.privateIntVal: {}", args5);


		meta.setPathProperty(bean, "privateMetaObjectTestBeanVal", new MetaObjectTestBean());
		meta.setPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal", "test");
		Assertions.assertEquals("test", meta.getPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal"));
		Object[] args4 = new Object[]{meta.getPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal")};
		Consoles.log("$.privateMetaObjectTestBeanVal.publicStringVal: {}", args4);

		meta.setPathProperty(bean, "publicMetaObjectTestBeanVal", new MetaObjectTestBean());
		meta.setPathProperty(bean, "publicMetaObjectTestBeanVal.publicStringVal", "test");
		Assertions.assertEquals("test", meta.getPathProperty(bean, "publicMetaObjectTestBeanVal.publicStringVal"));
		Object[] args3 = new Object[]{meta.getPathProperty(bean, "publicMetaObjectTestBeanVal.publicStringVal")};
		Consoles.log("$.publicMetaObjectTestBeanVal.publicStringVal: {}", args3);


		meta.setPathProperty(bean, "privateList.1", new MetaObjectTestBean());
		Assertions.assertNotNull(meta.getPathProperty(bean, "privateList.1"));
		Assertions.assertNull(meta.getPathProperty(bean, "privateList.1.publicStringVal"));
		meta.setPathProperty(bean, "privateList.1.publicStringVal", "test");
		Assertions.assertEquals("test", meta.getPathProperty(bean, "privateList.1.publicStringVal"));
		Object[] args2 = new Object[]{meta.getPathProperty(bean, "privateList.1.publicStringVal")};
		Consoles.log("$.privateList.1.publicStringVal: {}", args2);


		meta.setPathProperty(bean, "privateMap.1", new MetaObjectTestBean());
		Assertions.assertNotNull(meta.getPathProperty(bean, "privateMap.1"));
		Assertions.assertNull(meta.getPathProperty(bean, "privateMap.1.publicStringVal"));
		meta.setPathProperty(bean, "privateMap.1.publicStringVal", "test");
		Assertions.assertEquals("test", meta.getPathProperty(bean, "privateMap.1.publicStringVal"));
		Object[] args1 = new Object[]{meta.getPathProperty(bean, "privateMap.1.publicStringVal")};
		Consoles.log("$.privateMap.1.publicStringVal: {}", args1);

		Object[] args = new Object[]{Jsons.serialize(bean)};
		Consoles.log("bean: {}", args);
	}

	@Test
	void testLambdaMetaObject() {
		LambdaMetaObject<MetaObjectTestBean> meta = LambdaMetaObject.of(MetaObjectTestBean.class);
		MetaObjectTestBean bean = new MetaObjectTestBean();

		meta.setProperty(bean, "privateStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "privateStringVal"));
		Object[] args7 = new Object[]{meta.getProperty(bean, "privateStringVal")};
		Consoles.log("$.privateStringVal: {}", args7);

		meta.setProperty(bean, "publicStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "publicStringVal"));
		Object[] args6 = new Object[]{meta.getProperty(bean, "publicStringVal")};
		Consoles.log("$.publicStringVal: {}", args6);

//		meta.setProperty(bean, "privateIntVal", "test"); //error
//		Assertions.assertEquals(0, meta.getProperty(bean, "privateIntVal"));
		meta.setProperty(bean, "privateIntVal", "123");
		Assertions.assertEquals(123, meta.getProperty(bean, "privateIntVal"));
		Object[] args5 = new Object[]{meta.getProperty(bean, "privateIntVal")};
		Consoles.log("$.privateIntVal: {}", args5);


		meta.setPathProperty(bean, "privateMetaObjectTestBeanVal", new MetaObjectTestBean());
		meta.setPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal", "test");
		Assertions.assertEquals("test", meta.getPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal"));
		Object[] args4 = new Object[]{meta.getPathProperty(bean, "privateMetaObjectTestBeanVal.publicStringVal")};
		Consoles.log("$.privateMetaObjectTestBeanVal.publicStringVal: {}", args4);

		meta.setPathProperty(bean, "publicMetaObjectTestBeanVal", new MetaObjectTestBean());
		meta.setPathProperty(bean, "publicMetaObjectTestBeanVal.publicStringVal", "test");
		Assertions.assertEquals("test", meta.getPathProperty(bean, "publicMetaObjectTestBeanVal.publicStringVal"));
		Object[] args3 = new Object[]{meta.getPathProperty(bean, "publicMetaObjectTestBeanVal.publicStringVal")};
		Consoles.log("$.publicMetaObjectTestBeanVal.publicStringVal: {}", args3);


		meta.setPathProperty(bean, "privateList.1", new MetaObjectTestBean());
		Assertions.assertNotNull(meta.getPathProperty(bean, "privateList.1"));
		Assertions.assertNull(meta.getPathProperty(bean, "privateList.1.publicStringVal"));
		meta.setPathProperty(bean, "privateList.1.publicStringVal", "test");
		Assertions.assertEquals("test", meta.getPathProperty(bean, "privateList.1.publicStringVal"));
		Object[] args2 = new Object[]{meta.getPathProperty(bean, "privateList.1.publicStringVal")};
		Consoles.log("$.privateList.1.publicStringVal: {}", args2);


		meta.setPathProperty(bean, "privateMap.1", new MetaObjectTestBean());
		Assertions.assertNotNull(meta.getPathProperty(bean, "privateMap.1"));
		Assertions.assertNull(meta.getPathProperty(bean, "privateMap.1.publicStringVal"));
		meta.setPathProperty(bean, "privateMap.1.publicStringVal", "test");
		Assertions.assertEquals("test", meta.getPathProperty(bean, "privateMap.1.publicStringVal"));
		Object[] args1 = new Object[]{meta.getPathProperty(bean, "privateMap.1.publicStringVal")};
		Consoles.log("$.privateMap.1.publicStringVal: {}", args1);

		Object[] args = new Object[]{Jsons.serialize(bean)};
		Consoles.log("bean: {}", args);
	}


}
