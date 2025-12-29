# Omikuji
## 概要
おみくじを実装するPluginです。  
動作期待バージョン: 1.21.11
動作確認済みバージョン: 1.21.11
ダウンロードは[こちら](https://github.com/mikan010/Omikuji/releases/latest)

## Fork / Upstream（重要）
本リポジトリは **siloneco** 氏によるプロジェクトを元にした **fork（派生版）** です。  
- Upstream（元）: [Omikuji](https://github.com/siloneco/Omikuji)
- Original author: [siloneco](https://github.com/siloneco)
- Maintainer (this fork): [mikan010](https://github.com/mikan010)
- 注意: 本リポジトリは upstream の公式版ではありません（非公式の派生版です）

## Changes（このforkでの変更点）
upstream からの主な変更点:
- 対応バージョンの更新

## 機能
* おみくじが引ける
* 結果を自由に編集できる
* 結果によってアイテムを与えることができる
* おみくじに料金を課せられる
* プレイヤーごとに引ける回数を指定できる
* コマンドと看板両方で引ける (設定で無効化することも可能)
* 設定時刻になったら自動的におみくじを解禁、禁止できる

## 設定方法
1. Pluginを導入し、サーバーを再起動 (Plugmanを導入している場合は /plugman load Omikujiでも可)
2. 生成された config.yml を好みの設定に変更し、/omikuji reload を実行
3. 当たった時に貰えるアイテムを設定したい場合は、サーバーに入り /omikuji setItem \<ID> を使用して設定
4. 看板を使用する場合は看板を設置 (看板の設置方法は下に記載)
5. 必要ならば動作確認

## 看板の設置方法
1. 任意の場所に看板を設置し、1行目に ``[omikuji]`` と入力
2. 2-4行目は自由。カラーコードは & で使用出来ます


## コマンド
|        コマンド        | 内容                                   |       権限        |
| :--------------------: | :------------------------------------- | :---------------: |
|        /omikuji        | おみくじを引きます                     | omikuji.allowdraw |
|     /omikuji help      | コマンド一覧を表示します               |   omikuji.admin   |
|    /omikuji reload     | 設定ファイルをリロードします           |   omikuji.admin   |
|     /omikuji info      | おみくじの結果一覧を表示します         |   omikuji.admin   |
| /omikuji viewItem <ID> | 当たった時に貰えるアイテムを表示します |   omikuji.admin   |
| /omikuji setItem <ID>  | 当たった時に貰えるアイテムを設定します |   omikuji.admin   |

## 権限
|             権限             | 説明                                                       | デフォルト |
| :--------------------------: | :--------------------------------------------------------- | :--------: |
|      omikuji.allowdraw       | おみくじを引くことができる権限                             |    全員    |
|   omikuji.command.omikuji    | /omikujiコマンドを実行できる権限                           |    全員    |
|        omikuji.admin         | /omikujiコマンドの運営用パラメーターを実行できる権限       |     OP     |
| omikuji.bypass.drawtimelimit | AutoEnable/AutoDisableを設定中に、その制限を免除できる権限 |     OP     |
|     omikuji.bypass.cost      | 料金を設定している場合、それを免除できる権限               |     OP     |
|    omikuji.bypass.amount     | 引ける量を制限している場合、それを免除できる権限           |     OP     |

## ライセンス (License)
[GNU General Public License v3.0](LICENSE)

## クレジット (Credits)
- Original author: [siloneco](https://github.com/siloneco)
- Maintainer (this fork): [mikan010](https://github.com/mikan010)
