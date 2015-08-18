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
	background:#b5cfd2 url('cell-blue.jpg');
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

tr.hiddenRow
{
    display:none;
}

</style>
</head>
<body>

<script>
$(document).ready(function()
	{
		$("#myTable").tablesorter();
		
		$( "#filter" ).change(function() {
		var allRows = $('.imagetable tbody tr');
				allRows.removeClass('hiddenRow');
				var clickedText = $('#filter').val();
				allRows.each(function(){
					//$(this).contains
					if( ! $(this).is(':contains("'+clickedText+'")'))  {
						$(this).addClass('hiddenRow');
					}
				});
		});
	
	$("td").click(function() {
	  $('#filter').val($(this).text());
	});
	
	}
	
	

);

function filter(){
$( "#filter" ).trigger( "change" );
}

function clearfilter(){
 $('#filter').val("");
$( "#filter" ).trigger( "change" );
}



/*function readcellvalue(){
var Something = $('#myTable').closest('tr').find('td:eq(1)').text();
$('#filter').val(Something);
}*/

</script>
<h2>Execution Details </h2> 
Filter Data : <input type="text" id="filter" /><input type='button' onclick='filter()' value='Go' /><input type='button' onclick='clearfilter()' value='Clear' />
<div align="right"><a href="/projects.php"> Show All Projects </a> | <a href="/report.php"> Show ROI Report </a></div>
<?php


$servername = "localhost";
$username = "roi";
$password = "roipass";
$dbname = "roidev";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
     die("Connection failed: " . $conn->connect_error);
} 

	$sql = "SELECT r.runid, p.pname as 'Project Name', p.pgroup as 'Project Group', r.rundate as 'Started', 
		r.runcompletedate as 'Completed', TIMEDIFF(IFNULL(r.runcompletedate,NOW()), r.rundate) as 'Running Since',
		r.machinename as 'MACHINE', r.machineuser as 'USER',p.ptestcasecount as 'TC Count', r.passcount as 'PASS COUNT', r.failcount as 'FAIL COUNT',
		r.currentstatus as 'Current STATUS' , r.comments 
		FROM runs r JOIN  project p ON r.pid = p.pid WHERE r.rundate> CURRENT_DATE()-3 ORDER BY runid DESC";	
			
	$result = $conn->query($sql);
	//$row = mysqli_fetch_assoc($result);
	//var_dump($row); 
	
	echo "<table width='100%'  class='imagetable' id='myTable'>";
	$columnsprinted = false; 
	$counter = 0; 
	$failcountColIndex = 0;
	while ($row = mysqli_fetch_assoc($result))
	{	
		if (!$columnsprinted){
		 echo "<thead>";
		 echo "<tr>"; 
		 
		 foreach($row as $key => $value) {
			
			print strtoupper("<th><a href='#'>$key</a></td>");
			if($key=='FAIL COUNT'){
				$failcountColIndex = $counter; 
			}
			$failcountColIndex++;
		 }
		 echo "</tr>"; 
		 echo "</thead>";
		 echo "<tbody>";
		 $columnsprinted = true; 
		}
		$counter = 0; 
		echo "<tr>";
		foreach($row as $value)
		{	
			if($counter==$failcountColIndex)
			{
				echo "<td><a href='/failinfo.hp'>".$value."</a></td>";
			}
			elseif($value=="Running"){
				echo "<td align='center'><img width='16' height='16' src='loading.gif' alt='Running' /><span style='display:none;'>Running</span></td>";
			}
			elseif($value=="Stopped"){
				echo "<td align='center'><img  width='16' height='16' src='stopped.png' alt='Stopped' /><span style='display:none;'>Stopped</span></td>";
			}
			elseif($value=="Completed"){
				echo "<td align='center'><img  width='16' height='16' src='completed.png' alt='Completed' /><span style='display:none;'>Completed</span></td>";
			}
			else {
				echo "<td>".$value."</td>";
			}
			$counter ++; 
		}
		echo "</tr>";
	}
	echo "</tbody>";
	echo "</table>";

 

$conn->close();
?> 

<script>
$( "tr:odd" ).css( "background-color", "#E9E9E9" );
var bgColor = "#E9E9E9";
$("tr").not(':first').hover(
  function () {
	bgColor = $(this).css("background-color");
    $(this).css("background","lightgreen");
  }, 
  function () {
    $(this).css("background",bgColor);
  }
);
</script>


 
 </body>
 
 </html>
 
