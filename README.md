# 無効化アプリ一覧

##どんなアプリ？
<p>端末購入直後にモリモリ入ってるプリインアプリの無効化を補助するアプリです。</p>
1. 新機種発売
2. 人柱が無効化を試す
3. 結果を共有(2chやブログなど)
4. 人柱の結果を後発ユーザーが参考をする

<p>上記のような流れを補助することを想定しています。</p>

##機能
- 無効化されたアプリの一覧を表示する
- 無効化が可能で、まだ無効化されていないアプリの一覧を表示する
- 無効化が不可能なシステムアプリの一覧を表示する
- ユーザーがインストールした一般アプリの一覧を表示する
- 表示中のアプリ一覧をメーラーなどに共有する
- 一覧画面でアプリをタップすると設定画面に飛ぶ
- 各アプリにコメントをつける（「無効化による弊害」などを書いておき、共有のカスタムフォーマットを使う）

##諸注意

<p>このアプリは非rootユーザー向けです。アプリの無効化はAndroid（ICS以上）のOS標準機能です。</p>

<p>アプリの無効化は自己責任で行ってください。無効化可能アプリの中にも、無効化すべきでないアプリは存在します。</p>

<p>無効化が可能かどうかの判定は、Androidのソースをもとにそれっぽい処理をしているだけなので、端末によっては正常に動作しないかもしれません。対応には限界があるので、無効化できないやつが混ざっていても怒らないでください。（SH-02Eにおいて、一部のドコモのアプリが無効化できないにも関わらず一覧に表示されるのを確認しています。）</p>

##要求パーミッション

<p>ありません。</p>

##動作環境

<p>OS4.0.3以上で動作可能です。</p>

##ダウンロード

http://ux.getuploader.com/75py/download/7/Disabled_Apps_List_v1.3.0.apk

<p>もう少し手直ししたらplayにあげる予定です。</p>

##ライセンス

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

アイコンファイルの無断転載を禁止します。

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