package com.ourdax.coindocker.common.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * @author hongzong.li
 */
public final class DateFormatUtil {
    private DateFormatUtil() {}

    private static LazyMap<String, FastDateFormat> dateFormatLazyMap =
            new LazyMap<String, FastDateFormat>() {
                @Override
                protected FastDateFormat load(String key) {
                    return FastDateFormat.getInstance(key);
                }
            };

    private static LazyMap<String, ThreadLocal<SimpleDateFormat>> dateParseLazyMap =
            new LazyMap<String, ThreadLocal<SimpleDateFormat>>() {
                @Override
                protected ThreadLocal<SimpleDateFormat> load(final String format) {
                    return new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(format);
                        }
                    };
                }
            };

    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String y4M2d2 = "yyyy-MM-dd";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
    public static final String y4M2d2H2m2s2 = "yyyy-MM-dd HH:mm:ss";
    public static final String H2m2s2 = "HH:mm:ss";


    // ----------------------------Parse----------------------------

    // 2016-06-22
    public static Date parse4y2M2d(String date) {
        return parse(date, y4M2d2);
    }

    // 20160622
    public static Date parseyyyyMMdd(String date) {
        return parse(date, yyyyMMdd);
    }

    // 2016-06-22 12:00:00
    public static Date parse4y2M2d2H2m2s(String date) {
        return parse(date, y4M2d2H2m2s2);
    }

    // 20160622120000
    public static Date parseyyyyMMddHHmmss(String date) {
        return parse(date, yyyyMMddHHmmss);
    }

    public static Date parse(String date, String format) {
        checkNotNull(date);
        checkNotNull(format);
        try {
            return dateParseLazyMap.get(format).get().parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean validFormat(String date, String format) {
        try {
            parse(date, format);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //---------------------------Format--------------------------------

    // 2016-06-22
    public static String format4y2M2d(Date date) {
        return format(date, y4M2d2);
    }

    // 20160622
    public static String formatyyyyMMdd(Date date) {
        return format(date, yyyyMMdd);
    }

    // 20160622120000
    public static String formatyyyyMMddHHmmss(Date date) {
        return format(date, yyyyMMddHHmmss);
    }

    // 20160801154136093
    public static String formatyyyyMMddHHmmssSSS(Date date) {
        return format(date, yyyyMMddHHmmssSSS);
    }

    // 2016-06-22 12:00:00
    public static String format4y2M2d2H2m2s(Date date) {
        return format(date, y4M2d2H2m2s2);
    }

    // 12:00:00
    public static String format2H2m2s(Date date) {
        return format(date, H2m2s2);
    }

    public static String format(Date date, String format) {
        checkNotNull(date);
        checkNotNull(format);
        return dateFormatLazyMap.get(format).format(date);
    }

    static abstract class LazyMap<K, V> {
        private ConcurrentMap<K, V> map = Maps.newConcurrentMap();

        public V get(K key) {
            V value = map.get(key);
            if (value != null)
                return value;

            value = load(key);
            V existsValue = (value == null ? null : map.putIfAbsent(key, value));// NullPointerException
            return existsValue == null ? value : existsValue;
        }

        protected abstract V load(K key);
    }
}