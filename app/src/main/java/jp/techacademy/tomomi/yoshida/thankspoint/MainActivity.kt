package jp.techacademy.tomomi.yoshida.thankspoint

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // ListViewの準備
    private lateinit var favorites: Favorites
    private lateinit var mListView: ListView
    private lateinit var mFavoritesArrayList: ArrayList<Favorite>
    private lateinit var mAdapter: FavoritesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // UIの初期設定
        title = "ありがとう"

        // ログイン済みのユーザーを取得する
        val user = FirebaseAuth.getInstance().currentUser
        // ログインしていなければログイン画面に遷移させる
        if (user == null) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }else{
            // -------- ListViewの準備 start ----------------------
            // favoritesのグローバルインスタンスを取得
            ThanksPointApp.setFavorites()
            favorites = ThanksPointApp.getFavorites()
            // ListViewのレイアウト定義
            mListView = findViewById(R.id.listView)
            // Adapterの定義（favoritesのインスタンスから取得）
            mAdapter = favorites.getAdapter()
            // モデルのリストの定義（favoritesのインスタンスから取得）
            mFavoritesArrayList = favorites.getArrayList()
            // モデルのリストをAdapterにセット
            mAdapter.setFavoritesArrayList(mFavoritesArrayList)
            // Adapterの更新を周知
            mAdapter.notifyDataSetChanged()
            // AdapterをListViewに関連づけ
            mListView.adapter = mAdapter
        }
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
