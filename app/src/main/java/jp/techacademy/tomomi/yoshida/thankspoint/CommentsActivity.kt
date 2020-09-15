package jp.techacademy.tomomi.yoshida.thankspoint

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity: AppCompatActivity()  {

    // ListViewの準備
    private lateinit var mListView: ListView
    private lateinit var mCommentArrayList: ArrayList<String>
    private lateinit var mAdapter: CommentListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        // UIの初期設定
        title = "もらったコメント"

        // 渡ってきたnameとcommentのオブジェクトを保持する
        val extras = intent.extras
        val name = extras!!.get("name").toString()
        mCommentArrayList = extras!!.get("comments") as ArrayList<String>
        // デバッグ S
        //for (c in mCommentArrayList){
        //    Log.d("ThanksPoint","コメント表示ボタン コメント=[${c}]")
        //}

        val nameText = this.name
        nameText.text = name

        // -------- ListViewの準備 start ----------------------
        // ListViewのレイアウト定義
        mListView = findViewById(R.id.listView)
        // Adapterの定義
        mAdapter = CommentListAdapter(this)
        // モデルのリストの定義（↑の「渡ってきたオブジェクトを保持」で実施済み）
        //mCommentArrayList = ArrayList<String>()
        // モデルのリストをAdapterにセット
        mAdapter.setCommentArrayList(mCommentArrayList)
        // Adapterの更新を周知
        mAdapter.notifyDataSetChanged()
        // AdapterをListViewに関連づけ
        mListView.adapter = mAdapter

    }

    // メニュー
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    // メニュー処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  MyMenu.selected(applicationContext,item)
    }

}