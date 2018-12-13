package org.seally.data.sync.canal;

import java.net.InetSocketAddress;
import java.util.List;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.Message;

/**
 * @Date 2018年11月10日
 * @author dnc
 * @Description dennis数据库增量订阅类
 */
public class DennisSubscriber {

	public static void main(String args[]) {
		//参数分别为：canal服务主机host、canal服务主机port、订阅实例名称、用户名、用户密码
		//CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("localhost", 11111), "example", "", "");
		
		//基于zookeeper动态获取canal server的地址，建立链接，其中一台server发生crash，可以支持failover
		//CanalConnector connector = CanalConnectors.newClusterConnector("zookeeper1:2181,zookeeper2:2180,zookeeper2:2179", "example", "", "");
		CanalConnector connector = CanalConnectors.newClusterConnector("www.seally.cn:2181", "example", "", "");
		
		int batchSize = 1000;
		try {
			connector.connect();
			connector.subscribe(".*\\..*");//订阅表规则
			connector.rollback();
			while (true) {
				Message message = connector.getWithoutAck(batchSize);//每次获取指定数量batchSize的数据
				long batchId = message.getId();
				int size = message.getEntries().size();//本次实际获取到变更数
				if (batchId == -1 || size == 0) {//本次没有变更
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {//本次有变更，处理增量变更数据
					printEntry(message.getEntries());
					connector.ack(batchId);//提交确认已成功处理
					//connector.rollback(batchId);//处理失败,回滚本批次数据，下次继续获取该批次处理
				}
			}
		} finally {
			//connector.disconnect();
		}
	}

	private static void printEntry(List<Entry> entrys) {
		for (Entry entry : entrys) {
			//如果是事务头、事务尾类型不处理
			if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
				continue;
			}
			//如果是数据内容则进行相应业务处理
			RowChange rowChage = null;
			
			try {
				rowChage = RowChange.parseFrom(entry.getStoreValue());
			} catch (Exception e) {
				throw new RuntimeException("ERROR##parseroferomanga-eventhasanerror,data:" + entry.toString(), e);
			}
			
			EventType eventType = rowChage.getEventType();
			System.out.println(String.format("日志文件名称：%s",entry.getHeader().getLogfileName()));
			System.out.println(String.format("日志文件位置：%d",entry.getHeader().getLogfileOffset()));
			System.out.println(String.format("变更数据库名：%s",entry.getHeader().getSchemaName()));
			System.out.println(String.format("变更数据库表名：%s",entry.getHeader().getTableName()));
			System.out.println(String.format("变更类型：%s",eventType));
			
			for (RowData rowData : rowChage.getRowDatasList()) {
				if (eventType == EventType.DELETE) {//删除操作，获取删除前的数据
					printColumn(rowData.getBeforeColumnsList());
				} else if (eventType == EventType.INSERT) {//新增操作，获取新增后的数据
					printColumn(rowData.getAfterColumnsList());
				} else {//修改操作，获取修改前后的数据
					System.out.println("修改前：");
					printColumn(rowData.getBeforeColumnsList());
					System.out.println("修改后：");
					printColumn(rowData.getAfterColumnsList());
				}
			}
		}
	}

	private static void printColumn(List<Column> columns) {
		for (Column column : columns) {
			System.out.println(column.getName() + ":" + column.getValue());
		}
	}
	
}
