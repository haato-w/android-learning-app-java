<?php
    #echo "プログラム起動" . "<br>";

    if(!isset($_GET["file"])){
        echo "select file" . "<br>";
        exit(0);
    }

    $file_name = $_GET["file"];

    $path = "./data/" . $file_name;

    $data = array();

    $f = fopen($path, "r");
    
    while($line = fgetcsv($f)) {
        #var_dump($line);
        if($line[0] == "") {
            break;
        }
        $data_line = array();
        $data_line["question"] = $line[1];
        $data_line["answer1"] = $line[2];
        $data_line["answer2"] = $line[3];
        $data[count($data)] = $data_line;
    }
    fclose($f);

    #var_dump($data);

    $json_data = json_encode($data, JSON_UNESCAPED_UNICODE|JSON_PRETTY_PRINT);
    #var_dump($json_data);
    echo $json_data;
?>