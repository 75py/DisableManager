# 無効化アプリ一覧

##どんなアプリ？
<p>プリインストールアプリの無効化を補助するアプリです。</p>
1. 新機種発売
2. 人柱が無効化を試す
3. 結果を共有(2chやブログなど)
4. 人柱の結果を後発ユーザーが参考をする

<p>上記のような流れを補助することを目的としています。</p>

##機能
- 無効化されたアプリの一覧を表示する<br>一通り無効化したら、このリストを共有すると良い
- 無効化が可能で、まだ無効化されていないアプリの一覧を表示する<br>無効化作業をサポート
- 無効化が不可能なシステムアプリの一覧を表示する<br>残念ながら無効化できない子たち
- ユーザーがインストールした一般アプリの一覧を表示する<br>おまけ機能
- 表示中のアプリ一覧をメーラーなどに共有する
- 一覧画面でアプリをタップすると設定画面に飛ぶ<br>「無効にする」を押しに行けます。
- 各アプリにコメントをつける<br>「無効化による弊害」などを書いておき、共有すると良いかも。要カスタムフォーマット
- 実行中のプロセスのみ表示機能<br>無効化を検討する際の基準に使えます。

##諸注意

<p>このアプリは非rootユーザー向けです。アプリの無効化はAndroid（ICS以上）のOS標準機能です。</p>

<p>アプリの無効化は自己責任で行ってください。無効化可能アプリの中にも、無効化すべきでないアプリは存在します。</p>

<p>無効化が可能かどうかの判定は、Androidのソースをもとにそれっぽい処理をしているだけなので、端末によっては正常に動作しないかもしれません。<br>
対応には限界があるので、無効化できないやつが混ざっていても怒らないでください。<br>
（SH-02Eにおいて、一部のドコモのアプリが無効化できないにも関わらず一覧に表示されるのを確認しています。）<br>
無効化できない子たちはリストに並ばれると邪魔なので、手動にて「長押し→除外リストに追加」で除外してください。</p>

##要求パーミッション
ありません。

##動作環境
4.0.3、4.1.2、4.2のエミュレーターで動作確認済み。OS4.0.3以上で動作可能です。

##ダウンロード

https://play.google.com/store/apps/details?id=com.nagopy.android.disablemanager

##License

EXCEPT icon resources, this source and related resources are available under Apache License 2.0.
Unauthorized copying and replication of the icon files are strictly prohibited. All Rights Reserved.

アイコンファイルを除いて、このソースコードや関連リソースはApache License 2.0とします。
アイコンファイルの無断転載を禁止します。

<pre>
Copyright 2013 75py

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>

無効化できるアプリを判定する際に、Androidのソースコードの一部を利用しています。
該当するクラスとライセンスは以下の通りです。

###android.app.admin.DevicePolicyManager
<pre>
Copyright (C) 2010 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>

###com.android.settings.applications.InstalledAppDetails
<pre>
Copyright (C) 2007 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
</pre>