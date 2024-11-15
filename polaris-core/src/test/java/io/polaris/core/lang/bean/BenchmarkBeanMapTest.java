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
public class BenchmarkBeanMapTest {

	public static void main(String[] args) throws RunnerException {
		for (int i = 1; i <= 3; i++) {
			String time = Dates.YYYYMMDDHHMMSSSSS.format(Instant.now());
			String fileName = "/data/benchmark/BenchmarkBeanMapTest." + i + "." + time;
			Options options = new OptionsBuilder().include(BenchmarkBeanMapTest.class.getSimpleName())
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

	@State(Scope.Thread)
	public static class ThreadState {
		public final MetaObjectTestBean source = new MetaObjectTestBean();
		public final BeanMapV1 beanMapV1 = new BeanMapV1<>(source);
		public final BeanMap beanMap = Beans.newBeanMap(source);
	}

	@Benchmark
	public Object Origin_setProperty(ThreadState state) {
		MetaObjectTestBean bean = state.source;
		bean.setPrivateStringVal("test");
		return bean.getPrivateStringVal();
	}

	@Benchmark
	public Object Reflect_set(BenchmarkMetaObjectTest.ThreadState state) throws ReflectiveOperationException {
		MetaObjectTestBean bean = state.source;
		Method setPrivateStringVal = Reflects.getMethod(bean.getClass(), "setPrivateStringVal", String.class);
		Method getPrivateStringVal = Reflects.getMethod(bean.getClass(),"getPrivateStringVal");
		Reflects.invoke(bean, setPrivateStringVal, "test");
		return Reflects.invoke(bean, getPrivateStringVal);
	}

	@Benchmark
	public Object BeanMapV1_new(ThreadState state) {
		return new BeanMapV1<>(state.source);
	}

	@Benchmark
	public Object BeanMapV2_new(ThreadState state) {
		return Beans.newBeanMap(state.source);
	}

	@Benchmark
	public Object BeanMapV1_put_get(ThreadState state) {
		BeanMapV1 beanMap = state.beanMapV1;

		beanMap.put("privateStringVal", "test");
		return beanMap.get("privateStringVal");
	}

	@Benchmark
	public Object BeanMapV2_put_get(ThreadState state) {
		BeanMap beanMap = state.beanMap;
		beanMap.put("privateStringVal", "test");
		return beanMap.get("privateStringVal");
	}

	@Test
	void testBeanMapV1() {
		MetaObjectTestBean bean = new MetaObjectTestBean();
		BeanMapV1<MetaObjectTestBean> beanMap = new BeanMapV1<>(bean);

		beanMap.put("privateStringVal", "test");
		Assertions.assertEquals("test", beanMap.get("privateStringVal"));
		Object[] args2 = new Object[]{beanMap.get("privateStringVal")};
		Consoles.log("$.privateStringVal: {}", args2);

		Assertions.assertThrows(Exception.class, () -> {
			beanMap.put("publicStringVal", "test");
			Assertions.assertEquals("test", beanMap.get("publicStringVal"));
			Object[] args = new Object[]{beanMap.get("publicStringVal")};
			Consoles.log("$.publicStringVal: {}", args);
		});

		beanMap.put("privateIntVal", "123");
		Assertions.assertEquals(123, beanMap.get("privateIntVal"));
		Object[] args1 = new Object[]{beanMap.get("privateIntVal")};
		Consoles.log("$.privateIntVal: {}", args1);


		Object[] args = new Object[]{Jsons.serialize(bean)};
		Consoles.log("bean: {}", args);
	}

	@Test
	void testBeanMap() {
		LambdaMetaObject<MetaObjectTestBean> meta = LambdaMetaObject.of(MetaObjectTestBean.class);
		MetaObjectTestBean bean = new MetaObjectTestBean();
		BeanMap<MetaObjectTestBean> beanMap = Beans.newBeanMap(bean);

		beanMap.put("privateStringVal", "test");
		Assertions.assertEquals("test", beanMap.get("privateStringVal"));
		Object[] args3 = new Object[]{beanMap.get("privateStringVal")};
		Consoles.log("$.privateStringVal: {}", args3);

		beanMap.put("publicStringVal", "test");
		Assertions.assertEquals("test", beanMap.get("publicStringVal"));
		Object[] args2 = new Object[]{beanMap.get("publicStringVal")};
		Consoles.log("$.publicStringVal: {}", args2);

		beanMap.put("privateIntVal", "123");
		Assertions.assertEquals(123, beanMap.get("privateIntVal"));
		Object[] args1 = new Object[]{beanMap.get("privateIntVal")};
		Consoles.log("$.privateIntVal: {}", args1);

		Object[] args = new Object[]{Jsons.serialize(bean)};
		Consoles.log("bean: {}", args);
	}


}
