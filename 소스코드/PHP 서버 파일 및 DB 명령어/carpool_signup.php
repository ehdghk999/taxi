<?php
	require_once('dbcon.php');
	$dbc = mysqli_connect($host, $user, $pass, $dbname)
			or die('Error Connecting to MySQL server.');
	mysqli_query($dbc, "set names utf8");
	$studentID = mysqli_real_escape_string($dbc, trim($_POST['studentID']));
	$password = mysqli_real_escape_string($dbc, trim($_POST['password']));
	$studentEmail = mysqli_real_escape_string($dbc, trim($_POST['studentEmail']));
	$query = "insert into user_info values ('','$studentID', '$password', '$studentEmail')";
	$result = mysqli_query($dbc, $query);
?>
