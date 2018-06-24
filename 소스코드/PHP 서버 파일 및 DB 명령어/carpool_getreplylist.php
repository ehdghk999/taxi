<?php
  header('Content-Type: application/json; charset=utf8');
  require('dbcon.php');
  $dbc = mysqli_connect($host, $user, $pass, $dbname)
      or die('Error Connecting to MySQL server.');
  mysqli_query($dbc, "set names utf8");
  $post_id = mysqli_real_escape_string($dbc, trim($_POST['post_id']));
  $query = "select * from reply_list where post_id='$post_id'";
  $result = mysqli_query($dbc, $query);
  $data = array();
  if($result){
    while($row = mysqli_fetch_array($result)){
      array_push($data,
        array(
          'id'=>$row[0],
          'post_id'=>$row[1],
          'user_id'=>$row[2],
          'reply'=>$row[3],
          'reply_date'=>$row[4]
        ));
    }

    $json = json_encode(array("reply_list"=>$data));
    echo $json;
  }
  else{
    echo "SQL문 처리중 에러 발생: ";
    echo mysqli_error($dbc);
  }
  mysqli_close($dbc);
?>
