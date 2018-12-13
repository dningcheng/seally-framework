package org.seally.base.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @Description 基于JDK8新的日期API工具类，新的日期API中对日期和时间表示划分的比较清晰，分别用对应的类实例来表示，建议使用工具类时简要阅读以下两点说明：
 *
 * 一、JDK8新的日期时间包日期API中本具类用到的几个常用日期或是时间类：
 * 1、YearMonth：该类只能存储年、月2个信息，因此最小精度到月，通常用来表示固定年月组合日期如信用卡到期日等
 * 2、LocalDate：该类只能存储年、月、日3个信息，因此最小精度到日，通常用于只需要精确到具体每一天的日期如生日等
 * 3、LocalTime：该类只能存储时、分、秒、纳秒4个信息，最大精度为小时，最小精度为纳秒，通常用于表示每天内循环需要处理某事的时间表示
 * 4、LocalDateTime：该类刚好包含LocalDate、LocalTime的信息，构成我们常用的日期时间，在功能范围上可以等价于原Date类，工具方法中也提供了这两个类之间的相互转化和计算
 * 
 * 二、在此工具类中提供的常用方法简要说明
 * 1、parse*系列方法为解析相关【完成各种日期时间字符串--->解析--->日期时间对象】
 * 2、format*系列方法为格式化相关【完成日期时间对象/日期时间字符串--->格式化--->指定格式的新字符串】
 * 3、to*系列方法为转化相关【完成日期时间对象/日期时间字符串--->转化--->其它日期时间对象】
 * 4、toMin*系列方法为转化相关【完成日期时间对象--->转化--->当天00时00分00秒日期时间对象（当天时间上的最小值）】
 * 5、toMax*系列方法为转化相关【完成日期时间对象--->转化--->当天23时59分59秒99999999纳秒日期时间对象（当天时间上的最大值）】
 * 6、adjust*系列方法兼具计算、转化、格式化相关【完成日期时间对象/日期时间字符串--->计算、转化、格式化--->新的日期时间对象/日期时间字符串】
 * 7、betweenOf系列方法为计算各种日期间的差值【返回差距的正负值】
 * 8、distanceOf系列方法为计算各种日期间的绝对差值【返回正值距离】
 * 9、lengthOf系列方法计算指定日期对应月/年的天数
 * 10、dayOfWeek系列方法计算指定日期当天星期几
 * 
 * @Date 2018年12月10日
 * @author 邓宁城
 */
public class DateNUtil {
	
	private static final Map<String,DateTimeFormatter> CACHE_FORMATTER_MAP = new HashMap<>();
	
	/**============最小精度到达"年"的常用格式区域（后期如有新加此类格式，请放置此区域）===================**/
	public static final String STRING_FORMAT_TO_YEAR01 = "yyyy";
	public static final String STRING_FORMAT_TO_YEAR02 = "yy";
	public static final String STRING_FORMAT_TO_YEAR03 = "yyyy年";
	
	/**============最小精度到达"月"的常用格式区域（后期如有新加此类格式，请放置此区域）===================**/
	public static final String STRING_FORMAT_TO_MONTH01 = "yyyy-MM";
	public static final String STRING_FORMAT_TO_MONTH02 = "yyyy/MM";
	public static final String STRING_FORMAT_TO_MONTH03 = "yyyy.MM";
	public static final String STRING_FORMAT_TO_MONTH04 = "MM.yyyy";
	public static final String STRING_FORMAT_TO_MONTH05 = "MM yyyy";
	public static final String STRING_FORMAT_TO_MONTH06 = "yyyyMM";
	public static final String STRING_FORMAT_TO_MONTH07 = "yyyy年MM月";
	public static final String STRING_FORMAT_TO_MONTH08 = "MM-yy";
	public static final String STRING_FORMAT_TO_MONTH09 = "yyMM";
	public static final String STRING_FORMAT_TO_MONTH10 = "MM/yyyy";
	
	/**============最小精度到达"天"的常用格式区域（后期如有新加此类格式，请放置此区域）===================**/
	public static final String STRING_FORMAT_TO_DAY01 = "yyyy-MM-dd";
	public static final String STRING_FORMAT_TO_DAY02 = "yyyy/MM/dd";
	public static final String STRING_FORMAT_TO_DAY03 = "yyyy.MM.dd";
	public static final String STRING_FORMAT_TO_DAY04 = "dd.MM.yyyy";
	public static final String STRING_FORMAT_TO_DAY05 = "dd MM yyyy";
	public static final String STRING_FORMAT_TO_DAY06 = "yyyyMMdd";
	public static final String STRING_FORMAT_TO_DAY07 = "yyyy年MM月dd日";
	public static final String STRING_FORMAT_TO_DAY08 = "MM-dd-yy";
	public static final String STRING_FORMAT_TO_DAY09 = "dd-MM-yy";
	public static final String STRING_FORMAT_TO_DAY10 = "yyMMdd";
	public static final String STRING_FORMAT_TO_DAY11 = "MM/dd/yyyy";
	
	/**============最小精度到达"小时"的常用格式区域（后期如有新加此类格式，请放置此区域）===================**/
	public static final String STRING_FORMAT_TO_HOUR01 = "yyyyMMddHH";
	
	/**============最小精度到达"分钟"的常用格式区域（后期如有新加此类格式，请放置此区域）===================**/
	public static final String STRING_FORMAT_TO_MINUTE01 = "yyyy-MM-dd HH:mm";
	public static final String STRING_FORMAT_TO_MINUTE02 = "MM/dd/yyyy HH:mm a";
	public static final String STRING_FORMAT_TO_MINUTE03 = "yyyyMMddHHmm";
	public static final String STRING_FORMAT_TO_MINUTE04 = "MM月dd日 HH:mm";
	public static final String STRING_FORMAT_TO_MINUTE05 = "yyyy年MM月dd日 HH:mm";
	public static final String STRING_FORMAT_TO_MINUTE06 = "yy-MM-dd HH:mm";
	public static final String STRING_FORMAT_TO_MINUTE07 = "yyyy年MM月dd日 HH时mm分";
	public static final String STRING_FORMAT_TO_MINUTE08 = "yyyy/MM/dd HH:mm";
	public static final String STRING_FORMAT_TO_MINUTE09 = "dd.MM.yyyy HH:mm";
	
