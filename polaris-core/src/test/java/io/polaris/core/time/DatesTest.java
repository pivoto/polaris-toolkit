package io.polaris.core.time;

import java.time.*;
import java.time.temporal.TemporalAccessor;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

class DatesTest {


	@Test
	void test_formatDefault() {
		TestConsole.printx(Dates.nowStr());
		TestConsole.printx(Dates.formatDefault(Instant.now()));
		TestConsole.printx(Dates.formatDefault(LocalDateTime.now()));
		TestConsole.printx(Dates.formatDefault(LocalDate.now()));
		TestConsole.printx(Dates.formatDefault(LocalTime.now()));
	}

	@Test
	void test_toLocalDateTime() {
		TestConsole.printx(Dates.toLocalDateTime(Instant.now()));
		TestConsole.printx(Dates.toLocalDateTime(ZonedDateTime.now()));
		TestConsole.printx(Dates.toLocalDateTime(OffsetDateTime.now()));
		TestConsole.printx(Dates.toLocalDateTime(LocalDateTime.now()));
		TestConsole.printx(Dates.toLocalDateTime(LocalDate.now()));
		TestConsole.printx(Dates.toLocalDateTime(LocalTime.now()));
		TestConsole.printx(Dates.toLocalDateTime(YearMonth.now()));
		TestConsole.printx(Dates.toLocalDateTime(Year.now()));
		TestConsole.printx(Dates.toLocalDateTime(MonthDay.now()));
		TestConsole.printx(Dates.toLocalDateTime(Month.from(LocalDate.now())));
		TestConsole.printx(Dates.toLocalDateTime(DayOfWeek.from(LocalDate.now())));
	}

	@Test
	void test_toLocalTime() {
		TestConsole.printx(Dates.toLocalTime(Instant.now()));
		TestConsole.printx(Dates.toLocalTime(ZonedDateTime.now()));
		TestConsole.printx(Dates.toLocalTime(OffsetDateTime.now()));
		TestConsole.printx(Dates.toLocalTime(LocalDateTime.now()));
		TestConsole.printx(Dates.toLocalTime(LocalDate.now()));
		TestConsole.printx(Dates.toLocalTime(LocalTime.now()));
		TestConsole.printx(Dates.toLocalTime(YearMonth.now()));
		TestConsole.printx(Dates.toLocalTime(Year.now()));
		TestConsole.printx(Dates.toLocalTime(MonthDay.now()));
		TestConsole.printx(Dates.toLocalTime(Month.from(LocalDate.now())));
		TestConsole.printx(Dates.toLocalTime(DayOfWeek.from(LocalDate.now())));
	}

	@Test
	void test_toLocalDate() {
		TestConsole.printx(Dates.toLocalDate(Instant.now()));
		TestConsole.printx(Dates.toLocalDate(ZonedDateTime.now()));
		TestConsole.printx(Dates.toLocalDate(OffsetDateTime.now()));
		TestConsole.printx(Dates.toLocalDate(LocalDateTime.now()));
		TestConsole.printx(Dates.toLocalDate(LocalDate.now()));
		TestConsole.printx(Dates.toLocalDate(LocalTime.now()));
		TestConsole.printx(Dates.toLocalDate(YearMonth.now()));
		TestConsole.printx(Dates.toLocalDate(Year.now()));
		TestConsole.printx(Dates.toLocalDate(MonthDay.now()));
		TestConsole.printx(Dates.toLocalDate(Month.from(LocalDate.now())));
		TestConsole.printx(Dates.toLocalDate(DayOfWeek.from(LocalDate.now())));
	}


	@Test
	void test_diff() {
		{
			TemporalAccessor t1 = Dates.parse("2020-03-15 11:30:00.000");
			TemporalAccessor t2 = Dates.parse("2022-03-16 12:30:00.000");
			TestConsole.printx("diffYears: {}", Dates.diffYears(t1, t2));
			TestConsole.printx("diffMonths: {}", Dates.diffMonths(t1, t2));
			TestConsole.printx("diffDays: {}", Dates.diffDays(t1, t2));
			TestConsole.printx("diffHours: {}", Dates.diffHours(t1, t2));
			TestConsole.printx("diffMinutes: {}", Dates.diffMinutes(t1, t2));
			TestConsole.printx("diffSeconds: {}", Dates.diffSeconds(t1, t2));
			TestConsole.printx("diffMillis: {}", Dates.diffMillis(t1, t2));
			TestConsole.printx("diffNanos: {}", Dates.diffNanos(t1, t2));
		}
	}
}
