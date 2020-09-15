package jp.techacademy.tomomi.yoshida.thankspoint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import java.util.ArrayList
import kotlinx.android.synthetic.main.list_users.view.*

class UsersListAdapter(context: Context) : BaseAdapter(){
    // 他のxmlリソースのViewを取り扱うための仕組みであるLayoutInflaterをプロパティとして定義
    private var mLayoutInflater: LayoutInflater
    // Userのリスト
    private var mUsersArrayList = ArrayList<User>()

    init {
        // LayoutInflaterを取得
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mUsersArrayList.size
    }

    override fun getItem(position: Int): Any {
        return mUsersArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView

        if (convertView == null) {
            // convertViewがnullのときはLayoutInflaterを使ってlist_usersからViewを取得します
            // attachToRootにtrue,falseを指定したするとviewのルートが変わるらしい
            // true:parentで指定したものをルートにする、false:parentで指定したものをルートにしない
            convertView = mLayoutInflater.inflate(R.layout.list_users, parent, false)
        }

        val uidText = convertView!!.findViewById<View>(R.id.userId) as TextView
        val uid = mUsersArrayList[position].uid
        uidText.text = uid

        val nameText = convertView!!.findViewById<View>(R.id.userName) as TextView
        val name= mUsersArrayList[position].name
        nameText.text = name

        // お気に入の初期化
        convertView.favoriteOnButton.isEnabled = true
        convertView.favoriteOnButton.isVisible = true
        convertView.favoriteOffButton.isEnabled = false
        convertView.favoriteOffButton.isVisible = false
        // お気に入りボタン処理
        favoriteButton(convertView, uid, name)

        // お気に入りリストにあるユーザーの有り無しでお気に入りボタンの処理を分ける。
        var favoritesList = ThanksPointApp.getFavoriteGList()
        for (favorite in favoritesList) {
            //Log.d("ThanksPoint", "favoriteUid=" + favorite.uid)
            if(uid.equals(favorite.uid)){
                // お気に入Ｏｆｆボタン表示
                convertView.favoriteOnButton.isEnabled = false
                convertView.favoriteOnButton.isVisible = false
                convertView.favoriteOffButton.isEnabled = true
                convertView.favoriteOffButton.isVisible = true
                // for分を抜ける。
                break
            }
        }

        return convertView
    }

    // ユーザーリストのAdapterのset
    fun setUsersArrayList(usersArrayList: ArrayList<User>) {
        mUsersArrayList = usersArrayList
    }

    // お気に入りボタン処理
    private fun favoriteButton(convertView: View,uid: String, name: String){

        // お気に入りに登録
        convertView.favoriteOnButton.setOnClickListener{
            convertView.favoriteOnButton.isEnabled = false
            convertView.favoriteOnButton.isVisible = false
            // お気に入りグローバルインスタンスのpushメソッドでお気に入り登録
            ThanksPointApp.getFavorites().pushFavorite(uid,name)
            convertView.favoriteOffButton.isEnabled = true
            convertView.favoriteOffButton.isVisible = true
        }
        // お気に入りから削除
        convertView.favoriteOffButton.setOnClickListener{
            convertView.favoriteOffButton.isEnabled = false
            convertView.favoriteOffButton.isVisible = false
            // お気に入りグローバルインスタンスのremoveメソッドでお気に入り削除
            ThanksPointApp.getFavorites().removeFavorite(uid)
            convertView.favoriteOnButton.isEnabled = true
            convertView.favoriteOnButton.isVisible = true
        }
    }

}