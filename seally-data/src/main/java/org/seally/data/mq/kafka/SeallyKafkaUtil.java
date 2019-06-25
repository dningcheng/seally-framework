package org.seally.data.mq.kafka;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.DeleteTopicsOptions;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupsOptions;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;

/**
 * @Description kafka工具类 version 2.2.0
 * @Date 2019年4月18日
 * @author 邓宁城
 */
public class SeallyKafkaUtil {
	
	private static final int EXECUTE_TIMEOUT_MILLISECONDS = 2000;
	
	private final Logger logger = LoggerFactory.getLogger(SeallyKafkaUtil.class);
	
	
//	private final static int REQ_TIMEOUT = 1000*2;	//请求趕时间
	private final TopicNameCache topicNameCache = new TopicNameCache();
	
	@Value("${kafka.bootstrap.servers}")
	private String bootstrapServers;
	@Value("${kafka.req.timeout.ms:2000}")
	private int reqTimeOut;	//请求超时时间，默认两秒
//	private String bootstrapServers = "localhost:9091,localhost:9092,localhost:9093";
	@Value("${kafka.producer.batch.size}")
    private int batchSize;
	@Value("${kafka.producer.linger.ms}")
    private int lingerMs;
	@Value("${kafka.producer.max.request.size}")
    private int maxRequestSize;
	@Value("${kafka.sasl.username}")
    private String saslUsername;	//安全认证用户名
	@Value("${kafka.sasl.password}")
    private String saslPassword;	//安全认证密码
	@Value("${kafka.consumer.group}")
	public String group;	//消费者所使用的组
	@Value("${kafka.consumer.max.poll.records}")
	private String maxPullRecords;	//消费者批量消费时最多获取的记录数
	
	@Value("${kafka.topic.config.partition}")
	private int topicPartition;
	@Value("${kafka.topic.config.replication}")
    private short topicReplication;
	@Value("${kafka.topic.config.cleanup.policy}")
    private String topicCleanupPolicy;
    @Value("${kafka.topic.config.delete.retention.ms}")
    private String topicDeleteRetention;
    @Value("${kafka.topic.config.min.compaction.lag.ms}")
    private String topicMinCompactionLag;
    @Value("${kafka.topic.config.segment.bytes}")
    private String topicSegmentBytes;
	
	private  KafkaProducer<String, String> producer; //生产者，整个应用生命周期只需要一个对象
	private  KafkaAdminClient adminClient; //kafka管理客户端，整个应用生命周期只需要一个对象
	
	
	
	/**
	 * @Description 创建主题
	 * @Date 2019年4月18日
	 * @author 邓宁城
	 * @param partitions 主题分区数
	 * @param replications 主题副本数
	 * @param names 主题名称
	 * @return
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public boolean createTopic(int partitions,short replications,String ...names) throws InterruptedException, ExecutionException, TimeoutException {
		Map<String,NewTopic> topics = new HashMap<>();
		for(String name : new HashSet<>(Arrays.asList(names))) {
			if(StringUtils.isEmpty(name)) {
				continue;
			}
			topics.put(name.trim(),new NewTopic(name, partitions, replications));
		}
		if(topics.isEmpty()) {
			logger.error("主题名称names不合法,不执行主题创建...");
			return false;
		}
		CreateTopicsOptions cOptions = new CreateTopicsOptions().timeoutMs(EXECUTE_TIMEOUT_MILLISECONDS);
		//创建主题的时候调用all()得到KafkaFuture并继而调用get()阻塞等待创建执行完毕
		adminClient.createTopics(topics.values(),cOptions).all().get(EXECUTE_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
		return true;
	}
	
	/**
	 * @Description 删除主题
	 * @Date 2019年4月18日
	 * @author 邓宁城
	 * @param topics 主题名称
	 * @return
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public boolean deleteTopic(String ...topics) throws InterruptedException, ExecutionException, TimeoutException {
		if(null == topics) {
			return true;
		}
		DeleteTopicsOptions cOptions = new DeleteTopicsOptions().timeoutMs(EXECUTE_TIMEOUT_MILLISECONDS);
		//删除主题的时候调用all()得到KafkaFuture并继而调用get()阻塞等待删除执行完毕
		adminClient.deleteTopics(Arrays.asList(topics),cOptions).all().get(EXECUTE_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
		return true;
	}
	
	/**
	 * @Description 获取已有主题列表
	 * @Date 2019年4月18日
	 * @author 邓宁城
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Set<String> listTopics() throws InterruptedException, ExecutionException {
		ListTopicsOptions cOptions = new ListTopicsOptions().timeoutMs(EXECUTE_TIMEOUT_MILLISECONDS);
		return adminClient.listTopics(cOptions).names().get();
	}
	
	/**
	 * @Description 获取已有消费者组id
	 * @Date 2019年4月18日
	 * @author 邓宁城
	 * @param fetchSimpleConsumerGroup 是否只获取低版本消费者组
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public Set<String> listConsumerGroups(boolean fetchSimpleConsumerGroup) throws InterruptedException, ExecutionException, TimeoutException {
		ListConsumerGroupsOptions cOptions = new ListConsumerGroupsOptions().timeoutMs(EXECUTE_TIMEOUT_MILLISECONDS);
		Collection<ConsumerGroupListing> groups = adminClient.listConsumerGroups(cOptions).all().get(EXECUTE_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
		return groups.stream().filter(group -> (!fetchSimpleConsumerGroup) || (fetchSimpleConsumerGroup && group.isSimpleConsumerGroup())).map(group -> group.groupId()).collect(Collectors.toSet());
	}
	
	/**
	 * @Description 发送消息
	 * @Date 2019年4月23日
	 * @author 邓宁城
	 * @param topic 发送的主题
	 * @param message 发送的消息内容
	 */
	public void sendMessage(String topic,String message) {
		producer.send(new ProducerRecord<>(topic, message));
	}
	
