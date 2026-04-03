package io.polaris.agent.apm;

import java.util.List;
import java.util.function.Consumer;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import io.polaris.core.map.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Feb 14, 2026
 */
@Slf4j
public class OpenTracingTest {


	@Test
	void test01() {
		List<MockSpan> spans = callTrace(
			tracer -> {
				callSpan(tracer,
					tracer1 -> {
						callSpan(tracer);
						callSpan(tracer);
					}
				);
			},
			tracer -> {
				callSpan(tracer,
					tracer1 -> {
						callSpan(tracer);
						callSpan(tracer);
					}
				);
			}
		);
		for (MockSpan span : spans) {
			System.out.println(span);
		}
		spans = callTrace(
			tracer -> {
				callSpan(tracer,
					tracer1 -> {
						callSpan(tracer);
					}
				);
			},
			tracer -> {
				callSpan(tracer,
					tracer1 -> {
						callSpan(tracer);
					}
				);
			}
		);
		for (MockSpan span : spans) {
			System.out.println(span);
		}
	}

	@SafeVarargs
	final List<MockSpan> callTrace(Consumer<Tracer>... consumers) {
		MockTracer tracer = new MockTracer();
		MockSpan span = tracer.buildSpan("root").start();
		try (Scope scope = tracer.scopeManager().activate(span);) {
			span.setTag(Tags.COMPONENT, "root");
			log.info("span: {}", tracer.activeSpan());

			for (Consumer<Tracer> consumer : consumers) {
				consumer.accept(tracer);
			}
		} catch (Exception e) {
			Tags.ERROR.set(span, true);
		} finally {
			span.finish();
		}
		return tracer.finishedSpans();
	}

	@SafeVarargs
	final void callSpan(Tracer tracer, Consumer<Tracer>... consumers) {
		Span span = tracer.buildSpan("sub").start();
		try (Scope scope = tracer.activateSpan(span);) {
			span.setTag(Tags.COMPONENT, "sub");
			log.info("span: {}", tracer.activeSpan());
			for (Consumer<Tracer> consumer : consumers) {
				consumer.accept(tracer);
			}
		} catch (Exception e) {
			Tags.ERROR.set(span, true);
		} finally {
			span.finish();
		}
	}

	@Test
	void test02() {
		GroovyShell shell = new GroovyShell();
		Binding binding = shell.getContext();
		Script script = shell.parse("def a = 1; return a + '-' + args[1]");
		{
			binding.setVariable("args", new Object[]{"arg0", "arg1", "arg2"});
			Object rs = shell.evaluate("def a = 1; return a + '-' + args[1]");
			log.info("rs : {}", rs);
			log.info("rs : {}", script.run());
			script.setBinding(new Binding(Maps.asMap("args","0123")));
			log.info("rs : {}", script.run());
		}
		{
			binding.setVariable("args", new Object[]{"1", "2", "3"});
			Object rs = shell.evaluate("def a = 1; return a + '-' + args[1]");
			log.info("rs : {}", rs);
			log.info("rs : {}", script.run());
		}
	}

}
