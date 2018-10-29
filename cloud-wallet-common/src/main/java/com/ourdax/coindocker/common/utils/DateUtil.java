package com.ourdax.coindocker.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public static Date now() {
        return new Date();
    }

    public static String now(String format) {
        return DateFormatUtil.format(now(), format);
    }

    public static Date addYears(Date date, long years) {
        LocalDateTime ldt = toLocalDateTime(date);
        return toDate(ldt.plusYears(years));
    }

    public static Date minusYears(Date date, long years) {
        return addYears(date, -years);
    }

    public static Date addMonths(Date date, long months) {
        LocalDateTime ldt = toLocalDateTime(date);
        return toDate(ldt.plusMonths(months));
    }

    public static Date minusMonths(Date date, long months) {
        return addMonths(date, -months);
    }

    public static Date addDays(Date date, long days) {
        LocalDateTime ldt = toLocalDateTime(date);
        return toDate(ldt.plusDays(days));
    }

    public static Date minusDays(Date date, long days) {
        return addDays(date, -days);
    }

    public static Date addHours(Date date, long hours) {
        LocalDateTime ldt = toLocalDateTime(date);
        return toDate(ldt.plusHours(hours));
    }

    public static Date minusHours(Date date, long hours) {
        return addHours(date, -hours);
    }

    public static Date addMinutes(Date date, long minutes) {
        LocalDateTime ldt = toLocalDateTime(date);
        return toDate(ldt.plusMinutes(minutes));
    }

    public static Date minusMinutes(Date date, long minutes) {
        return addMinutes(date, -minutes);
    }

    public static Date addSeconds(Date date, long seconds) {
        LocalDateTime ldt = toLocalDateTime(date);
        return toDate(ldt.plusSeconds(seconds));
    }

    public static Date minusSeconds(Date date, long seconds) {
        return addSeconds(date, -seconds);
    }

    public static Date addWeeks(Date date, long weeks) {
        LocalDateTime ldt = toLocalDateTime(date);
        return toDate(ldt.plusWeeks(weeks));
    }

    public static Date minusWeeks(Date date, long weeks) {
        return addWeeks(date, -weeks);
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }


    public static Date oneDayBefore(Date date) {
        return addDays(date, -1);
    }

    public static Date yesterday() {
        return addDays(now(), -1);
    }

    public static Date beginOfYesterday() {
        return addDays(beginOfToday(), -1);
    }

    public static Date endOfYesterday() {
        return addDays(endOfToday(), -1);
    }

    public static Date beginOfDay(Date date) {
        LocalDateTime ldt = toLocalDateTime(date);
        return Date.from(ldt.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date beginOfToday() {
        return beginOfDay(now());
    }

    public static Date endOfDay(Date data) {
        LocalDateTime ldt = toLocalDateTime(data);
        LocalDateTime endOfDay = ldt.toLocalDate().atTime(23, 59, 59);
        return toDate(endOfDay);
    }

    public static Date endOfToday() {
        return endOfDay(now());
    }

}