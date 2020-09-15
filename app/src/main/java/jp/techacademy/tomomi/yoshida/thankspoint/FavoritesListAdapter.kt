package jp.techacademy.tomomi.yoshida.thankspoint

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.list_favorites.view.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FavoritesListAdapter(context: Context) : BaseAdapter() {
    // 他のxmlリソースのViewを取り扱うための仕組みであるLayoutInflaterをプロパティとして定義
    private var mLayoutInflater: LayoutInflater
    // お気に入りリスト
    private var mFavoritesArrayList = ArrayList<Favorite>()
    // Pointsインスタンス
    private var points: Points
    // Contextインスタンス
    private var context: Context
    // ログイン済みのユーザーを取得する
    private val currentUser = FirebaseAuth.getInstance().currentUser

    init {
        // LayoutInflaterを取得
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // Pointsインスタンスの生成
        points = Points(context)
        // Contextインスタンス
        this.context = context
    }

    override fun getCount(): Int {
        return mFavoritesArrayList.size
    }

    override fun getItem(position: Int): Any {
        return mFavoritesArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView

        if (convertView == null) {
            // convertViewがnullのときはLayoutInflaterを使ってlist_favoritesからViewを取得します
            // attachToRootにtrue,falseを指定したするとviewのルートが変わるらしい
            // true:parentで指定したものをルートにする、false:parentで指定したものをルートにしない
            convertView = mLayoutInflater.inflate(R.layout.list_favorites, parent, false)
        }

        val uidText = convertView!!.findViewById<View>(R.id.favoriteUid) as TextView
        val uid = mFavoritesArrayList[position].uid
        uidText.text = uid

        val nameText = convertView!!.findViewById<View>(R.id.favoriteName) as TextView
        val name= mFavoritesArrayList[position].name
        nameText.text = name

        // ありがとうボタン処理
        thanksButton(convertView, uid, name)

        return convertView
    }

    // お気に入りリストのAdapterのset
    fun setFavoritesArrayList(favoritesArrayList: ArrayList<Favorite>) {
        mFavoritesArrayList = favoritesArrayList
    }

    // ありがとうボタン処理
    private fun thanksButton(convertView: View, uid: String, name: String){

        // ありがとうを言う
        convertView.thanksButton.setOnClickListener{

            // デバッグ用処理 s
            val yearText = convertView!!.findViewById<View>(R.id.yearText) as EditText
            val year = yearText.text.toString()
            val monthText = convertView!!.findViewById<View>(R.id.monthText) as EditText
            val month = monthText.text.toString()
            // デバッグ用処理 e

            // パラメータ作成
            val uidText = convertView!!.findViewById<View>(R.id.favoriteUid) as TextView
            val uid = uidText.text.toString()

            val nameText = convertView!!.findViewById<View>(R.id.favoriteName) as TextView
            val name = nameText.text.toString()

            val commentText = convertView!!.findViewById<View>(R.id.commentText) as TextView
            val comment = commentText.text.toString()

            val thankedData = HashMap<String, String>()
            thankedData["name"] = name
            // Thankedポイント追加（DB更新）　nameとcommentとthanked追加・更新
            points.pushThankedPoint(uid,thankedData,comment)
            // デバッグ用処理 s
            //points.pushThankedPointDebug(uid,thankedData,comment,year,month)
            // デバッグ用処理 e

            // Preferenceから名前を取る
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val currentName = sp.getString(NameKEY, "")
            val thanksData = HashMap<String, String>()
            thanksData["name"] = currentName!!
            // Thanksポイント追加（DB更新）　nameとthanks追加・更新
            points.pushThanksPoint(currentUser!!.uid, thanksData)
            // デバッグ用処理 s
            //points.pushThanksPointDebug(currentUser!!.uid, thanksData,year,month)
            // デバッグ用処理 e

            Log.d("ThanksPoint", "ありがとうDB処理=${name}")
            // メッセージ
            Snackbar.make(convertView, "${name}さん、ありがとう（＋１点）！", Snackbar.LENGTH_LONG).show()
        }
    }

}