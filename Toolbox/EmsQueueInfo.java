package com.automation.toolbox.messaging;


import com.tibco.tibjms.admin.QueueInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;


public class EmsQueueInfo {

	String serverName;
	int port;
	String userName;
	String password;
	String queueName;
	
	
	public EmsQueueInfo(String serverName, String port, String userName, String password, String queueName){
		this.serverName = serverName;
		this.port = Integer.parseInt(port);
		this.userName = userName;
		this.password = password;
		this.queueName = queueName;
	}
	
	public EmsQueueInfo(String serverName, int port, String userName, String password, String queueName) {
		
		this.serverName = serverName;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.queueName = queueName;
	}


	
	public long getPendingMessageCount(){
		TibjmsAdmin admin;
		try {
			admin = new TibjmsAdmin(serverName + ":" + port ,userName, password);
			QueueInfo qi = admin.getQueue(queueName);
			//QueueInfo qi = admin.getQueue("SETCLEAR.PERF.MSGRTR.CLSA.TRADE.OUTBOUND.QUEUE");
			//QueueInfo qi = admin.getQueue("CLSA.PERF.CTM.OUTBOUND.QUEUE");
			long count =  qi.getPendingMessageCount();
			admin.close();
			return count;
		} catch (TibjmsAdminException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1;
		
	}

}
