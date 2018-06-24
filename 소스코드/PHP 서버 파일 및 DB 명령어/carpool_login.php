<?
ini_set('register_globals','1');
ini_set('session.bug_compat_42','1');
ini_set('session.bug_compat_warn','0');
ini_set('session.auto_start','1');
?>
<?php
	ob_start();
	session_start();
  header('Content-Type: application/json; charset=utf8');
  require_once('dbcon.php');
  $dbc = mysqli_connect($host, $user, $pass, $dbname)
      or die('Error Connecting to MySQL server.');

  $studentID = mysqli_real_escape_string($dbc, trim($_POST['studentID']));
  $password = mysqli_real_escape_string($dbc, trim($_POST['password']));
  if($_SESSION['studentID'] == $studentID){
    echo "a";
		mysqli_close($dbc);
    exit('');
	}
  mysqli_query($dbc, "set names utf8");
  $query = "select studentID from user_info where studentID='$studentID'";
  $result = mysqli_query($dbc, $query)
    or die('Error Querying database.');
  if(mysqli_num_rows($result) == 0 ){
    echo "b";
    mysqli_free_result($result);
    mysqli_close($dbc);
    exit('');
  }else{
    $query1 = "select * from user_info where studentID='$studentID'";
    $result1 = mysqli_query($dbc, $query1)
			or die('Error Querying database.');
    $row = mysqli_fetch_array($result1);
    if($row['password'] != $password){
      echo "c";
			mysqli_free_result($result1);
			mysqli_close($dbc);
			exit('');
		}else{
      $row=mysqli_fetch_assoc($result1);
      $userid = $studentID;
      $_SESSION['id'] = $userid;
      setcookie('id', $userid, time()+(60*60*24));
      echo "d";
      mysqli_free_result($result1);
      mysqli_free_result($result);
			mysqli_close($dbc);
			exit('');
    }
  }
?>