	/**============最小精度到达"秒钟"的常用格式区域（后期如有新加此类格式，请放置此区域）===================**/
	public static final String STRING_FORMAT_TO_SECOND01 = "yyyy-MM-dd HH:mm:ss";
	public static final String STRING_FORMAT_TO_SECOND02 = "MM/dd/yyyy HH:mm:ss a";
	public static final String STRING_FORMAT_TO_SECOND03 = "yyyyMMddHHmmss";
	public static final String STRING_FORMAT_TO_SECOND04 = "MM月dd日 HH:mm:ss";
	public static final String STRING_FORMAT_TO_SECOND05 = "yyyy年MM月dd日 HH:mm:ss";
	public static final String STRING_FORMAT_TO_SECOND06 = "yy-MM-dd HH:mm:ss";
	public static final String STRING_FORMAT_TO_SECOND07 = "yyyy年MM月dd日 HH时mm分ss秒";
	public static final String STRING_FORMAT_TO_SECOND08 = "yyyy/MM/dd HH:mm:ss";
	public static final String STRING_FORMAT_TO_SECOND09 = "dd.MM.yyyy HH:mm:ss";
	
	/**============最小精度到达"毫秒"的常用格式区域（后期如有新加此类格式，请放置此区域）===================**/
	public static final String STRING_FORMAT_TO_MILLISECOND01 = "yyyy-MM-dd HH:mm:ss.SSS";
	
	
	/**
	 * @Description 初始化一个格式类
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param pattern
	 * @return
	 */
	private static DateTimeFormatter initFormatter(String pattern){
		if(DateNUtil.CACHE_FORMATTER_MAP.containsKey(pattern)){
			return DateNUtil.CACHE_FORMATTER_MAP.get(pattern);
		}
		DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
		DateNUtil.CACHE_FORMATTER_MAP.put(pattern, format);
		return format;
	}
	
	/**
	 * @Description 获取指定两个日期间的所有日期yyyyMMdd格式集合，如入参：20181230，20190102 --->出参： [20181230,20181231,20190101]
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param minDay 开始日期（yyyyMMdd）返回结果包含
	 * @param maxDay 结束日期（yyyyMMdd）返回结果不包含
	 * @return
	 */
	public static List<Integer> listBetweenDays(Integer minDay,Integer maxDay){
		//遍历获取所需要返回的日期键值
		List<Integer> resultKeys = new ArrayList<>();
		for(int tempDate = minDay ; tempDate < maxDay ; ){
			resultKeys.add(tempDate);
			String checkDate = tempDate+"";
			if(Pattern.matches("^\\d*(28|29|30|31)$", checkDate)){
				tempDate = Integer.parseInt(adjustLocalDateToString(checkDate, "yyyyMMdd","yyyyMMdd", ChronoUnit.DAYS, 1));
			}else{
				tempDate ++;
			}
		}
		return resultKeys;
	}
	
	/**
	 * @Description 获取指定两个日期间的所有日期yyyyMM格式集合，如入参：201812，201903 --->出参： [201812,201901,201902]
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param minMonth 开始日期（yyyyMM）返回结果包含
	 * @param maxMonth 结束日期（yyyyMM）返回结果不包含
	 * @return
	 */
	public static List<Integer> listBetweenMonths(Integer minMonth,Integer maxMonth){
		//遍历获取所需要返回的日期键值
		List<Integer> resultKeys = new ArrayList<>();
		
		for(int tempMonth = minMonth ; tempMonth < maxMonth ; ){
			resultKeys.add(tempMonth);
			String checkMonth = tempMonth+"";
			if(Pattern.matches("^\\d*(12)$", checkMonth)){
				tempMonth = Integer.parseInt(adjustYearMonthToString(checkMonth, "yyyyMM","yyyyMM", ChronoUnit.MONTHS, 1));
			}else{
				tempMonth ++;
			}
		}
		return resultKeys;
	}
	
	/**
	 * @Description 获取指定日期时间的月份总天数（1-28/29/30/31）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int lengthOfMonth(Date source){
		Objects.requireNonNull(source, "source");
		return LocalDate.from(toLocalDateTime(source)).lengthOfMonth();
	}
	
	/**
	 * @Description 获取指定日期时间的月份总天数（1-28/29/30/31）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int lengthOfMonth(LocalDateTime source){
		Objects.requireNonNull(source, "source");
		return LocalDate.from(source).lengthOfMonth();
	}
	
	/**
	 * @Description 获取指定日期时间的月份总天数（1-28/29/30/31）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int lengthOfMonth(LocalDate source){
		Objects.requireNonNull(source, "source");
		return source.lengthOfMonth();
	}
	
	/**
	 * @Description 获取指定日期时间的月份总天数（1-28/29/30/31）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int lengthOfMonth(YearMonth source){
		Objects.requireNonNull(source, "source");
		return source.lengthOfMonth();
	}
	
	/**
	 * @Description 获取指定字符串日期的月份总天数（1-28/29/30/31）
	 * @remark 注意：sourceType代表了传递字符不同的精度，调用此方法请根据下面精度数值说明传递
	 * 精度A:不传递/null=低精度 yyyy、MM的组合
	 * 精度B:1=中精度 yyyy、MM、dd的组合
	 * 精度C:2=高精度 yyyy、MM、dd、HH、mm、ss的组合
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原字符串
	 * @param sourcePattern 原字符串格式
	 * @param sourceType 精度级别
	 * @return
	 */
	public static int lengthOfMonth(String source,String sourcePattern,Integer sourceType){
		sourceType = null == sourceType ? 0 : sourceType;
		switch (sourceType) {
		case 1:
			return parseLocalDate(source, sourcePattern).lengthOfMonth();
		case 2:
			return LocalDate.from(parseLocalDateTime(source, sourcePattern)).lengthOfMonth();
		default:
			return parseYearMonth(source, sourcePattern).lengthOfMonth();
		}
	}
	
