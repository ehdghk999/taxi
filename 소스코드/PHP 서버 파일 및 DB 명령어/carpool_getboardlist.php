<?php
  header('Content-Type: application/json; charset=utf8');
  require('dbcon.php');
  $dbc = mysqli_connect($host, $user, $pass, $dbname)
      or die('Error Connecting to MySQL server.');
  mysqli_query($dbc, "set names utf8");
  $query = "select * from board_list";
  $result = mysqli_query($dbc, $query);
  $data = array();
  if($result){
    while($row = mysqli_fetch_array($result)){
      array_push($data,
        array(
          'id'=>$row[0],
          'start_location'=>$row[1],
          'end_location'=>$row[2],
          'start_time'=>$row[3],
          'studentID'=>$row[4],
          'write_date'=>$row[5]
        ));
    }

    $json = json_encode(array("board_list"=>$data));
    echo $json;
  }
  else{
    echo "SQL문 처리중 에러 발생: ";
    echo mysqli_error($dbc);
  }
  mysqli_close($dbc);
?>
