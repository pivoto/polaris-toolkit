package io.polaris.core.time;

import java.time.*;
import java.time.temporal.TemporalAccessor;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class DatesTest {


	@Test
	void test_formatDefault() {
		String msg4 = Dates.nowStr();
		Consoles.log(msg4);
		String msg3 = Dates.formatDefault(Instant.now());
		Consoles.log(msg3);
		String msg2 = Dates.formatDefault(LocalDateTime.now());
		Consoles.log(msg2);
		String msg1 = Dates.formatDefault(LocalDate.now());
		Consoles.log(msg1);
		String msg = Dates.formatDefault(LocalTime.now());
		Consoles.log(msg);
	}

	@Test
	void test_toLocalDateTime() {
		Object[] args10 = new Object[]{Dates.toLocalDateTime(Instant.now())};
		Consoles.log("", args10);
		Object[] args9 = new Object[]{Dates.toLocalDateTime(ZonedDateTime.now())};
		Consoles.log("", args9);
		Object[] args8 = new Object[]{Dates.toLocalDateTime(OffsetDateTime.now())};
		Consoles.log("", args8);
		Object[] args7 = new Object[]{Dates.toLocalDateTime(LocalDateTime.now())};
		Consoles.log("", args7);
		Object[] args6 = new Object[]{Dates.toLocalDateTime(LocalDate.now())};
		Consoles.log("", args6);
		Object[] args5 = new Object[]{Dates.toLocalDateTime(LocalTime.now())};
		Consoles.log("", args5);
		Object[] args4 = new Object[]{Dates.toLocalDateTime(YearMonth.now())};
		Consoles.log("", args4);
		Object[] args3 = new Object[]{Dates.toLocalDateTime(Year.now())};
		Consoles.log("", args3);
		Object[] args2 = new Object[]{Dates.toLocalDateTime(MonthDay.now())};
		Consoles.log("", args2);
		Object[] args1 = new Object[]{Dates.toLocalDateTime(Month.from(LocalDate.now()))};
		Consoles.log("", args1);
		Object[] args = new Object[]{Dates.toLocalDateTime(DayOfWeek.from(LocalDate.now()))};
		Consoles.log("", args);
	}

	@Test
	void test_toLocalTime() {
		Object[] args10 = new Object[]{Dates.toLocalTime(Instant.now())};
		Consoles.log("", args10);
		Object[] args9 = new Object[]{Dates.toLocalTime(ZonedDateTime.now())};
		Consoles.log("", args9);
		Object[] args8 = new Object[]{Dates.toLocalTime(OffsetDateTime.now())};
		Consoles.log("", args8);
		Object[] args7 = new Object[]{Dates.toLocalTime(LocalDateTime.now())};
		Consoles.log("", args7);
		Object[] args6 = new Object[]{Dates.toLocalTime(LocalDate.now())};
		Consoles.log("", args6);
		Object[] args5 = new Object[]{Dates.toLocalTime(LocalTime.now())};
		Consoles.log("", args5);
		Object[] args4 = new Object[]{Dates.toLocalTime(YearMonth.now())};
		Consoles.log("", args4);
		Object[] args3 = new Object[]{Dates.toLocalTime(Year.now())};
		Consoles.log("", args3);
		Object[] args2 = new Object[]{Dates.toLocalTime(MonthDay.now())};
		Consoles.log("", args2);
		Object[] args1 = new Object[]{Dates.toLocalTime(Month.from(LocalDate.now()))};
		Consoles.log("", args1);
		Object[] args = new Object[]{Dates.toLocalTime(DayOfWeek.from(LocalDate.now()))};
		Consoles.log("", args);
	}

	@Test
	void test_toLocalDate() {
		Object[] args10 = new Object[]{Dates.toLocalDate(Instant.now())};
		Consoles.log("", args10);
		Object[] args9 = new Object[]{Dates.toLocalDate(ZonedDateTime.now())};
		Consoles.log("", args9);
		Object[] args8 = new Object[]{Dates.toLocalDate(OffsetDateTime.now())};
		Consoles.log("", args8);
		Object[] args7 = new Object[]{Dates.toLocalDate(LocalDateTime.now())};
		Consoles.log("", args7);
		Object[] args6 = new Object[]{Dates.toLocalDate(LocalDate.now())};
		Consoles.log("", args6);
		Object[] args5 = new Object[]{Dates.toLocalDate(LocalTime.now())};
		Consoles.log("", args5);
		Object[] args4 = new Object[]{Dates.toLocalDate(YearMonth.now())};
		Consoles.log("", args4);
		Object[] args3 = new Object[]{Dates.toLocalDate(Year.now())};
		Consoles.log("", args3);
		Object[] args2 = new Object[]{Dates.toLocalDate(MonthDay.now())};
		Consoles.log("", args2);
		Object[] args1 = new Object[]{Dates.toLocalDate(Month.from(LocalDate.now()))};
		Consoles.log("", args1);
		Object[] args = new Object[]{Dates.toLocalDate(DayOfWeek.from(LocalDate.now()))};
		Consoles.log("", args);
	}


	@Test
	void test_diff() {
		{
			TemporalAccessor t1 = Dates.parse("2020-03-15 11:30:00.000");
			TemporalAccessor t2 = Dates.parse("2022-03-16 12:30:00.000");
			Object[] args7 = new Object[]{Dates.diffYears(t1, t2)};
			Consoles.log("diffYears: {}", args7);
			Object[] args6 = new Object[]{Dates.diffMonths(t1, t2)};
			Consoles.log("diffMonths: {}", args6);
			Object[] args5 = new Object[]{Dates.diffDays(t1, t2)};
			Consoles.log("diffDays: {}", args5);
			Object[] args4 = new Object[]{Dates.diffHours(t1, t2)};
			Consoles.log("diffHours: {}", args4);
			Object[] args3 = new Object[]{Dates.diffMinutes(t1, t2)};
			Consoles.log("diffMinutes: {}", args3);
			Object[] args2 = new Object[]{Dates.diffSeconds(t1, t2)};
			Consoles.log("diffSeconds: {}", args2);
			Object[] args1 = new Object[]{Dates.diffMillis(t1, t2)};
			Consoles.log("diffMillis: {}", args1);
			Object[] args = new Object[]{Dates.diffNanos(t1, t2)};
			Consoles.log("diffNanos: {}", args);
		}
	}
}
