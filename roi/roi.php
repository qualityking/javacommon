<?php

$action = $_REQUEST['action'];

$runid="";
$pid = "";
$currentstatus = "";
$result = "";
$machinename = "";
$machineuser = "";
$passcount = "";
$failcount	= "";
$sql ="";
$comments = ""; 
$testcasename="";
$failreason=""; 
$additionalinfo=""; 


if($action =='add'){

	$pid = $_REQUEST['pid'];
	$currentstatus = $_REQUEST['currentstatus'];
	$machinename = $_REQUEST['machinename'];
	$machineuser = $_REQUEST['machineuser'];

	if(isset($_REQUEST['comments'])){
		$comments = $_REQUEST['comments'];
		$sql = "INSERT INTO `runs` (`pid`, `currentstatus`, `rundate`, 	`machinename`, 	`machineuser`, `comments`) 
		VALUES 	( '$pid', 	'$currentstatus', 	now(), 	'$machinename', '$machineuser' , '$comments')";
	}
	else {
		$sql = "INSERT INTO `runs` (`pid`, `currentstatus`, `rundate`, 	`machinename`, 	`machineuser`)
		VALUES 	( '$pid', 	'$currentstatus', 	now(), 	'$machinename', '$machineuser' )";
	}

}elseif($action =='IncreasePass'){
	$runid = $_REQUEST['runid'];
	$sql = "UPDATE runs SET passcount = passcount + 1 WHERE runid = " .$runid ;
}
elseif($action =='IncreaseFail'){
	$runid = $_REQUEST['runid'];
	$sql = "UPDATE runs SET failcount = failcount + 1 WHERE runid = " .$runid ;
}
elseif($action =='update'){
	$runid = $_REQUEST['runid'];
	$currentstatus = $_REQUEST['currentstatus'];
	$sql = "UPDATE runs SET currentstatus = '". $currentstatus ."', runcompletedate=NOW() WHERE runid = " .$runid ;
}

elseif($action =='failinfo'){
	$runid = $_REQUEST['runid'];
	$testcasename = $_REQUEST['testcasename'];
	$failreason = $_REQUEST['failreason'];
	$additionalinfo = $_REQUEST['additionalinfo'];
	$sql = "INSERT INTO `failinfo` 	(`runid`,`testcasename`,`failreason`, `additionalinfo`)	
		 VALUES('$runid', '$testcasename','$failreason','$additionalinfo')"; 
}

$servername = "localhost";
$username = "roi";
$password = "roipass";
$dbname = "roidev";

$error = false; 
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
     //die("Connection failed: " . $conn->connect_error);
	 echo '-1'; 
} 

if($action =='add' && $machinename !=''){
		$sql1 = "UPDATE runs SET `currentstatus` = 'Stopped', runcompletedate=NOW() WHERE `currentstatus` = 'Running' AND pid='$pid' AND machinename  = '$machinename'";	
		$conn->query($sql1);
	}
if ($conn->query($sql) === TRUE) {
	if($action =='add'){
		$sql = "SELECT MAX(runid) max FROM runs LIMIT 1";	
		$result = $conn->query($sql);
		$row = mysqli_fetch_array($result);
		echo $row[0];
	}
} else {
    //echo "Error: " . $sql . "<br>" . $conn->error;
	echo '-1'; 
}

$conn->close();
 ?> 
 
 