	/**
	 * @Description 获取指定日期时间的一年总天数（1-365/366）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param year yyyy格式四位年份整数
	 * @return
	 */
	public static int lengthOfYear(Integer year){
		Objects.requireNonNull(year, "year");
		return YearMonth.of(year, 1).lengthOfYear();
	}
	
	/**
	 * @Description 获取指定日期时间的一年总天数（1-365/366）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int lengthOfYear(Date source){
		Objects.requireNonNull(source, "source");
		return LocalDate.from(toLocalDateTime(source)).lengthOfYear();
	}
	
	/**
	 * @Description 获取指定日期时间的一年总天数（1-365/366）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int lengthOfYear(LocalDateTime source){
		Objects.requireNonNull(source, "source");
		return LocalDate.from(source).lengthOfYear();
	}
	
	/**
	 * @Description 获取指定日期时间的一年总天数（1-365/366）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int lengthOfYear(LocalDate source){
		Objects.requireNonNull(source, "source");
		return source.lengthOfYear();
	}
	
	/**
	 * @Description 获取指定日期时间的一年总天数（1-365/366）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int lengthOfYear(YearMonth source){
		Objects.requireNonNull(source, "source");
		return source.lengthOfYear();
	}
	
	/**
	 * @Description 获取指定字符串日期的一年总天数（1-365/366）
	 * @remark 注意：sourceType代表了传递字符不同的串精度，调用此方法请更具下面精度数值说明传递
	 * 精度A:不传递/null=低精度 yyyy、MM的组合
	 * 精度B:1=中精度 yyyy、MM、dd的组合
	 * 精度C:2=高精度 yyyy、MM、dd、HH、mm、ss的组合
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原字符串
	 * @param sourcePattern 原字符串格式
	 * @param sourceType 精度级别
	 * @return
	 */
	public static int lengthOfYear(String source,String sourcePattern,Integer sourceType){
		sourceType = null == sourceType ? 0 : sourceType;
		switch (sourceType) {
		case 1:
			return parseLocalDate(source, sourcePattern).lengthOfYear();
		case 2:
			return LocalDate.from(parseLocalDateTime(source, sourcePattern)).lengthOfYear();
		default:
			return parseYearMonth(source, sourcePattern).lengthOfYear();
		}
	}
	
	/**
	 * @Description 获取指定日期的星期几（1-7）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int dayOfWeek(Date source){
		return toLocalDateTime(source).getDayOfWeek().getValue();
	}
	
	/**
	 * @Description 获取指定日期的星期几（1-7）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static int dayOfWeek(LocalDateTime source){
		return source.getDayOfWeek().getValue();
	}
	
	/**
	 * @Description 获取指定日期的星期几（1-7）
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原日期
	 * @return
	 */
	public static int dayOfWeek(LocalDate source){
		return source.getDayOfWeek().getValue();
	}
	
	/**
	 * @Description 获取指定日期的星期几（1-7）
	 * @remark 注意：若isLocalDate=true代表传递字符串精度为yyyy、MM、dd的组合（低精度），且各自有且仅有一次出现，若isLocalDate=false代表精度为yyyy、MM、dd、HH、mm、ss的组合（高精度），且各自有且仅有一次出现
	 * @Date 2018年12月10日
	 * @author 邓宁城
	 * @param source 原字符串格式日期
	 * @param sourcePattern 原日期格式
	 * @param isLocalDate 是否是低精度格式格式
	 * @return
	 */
	public static int dayOfWeek(String source,String sourcePattern,boolean isLocalDate){
		if(isLocalDate){
			return parseLocalDate(source, sourcePattern).getDayOfWeek().getValue();
		}
		return parseLocalDateTime(source, sourcePattern).getDayOfWeek().getValue();
	}
	
	/**
	 * @Description 格式化成指定格式字符串
	 * @remark 参数returnPattern必须由yyyy、MM、dd三个的完整任意组合而成，每次出现次数不限，不能由y、yyy、M、d等不完整的组合出现，否则可能得不到想要的结果
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @param returnPattern 返回格式
	 * @return
	 */
	public static String formatLocalDateString(LocalDate source,String returnPattern){
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(returnPattern, "returnPattern");
		LocalDateTime localDateTime = LocalDateTime.of(source, LocalTime.of(0, 0, 0));
		return localDateTime.format(initFormatter(returnPattern));
	}
	
	/**
	 * @Description 格式化年月日格式字符串为其他指定年月日格式字符串
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM、dd有且仅有一次的任意顺序组合，否则会抛出异常
	 * @remark 注意：returnPattern必须由yyyy、yy、MM、dd的任务完整组合，次数不限，不能由yyy、y、m、d等不完整组合，否则可能得不到想要的结果
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串年月日
	 * @param sourcePattern 原字符串年月日格式
	 * @param returnPattern 返回字符串格式
	 * @return
	 */
	public static String formatLocalDateString(String source,String sourcePattern,String returnPattern){
		return formatLocalDateString(parseLocalDate(source, sourcePattern), returnPattern);
	}
	
	/**
	 * @Description 格式化成指定格式字符串
	 * @remark 注意：returnPattren参数请使用年月日时分秒单项的完整格式，即：yyyy、yy、MM、dd、hh、mm、ss任意组合，不能使用y、yyy、M、d、h、m、s的不完整组合，否则可能得不到想要的格式结果
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @param returnPattern 返回格式
	 * @return
	 */
	public static String formatDateTimeString(Date source,String returnPattern){
		return adjustToString(source,returnPattern,null,0);
	}
	
