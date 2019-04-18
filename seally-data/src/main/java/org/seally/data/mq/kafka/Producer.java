package org.seally.data.mq.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Producer {

	public static void main(String[] args) throws InterruptedException {

		KafkaProducer<String, String> producer = KafkaUtil.getProducer();

		int i = 0;
		while (true) {
			final String msg = "这是发送出去的第"+i+"条消息";
			ProducerRecord<String, String> record = new ProducerRecord<String, String>("test-topic",msg);//topic、value
			producer.send(record, new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null)	e.printStackTrace();
					System.out.println("消息["+msg+"]已发送成功,分区号= " + metadata.partition() + ", offset=" + metadata.offset());
				}
			});
			i++;
			Thread.sleep(1000);
		}
	}
}
