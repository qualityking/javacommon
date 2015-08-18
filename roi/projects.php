<html> 
<head>
<script type="text/javascript" src="jquery-latest.js"></script>
<script type="text/javascript" src="jquery.tablesorter.js"></script>

<style type="text/css">
table.imagetable {
	font-family: verdana,arial,sans-serif;
	font-size:11px;
	color:#333333;
	border-width: 1px;
	border-color: #999999;
	border-collapse: collapse;
}
table.imagetable th {
	background:#b5cfd2 url('cell-grey.jpg');
	border-width: 1px;
	padding: 3px;
	border-style: solid;
	border-color: #999999;
}
table.imagetable td {
	/*background:#dcddc0 url('cell-grey.jpg');*/
	border-width: 1px;
	padding: 2px;
	border-style: solid;
	border-color: #999999;
}


</style>

</head>
<body>
<script>
$(document).ready(function()
	{
		$("#myTable").tablesorter();
	}
);
</script>
<h2> Projects </h2>
<div align="right"><a href="/report.php"> Show ROI Report </a> | <a href=""> Show Home Page </a></div>

<?php


$servername = "localhost";
$username = "roi";
$password = "roipass";
$dbname = "roidev";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
     die("Connection failed: " . $conn->connect_error);
} 

	$sql = "SELECT * from project";	
			
	$result = $conn->query($sql);
	//$row = mysqli_fetch_assoc($result);
	//var_dump($row); 
	
	echo "<table width='100%'  class='imagetable' id='myTable'>";
	$columnsprinted = false; 
	while ($row = mysqli_fetch_assoc($result))
	{	
		if (!$columnsprinted){
		 echo "<thead>";	
		 echo "<tr>"; 
		 foreach($row as $key => $value) {
			print strtoupper("<th><a href='#'>$key</a></td>");
		 }
		 echo "</tr>"; 
		 echo "</thead>";
		 $columnsprinted = true; 
		}
		
		echo "<tr>";
		foreach($row as $value)
		{
			if($value=="Running"){
				echo "<td align='center'><img width='16' height='16' src='loading.gif' /></td>";
			}
			elseif($value=="Stopped"){
				echo "<td align='center'><img  width='16' height='16' src='stopped.png' /></td>";
			}
			elseif($value=="Completed"){
				echo "<td align='center'><img  width='16' height='16' src='completed.png' /></td>";
			}
			else {
				echo "<td>".$value."</td>";
			}
		}
		echo "</tr>";
	}
	echo "</table>";

 

$conn->close();
 ?> 
 
  </body>
 
 </html>