	/**
	 * @Description 格式化成指定格式字符串
	 * @remark 注意：returnPattren参数请使用年月日时分秒单项的完整格式，即：yyyy、yy、MM、dd、hh、mm、ss任意组合，不能使用y、yyy、M、d、h、m、s的不完整组合，否则可能得不到想要的格式结果
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @param returnPattern 返回格式
	 * @return
	 */
	public static String formatDateTimeString(LocalDateTime source,String returnPattern){
		return adjustToString(source, returnPattern, null, 0);
	}
	
	/**
	 * @Description 格式化字符串日期成指定格式字符串
	 * @remark 注意：source以及sourcePattern格式必须由yyyy、MM、dd、HH、mm、ss有且仅有一次的任意顺序组合，否则将抛出异常
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原字符串格式日期时间
	 * @param sourcePattern 原字符串格式日期时间格式
	 * @param returnPattern 返回格式
	 * @return
	 */
	public static String formatDateTimeString(String source,String sourcePattern,String returnPattern){
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(sourcePattern, "sourcePattern");
		LocalDateTime dateTime = LocalDateTime.parse(source, initFormatter(sourcePattern));
		
		return adjustToString(dateTime, returnPattern, null, 0);
	}
	
	/**
	 * @Description LocalDateTime转化为Date
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static Date toDate(LocalDateTime source){
		Objects.requireNonNull(source, "source");
		return Date.from(source.toInstant(ZoneOffset.ofHours(8)));
	}
	
	/**
	 * @Description Date转化为LocalDate
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原Date
	 * @return
	 */
	public static LocalDate toLocalDate(Date source){
		return LocalDate.from(toLocalDateTime(source));
	}
	
	/**
	 * @Description LocalDateTime转化为LocalDate
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原LocalDateTime
	 * @return
	 */
	public static LocalDate toLocalDate(LocalDateTime source){
		return LocalDate.from(source);
	}
	
	/**
	 * @Description Date转化为LocalDateTime
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @return
	 */
	public static LocalDateTime toLocalDateTime(Date source){
		Objects.requireNonNull(source, "source");
		return LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
	}
	
	/**
	 * @Description Date转化为当天最小Date
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static Date toMinDate(Date source){
		Objects.requireNonNull(source, "source");
		return toDate(LocalDateTime.of(LocalDate.from(toLocalDateTime(source)), LocalTime.MIN));
	}
	
	/**
	 * @Description LocalDate转化为当天最小Date
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static Date toMinDate(LocalDate source){
		Objects.requireNonNull(source, "source");
		return toDate(LocalDateTime.of(source, LocalTime.MIN));
	}
	
	/**
	 * @Description LocalDateTime转化为当天最小Date
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static Date toMinDate(LocalDateTime source){
		Objects.requireNonNull(source, "source");
		return toDate(LocalDateTime.of(LocalDate.from(source), LocalTime.MIN));
	}
	
	/**
	 * @Description Date转化为当天最小LocalDateTime
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static LocalDateTime toMinLocalDateTime(Date source){
		Objects.requireNonNull(source, "source");
		return LocalDateTime.of(LocalDate.from(toLocalDateTime(source)), LocalTime.MIN);
	}
	
	/**
	 * @Description LocalDate转化为当天最小LocalDateTime
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static LocalDateTime toMinLocalDateTime(LocalDate source){
		Objects.requireNonNull(source, "source");
		return LocalDateTime.of(source, LocalTime.MIN);
	}
	
	/**
	 * @Description LocalDateTime转化为当天最小LocalDateTime
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static LocalDateTime toMinLocalDateTime(LocalDateTime source){
		Objects.requireNonNull(source, "source");
		return LocalDateTime.of(LocalDate.from(source), LocalTime.MIN);
	}
	
	/**
	 * @Description Date转化为当天最大Date
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static Date toMaxDate(Date source){
		Objects.requireNonNull(source, "source");
		return toDate(LocalDateTime.of(LocalDate.from(toLocalDateTime(source)), LocalTime.MAX));
	}
	
	/**
	 * @Description LocalDate转化为当天最大Date
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static Date toMaxDate(LocalDate source){
		Objects.requireNonNull(source, "source");
		return toDate(LocalDateTime.of(source, LocalTime.MAX));
	}
	
	/**
	 * @Description LocalDateTime转化为当天最大Date
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static Date toMaxDate(LocalDateTime source){
		Objects.requireNonNull(source, "source");
		return toDate(LocalDateTime.of(LocalDate.from(source), LocalTime.MAX));
	}
	
	/**
	 * @Description Date转化为当天最大LocalDateTime
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static LocalDateTime toMaxLocalDateTime(Date source){
		Objects.requireNonNull(source, "source");
		return LocalDateTime.of(LocalDate.from(toLocalDateTime(source)), LocalTime.MAX);
	}
	
	/**
	 * @Description LocalDate转化为当天最大LocalDateTime
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static LocalDateTime toMaxLocalDateTime(LocalDate source){
		Objects.requireNonNull(source, "source");
		return LocalDateTime.of(source, LocalTime.MAX);
	}
	
	/**
	 * @Description LocalDateTime转化为当天最大LocalDateTime
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @return
	 */
	public static LocalDateTime toMaxLocalDateTime(LocalDateTime source){
		Objects.requireNonNull(source, "source");
		return LocalDateTime.of(LocalDate.from(source), LocalTime.MAX);
	}
	
	/**
	 * @Description 解析指定格式字符串年月为年月对象
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM有且仅有一次的完整格式组合而成，不能出现yyy、yy、y、M等不完整的组合，否则报异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串格式年月
	 * @param sourcePattern 原格式
	 * @return
	 */
	public static YearMonth parseYearMonth(String source,String sourcePattern){
		Objects.requireNonNull(source, "sourcePattern");
		return YearMonth.parse(source, initFormatter(sourcePattern));
	}
	
