<?php
	require_once('dbcon.php');
	$dbc = mysqli_connect($host, $user, $pass, $dbname)
			or die('Error Connecting to MySQL server.');
	mysqli_query($dbc, "set names utf8");
	$post_num = mysqli_real_escape_string($dbc, trim($_POST['post_num']));
	$user_id = mysqli_real_escape_string($dbc, trim($_POST['user_id']));
	$query = "delete from board_list where id='$post_num' and studentID='$user_id'";
	$result = mysqli_query($dbc, $query);
?>
