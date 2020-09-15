package jp.techacademy.tomomi.yoshida.thankspoint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CommentListAdapter(context: Context) : BaseAdapter() {

    // 他のxmlリソースのViewを取り扱うための仕組みであるLayoutInflaterをプロパティとして定義
    private var mLayoutInflater: LayoutInflater
    // コメントリスト
    private var mCommentArrayList = ArrayList<String>()


    init {
        // LayoutInflaterを取得
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            // convertViewがnullのときはLayoutInflaterを使ってlist_favoritesからViewを取得します
            // attachToRootにtrue,falseを指定したするとviewのルートが変わるらしい
            // true:parentで指定したものをルートにする、false:parentで指定したものをルートにしない
            convertView = mLayoutInflater.inflate(R.layout.list_comments, parent, false)
        }

        val commentText = convertView!!.findViewById<View>(R.id.comment) as TextView
        val comment = mCommentArrayList[position]
        commentText.text = comment + " ありがとう"

        return convertView
    }

    // コメントリストのAdapterのset
    fun setCommentArrayList(commentArrayList: ArrayList<String>) {
        mCommentArrayList = commentArrayList
    }

    override fun getItem(position: Int): Any {
        return mCommentArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mCommentArrayList.size
    }
}