	/**
	 * @Description 解析字符串格式日期时间为Date
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM、dd、HH、mm、ss有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串格式日期时间
	 * @param sourcePattern 原字符串格式日期时间格式
	 * @return
	 */
	public static Date parseDate(String source,String sourcePattern){
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(sourcePattern, "sourcePattern");
		
		return toDate(LocalDateTime.parse(source, initFormatter(sourcePattern)));
	}
	
	/**
	 * @Description 解析字符串年月日为对象
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM、dd有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串年月日
	 * @param sourcePattern 原字符串年月日格式
	 * @return
	 */
	public static LocalDate parseLocalDate(String source,String sourcePattern){
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(sourcePattern, "sourcePattern");
		return LocalDate.parse(source, initFormatter(sourcePattern));
	}
	
	/**
	 * @Description 解析字符串格式日期时间为LocalDateTime
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM、dd、HH、mm、ss有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串格式日期时间
	 * @param sourcePattern 原字符串格式日期时间格式
	 * @return
	 */
	public static LocalDateTime parseLocalDateTime(String source,String sourcePattern){
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(sourcePattern, "sourcePattern");
		
		return LocalDateTime.parse(source, initFormatter(sourcePattern));
	}
	
	/**
	 * @Description 解析并计算字符串年月并反回计算后的结果
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM有且仅有一次的完整格式组合而成，即不能出现yyy、yy、y、M等不完整的组合，否则报异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串年月
	 * @param sourcePattern 原字符串年月格式
	 * @param adjustUnit 计算单位，可取值：ChronoUnit.YEARS、ChronoUnit.MONTHS共2个
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static YearMonth adjustToYearMonth(String source,String sourcePattern,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "sourcePattern");
		return adjustToYearMonth(parseYearMonth(source, sourcePattern), adjustUnit, adjustValue);
	}
	
	/**
	 * @Description 计算YearMonth反回计算后的结果
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月
	 * @param adjustUnit 计算单位，可取值：ChronoUnit.YEARS、ChronoUnit.MONTHS共2个
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static YearMonth adjustToYearMonth(YearMonth source,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "source");
		if(Objects.nonNull(adjustUnit)){
			if(adjustValue > 0){
				source = source.plus(adjustValue, adjustUnit);
			}else if(adjustValue < 0){
				source = source.minus(-adjustValue, adjustUnit);
			}
		}
		return source;
	}
	
	/**
	 * @Description 计算指定日期并返回计算结果日期
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原日期
	 * @param adjustUnit 计算单位
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static Date adjustToDate(Date source,ChronoUnit adjustUnit,int adjustValue){
		LocalDateTime localDateTime = adjustToLocalDateTime(source, adjustUnit, adjustValue);
		return toDate(localDateTime);
	}
	
	/**
	 * @Description 计算指定日期并返回计算结果日期时间
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原LocalDateTime日期时间
	 * @param adjustUnit 计算单位
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static Date adjustToDate(LocalDateTime source,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "source");
		 
		return toDate(adjustToLocalDateTime(source, adjustUnit, adjustValue));
	}
	
	/**
	 * @Description 解析并计算指定日期，返回日期时间结果
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM、dd、HH、mm、ss有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串格式日期时间
	 * @param sourcePattern 原字符串格式日期时间格式
	 * @param adjustUnit 计算单位
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static Date adjustToDate(String source,String sourcePattern,ChronoUnit adjustUnit,int adjustValue){
		return toDate(adjustToLocalDateTime(source, sourcePattern, adjustUnit, adjustValue));
	}
	
	/**
	 * @Description 计算指定日期并返回计算结果日期
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原Date日期时间
	 * @param adjustUnit 计算单位
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static LocalDateTime adjustToLocalDateTime(Date source,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "source");
		return adjustToLocalDateTime(toLocalDateTime(source), adjustUnit, adjustValue);
	}
	
	/**
	 * @Description 计算指定日期并返回计算结果日期
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原LocalDateTime日期时间
	 * @param adjustUnit 计算单位
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static LocalDateTime adjustToLocalDateTime(LocalDateTime source,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "source");
		
		if(Objects.nonNull(adjustUnit)){
			if(adjustValue > 0){
				source = source.plus(adjustValue, adjustUnit);
			}else if(adjustValue < 0){
				source = source.minus(-adjustValue, adjustUnit);
			}
		}
		
		return source;
	}
	
	/**
	 * @Description 解析并结算日期，返回计算结果
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM、dd、HH、mm、ss有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串格式日期时间
	 * @param sourcePattern 原字符串格式日期时间格式
	 * @param adjustUnit 计算单位
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static LocalDateTime adjustToLocalDateTime(String source,String sourcePattern,ChronoUnit adjustUnit,int adjustValue){
		LocalDateTime localDateTime = parseLocalDateTime(source, sourcePattern);
		return adjustToLocalDateTime(localDateTime, adjustUnit, adjustValue);
	}
	
	/**
	 * @Description 计算年月日
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @param adjustUnit 计算单位，可取ChronoUnit.YEARS、ChronoUnit.MONTHS、ChronoUnit.DAYS三个
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static LocalDate adjustToLocalDate(LocalDate source,ChronoUnit adjustUnit,int adjustValue){
		
		if(Objects.nonNull(adjustUnit)){
			if(adjustValue > 0){
				source = source.plus(adjustValue, adjustUnit);
			}else if(adjustValue < 0){
				source = source.minus(-adjustValue, adjustUnit);
			}
		}
		
		return source;
	}
	
	/**
	 * @Description 格式化并计算年月日
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM、dd有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串格式年月日
	 * @param sourcePattern 原字符串格式年月日格式
	 * @param adjustUnit 计算单位，可取ChronoUnit.YEARS、ChronoUnit.MONTHS、ChronoUnit.DAYS三个
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static LocalDate adjustToLocalDate(String source,String sourcePattern,ChronoUnit adjustUnit,int adjustValue){
		return adjustToLocalDate(parseLocalDate(source, sourcePattern), adjustUnit, adjustValue);
	}
	
	/**
	 * @Description 计算并格式化成指定格式字符串
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @param returnPattren 返回格式（注意请使用年月日时分秒的完整的即：yyyy、yy、MM、dd、hh、mm、ss任意组合，不能使用y、yyy、M、d、h、m、s的不完整组合，否则可能得不到想要的格式结果）
	 * @param adjustUnit 计算单位 ChronoUnit，见常量如：ChronoUnit.YEARS、ChronoUnit.MONTHS、ChronoUnit.WEEKS、ChronoUnit.DAYS
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static String adjustToString(Date source,String returnPattren,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "source");
		return adjustToString(toLocalDateTime(source), returnPattren, adjustUnit, adjustValue);
	}
	
	/**
	 * @Description 计算并格式化成指定格式字符串
	 * @remark 注意：returnPattren参数请使用年月日时分秒单项的完整格式，即：yyyy、yy、MM、dd、hh、mm、ss任意组合，不能使用y、yyy、M、d、h、m、s的不完整组合，否则可能得不到想要的格式结果
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原日期时间
	 * @param returnPattren 返回格式
	 * @param adjustUnit 计算单位 ChronoUnit，见常量如：ChronoUnit.YEARS、ChronoUnit.MONTHS、ChronoUnit.WEEKS、ChronoUnit.DAYS
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static String adjustToString(LocalDateTime source,String returnPattren,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(returnPattren, "returnPattren");
		
		if(Objects.nonNull(adjustUnit)){
			if(adjustValue > 0){
				source = source.plus(adjustValue, adjustUnit);
			}else if(adjustValue < 0){
				source = source.minus(-adjustValue, adjustUnit);
			}
		}
			
		return source.format(initFormatter(returnPattren));
	}
	
	/**
	 * @Description 计算并格式化成指定格式字符串
	 * @remark 注意：source以及sourcePattren必须由yyyy、MM、dd、HH、mm、ss有且仅有一次的任意顺序组合，否则将抛出异常
	 * @Date 2018年12月8日
	 * @author 邓宁城
	 * @param source 原字符串格式日期时间
	 * @param sourcePattren 原字符串格式日期时间格式
	 * @param returnPattern 计算后返回格式
	 * @param adjustUnit
	 * @param adjustValue
	 * @return
	 */
	public static String adjustLocalDateTimeToString(String source,String sourcePattren,String returnPattern,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(sourcePattren, "sourcePattren");
		LocalDateTime dateTime = LocalDateTime.parse(source, initFormatter(sourcePattren));
		
		return adjustToString(dateTime, returnPattern, adjustUnit, adjustValue);
	}
	
