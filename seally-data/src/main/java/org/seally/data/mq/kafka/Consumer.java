package org.seally.data.mq.kafka;

import java.time.Duration;
import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class Consumer {

	public static void main(String[] args) throws InterruptedException {

		KafkaConsumer<String, String> consumer_G1 = KafkaUtil.getConsumer("G1");
		KafkaConsumer<String, String> consumer_G2 = KafkaUtil.getConsumer("G1");
		consumer_G1.subscribe(Arrays.asList("test-topic"));
		consumer_G2.subscribe(Arrays.asList("test-topic"));
		
		//如果是需要重新制定偏移量进行消费，需要消费前先poll一次忙否则会抛出异常：No current assignment for partition
		//ConsumerRecords<String, String> recordTemp = consumer.poll(0);
		//consumer.seek(new TopicPartition("test",0), 0);
		while (true) {
			ConsumerRecords<String, String> records = consumer_G1.poll(Duration.ofMinutes(10));
			ConsumerRecords<String, String> records2 = consumer_G2.poll(Duration.ofMinutes(10));
			for (ConsumerRecord<String, String> record : records) {
				System.out.println("consumer_G1 从分区: " + record.partition() + ", offset: " + record.offset()+ ", 获取消息: " + record.value());
			}
			
			for (ConsumerRecord<String, String> record : records2) {
				System.out.println("consumer_G2 从分区: " + record.partition() + ", offset: " + record.offset()+ ", 获取消息: " + record.value());
			}
			
			Thread.sleep(1000);
		}
	}

}
