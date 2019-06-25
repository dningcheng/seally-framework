package org.seally.data.mq.kafka;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateAclsOptions;
import org.apache.kafka.clients.admin.CreateAclsResult;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;

public class OeasyKafkaUtil {
	final Logger logger = LoggerFactory.getLogger(OeasyKafkaUtil.class);
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
	
	private  KafkaProducer<String, String> producer;	//生产者，整个应用生命周期只需要一个对象
	private  AdminClient adminClient;					//kafka管理客户端，整个应用生命周期只需要一个对象
	private  KafkaAdminClient kAdminClient;					//kafka管理客户端，整个应用生命周期只需要一个对象
	private  Map<String, String> topicConfigs = new HashMap<String, String>();
	
	@Value("${kafka.topic.name.doorwy}")
	public String topicDoorWY;	//物业门禁主题
	@Value("${kafka.consumer.threads}")
	public int consummerThreads;	//消费者线程数

	
	
	@PostConstruct
	public void init() {
		initProducer();
		initAdminClient();
		initTopicConfigs();
	}

	/**
	 * 发送消息
	 * @date 2018年10月22日
	 * @author chenyongchao
	 * @param topic 主题
	 * @param key 数据唯一键
	 * @param message	消息内容
	 */
    public void sendMessage(final String topic, final String key, final String message) {
    	
    	
    	
    	//发消息前先检查主题是否存在，不存在自动创建，仅限于宁波项目，如果在零壹平台要考虑主题该如何创建，因为还有一个权限问题
    	if(!topicExists(topic)) {
    		 //descrbe 暂时还没想法写什么，就先用"kafka data"
    		try {
				createTopic(topic, topicPartition,topicReplication);
			} catch (Exception e) {
				logger.warn("create topic error ",e);
			}
    	}
    	//异步发送消息
    	producer.send(new ProducerRecord<>(topic,key, message),new Callback() {
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				logger.debug("send kafka message complete topic : {} key : {} offset : {} message : {}",topic,key,metadata.offset(),message);
				if(exception != null) {
					logger.warn("send message error message ",exception);
				}
			}
		});
    }
	
	/**
	 * 发送消息
	 * @date 2018年10月22日
	 * @author chenyongchao
	 * @param topic 主题
	 * @param message	消息内容
	 */
    public void sendMessage(final String topic, final String message) {
    	//异步发送消息
    	producer.send(new ProducerRecord<String, String>(topic, message),new Callback() {
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				logger.debug("send kafka message complete topic : {} offset : {} message : {}",topic,metadata.offset(),message);
				if(exception != null) {
					logger.warn("send message error message ",exception);
				}
			}
		});
    }

    /**
     * 返回主题列表
     * 如果数据过期或者没有数据从kafka服务中继续重新获取
     * @date 2018年10月22日
     * @author chenyongchao
     * @return
     */
    public Set<String> listTopic(){
    	if(topicNameCache.isExpire() || topicNameCache.isEmpty()) {
	    	ListTopicsOptions options = new ListTopicsOptions();
	    	options.timeoutMs(reqTimeOut);	
	    	ListTopicsResult ltResult = adminClient.listTopics(options);
	    	try {
	    		Set<String> result = new HashSet<>(ltResult.names().get());
	    		logger.debug("list topics "+JSON.toJSONString(result));
	    		topicNameCache.topics = result;
	    		topicNameCache.expireTime = System.currentTimeMillis() + EXPIRE_TIME;
			} catch (Exception e) {
				logger.warn("listTopic result error",e);
			}
    	}
    	return topicNameCache.topics;
    }
    
    /**
     * 获取消费者连接配置信息
     * @date 2018年10月26日
     * @author chenyongchao
     * @param username	sasl认证用户名
     * @param password	sasl认证密码
     * @param group		消费的组
     * @return
     */
    public Map<String, Object> getConsumerConfig(String username,String password,String group){
    	Map<String, Object> config = new HashMap<>();
    	config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    	config.put(ConsumerConfig.GROUP_ID_CONFIG, group); 
    	config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");       
    	config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    	config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
    	config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPullRecords);
		
		//SASL认证相关,如果 saslUsername、saslPassword 没有配置说明不需要进行sasl 认证，如果没有传username、password 说明不需要sasl
		if(StringUtils.isNotEmpty(saslUsername) && StringUtils.isNotEmpty(saslPassword) && StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
			config.put("sasl.jaas.config",
	                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""+username+"\" password=\""+password+"\";");
			config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");    
			config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		}
		return config;
    }
    
    /**
     * 根据时间戳修改消费者指定主题的offset
     * @date 2018年10月29日
     * @author chenyongchao
     * @param topics
     * @param time
     * @param configs
     */
    public void changeOffset(Set<String> topics,long time,Map<String,Object> configs) {
    	Consumer<String, String> consumer = createConsumer(configs);
    	for(String topic : topics) {
    		changeOffset(consumer,topic, time, (String)configs.get(ConsumerConfig.GROUP_ID_CONFIG));
    	}
	}
    
    
    public void changeOffset(Consumer<String, String> consumer,String topic,long time,String group) {
    	List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
		List<TopicPartition> tPartions = new ArrayList<>();
		Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
		Map<TopicPartition, Long> timesearch = new HashMap<>();
		for(PartitionInfo info : partitionInfos) {
			logger.debug("partition : "+info.partition()+" replicas : "+info.replicas().length);
			TopicPartition tp = new TopicPartition(topic, info.partition());
			tPartions.add(tp);
			timesearch.put(tp, time);
		}
		Map<TopicPartition, OffsetAndTimestamp> offsetTime = consumer.offsetsForTimes(timesearch);
		
		consumer.assign(tPartions);
		for(TopicPartition tp:offsetTime.keySet()) {
			OffsetAndTimestamp oaTime = offsetTime.get(tp);
			//没有消费过的情况下 oaTime == null
			if(oaTime != null) {
				logger.info("topic {} partition {} group {} reset offset to {} by time{}",topic,tp.partition(),group,oaTime.offset(),new Timestamp(time));
				offsets.put(tp, new OffsetAndMetadata(oaTime.offset(), "reset"));
			}else {
				logger.info("topic {} partition {} group {} never consumer ",topic,tp.partition(),group);
			}
		}
		consumer.commitSync(offsets, Duration.ofMillis(2000));
		logger.info("topic {} group {} offset 完成",topic,group);
    }
    
    
    /**
     * 添加消费者权限
	 * 主题可以是正则表达式
	 * @param topic
	 * @param username
	 * @param host
	 */
	private void addConsumerSasl(String topic,String username,String host) {
		List<AclBinding> acls = new ArrayList<>();
		
		ResourcePattern pattern = new ResourcePattern(ResourceType.TOPIC, topic, PatternType.LITERAL);
		
		AccessControlEntry entry = new AccessControlEntry("User:"+username, host, AclOperation.READ, AclPermissionType.ALLOW);
		AclBinding binding = new AclBinding(pattern, entry);
		acls.add(binding);
		
		entry = new AccessControlEntry("User:"+username, host, AclOperation.DESCRIBE, AclPermissionType.ALLOW);
		binding = new AclBinding(pattern, entry);
		acls.add(binding);
		
		CreateAclsOptions caOptions = new CreateAclsOptions();
		caOptions.timeoutMs(2000);
		CreateAclsResult aclsResult = adminClient.createAcls(acls,caOptions);
		 Map<AclBinding, KafkaFuture<Void>> values = aclsResult.values();
		 for(AclBinding acl:values.keySet()) {
			 KafkaFuture<Void> future = values.get(acl);
			 try {
				future.get();
				logger.info("权限创建完成！"+acl);
			} catch (InterruptedException | ExecutionException e) {
				logger.warn("权限创建异常",e);
			}
		 }
	} 
    
    
    /**
     * 创建主题
     * @date 2018年10月22日
     * @author chenyongchao
     * @param name		主题名称
     * @param partition	分区数
     * @param replication	备份数
     * @param descrbe		描述
     * @throws TimeoutException 
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    private void createTopic(String name,int partition,short replication) throws InterruptedException, ExecutionException, TimeoutException {
    	if(topicExists(name)) {
    		logger.info("createTopic topic {} exists",name);
    		return;
    	}
   	  	NewTopic newTopic = new NewTopic(name,partition,replication);
	  	newTopic.configs(topicConfigs);
   	  	CreateTopicsOptions cOptions = new CreateTopicsOptions();
   	  	cOptions.timeoutMs(reqTimeOut);
		
		CreateTopicsResult result = adminClient.createTopics(Arrays.asList(newTopic), cOptions);
		KafkaFuture<Void> fauture = result.all();
		//调用get方法是为了让创建主题是同步的，这样创建完成后才能顺利的发送信息
		fauture.get(reqTimeOut, TimeUnit.MILLISECONDS);
		topicNameCache.add(name);
		logger.info(name +" 主题创建完成");
    }
    
    /**
     * 创建消费者对像
     * 会指定消费的主题
     * @date 2018年10月18日
     * @author chenyongchao
     * @param topics 要消费的主题
     * @return
     */
    public KafkaConsumer<String, String> createConsumer(String ...topics){
    	KafkaConsumer<String, String> consumer = createConsumer(saslUsername,saslPassword,group);
    	if(topics != null && topics.length > 0) {
			consumer.subscribe(Arrays.asList(topics));
		}
    	return consumer;
	}
    
    /**
     * 创建不注册主题的消费者
     * @date 2018年11月8日
     * @author chenyongchao
     * @return
     */
    public KafkaConsumer<String, String> createConsumer(){
    	return createConsumer(saslUsername,saslPassword,group);
    }
    
    /**
     * 创建消费者对像
     * 不会指定消费的主题
     * @date 2018年10月18日
     * @author chenyongchao
     * @param username	sasl用户名
     * @param password	sasl密码
     * @param group		消费的组
     * @return
     */
    public KafkaConsumer<String, String> createConsumer(String username,String passwrod,String group){
		Map<String, Object> configs = getConsumerConfig(username,passwrod,group);
		return new KafkaConsumer<>(configs,new StringDeserializer(),new StringDeserializer());
	}
    
    /**
     * 创建消费者对像
     * 不会指定消费的主题
     * @date 2018年10月18日
     * @author chenyongchao
     * @param configs 配置信息
     * @return
     */
    public KafkaConsumer<String, String> createConsumer(Map<String, Object> configs){
		return new KafkaConsumer<>(configs,new StringDeserializer(),new StringDeserializer());
	}
    
    /**
     * 检查主题是否已存在
     * 如果缓存的jvm数据为空则重新加载
     * @date 2018年10月22日
     * @author chenyongchao
     * @param topicName
     * @return
     */
    private boolean topicExists(String topicName) {
    	listTopic();	//这个方法会检查数据是否已经过期，过期重新获取
    	return topicNameCache.isExists(topicName);
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
   	  	adminClient = AdminClient.create(props);
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