	/**
	 * @Description 
	 * @Date 2019年4月23日
	 * @author 邓宁城
	 * @param topic 发送的主题
	 * @param key 发送的消息key
	 * @param message 发送的消息内容
	 */
	public void sendMessage(String topic,String key,String message) {
		producer.send(new ProducerRecord<>(topic,key,message));
	}
	
	/**
     * 获取生产者对像
     * @date 2018年10月18日
     * @author chenyongchao
     * @param servers
     * @param username
     * @param password
     * @return
     */
    private void initProducer(){
		Properties props = new Properties();
		if(StringUtils.isNotEmpty(saslUsername) && StringUtils.isNotEmpty(saslPassword)) {
			//SASL安全认证
			props.put(SaslConfigs.SASL_JAAS_CONFIG, 
					"org.apache.kafka.common.security.plain.PlainLoginModule required username=\""+saslUsername+"\" password=\""+saslPassword+"\";");
			props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");    
			props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		}
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);		//到达16bk就发
		props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);			//时间到达100ms就发
		props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);	//每次请求最大20kb
		producer = new KafkaProducer<>(props,new StringSerializer(),new StringSerializer());
	}
    
    /**
     * 初始化kafka管理类
     * @date 2018年10月22日
     * @author chenyongchao
     */
    private void initAdminClient() {
		Properties props = new Properties();
   	  	props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
   	  	if(StringUtils.isNotEmpty(saslUsername) && StringUtils.isNotEmpty(saslPassword)) {
			//SASL安全认证
			props.put(SaslConfigs.SASL_JAAS_CONFIG, 
					"org.apache.kafka.common.security.plain.PlainLoginModule required username=\""+saslUsername+"\" password=\""+saslPassword+"\";");
			props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");    
			props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		}
   	  	adminClient = KafkaAdminClient.create(props);
    }
    
    /**
     * 初始化创建主题时的配置信息
     * 当创建主题{@code createTopic}时要用到这些
     * @date 2018年10月24日
     * @author chenyongchao
     */
    private void initTopicConfigs() {
    	if(StringUtils.isNotBlank(topicCleanupPolicy)) {
    		topicConfigs.put("cleanup.policy", topicCleanupPolicy);	//清理日志方式
    	}
    	if(StringUtils.isNotBlank(topicDeleteRetention)) {
    		topicConfigs.put("delete.retention.ms", topicDeleteRetention);	//多少毫秒检查清理日志
    	}
    	if(StringUtils.isNotBlank(topicMinCompactionLag)) {
    		topicConfigs.put("min.compaction.lag.ms", topicMinCompactionLag);	//消息在日志中保持未压缩的最短时间
    	}
    	if(StringUtils.isNotBlank(topicSegmentBytes)) {
    		topicConfigs.put("segment.bytes", topicSegmentBytes);	//topic每个segent的大小
    	}
    }
    
    private static long EXPIRE_TIME = 1000 * 60 * 5; //默认缓存5分种
    /**
     * 保存主题名称缓存的封装类
     * 避免频繁的从kafka里查询
     * @ate 2018年10月22日
     * @author chenyongchao
     */
    private class TopicNameCache{
    	long expireTime;		//过期时间
    	Set<String> topics;		//存在的主题
    	
    	public TopicNameCache() {
    		expireTime = System.currentTimeMillis() - 1;
    	}
    	
    	public void add(String topicName) {
    		if(topics == null) {
    			topics = new HashSet<>();
    		}
    		topics.add(topicName);
    		logger.debug("topic name "+topicName +" after add "+JSON.toJSONString(topics));
    	}
    	
    	public boolean isExpire() {
    		return expireTime < System.currentTimeMillis();
    	}
    	
    	public boolean isEmpty() {
    		if(topics == null) {
    			return true;
    		}
    		return topics.isEmpty();
    	}
    	
    	public boolean isExists(String topicName) {
    		if(topics == null) {
    			return  false;
    		}
    		return topics.contains(topicName);
    	}
    }
}
