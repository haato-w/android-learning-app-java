# android-learning-app-java

勉強用のAndroidアプリケーションのプログラムです。<br>
サーバー側にcsv形式で問題と分野名を置いて、問題を開始するタイミングでサーバーから問題の情報を引っ張ってきています。<br>
サンプルは中学社会の問題を入れていますが、一問一答形式であれば好きな問題をcsvで保存しておくことで様々な応用が可能です。<br>

https://user-images.githubusercontent.com/64346215/151975724-74479ac5-916b-40c1-9ab3-e2d4cbec1469.MP4

## 【実行方法】
1. src内のmainディレクトリをAndroidのプロジェクトに入れる
2. src内のserverディレクトリの内容（phpコードとcsvデータ）をサーバーに置く
3. AndroidコードのMainActivity.java(42行目)とFieldSelectActivity.java(43行目)にサーバーのURLを書き込む
4. Androidアプリを起動する

## 【詳細】
**言語**<br>
アプリ側：Java<br>
サーバー側：PHP<br>

**アプリ側ファイル構成**<br>
○Java<br>
ExeriseActivity.java：問題の出題, 解答判定, 間違えた問題数のカウントを行う処理を記述<br>
MainActivity.java：教科選択画面(初期画面)の動作を記述, 選択された教科の分野をサーバーから取得する<br>
FieldSelectActivity.java：分野選択画面の動作を記述, 選択された分野の問題と答えをサーバーから取得する<br>
LookBackActivity.java：間違えた問題数を表示, 間違えた問題の一覧を表示する処理を記述<br>

○XML<br>
activity_exercise.xml：問題の出題画面を記述<br>
activity_field_select.xml：分野の選択画面を記述<br>
activity_look_back.xml：間違えた問題数, 間違えた問題の一覧の画面を記述<br>
activity_main.xml：教科選択画面を記述<br>
row.xml：activity_look_back.xmlの間違えた問題一覧に使われるリスト内部の構成を記述<br>

○遷移図<br>
<img width="1000" alt="フロー図" src="https://user-images.githubusercontent.com/64346215/151970938-cf6524e3-661c-4d8d-add0-998e46d54374.png">

①科目の選択画面 選択した科目の分野一覧をサーバーから取得：ExeriseActivity.java, activity_main.xml<br>
②分野の選択画面 選択した分野の問題と正解の一覧をサーバーから取得：FieldSelectActivity.java, activity_field_select.xml<br>
③問題出題画面：ExeriseActivity.java, activity_exercise.xml<br>
④正解数, 間違えた問題の一覧画面：LookBackActivity.java, activity_look_back.xml, row.xml<br>

[1]選択した科目の分野一覧データが渡される<br>
[2]選択した分野の問題と正解の一覧データが渡される<br>
[3]問題と正解の一覧と間違えた問題数とその一覧が渡される<br><br>

**サーバー側ファイル構成**<br>
○PHPファイル<br>
get_json_from_csv.php：指定された分野の問題と正解の一覧をCSVファイルから取得し、JSON形式で返す<br>
get_json_name_table.php：指定された科目の分野一覧をCSVファイルから取得し、JSON形式で返す<br><br>

○データディレクトリ<br><br>
<img width="600" alt="フロー図" src="https://user-images.githubusercontent.com/64346215/151970115-c074882f-d481-4cbb-bff0-eaa2ff6c2a0f.png">


## 【科目・分野・問題の追加・変更方法】
1. MainActivity.javaの表示項目(科目)を変更
2. dataディレクトリ直下の科目ディレクトリを追加・変更
3. 科目ディレクトリの分野一覧CSV(name_table)を変更・追加
4. 分野別の問題・正解CSVファイルを変更・追加
