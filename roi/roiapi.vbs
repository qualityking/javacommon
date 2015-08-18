'Developed by Manish Bansal 
'Date : 9th Jan 2015 

'Usage 
 'runid =  ExecutionStarted(1)	'pass project id, which is already available in the database and get a run id back 
 
 'IncreasePassCount(runid)	 ' every test case getting pass should be calling this method  
 'IncreaseFailCount(runid)	' for every fail test case please call this. 
 'ExecutionStopped(runid) 'if your test got stopped without completed, you can call this method 
 'ExecutionCompleted(runid) 'as soon as your execution is completed please call this method with runid  
 

Public Function ExecutionStarted(pid)	
  ExecutionStarted = -1	
  sRequestMessage = "action=add&pid=" & pid & "&currentstatus=Running&machinename="& getMachineName() & "&machineuser=" & getLoggedOnUserName()
  ExecutionStarted = SendMessageOverHTTP(sRequestMessage)
End Function


Public Sub ExecutionCompleted(runid)	
  sRequestMessage = "action=update&runid=" & runid & "&currentstatus=Completed"
  SendMessageOverHTTP(sRequestMessage)
End Sub

Public Sub ExecutionStopped(runid)	
  sRequestMessage = "action=update&runid=" & runid & "&currentstatus=Stopped"
  SendMessageOverHTTP(sRequestMessage)
End Sub

Public Sub IncreasePassCount(runid)	
  sRequestMessage = "action=IncreasePass&runid=" & runid 
  SendMessageOverHTTP(sRequestMessage)
End Sub

Public Sub IncreaseFailCount(runid)	
  sRequestMessage = "action=IncreaseFail&runid=" & runid 
  SendMessageOverHTTP(sRequestMessage)
End Sub



Private Function SendMessageOverHTTP(Message)
  On Error Resume Next 
  sUrl = "http://hkdv01v20008:8081/roi.php"
  set oHTTP = CreateObject("Microsoft.XMLHTTP")
  oHTTP.open "POST", sUrl,false
  oHTTP.setRequestHeader "Content-Type", "application/x-www-form-urlencoded"
  oHTTP.setRequestHeader "Content-Length", Len(Message)
  oHTTP.send Message
  SendMessageOverHTTP = oHTTP.responseText
End Function 

Private Function getMachineName()
	On Error Resume Next 
	Set wshShell = WScript.CreateObject( "WScript.Shell" )
	strComputerName = wshShell.ExpandEnvironmentStrings( "%COMPUTERNAME%" )
	getMachineName = strComputerName
End Function 

Private Function getLoggedOnUserName()
	On Error Resume Next 
	Set objNetwork = CreateObject("Wscript.Network")
	getLoggedOnUserName = objNetwork.UserName
End Function 
