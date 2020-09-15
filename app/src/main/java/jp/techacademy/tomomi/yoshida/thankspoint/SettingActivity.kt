package jp.techacademy.tomomi.yoshida.thankspoint

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.nameText
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    // ListViewの準備
    private lateinit var mDataBaseReference: DatabaseReference
    private lateinit var mUsersRef: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mUsersArrayList: ArrayList<User>
    private lateinit var mAdapter: UsersListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Preferenceから表示名を取得してEditTextに反映させる
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val name = sp.getString(NameKEY, "")
        nameText.setText(name)

        // UIの初期設定
        title = "設定"

        // ニックネームの変更はしないことにした。各DBテーブルに変更を反映するのがハードルが高い。
        changeButton.setOnClickListener{v ->
            // キーボードが出ていたら閉じる
            val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていない場合は何もしない
                Snackbar.make(v, "ログインしていません", Snackbar.LENGTH_LONG).show()
            } else {
                // 変更した表示名をFirebaseに保存する
                val name = nameText.text.toString()
                val userRef = mDataBaseReference.child(UsersPATH).child(user.uid)
                val data = HashMap<String, String>()
                data["name"] = name
                userRef.setValue(data)

                // 変更した表示名をPreferenceに保存する
                val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val editor = sp.edit()
                editor.putString(NameKEY, name)
                editor.commit()
                Snackbar.make(v, "ニックネームを変更しました", Snackbar.LENGTH_LONG).show()
            }
        }

        // -------- ListViewの準備 start ----------------------
        // FirebaseのDatabaseReferenceを取得する
        mDataBaseReference = FirebaseDatabase.getInstance().reference
        // ListViewのレイアウト定義
        mListView = findViewById(R.id.listView)
        // Adapterの定義
        mAdapter = UsersListAdapter(this)
        // モデルのリストの定義
        mUsersArrayList = ArrayList<User>()
        // データベースのReferenceの定義
        mUsersRef = mDataBaseReference.child(UsersPATH)
        // データベースのイベントリスナーをデータベースのReferenceに追加
        mUsersRef.addChildEventListener(mEventListener)
        // モデルのリストをAdapterにセット
        mAdapter.setUsersArrayList(mUsersArrayList)
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

    // --------- データベースのイベントリスナー　start --------------------------
    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val uid = dataSnapshot.key ?: ""
            val name = map[NameKEY] ?: ""
            val user = User(uid,name)
            mUsersArrayList.add(user)
            mAdapter.notifyDataSetChanged()
            //Log.d("ThanksPoint", "onChildAdded mUsersArrayList.size:" + mUsersArrayList.size)
        }
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            // for文で使う基のListは変更できないのでクローンする。
            var tempUserArrayList = ArrayList<User>()
            tempUserArrayList = mUsersArrayList.clone() as ArrayList<User>
            // 変更があったnameを探す
            for (user in tempUserArrayList) {
                if (dataSnapshot.key.equals(user.uid)) {
                    // このアプリで変更がある可能性があるのはnameのみ
                    mUsersArrayList.remove(user)
                    val uid = dataSnapshot.key ?: ""
                    val name = map[NameKEY] ?: ""
                    val user = User(uid,name)
                    mUsersArrayList.add(user)
                    mAdapter.notifyDataSetChanged()
                }
            }
            //Log.d("ThanksPoint", "onChildChanged mUsersArrayList.size:" + mUsersArrayList.size)
        }

        override fun onChildRemoved(p0: DataSnapshot) {}
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        override fun onCancelled(p0: DatabaseError) {}
    }
    // --------- データベースのイベントリスナー　end --------------------------

}