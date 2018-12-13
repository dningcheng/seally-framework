package org.seally.data.mq.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

public class KafkaUtil {

	private static KafkaProducer<String, String> kp;
//	private static KafkaConsumer<String, String> kc;
	private static Map<String,KafkaConsumer<String, String>> kcs = new HashMap<>();//对于消费者来说，消费者需要指定组id因此用集合存储
	
	
	public static KafkaProducer<String, String> getProducer() {
		if (kp == null) {
			Properties props = new Properties();
			props.put("bootstrap.servers", "www.seally.cn:9092");
			props.put("acks", "1");
			props.put("retries", 1);
			props.put("batch.size", 16384);
			props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			kp = new KafkaProducer<String, String>(props);
		}
		return kp;
	}

	public static KafkaConsumer<String, String> getConsumer(String groupId) {  
        if(!kcs.containsKey(groupId)) {  
            Properties props = new Properties();  
            props.put("bootstrap.servers", "www.seally.cn:9092");  
            props.put("group.id", groupId);  
            props.put("enable.auto.commit", "true");  
            props.put("auto.commit.interval.ms", "1000");  
            props.put("session.timeout.ms", "30000");  
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  
           
            kcs.put(groupId, new KafkaConsumer<String, String>(props));
        }  
        return kcs.get(groupId);  
	}
}
