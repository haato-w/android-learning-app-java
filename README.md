# android-learning-app-java

勉強用のAndroidアプリケーションのプログラムです。
サーバー側にcsv形式で問題と分野名を置いて、問題を開始するタイミングでサーバーから問題の情報を引っ張ってきています。
サンプルは中学社会の問題を入れていますが、一問一答形式であれば好きな問題をcsvで保存しておくことで様々な応用が可能です。


## 【実行方法】
1. src内のmainディレクトリをAndroidのプロジェクトに入れる
2. src内のserverディレクトリの内容（phpコードとcsvデータ）をサーバーに置く
3. AndroidコードのMainActivity.java(42行目)とFieldSelectActivity.java(43行目)にサーバーのURLを書き込む
4. Androidアプリを起動する

## 【詳細】
**言語**
アプリ側：Java
サーバー側：PHP

**アプリ側ファイル構成**
○Java
ExeriseActivity.java：問題の出題, 解答判定, 正解数のカウントを行う処理を記述
MainActivity.java：教科選択画面(初期画面)の動作を記述, 選択された教科の分野をサーバーから取得する
FieldSelectActivity.java：分野選択画面の動作を記述, 選択された分野の問題と答えをサーバーから取得する
LookBackActivity.java：正解数を表示, 間違えた問題の一覧を表示する処理を記述

○XML
activity_exercise.xml：問題の出題画面を記述
activity_field_select.xml：分野の選択画面を記述
activity_look_back.xml：正解数, 間違えた問題の一覧の画面を記述
activity_main.xml：教科選択画面を記述
row.xml：activity_look_back.xmlの間違えた問題一覧に使われるリスト内部の構成を記述

○遷移図
![flow](https://user-images.githubusercontent.com/64346215/151960050-9430088d-3933-4a1a-b0eb-74cbf4506db4.png)

①科目の選択画面：ExeriseActivity.java, activity_main.xml
②分野の選択画面：FieldSelectActivity.java, activity_field_select.xml
③問題出題画面：ExeriseActivity.java, activity_exercise.xml
④正解数, 間違えた問題の一覧画面：LookBackActivity.java, activity_look_back.xml, row.xml

**サーバー側ファイル構成**
○PHPファイル
get_json_from_csv.php：指定された分野の問題と正解の一覧をCSVファイルから取得し、JSON形式で返す
get_json_name_table.php：指定された科目の分野一覧をCSVファイルから取得し、JSON形式で返す

○データディレクトリ


## 【科目・分野・問題の追加・変更方法】






