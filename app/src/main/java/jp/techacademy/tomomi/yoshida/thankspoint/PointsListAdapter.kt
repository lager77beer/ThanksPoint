package jp.techacademy.tomomi.yoshida.thankspoint

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.list_points.view.*
import java.util.ArrayList
import androidx.core.content.ContextCompat.startActivity

class PointsListAdapter(context: Context) : BaseAdapter() {
    // 他のxmlリソースのViewを取り扱うための仕組みであるLayoutInflaterをプロパティとして定義
    private var mLayoutInflater: LayoutInflater
    // ポイントリスト
    private var mPointsArrayList = ArrayList<Point>()
    // ポイント種別
    private var pointKind: String = ""

    init {
        // LayoutInflaterを取得
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mPointsArrayList.size
    }

    override fun getItem(position: Int): Any {
        return mPointsArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView

        if (convertView == null) {
            // convertViewがnullのときはLayoutInflaterを使ってlist_pointsからViewを取得します
            // attachToRootにtrue,falseを指定したするとviewのルートが変わるらしい
            // true:parentで指定したものをルートにする、false:parentで指定したものをルートにしない
            convertView = mLayoutInflater.inflate(R.layout.list_points, parent, false)
        }

        val uidText = convertView!!.findViewById<View>(R.id.pointUid) as TextView
        val uid = mPointsArrayList[position].uid
        uidText.text = uid

        val nameText = convertView!!.findViewById<View>(R.id.pointName) as TextView
        val name= mPointsArrayList[position].name
        nameText.text = name

        val pointText = convertView!!.findViewById<View>(R.id.point) as TextView
        val point: String

        if(this.pointKind.equals(ThanksPATH)){
            point= mPointsArrayList[position].thanksPoint.toString()
        }else{
            point= mPointsArrayList[position].thankedPoint.toString()
        }
        pointText.text = point

        // コメントリスト（Thankedポイントの時のみ）
        if(this.pointKind.equals(ThankedPATH)){
            val mCommentArrayList = mPointsArrayList[position].comments!!
            // コメント表示ボタン処理
            convertView!!.commentsShowButton.setOnClickListener{v ->
                // コメント表示画面に遷移
                var intent = Intent(v.context, CommentsActivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("comments", mCommentArrayList)
                startActivity(v.context,intent,null)
            }
        }else{
            convertView!!.commentsShowButton.isEnabled = false
            convertView!!.commentsShowButton.setBackgroundColor(Color.WHITE);
        }

        return convertView
    }

    // ポイント種別をset
    public fun setPointKind(pointKind: String){
        this.pointKind = pointKind
    }

    // ポイントリストのAdapterのset
    fun setPointsArrayList(pointsArrayList: ArrayList<Point>) {
        mPointsArrayList = pointsArrayList
    }


}