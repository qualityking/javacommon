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
	background:#b5cfd2 url('cell-green.jpg');
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
		$("#myTable1").tablesorter();
		$("#myTable2").tablesorter();
		$("#monthlist")[0].selectedIndex = $("#monthval").val()-1;

	}
);
</script>


<div align="right"><a href="/projects.php"> Show All Projects </a> | <a href=""> Show Home Page </a></div>
<?php
$servername = "localhost";
$username = "roi";
$password = "roipass";
$dbname = "roidev";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
     die("Connection failed: " . $conn->connect_error);
} 

	$sql = "SELECT aMonth AS 'Month', SUM(TotalSaving) AS 'Total Monthly Saving' FROM (
			SELECT 	p.psaving * COUNT(r.pid) AS 'TotalSaving' , m.month AS aMonth FROM runs r 
			JOIN months m ON m.month = MONTH(r.rundate) 
			JOIN project p ON p.pid = r.pid WHERE r.currentstatus = 'Completed' 
			GROUP BY r.pid, m.month ORDER BY r.pid ASC ) t GROUP BY aMonth 
			";	
	printtable($conn,$sql, "myTable1");
	
	
?>
<br><br><br>	
<hr / >
<form method="post" action="/report.php">
Select Another Month <select name="month" id="monthlist">
<option value=1>JAN</option><option value=2>FEB</option><option value=3>MARCH</option><option value=4>APRIL</option>
<option value=5>MAY</option><option value=6>JUNE</option><option value=7>JULY</option><option value=8>AUG</option>
<option value=9>SEPT</option><option value=10>OCT</option><option value=11>NOV</option><option value=12>DEC</option>
</select> <input type="text" name="year" value="2015" />
<input type="submit" value="GO" />
</form>

<?php 
$now   = new DateTime();
$month = (int)$now->format("m");
$year =  (int) '20' . $now->format("y");

if(isset($_REQUEST['month'])){
	$month = $_REQUEST['month'];
	$year = $_REQUEST['year'];
}

echo "<h2> $month'st Month's ROI Report </h2>";
echo "<input type='hidden' value='$month' id='monthval' />";
?>

<?php 

	$sql = "SELECT `Project Group`, SUM(`Total Completed Iterations`) as 'Total Completed Iterations', SUM(`Total Saving`) as 'Total Saving'  FROM (
			SELECT 	p.pgroup AS 'Project Group', COUNT(r.pid) AS 'Total Completed Iterations',
			p.psaving * COUNT(r.pid) AS 'Total Saving'   FROM runs r JOIN project p ON 
			p.pid = r.pid WHERE r.currentstatus = 'Completed' 
			AND MONTH(r.rundate) =  $month AND YEAR(r.rundate) = $year
			GROUP BY p.pid) tab GROUP BY `Project Group` ORDER BY `Project Group` ASC ";	
	printtable($conn,$sql, "myTable2");
	
?>



<br><br><br>	
<h2> Report Project Wise </h2>
	
	<?php
	
	
	$sql = "SELECT 	r.pid AS 'Project ID', p.pname AS 'Project Name', p.pgroup as 'Project Group', COUNT(r.pid) AS 'Total Completed Iterations',
			p.psaving AS 'Saving Per Run', p.psaving * COUNT(r.pid) AS 'Total Saving'   FROM runs r JOIN project p ON 
			p.pid = r.pid WHERE r.currentstatus = 'Completed' 
			AND MONTH(r.rundate) = $month AND YEAR(r.rundate) = $year
			GROUP BY r.pid ORDER BY r.pid ASC ";	
	printtable($conn,$sql, "myTable" );

	function printtable($conn,$sql, $tableid){	
	$result = $conn->query($sql);
	//$row = mysqli_fetch_assoc($result);
	//var_dump($row); 
	
	echo "<table width='100%'  class='imagetable' id='$tableid'>";
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

 }

//$conn->close();
 ?> 
  </body>
 
 </html>
 
