package com.automation.toolbox.messaging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;

import com.tibco.tibjms.TibjmsQueueConnectionFactory;

public class EmsMessageSender {

	String serverUrl;
	String userName;
	String password;
	String queueName;

	QueueSender sender;
	QueueConnection connection;
	QueueSession session;
	Queue senderQueue;

	private boolean closeConnection = true;   
	

	public EmsMessageSender(String EmsServerAndPort, String Username, String Password, String QueueName) {
		this.serverUrl = EmsServerAndPort;
		this.userName = Username;
		this.password = Password;
		this.queueName = QueueName;

		QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(serverUrl);
		try {
			connection = factory.createQueueConnection(userName, password);
			session = connection.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			senderQueue = session.createQueue(queueName);
			sender = session.createSender(senderQueue);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SendTextMessage(String message) {
		try {
			TextMessage jmsMessage = session.createTextMessage();
			jmsMessage.setText(message);
			sender.send(jmsMessage);
			if(closeConnection){
				connection.close();
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendTextMessagesFromFile(String MessagesFilePath){
		closeConnection = false; 
		try {
			List<String> lines = Files.readAllLines(Paths.get(MessagesFilePath), Charset.defaultCharset());
			System.out.println("Total Messages available in file : " +   lines.size()) ;
			int i= 0; 
			for (String string : lines) {
				SendTextMessage(string);
				i++; 
			}
			System.out.println("Total Messages Sent : " + i);
			try {
				connection.close();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection = true; 
	}
	
	
	public void SendTextMessage(ArrayList<String> messages) {
		closeConnection = false; 
		for (String message : messages) {
			SendTextMessage(message);
		}
		try {
			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection = true; 
	}
}
