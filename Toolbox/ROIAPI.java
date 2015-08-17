package com.automation.toolbox.roi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

public class ROIAPI {


	
	public int executionStarted(int pid){
		Form form = Form.form();
		form.add("action", "add");
		form.add("pid", "" + pid); 
		form.add("currentstatus", "Running"); 
		form.add("machinename", getMachineName()); 
		form.add("machineuser", getLoggedOnUserName()); 
		String output =  SendMessageOverHTTP(form); 
		if(output==null){
			return 0;
		}
		return Integer.parseInt(output);
		
	}
	
	public void executionCompleted(int runId){
		Form form = Form.form();
		form.add("action", "update");
		form.add("runid", "" + runId); 
		form.add("currentstatus", "Completed"); 
		SendMessageOverHTTP(form); 
	}
	
	public void executionStopped(int runId){
		Form form = Form.form();
		form.add("action", "update");
		form.add("pid", "" + runId); 
		form.add("currentstatus", "Stopped"); 
		SendMessageOverHTTP(form); 
	}
	
	public void increasePassCount(int runId){
		Form form = Form.form();
		form.add("action", "IncreasePass");
		form.add("pid", "" + runId);
		SendMessageOverHTTP(form); 
	}
	
	public void increaseFailCoun(int runId){
		Form form = Form.form();
		form.add("action", "IncreaseFail");
		form.add("pid", "" + runId);
		SendMessageOverHTTP(form); 
	}
	
	
	private String getLoggedOnUserName(){
		return System.getProperty("user.name"); 
	}
	
	private String getMachineName(){
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private String SendMessageOverHTTP(Form form)
	{
		
		Response response = null;
		try {
			response = Request.Post("http://localhost:8081/roi.php").bodyForm(form.build()).execute();
			return response.returnContent().asString().trim();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

}
