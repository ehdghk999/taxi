<?php
	require_once('dbcon.php');
	$dbc = mysqli_connect($host, $user, $pass, $dbname)
			or die('Error Connecting to MySQL server.');
	mysqli_query($dbc, "set names utf8");
	$start_location = mysqli_real_escape_string($dbc, trim($_POST['start_loc']));
	$end_location = mysqli_real_escape_string($dbc, trim($_POST['end_loc']));
	$start_time = mysqli_real_escape_string($dbc, trim($_POST['start_tim']));
	$studentID = mysqli_real_escape_string($dbc, trim($_POST['studentID']));
	$write_date = date("ymd A h:i");
	$query1 = "insert into board_list values ('','$start_location','$end_location', '$start_time', '$studentID','$write_date')";
	$result1 = mysqli_query($dbc, $query1) or die('Error Querying database.') ;
?>
