<?php
    #echo "ƒvƒƒOƒ‰ƒ€‹N“®" . "<br>";

    if(!isset($_GET["folder"])){
        echo "select folder" . "<br>";
        exit(0);
    }

    $folder_name = $_GET["folder"];

    $path = "./data/" . $folder_name . "/name_table_" . $folder_name . ".csv";

    $data = array();

    $f = fopen($path, "r");
    
    while($line = fgetcsv($f)) {
        #var_dump($line);
        $data_line = array();
        $data_line["name"] = $line[0];
        $data_line["file"] = $line[1];
        $data[count($data)] = $data_line;
    }
    fclose($f);

    #var_dump($data);

    $json_data = json_encode($data, JSON_UNESCAPED_UNICODE|JSON_PRETTY_PRINT);
    #var_dump($json_data);
    echo $json_data;
?>