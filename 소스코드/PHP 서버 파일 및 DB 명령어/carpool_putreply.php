<?php
	require_once('dbcon.php');
	$dbc = mysqli_connect($host, $user, $pass, $dbname)
			or die('Error Connecting to MySQL server.');
	mysqli_query($dbc, "set names utf8");
	$post_num = mysqli_real_escape_string($dbc, trim($_POST['post_num']));
	$user_id = mysqli_real_escape_string($dbc, trim($_POST['user_id']));
	$reply = mysqli_real_escape_string($dbc, trim($_POST['reply']));
	$reply_date = date("ymd H:i:s");
	$query1 = "insert into reply_list values ('','$post_num','$user_id', '$reply','$reply_date')";
	$result1 = mysqli_query($dbc, $query1) or die('Error Querying database.') ;
?>
