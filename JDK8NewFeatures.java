package org.seally.base.demo;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;

/**
 * @Date 2018年11月12日
 * @author dnc
 * @Description JDK8新特性练习
 */
public class JDK8NewFeatures {

	public static void main(String[] args) {
		//lambdaSortTest();//排序测试
		
		//lambdaInterFuncTest();//lambda定义接口函数实现测试
		
		//lambdaStreamTest();
		
		newDateApiTest();
	}
	
	/**
	 * 使用lambda表达式排序
	 */
	public static void lambdaSortTest(){
		
		//数组排序
		String[] names = new String[]{"aaa","abc","bbb","aab","aa","dd"};
		System.out.println("排序前："+JSON.toJSONString(names));
		Arrays.sort(names, (String name1, String name2) -> (name1.compareTo(name2)));
		System.out.println("排序后："+JSON.toJSONString(names));
		
		//集合排序
		List<String> names2 = Arrays.asList(names);
		System.out.println("排序前："+JSON.toJSONString(names2));
		Collections.sort(names2, (name1,name2) -> (name1.compareTo(name2)));
		System.out.println("排序后："+JSON.toJSONString(names2));
		
		//结合自定义对象
		List<UserInfo> users = new ArrayList<>();users.add(new UserInfo(12, "张三"));users.add(new UserInfo(18, "王五"));users.add(new UserInfo(9, "李四"));
		System.out.println("排序前："+JSON.toJSONString(users));
		Collections.sort(users, (u1,u2) -> (u1.compareTo(u2)));
		System.out.println("排序后："+JSON.toJSONString(users));
		
	}
	
	/**
	 * 使用lambda表达式实现函数式接口
	 */
	public static void lambdaInterFuncTest(){
		
		CalcSum sumInstance1 = (a,b) -> {a*=2;b*=2; return (a+b);};//参数需要多步处理
		
		CalcSum sumInstance2 = (a,b) -> {return (a+b);};//只有返回值时去掉{}同时return可省略
		
		CalcSum sumInstance3 = (a,b) -> (a+b);//如果无需对参数进行额外处理，可直接返回
		
		CalcSum sumInstance4 = (a,b) -> a+b;//如果无需对参数进行额外处理，可直接返回
		
		System.out.println(sumInstance1.sum(12, 13));
		
		System.out.println(sumInstance2.sum(12, 13));
		
		System.out.println(sumInstance3.sum(12, 13));
		
		System.out.println(sumInstance4.sum(12, 13));
		
	}
	
