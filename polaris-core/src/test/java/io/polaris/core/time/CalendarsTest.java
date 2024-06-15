package io.polaris.core.time;

import java.util.Calendar;
import java.util.function.Function;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CalendarsTest {

	@Test
	public void testCeilWithMonth() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.ceil(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-02-01 12:34:56.789")
				, Calendar.MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2022-01-01 00:00:00.000", result);
	}

	@Test
	public void testFloorWithMonth() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.floor(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-02-01 12:34:56.789")
				, Calendar.MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-01-01 00:00:00.000", result);
	}

	@Test
	public void testRoundWithMonth1() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.round(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-07-01 12:34:56.789")
				, Calendar.MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2022-01-01 00:00:00.000", result);
	}

	@Test
	public void testRoundWithMonth2() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.round(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-06-01 12:34:56.789")
				, Calendar.MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-01-01 00:00:00.000", result);
	}


	@Test
	public void testCeilWithDay() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.ceil(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-02-15 12:34:56.789")
				, Calendar.DAY_OF_MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-03-01 00:00:00.000", result);
	}


	@Test
	public void testFloorWithDay() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.floor(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-02-15 12:34:56.789")
				, Calendar.DAY_OF_MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-02-01 00:00:00.000", result);
	}

	@Test
	public void testRoundWithDay1() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.round(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-02-13 12:34:56.789")
				, Calendar.DAY_OF_MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-02-01 00:00:00.000", result);
	}

	@Test
	public void testRoundWithDay2() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.round(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-02-14 12:34:56.789")
				, Calendar.DAY_OF_MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-02-01 00:00:00.000", result);
	}

	@Test
	public void testRoundWithDay3() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.round(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-01-15 12:34:56.789")
				, Calendar.DAY_OF_MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-01-01 00:00:00.000", result);
	}

	@Test
	public void testRoundWithDay4() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.round(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-01-16 12:34:56.789")
				, Calendar.DAY_OF_MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-02-01 00:00:00.000", result);
	}

	@Test
	public void testRoundWithDay5() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.round(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-04-15 12:34:56.789")
				, Calendar.DAY_OF_MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-04-01 00:00:00.000", result);
	}

	@Test
	public void testRoundWithDay6() {
		String result = Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS,
			Calendars.round(
				Calendars.parse(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS
					, "2021-04-16 12:34:56.789")
				, Calendar.DAY_OF_MONTH));
		TestConsole.printx(result);
		Assertions.assertEquals("2021-05-01 00:00:00.000", result);
	}


	@Test
	public void testAllWithHourOfDay() {
		int field = Calendar.HOUR_OF_DAY;
		Function<String, Calendar> ceil = c -> Calendars.ceil(Calendars.parse(c), field);
		Function<String, Calendar> floor = c -> Calendars.floor(Calendars.parse(c), field);
		Function<String, Calendar> round = c -> Calendars.round(Calendars.parse(c), field);
		Function<Calendar, String> format = c -> Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS, c);

		{
			String o = "2021-02-15 11:34:56.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-16 00:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 00:00:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 00:00:00.000", format.apply(round.apply(o)));
		}
		{
			String o = "2021-02-15 12:34:56.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-16 00:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 00:00:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-16 00:00:00.000", format.apply(round.apply(o)));
		}

	}

	@Test
	public void testAllWithHour() {
		int field = Calendar.HOUR;
		Function<String, Calendar> ceil = c -> Calendars.ceil(Calendars.parse(c), field);
		Function<String, Calendar> floor = c -> Calendars.floor(Calendars.parse(c), field);
		Function<String, Calendar> round = c -> Calendars.round(Calendars.parse(c), field);
		Function<Calendar, String> format = c -> Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS, c);

		{
			String o = "2021-02-15 05:34:56.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 00:00:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 00:00:00.000", format.apply(round.apply(o)));
		}
		{
			String o = "2021-02-15 06:34:56.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 00:00:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(round.apply(o)));
		}

		{
			String o = "2021-02-15 12:34:56.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-16 00:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(round.apply(o)));
		}

		{
			String o = "2021-02-15 17:34:56.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-16 00:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(round.apply(o)));
		}
		{
			String o = "2021-02-15 18:34:56.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-16 00:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-16 00:00:00.000", format.apply(round.apply(o)));
		}

	}


	@Test
	public void testAllWithMinute() {
		int field = Calendar.MINUTE;
		Function<String, Calendar> ceil = c -> Calendars.ceil(Calendars.parse(c), field);
		Function<String, Calendar> floor = c -> Calendars.floor(Calendars.parse(c), field);
		Function<String, Calendar> round = c -> Calendars.round(Calendars.parse(c), field);
		Function<Calendar, String> format = c -> Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS, c);

		{
			String o = "2021-02-15 11:29:56.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 11:00:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 11:00:00.000", format.apply(round.apply(o)));
		}
		{
			String o = "2021-02-15 11:30:56.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 11:00:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(round.apply(o)));
		}
	}

	@Test
	public void testAllWithSecond() {
		int field = Calendar.SECOND;
		Function<String, Calendar> ceil = c -> Calendars.ceil(Calendars.parse(c), field);
		Function<String, Calendar> floor = c -> Calendars.floor(Calendars.parse(c), field);
		Function<String, Calendar> round = c -> Calendars.round(Calendars.parse(c), field);
		Function<Calendar, String> format = c -> Calendars.format(Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS, c);

		{
			String o = "2021-02-15 11:29:29.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-15 11:30:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 11:29:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 11:29:00.000", format.apply(round.apply(o)));
		}
		{
			String o = "2021-02-15 11:29:30.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-15 11:30:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 11:29:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 11:30:00.000", format.apply(round.apply(o)));
		}
		{
			String o = "2021-02-15 11:59:30.789";
			TestConsole.printx("origin: {} | ceil:  {}", o, format.apply(ceil.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(ceil.apply(o)));
			TestConsole.printx("origin: {} | floor: {}", o, format.apply(floor.apply(o)));
			Assertions.assertEquals("2021-02-15 11:59:00.000", format.apply(floor.apply(o)));
			TestConsole.printx("origin: {} | round: {}", o, format.apply(round.apply(o)));
			Assertions.assertEquals("2021-02-15 12:00:00.000", format.apply(round.apply(o)));
		}
	}
}