	/**
	 * @Description 计算并格式化成指定格式字符串
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM、dd有且仅有一次的任意顺序组合，否则会抛出异常
	 * @remark 注意：returnPattern必须由yyyy、yy、MM、dd的任务完整组合，次数不限，不能由yyy、y、m、d等不完整组合，否则可能得不到想要的结果
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串格式年月日
	 * @param sourcePattern 原字符串格式年月日格式
	 * @param returnPattern 返回格式
	 * @param adjustUnit 计算单位，可取ChronoUnit.YEARS、ChronoUnit.MONTHS、ChronoUnit.DAYS三个
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static String adjustLocalDateToString(String source,String sourcePattern,String returnPattern,ChronoUnit adjustUnit,int adjustValue){
		return adjustToLocalDate(parseLocalDate(source, sourcePattern), adjustUnit, adjustValue).format(initFormatter(returnPattern));
	}
	
	/**
	 * @Description 计算并格式化成指定格式字符串
	 * @remark 注意：returnPattern必须由yyyy、yy、MM、dd的任务完整组合，次数不限，不能由yyy、y、m、d等不完整组合，否则可能得不到想要的结果
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月日
	 * @param returnPattern 返回格式
	 * @param adjustUnit 计算单位可取以下值：
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static String adjustLocalDateToString(LocalDate source,String returnPattern,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "returnPattern");
		return adjustToLocalDate(source, adjustUnit, adjustValue).format(initFormatter(returnPattern));
	}
	
	/**
	 * @Description 计算并返回指定格式字符串
	 * @remark 注意：returnPattern必须由yyyy、MM两个完整格式的全部组合而成，即不能出现yyy、yy、y、M等不完整的组合，否则可能得不到想要的结果
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原年月
	 * @param returnPattern 返回格式
	 * @param adjustUnit 计算单位，可取值：ChronoUnit.YEARS、ChronoUnit.MONTHS共2个
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static String adjustYearMonthToString(YearMonth source,String returnPattern,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "returnPattern");
		return adjustToYearMonth(source, adjustUnit, adjustValue).format(initFormatter(returnPattern));
	}
	
	/**
	 * @Description 解析年月格式字符串并进行给定单位加减计算后返回指定格式新字符串
	 * @remark 注意：source以及sourcePattern必须由yyyy、MM有且仅有一次的完整格式组合而成，即不能出现yyy、yy、y、M等不完整的组合，否则报异常
	 * @remark 注意：returnPattern必须由yyyy、MM两个完整格式的全部组合而成，即不能出现yyy、yy、y、M等不完整的组合，否则可能得不到想要的结果
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param source 原字符串年月
	 * @param sourcePattern 原字符串年月格式
	 * @param returnPattern 返回字符串年月格式
	 * @param adjustUnit 计算单位，可取值：ChronoUnit.YEARS、ChronoUnit.MONTHS共2个
	 * @param adjustValue 加减数值
	 * @return
	 */
	public static String adjustYearMonthToString(String source,String sourcePattern,String returnPattern,ChronoUnit adjustUnit,int adjustValue){
		Objects.requireNonNull(source, "returnPattern");
		return adjustToYearMonth(source, sourcePattern, adjustUnit, adjustValue).format(initFormatter(returnPattern));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long betweenOf(Date min,Date max,ChronoUnit adjustUnit){
		return adjustUnit.between(toLocalDateTime(min), toLocalDateTime(max));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long betweenOf(LocalDateTime min,LocalDateTime max,ChronoUnit adjustUnit){
		return adjustUnit.between(min, max);
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long betweenOf(Date min,LocalDateTime max,ChronoUnit adjustUnit){
		return adjustUnit.between(toLocalDateTime(min), max);
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long betweenOf(LocalDateTime min,Date max,ChronoUnit adjustUnit){
		return adjustUnit.between(min,toLocalDateTime(max));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：min、minPattern、max、maxPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param minPattern
	 * @param max 计算目标大日期
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long betweenOf(String min,String minPattern,String max,String maxPattern,ChronoUnit adjustUnit){
		return adjustUnit.between(parseLocalDateTime(min, minPattern),parseLocalDateTime(max, maxPattern));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：max以及maxPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long betweenOf(LocalDateTime min,String max,String maxPattern,ChronoUnit adjustUnit){
		return adjustUnit.between(min,parseLocalDateTime(max, maxPattern));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：min以及minPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param minPattern
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long betweenOf(String min,String minPattern,LocalDateTime max,ChronoUnit adjustUnit){
		return adjustUnit.between(parseLocalDateTime(min, minPattern),max);
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：max以及maxPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long betweenOf(Date min,String max,String maxPattern,ChronoUnit adjustUnit){
		return adjustUnit.between(toLocalDateTime(min),parseLocalDateTime(max, maxPattern));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：min以及minPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param minPattern
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long betweenOf(String min,String minPattern,Date max,ChronoUnit adjustUnit){
		return adjustUnit.between(parseLocalDateTime(min, minPattern),toLocalDateTime(max));
	}
	
	/**
	 * @Description 获取两个年月之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月
	 * @param max 计算目标大年月
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * @return
	 */
	public static long betweenOfYm(YearMonth min,YearMonth max,ChronoUnit adjustUnit){
		return adjustUnit.between(min, max);
	}
	
	/**
	 * @Description 获取两个年月之间的指定单位差值
	 * @remark 注意：min、minPattern、max、maxPattern必须由yyyy、MM各自有且仅有一次的完整格式的组合而成，不能出现yyy、yy、y、M等不完整的组合，否则报异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月
	 * @param minPattern
	 * @param max 计算目标大年月
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * @return
	 */
	public static long betweenOfYm(String min,String minPattern,String max,String maxPattern,ChronoUnit adjustUnit){
		return adjustUnit.between(parseYearMonth(min, minPattern), parseYearMonth(max, maxPattern));
	}
	
	/**
	 * @Description 获取两个年月之间的指定单位差值
	 * @remark 注意：max、maxPattern必须由yyyy、MM各自有且仅有一次的完整格式的组合而成，不能出现yyy、yy、y、M等不完整的组合，否则报异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月
	 * @param max 计算目标大年月
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * @return
	 */
	public static long betweenOfYm(YearMonth min,String max,String maxPattern,ChronoUnit adjustUnit){
		return adjustUnit.between(min, parseYearMonth(max, maxPattern));
	}
	
	/**
	 * @Description 获取两个年月之间的指定单位差值
	 * @remark 注意：min、minPattern必须由yyyy、MM各自有且仅有一次的完整格式的组合而成，不能出现yyy、yy、y、M等不完整的组合，否则报异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月
	 * @param minPattern
	 * @param max 计算目标大年月
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * @return
	 */
	public static long betweenOfYm(String min,String minPattern,YearMonth max,ChronoUnit adjustUnit){
		return adjustUnit.between(parseYearMonth(min, minPattern), max);
	}
	
	/**
	 * @Description 获取两个年月日之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月日
	 * @param max 计算目标大年月日
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.WEEKS（周）
	 * ChronoUnit.DAYS（日）
	 * @return
	 */
	public static long betweenOfYmd(LocalDate min,LocalDate max,ChronoUnit adjustUnit){
		return adjustUnit.between(min,max);
	}
	
	/**
	 * @Description 获取两个年月日之间的指定单位差值
	 * @remark 注意：min以及minPattern必须由yyyy、MM、dd三个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月日
	 * @param minPattern 
	 * @param max 计算目标大年月日
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.WEEKS（周）
	 * ChronoUnit.DAYS（日）
	 * @return
	 */
	public static long betweenOfYmd(String min,String minPattern,LocalDate max,ChronoUnit adjustUnit){
		return adjustUnit.between(parseLocalDate(min, minPattern),max);
	}
	
	/**
	 * @Description 获取两个年月日之间的指定单位差值
	 * @remark 注意：max以及maxPattern必须由yyyy、MM、dd三个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月日
	 * @param max 计算目标大年月日
	 * @param maxPattern 
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.WEEKS（周）
	 * ChronoUnit.DAYS（日）
	 * @return
	 */
	public static long betweenOfYmd(LocalDate min,String max,String maxPattern,ChronoUnit adjustUnit){
		return adjustUnit.between(min,parseLocalDate(max, maxPattern));
	}
	
	/**
	 * @Description 获取两个年月日之间的指定单位差值
	 * @remark 注意：min、minPattern、max、maxPattern必须由yyyy、MM、dd三个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月日
	 * @param minPattern 
	 * @param max 计算目标大年月日
	 * @param maxPattern 
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.WEEKS（周）
	 * ChronoUnit.DAYS（日）
	 * @return
	 */
	public static long betweenYmd(String min,String minPattern,String max,String maxPattern,ChronoUnit adjustUnit){
		return adjustUnit.between(parseLocalDate(min, minPattern),parseLocalDate(max, maxPattern));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long distanceOf(Date min,Date max,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(toLocalDateTime(min), toLocalDateTime(max)));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long distanceOf(LocalDateTime min,LocalDateTime max,ChronoUnit adjustUnit){
		return  Math.abs(adjustUnit.between(min, max));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long distanceOf(Date min,LocalDateTime max,ChronoUnit adjustUnit){
		return  Math.abs(adjustUnit.between(toLocalDateTime(min), max));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long distanceOf(LocalDateTime min,Date max,ChronoUnit adjustUnit){
		return  Math.abs(adjustUnit.between(min,toLocalDateTime(max)));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：min、minPattern、max、maxPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param minPattern
	 * @param max 计算目标大日期
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long distanceOf(String min,String minPattern,String max,String maxPattern,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(parseLocalDateTime(min, minPattern),parseLocalDateTime(max, maxPattern)));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：max以及maxPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long distanceOf(LocalDateTime min,String max,String maxPattern,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(min,parseLocalDateTime(max, maxPattern)));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：min以及minPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param minPattern
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long distanceOf(String min,String minPattern,LocalDateTime max,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(parseLocalDateTime(min, minPattern),max));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：max以及maxPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param max 计算目标大日期
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long distanceOf(Date min,String max,String maxPattern,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(toLocalDateTime(min),parseLocalDateTime(max, maxPattern)));
	}
	
	/**
	 * @Description 获取两个日期时间之间的指定单位差值
	 * @remark 注意：min以及minPattern必须由yyyy、MM、dd、HH、mm、ss 共6个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小日期
	 * @param minPattern
	 * @param max 计算目标大日期
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.DAYS（日）
	 * ChronoUnit.HOURS（时）
	 * ChronoUnit.MINUTES（分）
	 * ChronoUnit.SECONDS（秒）
	 * ChronoUnit.MILLIS（毫秒）
	 * ChronoUnit.MICROS（微秒）
	 * ChronoUnit.NANOS（纳秒）
	 * @return
	 */
	public static long distanceOf(String min,String minPattern,Date max,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(parseLocalDateTime(min, minPattern),toLocalDateTime(max)));
	}
	
	/**
	 * @Description 获取两个年月之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月
	 * @param max 计算目标大年月
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * @return
	 */
	public static long distanceOfYm(YearMonth min,YearMonth max,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(min, max));
	}
	
	/**
	 * @Description 获取两个年月之间的指定单位差值
	 * @remark 注意：min、minPattern、max、maxPattern必须由yyyy、MM各自有且仅有一次的完整格式的组合而成，不能出现yyy、yy、y、M等不完整的组合，否则报异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月
	 * @param minPattern
	 * @param max 计算目标大年月
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * @return
	 */
	public static long distanceOfYm(String min,String minPattern,String max,String maxPattern,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(parseYearMonth(min, minPattern), parseYearMonth(max, maxPattern)));
	}
	
	/**
	 * @Description 获取两个年月之间的指定单位差值
	 * @remark 注意：max、maxPattern必须由yyyy、MM各自有且仅有一次的完整格式的组合而成，不能出现yyy、yy、y、M等不完整的组合，否则报异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月
	 * @param max 计算目标大年月
	 * @param maxPattern
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * @return
	 */
	public static long distanceOfYm(YearMonth min,String max,String maxPattern,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(min, parseYearMonth(max, maxPattern)));
	}
	
	/**
	 * @Description 获取两个年月之间的指定单位差值
	 * @remark 注意：min、minPattern必须由yyyy、MM各自有且仅有一次的完整格式的组合而成，不能出现yyy、yy、y、M等不完整的组合，否则报异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月
	 * @param minPattern
	 * @param max 计算目标大年月
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * @return
	 */
	public static long distanceOfYm(String min,String minPattern,YearMonth max,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(parseYearMonth(min, minPattern), max));
	}
	
	/**
	 * @Description 获取两个年月日之间的指定单位差值
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月日
	 * @param max 计算目标大年月日
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.WEEKS（周）
	 * ChronoUnit.DAYS（日）
	 * @return
	 */
	public static long distanceOfYmd(LocalDate min,LocalDate max,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(min,max));
	}
	
	/**
	 * @Description 获取两个年月日之间的指定单位差值
	 * @remark 注意：min以及minPattern必须由yyyy、MM、dd三个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月日
	 * @param minPattern 
	 * @param max 计算目标大年月日
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.WEEKS（周）
	 * ChronoUnit.DAYS（日）
	 * @return
	 */
	public static long distanceOfYmd(String min,String minPattern,LocalDate max,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(parseLocalDate(min, minPattern),max));
	}
	
	/**
	 * @Description 获取两个年月日之间的指定单位差值
	 * @remark 注意：max以及maxPattern必须由yyyy、MM、dd三个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月日
	 * @param max 计算目标大年月日
	 * @param maxPattern 
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.WEEKS（周）
	 * ChronoUnit.DAYS（日）
	 * @return
	 */
	public static long distanceOfYmd(LocalDate min,String max,String maxPattern,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(min,parseLocalDate(max, maxPattern)));
	}
	
	/**
	 * @Description 获取两个年月日之间的指定单位差值
	 * @remark 注意：min、minPattern、max、maxPattern必须由yyyy、MM、dd三个有且仅有一次的任意顺序组合，否则会抛出异常
	 * @Date 2018年12月9日
	 * @author 邓宁城
	 * @param min 计算目标小年月日
	 * @param minPattern 
	 * @param max 计算目标大年月日
	 * @param maxPattern 
	 * @param adjustUnit 获取差值结果的单位：可取如下
	 * ChronoUnit.MILLENNIA（千年）
	 * ChronoUnit.CENTURIES（世纪/百年）
	 * ChronoUnit.DECADES（十年）
	 * ChronoUnit.YEARS（年）
	 * ChronoUnit.MONTHS（月）
	 * ChronoUnit.WEEKS（周）
	 * ChronoUnit.DAYS（日）
	 * @return
	 */
	public static long distanceOfYmd(String min,String minPattern,String max,String maxPattern,ChronoUnit adjustUnit){
		return Math.abs(adjustUnit.between(parseLocalDate(min, minPattern),parseLocalDate(max, maxPattern)));
	}
	
}