	/**
	 * jdk8 stream 测试
	 */
	public static void lambdaStreamTest(){
		
		/**==================== 一、获得stream的方法 ============================ **/
		//方式1、使用集合接口Collection的默认方法stream
		List<Integer> ints = Arrays.asList(1,2,2,null,5,6,6,8,9,10);
		Stream<Integer> stream1 = ints.stream();
		
		//方式2、使用类Stream的静态方法of
		Stream<Integer> stream2 = Stream.of(1,2,3,3,5,6,7,8,9,10);//可变参数
		
		/**==================== 二、转换stream的方法，打印结果用到了汇聚方法collect ============================ **/
		//方法1、filter 入参接受一个过滤函数，该函数返回布尔值,根据布尔接口保留或是丢弃该元素
		Stream<Integer> filterResultStream = Stream.of(1,2,3,null,5,6,null,8,9,10).filter(num -> num != null);
		System.out.println(JSON.toJSONString(filterResultStream.collect(Collectors.toList())));//[1,2,3,5,6,8,9,10]
		
		//方法2、distinct 依赖元素的equels方法去除重复的元素
		Stream<Integer> distinctResultStream = Stream.of(1,2,3,3,3,6).distinct();
		System.out.println(JSON.toJSONString(distinctResultStream.collect(Collectors.toList())));//[1,2,3,6]
		
		//方法3、map 对于Stream中包含的元素使用给定的转换函数进行转换操作，新生成的Stream只包含转换生成的元素。这个方法有三个对于原始类型的变种方法，分别是：mapToInt、mapToLong和mapToDouble
		Stream<Integer> mapResultStream = Stream.of(1,2,3,3,5,6,7,8,9,10).map(num -> num * 2);
		System.out.println(JSON.toJSONString(mapResultStream.collect(Collectors.toList())));// [2,4,6,6,10,12,14,16,18,20]
		
		//比如mapToInt就是把原始Stream转换成一个新的Stream，这个新生成的Stream中的元素都是int类型。之所以会有这样三个变种方法，可以免除自动装箱/拆箱的额外消耗
		IntStream mapToIntResultStream = Stream.of(1,2,3,3,5,6,7,8,9,10).mapToInt(num -> num * 2);
		System.out.println(JSON.toJSONString(mapToIntResultStream.boxed().collect(Collectors.toList())));//注意mapToIntResultStream调用boxed会装换成为Stream<Integer>,然后就方便收集为List
		
		//方法4、flatMap flatMap与map的区别在于 flatMap是将一个流中的每个值都转成一个个流，然后再将这些流扁平化成为一个流
		Stream<String> flatMapResultStream = Stream.of(1,2,3).flatMap(num -> Arrays.asList(num+"a",num+"b",num+"c").stream());
		System.out.println(JSON.toJSONString(flatMapResultStream.collect(Collectors.toList())));// ["1a","1b","1c","2a","2b","2c","3a","3b","3c"]
		
		//方法5、peek 生成一个包含原Stream的所有元素的新Stream，接受一个消费函数（Consumer实例），新Stream每个元素都会执行给定的消费函数，peek接收一个没有返回值的λ表达式，可以做一些输出，外部处理等。map接收一个有返回值的λ表达式，之后Stream的泛型类型将转换为map参数λ表达式返回的类型
		Stream<Integer> peekResultStream = Stream.of(1,2,3).peek(num -> num+=2);
		System.out.println(JSON.toJSONString(peekResultStream.collect(Collectors.toList()))); // [1,2,3] 但是如果是map的话为 [3,4,5]
		
		//方法6、limit 对一个Stream进行截断操作，获取其前N个元素，如果原Stream中包含的元素个数小于N，那就获取其所有的元素
		Stream<Integer> limitResultStream = Stream.of(1,2,3,3,5,6,7,8,9,10).limit(3);
		System.out.println(JSON.toJSONString(limitResultStream.collect(Collectors.toList()))); //[1,2,3]
		
		//方法7、skip 返回一个丢弃原Stream的前N个元素后剩下元素组成的新Stream，如果原Stream中包含的元素个数小于N，那么返回空Stream
		Stream<Integer> skipResultStream = Stream.of(1,2,3,4,5,6,7,8,9,10).skip(3);
		System.out.println(JSON.toJSONString(skipResultStream.collect(Collectors.toList()))); //[4,5,6,7,8,9,10]
		
		
		/**==================== 三、汇聚stream的方法 ============================ **/
		//方法1、collect 该方法特别灵活，依赖Collectors类的系列静态方法可实现系列场景下的复杂操作，该方法有两个重载方法收集元素
		/**
		 * 场景A:直接装换为常用集合结果、使用Collectors的静态方法获取比如：Collectors.toList()、Collectors.toSet()等
		 */
		List<Integer> collectListResult = Stream.of(1,2,3,3,5,6,7,8,9,10).collect(Collectors.toList());
		ArrayList<Integer> collectArrayListResult = Stream.of(1,2,3,3,5,6,7,8,9,10).collect(Collectors.toCollection(ArrayList::new));//使用构造器引用方式指定具体类型
		Set<Integer> collectSetResult = Stream.of(1,2,3,3,5,6,7,8,9,10).collect(Collectors.toSet());
		
		/**
		 * 场景B:分组 对应数据结构为map及如何从stream使用collect方法生成map
		 * （1）使用Collectors.toMap()生成的收集器，用户需要指定如何生成Map的key和value。
		 * （2）使用Collectors.partitioningBy()生成的收集器，对元素进行二分区操作时用到。
		 * （3）使用Collectors.groupingBy()生成的收集器，对元素做group操作时用到。
		 */
		//（1）使用Collectors.toMap()，第一个函数决定如何生成key（注意，生成的key如果重复则会抛异常），第二个函数决定如何生成value
		Map<String, Integer> collectMapResult = Stream.of(1,2,3,4,5,6,7,8,9,10).collect(Collectors.toMap(num -> {return num > 5 ? num+"大": num+"小";}, num -> num+1));
		System.out.println(JSON.toJSONString(collectMapResult));
		
		List<UserInfo> users = new ArrayList<>();users.add(new UserInfo(12, "张三"));users.add(new UserInfo(18, "王五"));users.add(new UserInfo(9, "李四"));
		
		//（2）使用Collectors.partitioningBy()，将元素按照某种条件(比如年龄>=12)分为互补的两部分(用下面的groupingBy方法也能够实现)
		Map<Boolean, List<UserInfo>> collect = users.stream().collect(Collectors.partitioningBy(s -> s.getAge() >= 12));//等价于 users.stream().collect(Collectors.partitioningBy(s -> {return s.getAge() >=  12 ? true : false;}));
		System.out.println(JSON.toJSONString(collect));
        
		//（3）使用Collectors.groupingBy()，将元素按照某个属性(比如姓名name)分到对应的组中
		Map<String, List<UserInfo>> collect2 = users.stream().collect(Collectors.groupingBy(UserInfo::getName));//等价于users.stream().collect(Collectors.groupingBy(s -> s.getName()))
		System.out.println(JSON.toJSONString(collect2));
		//将元素按照某个属性分组且自定义组别信息（用此也可以实现上面partitioningBy功能如collect参数为:Collectors.groupingBy(s -> {return s.getAge() >= 10 ? true : false;})）
		Map<String, List<UserInfo>> collect3 = users.stream().collect(Collectors.groupingBy(s -> {return s.getAge() < 10 ? "小孩" : "大人" ;}));
		System.out.println(JSON.toJSONString(collect3));
		
		//（4）使用Collectors.averagingInt()，求平均
		Double collectAvegeResult = Stream.of(1,2,3,3,5,6,7,8,9,10).collect(Collectors.averagingInt(num-> num));
		System.out.println(collectAvegeResult);
		
		//（5）使用Collectors.joining 进行拼接
		String joined1 = Stream.of("hello", "world", "!").collect(Collectors.joining()); //五参数五分隔
		System.out.println(joined1);// helloworld!
		String joined2 = Stream.of("hello", "world", "!").collect(Collectors.joining(",")); //参数：分隔符
		System.out.println(joined2);// hello,world,!
		String joined3 = Stream.of("hello", "world", "!").collect(Collectors.joining(",", "{", "}")); //参数1：分隔符  参数2：前缀符  参数3：后缀符
		System.out.println(joined3);// {hello,world,!}
		String nums = Stream.of(1,2,3,3,5,6,7,8,9,10).map(num -> num+"").collect(Collectors.joining());
		
		//方法2、sum 求和，注意：该方法只有IntStream、LongStream和DoubleStream才有
		int sumIntResult = Stream.of("s",2,3,4,5,6,7,8,9,10).mapToInt(num -> {if(num instanceof Integer){return (Integer)num;}else{return 0;}}).sum();
		System.out.println(sumIntResult);
		
		double sumDoubleResult = Stream.of(1D,20.5D,3D,4.5D,5D,6D,7D,8D,9D,10D).mapToDouble(num -> (Double)num).sum();
		System.out.println(sumDoubleResult);
		
		//方法3、max、min
		OptionalDouble max = Stream.of(1D,20.5D,3D,4.5D,5D,6D,7D,8D,9D,10D).mapToDouble(num -> (Double)num).max();
		System.out.println(max.isPresent() ? max.getAsDouble() : 0);
		
		Optional<Double> max2 = Stream.of(1D,20.5D,3D,4.5D,5D,6D,7D,8D,9D,10D).max((num1,num2) -> num1.compareTo(num2));
		System.out.println(max2.get());
		
		OptionalDouble min = Stream.of(1D,20.5D,3D,4.5D,5D,6D,7D,8D,9D,10D).mapToDouble(num -> (Double)num).min();
		System.out.println(min.isPresent() ? min.getAsDouble() : 0);
		
		//方法4、计数
		long countResult = Stream.of(1D,20.5D,3D,4.5D,5D,6D,7D,8D,9D,10D).count();
		System.out.println(countResult);
		
		//方法5、reduce reduce方法非常的通用，前面介绍的count，sum等都可以使用其实现，reduce擅长的是生成一个值，如果想要从Stream生成一个集合或者Map等复杂的对象使用collect，reduce方法有三个override的方法
		//（1）形式A：Optional<T> reduce(BinaryOperator<T> accumulator); 接受一个BinaryOperator类型的参数，我们可以用lambda表达式来
		//reduce方法接受一个函数，这个函数有两个参数，第一个参数是上次函数执行的返回值（也称为中间结果），第二个参数是stream中的元素，这个函数把这两个值相加，得到的和会被赋值给下次执行这个函数的第一个参数
		Optional<Double> reduceSumResult = Stream.of(1D,20.5D,3D,4.5D,5D,6D,7D,8D,9D,10D).reduce((result,ele) -> result+ele);//唯一参数：接受两个参数的函数
		System.out.println(reduceSumResult.isPresent() ? reduceSumResult.get() : 0);
		
		//获取最长的单词
		Optional<String> longest = Stream.of("I", "love", "you", "too").reduce((s1, s2) -> s1.length()>=s2.length() ? s1 : s2);
		System.out.println(longest.get());
		
		//（2）形式B：T reduce(T identity, BinaryOperator<T> accumulator); 允许用户提供一个循环计算的初始值，如果Stream为空，就直接返回该值。而且这个方法不会返回Optional，因为其不会出现null值
		Double reduceSumResult2 = Stream.of(1D,20.5D,3D,4.5D,5D,6D,7D,8D,9D,10D).reduce(0D,(result,ele) -> result+ele);//参数1：循环计算的初始值  参数2：接受两个参数的函数
		System.out.println(reduceSumResult2);
		
		//其它方法
		/**
		 * – allMatch：是不是Stream中的所有元素都满足给定的匹配条件
		 * – anyMatch：Stream中是否存在任何一个元素满足匹配条件
		 * – findFirst: 返回Stream中的第一个元素，如果Stream为空，返回空Optional
		 * – noneMatch：是不是Stream中的所有元素都不满足给定的匹配条件
		 * – max和min：使用给定的比较器（Operator），返回Stream中的最大|最小值
		 */
	}
	
	/**
	 * @Date 2018年12月7日
	 * @author 邓宁城
	 * @Description JDK新版日期Api测试
	 */
	public static void newDateApiTest(){
		
		/**
		 * 1、MonthDay（包月、日2个信息，常常用来检查处理基于月份和日的周期性事件：类似每月账单、结婚纪念日、EMI日或保险缴费日）
		 * Java 8 中的 MonthDay类。这个类组合了月份和日，去掉 了年，这意味着你可以用它判断每年都会发生事件
		 * 类似的对象还有 YearMonth（只含年月信息）
		 */
		//指定参数创建月日对象
		MonthDay monthDay = MonthDay.of(12, 31);
		//当前时间的月日对象
		MonthDay curMonthDay = MonthDay.now();
		//由其它时间对象比如日期初始化而来
		LocalDate date = LocalDate.now();
		MonthDay fromMonthDay = MonthDay.from(date);
		
		//判断是否相等
		System.out.println("是否相等（同月同天）："+fromMonthDay.equals(curMonthDay));
		//判断是先于其它月日对象
		System.out.println("是否在指定之前："+fromMonthDay.isBefore(monthDay));
		//判断是后于其它月日对象
		System.out.println("是否在指定之后："+fromMonthDay.isAfter(monthDay));
		
		
		/**
		 * 2、YearMonth（包年、月2个信息，表示信用卡到期等这类固定日期）
		 * 利用这个类可方便得出指定月份有多少天，该年是否是闰年等，当然带年、月的其它对象也可以
		 * （1）初始化方法：now()、of的系列重载、from(TemporalAccessor实例参数)、
		 * （2）基于实例通过指定值返回新对象方法：with(数值,单位)
		 * （3）加减计算方法：plus(数值,单位)、plusYears等、minus(数值,单位)、minusMonths等
		 * （4）比较方法：equals(YearMonth other)-是否相等、isAfter-大于比较对象、isBefore-小于比较对象
		 */
		YearMonth yearMonth = YearMonth.now();
		System.out.println("当前年月是："+yearMonth);
		System.out.println("当前年是否是闰年："+yearMonth.isLeapYear());
		System.out.println("当前月总天数："+yearMonth.lengthOfMonth());
		System.out.println("当前年总天数："+yearMonth.lengthOfYear());
		
		
		/**
		 * 3、LocalDate（包含年、月、日3个信息）
		 * Java 8 中的 LocalDate 用于表示当天日期。刚好组合了上面YearMonth和MonthDay的信息，与java.util.Date不同，它只有日期，不包含时间。当你仅需要表示日期时就用这个类。
		 */
		LocalDate today = LocalDate.now();//获取当前日期
		LocalDate pointToday = LocalDate.of(2018, 12, 7);//利用指定参数构建出日对象
		
		System.out.println("判断两个日期是否相等："+today.equals(pointToday));
		
		System.out.println("今天的日期："+today.toString());
		System.out.println("今天的年/月/日："+today.getYear()+"   "+ today.getMonthValue()+"   "+today.getDayOfMonth());
		System.out.println("本月的总天数："+today.lengthOfMonth());
		System.out.println("本年的总天数："+today.lengthOfYear());
		
		//日期的加减可使用plus、minus两个方法，参数一：数目    参数二：单位，单位使用ChronoUnit的常量可取：DAYS（天）、WEEKS（周）、MONTHS（月）、YEARS（年）、DECADES（十年）、CENTURIES（世纪/百年）、MILLENNIA（千年）、ERAS（纪元）
		System.out.println("今天往后加3天："+today.plus(3, ChronoUnit.DAYS));
		System.out.println("今天往前减4天："+today.plus(-4, ChronoUnit.DAYS));//建议用 today.minus(4, ChronoUnit.DAYS))
		//日期加减除了使用plus还可以直接使用静态方法如：
		System.out.println("今天往后加3天："+today.plusDays(3));//对应还有plusWeeks、plusMonths、plusYears
		System.out.println("今天往前减4天："+today.minusDays(4));//对应还有minusWeeks、minusMonths、minusYears
		
		//获取日期指定字段的长度数值，单位可取常量ChronoField的DAY_OF_WEEK（本周内第几天/星期几）、DAY_OF_MONTH（本月内第几天）、DAY_OF_YEAR（本年内第几天）、MONTH_OF_YEAR（本年内第几月）、...
		System.out.println("该日期在本周的星期数（1-7）："+today.getLong(ChronoField.DAY_OF_WEEK));
		System.out.println("该日期在本月的天数（1-31）："+today.getLong(ChronoField.DAY_OF_MONTH));
	
		//利用日期对象的atTime系列方法结合指定的时、分、秒、纳秒组合出时间对象
		System.out.println("该日期的上午03:30："+today.atTime(3, 30));
	
		
		/**
		 * 4、LocalTime （只包含时间信息，一般用于处理一天内的小时、分钟、秒以及毫秒的表示和计算问题）
		 * Java 8 中的 LocalTime类的创建方式和计算方式与前面方法类似：
		 * （1）初始化方法：now()、of的系列重载、from(TemporalAccessor实例参数)、
		 * （2）基于实例通过指定值返回新对象方法：with(数值,单位)
		 * （3）加减计算方法：plus(数值,单位)、plusHours等、minus(数值,单位)、minusMinutes等
		 * （4）比较方法：equals(LocalTime other)-是否相等、isAfter-大于比较对象、isBefore-小于比较对象
		 * （5）获取信息：getHour、getMinute、getSecond、getNano（获取纳秒）
		 * （6）转换成当天指定单位的数值方法：toSecondOfDay()-总秒数、toNanoOfDay()-总纳秒数
		 */
		LocalTime now = LocalTime.now();
		System.out.println("此刻的时间是："+now);
		
		
		/**
		 * 5、LocalDateTime日期时间类
		 * Java 8 使用LocalDateTime类来表示同时包含年月日时分秒等信息的时间，刚好包含了，其API与LocalTime类似
		 * （1）初始化方法：now()、of的系列重载、from(TemporalAccessor实例参数)、
		 * （2）基于实例通过指定值返回新对象方法：with(数值,单位)
		 * （3）加减计算方法：plus(数值,单位)、plusHours等、minus(数值,单位)、minusMinutes等
		 * （4）比较方法：equals(LocalTime other)-是否相等、isAfter-大于比较对象、isBefore-小于比较对象
		 */
		LocalDateTime localDateTime = LocalDateTime.now();
		System.out.println("此刻的日期时间是："+localDateTime);
		
		/**
		 * 6、ZoneDateTime特定时区日期时间类
		 * Java 8中用ZoneDateTime类来表示某时区下的时间，因此其包含信息比较丰富
		 */
		//把本地日期时间转换为其它时区下的时间
		ZoneId america = ZoneId.of("America/New_York");//获取指定时区id的时区对象，参数时区id可见 ZoneId.SHORT_IDS
		LocalDateTime localtDateAndTime = LocalDateTime.now();//本地日期时间
		ZonedDateTime dateAndTimeInNewYork = ZonedDateTime.of(localtDateAndTime, america);
		System.out.println("当前本地时间在美国时间是：" + dateAndTimeInNewYork);
		
		
		/**
		 * 7、Period周期类，用于“大时间”计算间隔，所谓“大时间”即年、月、日上的差值计算
		 * Java 8通过使用Period来计算两个日期之间的各种差值区间长度
		 * （1）初始化方法：of的系列重载、ofYears、ofMonths、ofWeeks、ofDays、通过between比较两个日期对象获取
		 * （2）基于实例通过指定值返回新对象方法：withYears、withMonths、withDays三个，注意：没有使用单位初始化的方法with
		 * （3）加减计算方法：plusYears、plusMonths、plusDays、minusYears、minusMonths、minusDays，注意：没有使用单位初始化的方法plus和minus
		 */
		//使用指定参数初始化一个Period
		Period period = Period.of(2019, 2, 12);
		System.out.println(period.getYears());
		
		//通过between比较两个日期对象获取Period后，查看这两个日期之间相差的年数、月数、天数
		//特别注意:这三个差值不是独立的，而是需要结合起来表示的，比如假如每年按365天，每月按30天计算的话，则结果中 两个比较日期间相差的总天数=(getYears*365 + getMonths*30 + getDays)，另外如果把小日期放后面的话，结果中这几个值可能是负数
		LocalDate nowDate = LocalDate.now();
		LocalDate comPareDate = LocalDate.of(2019, 11, 7);
		Period between = Period.between(nowDate, comPareDate);
		System.out.println("两个日期间相差年数："+between.getYears());
		System.out.println("两个日期间相差月数："+between.getMonths());
		System.out.println("两个日期间相差天数："+between.getDays());
		
		
		/**
		 * 8、Duration周期类，用于“小时间”计算间隔，所谓“小时间”即日、时、分、秒上的差值计算
		 * Java 8通过使用Duration来计算两个时间之间的各种差值区间长度
		 * （1）初始化方法：ofDays、ofHours、ofMinutes、ofSeconds、ofMillis、ofNanos、from、通过比较两个时间对象获取
		 * （2）基于实例通过指定值返回新对象方法：withSeconds、withNanos
		 * （3）加减计算方法：plus*、minus*
		 * （4）获取对应值的方法：toDays、toHours、toMinutes、...
		 * 备注：与Period对象不同的是该类获取到的各种时间单位长度值互相独立代表了该区间的长度，不需要配合计算
		 */
		LocalDateTime nowDateTime = LocalDateTime.now();
		LocalDateTime comPareDateTime = LocalDateTime.of(2018, 12, 8, 15, 30, 0);
		Duration duration = Duration.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的天数："+duration.toDays());
		System.out.println("两个时间相差的小时数："+duration.toHours());
		System.out.println("两个时间相差的分钟数："+duration.toMinutes());
		System.out.println("两个时间相差的秒数："+duration.getSeconds());//这个类很奇怪，就是没有toSeconds，可能是因为已经有了getSeconds方法同样代表了这个区间的秒数，因此不再需要刻意重写一个toSeconds
		System.out.println("两个时间相差的毫秒数："+duration.toMillis());
		System.out.println("两个时间相差的纳秒数："+duration.toNanos());
		
		/**
		 * 9、ChronoUnit 测量工具类，测量两个时间对象之间的各种差值 从年-纳秒级别都有
		 * 比较参数为Temporal接口的实例
		 */
		long years = ChronoUnit.YEARS.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的年数："+years);
		
		long months = ChronoUnit.MONTHS.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的月数："+months);
		
		long days = ChronoUnit.DAYS.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的天数："+days);
		
		long hours = ChronoUnit.HOURS.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的小时数："+hours);
		
		long minutes = ChronoUnit.MINUTES.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的分钟数："+minutes);
		
		long seconds = ChronoUnit.SECONDS.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的秒数："+seconds);
		
		long mllis = ChronoUnit.MILLIS.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的毫秒数："+mllis);
		
		long micros = ChronoUnit.MICROS.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的微秒数："+micros);
		
		long nanos = ChronoUnit.NANOS.between(nowDateTime, comPareDateTime);
		System.out.println("两个时间相差的纳秒数："+nanos);
		
		/**
		 * 10、ZoneOffset 表示与UTC时区偏移的固定区域，有两个常量值MAX和MIN分别表示最大和最小支持的区域偏移
		 * 我们可以用小时，分钟和秒的组合来创建 ZoneOffset
		 * （1）初始化方法：ofHours(小时)、ofHoursMinutes(小时、分钟)、ofHoursMinutesSeconds(小时、分钟、秒)、由from(时间对象)方法创建
		 * （2）由固定格式的偏移字符串id创建:of(String offsetId)，其中offsetId的格式可取：Z（直接基于UTC）、+h、+hh、+hh:mm、-hh:mm、+hhmm、-hhmm、+hh:mm:ss、-hh:mm:ss、+hhmmss、-hhmmss（以上hms的表达式为基于UTC偏移，hms需要用具体偏移的时分秒数值替换，如：+09:32）
		 */
		ZoneOffset zoneOffset = ZoneOffset.of("+06:30");
		
		/**
		 * 11、OffsetDateTime 表示包含时区信息的日期时间对象，与ZoneDateTime的区别是OffsetDateTime不包括夏令时调整的规则，因此从涵盖信息上由多到少为：ZoneDateTime > OffsetDateTime > LocalDateTime
		 * 我们ZoneOffset实例结合其他日期对象来为日期对象注入时区信息后成为具有时区信息的日期时间对象OffsetDateTime
		 * （1）初始化方法：ofHours(小时)、ofHoursMinutes(小时、分钟)、ofHoursMinutesSeconds(小时、分钟、秒)、由from(时间对象)方法创建
		 * （2）由固定格式的偏移字符串id创建:of(String offsetId)，其中offsetId的格式可取：Z（直接基于UTC）、+h、+hh、+hh:mm、-hh:mm、+hhmm、-hhmm、+hh:mm:ss、-hh:mm:ss、+hhmmss、-hhmmss（以上hms的表达式为基于UTC偏移，hms需要用具体偏移的时分秒数值替换，如：+09:32）
		 */
		LocalDateTime datetime = LocalDateTime.of(2018, Month.JANUARY, 14, 19, 30);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(datetime, zoneOffset); 
		System.out.println("结合时区偏移后不含夏令时调整规则的日期时间为：" + offsetDateTime);// 2018-01-14T19:30+06:30
		
		
		/**
		 * 12、Clock时钟类
		 * Java 8增加了一个Clock时钟类用于获取当时的时间戳，或当前时区下的日期时间信息。以前用到System.currentTimeInMillis()和TimeZone.getDefault()的地方都可用Clock替换。
		 */
		Clock systemUTC = Clock.systemUTC();
		System.out.println("当前系统时间戳："+systemUTC.millis());
		
		
		/**
		 * 13、Instant瞬时类/时间戳，从1970-01-01 00:00:00到当前时间的瞬时，now()获取当前的时间是当前的美国时间，和处于东八区的我们相差八个小时
		 * 时间戳信息里同时包含了日期和时间，这和java.util.Date很像。实际上Instant类确实等同于 Java 8之前的Date类，你可以使用Date类和Instant类各自的转换方法互相转换，例如：Date.from(Instant) 将Instant转换成java.util.Date，Date.toInstant()则是将Date类转换成Instant类。
		 * （1）初始化方法：now、now(Clock)、ofEpochSecond系列（使用距离1970-01-01 00:00:00的参数值初始化）、with方法
		 * （1）加减计算方法：plus*、minus*
		 * （2）先后比较方法：isAfter、isBefore，参数为另一个Instant瞬时类实例
		 */
		Instant instant = Instant.now();//当前系统时间戳
		Instant instantByClock = Instant.now(systemUTC);//通过时钟对象初始化
		Instant instantByDate = new Date().toInstant();//由日期对象获取
		Date from = Date.from(instantByClock);//转化为Date对象
		
		
		/**
		 * 14、DateTimeFormatter类用来创建日期格式化类，相当于之前的SimpleDateFormat，Java8的DateTimeFormatter是线程安全的，而SimpleDateFormat并不是线程安全
		 * （1）获取方法：of*方法，最方便的还是ofPattern(格式字符串)
		 * （2）一般格式化某个日期时都使用该日期对象的 实例.format(DateTimeFormatter)
		 * （3）一般解析某个字符串为日期时使用:类.parse("日期字符串",DateTimeFormatter)
		 */
		LocalDateTime localDate = LocalDateTime.now();
		System.out.println(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		System.out.println(LocalDateTime.parse("20181209 101230", DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")));
		
	}
	
	
}



interface CalcSum{
	int sum(int a,int b);//如果一个接口只有一个未实现的接口方法，则该接口可作为lambda表达式实例化的函数式接口
	
	static void hello(String name){
		System.out.println(name+ " 你好！");//static方法不影响函数式接口特性
	}
	
	default void say(String name){//default方法不影响函数式接口特性
		System.out.println(name+ " 你好！");
	}
}

class UserInfo implements Comparable<UserInfo>{
	private Integer age;
	private String name;
	
	public UserInfo(){}
	public UserInfo(Integer age,String name){this.age = age;this.name=name;}
	
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int compareTo(UserInfo o) {
		
		if(null == this.age){
			this.age = 0;
		}
		if(null == o.getAge()){
			o.setAge(0);
		}
		return this.getAge() - o.getAge();
	}
}