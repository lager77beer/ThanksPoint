package jp.techacademy.tomomi.yoshida.thankspoint

import android.content.Context
import android.util.Log
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Points : DatabaseReference.CompletionListener {

    private var mDataBaseReference: DatabaseReference
    private var mPointsRef: DatabaseReference
    private lateinit var month: String
    private  lateinit var mThankedPointRef: DatabaseReference
    //リストとアダプター
    private lateinit var mPointArrayList: ArrayList<Point>
    private lateinit var mPointsListAdapter: PointsListAdapter
    // ポイント種別
    private var pointKind: String = ""


    // 引数付きコンストラクタ
    constructor(context: Context) {
        // FirebaseのDatabaseReferenceを取得する
        mDataBaseReference = FirebaseDatabase.getInstance().reference
        //mPointsRefの生成
        mPointsRef = mDataBaseReference.child(PointsPATH)
    }

    // 引数（年月）付きコンストラクタ
    constructor(context: Context, year: String, month: String) {

        this.month = month

        // FirebaseのDatabaseReferenceを取得する
        mDataBaseReference = FirebaseDatabase.getInstance().reference
        //mPointsRefの生成
        if(!month.isNullOrEmpty()){
            //monthの指定があった場合は月の集計
            mPointsRef = mDataBaseReference.child(PointsPATH).child(year).child(month)
        }else{
            //monthの指定がなかった場合は年の集計
            mPointsRef = mDataBaseReference.child(PointsPATH).child(year)
        }

        if (mPointsRef != null) {
            mPointsRef.removeEventListener(pointEventListener)
        }
        mPointsRef.addChildEventListener(pointEventListener)

        //リストとアダプター
        mPointArrayList = ArrayList<Point>()
        mPointsListAdapter = PointsListAdapter(context)
        mPointsListAdapter.notifyDataSetChanged()
    }

    // リストのget
    public fun getArrayList(): ArrayList<Point>{
        return this.mPointArrayList
    }
    // アダプターのget
    public fun getAdapter(): PointsListAdapter{
        return this.mPointsListAdapter
    }

    // Thankedポイント追加（DB更新）
    public fun pushThankedPoint(uid: String, thankedData: HashMap<String, String>, comment: String){
        mThankedPointRef = mDataBaseReference.child(PointsPATH).child(Common.getTodayYear()).child(Common.getTodayMonth()).child(uid)
        // Thankedポイントのインクリメント
        incrementPoint("thanked", thankedData, comment)
    }

    // Thanksポイント追加（DB更新）
    public fun pushThanksPoint(uid: String, thanksData: HashMap<String, String>){
        mThankedPointRef = mDataBaseReference.child(PointsPATH).child(Common.getTodayYear()).child(Common.getTodayMonth()).child(uid)
        // Thanksポイントのインクリメント
        incrementPoint("thanks", thanksData, "")
    }

    // Thankedポイント追加（DB更新）デバッグ用
    public fun pushThankedPointDebug(uid: String, thankedData: HashMap<String, String>, comment: String, year: String, month: String){
        mThankedPointRef = mDataBaseReference.child(PointsPATH).child(year).child(month).child(uid)
        // Thankedポイントのインクリメント
        incrementPoint("thanked", thankedData, comment)
    }

    // Thanksポイント追加（DB更新）デバッグ用
    public fun pushThanksPointDebug(uid: String, thanksData: HashMap<String, String>, year: String, month: String){
        mThankedPointRef = mDataBaseReference.child(PointsPATH).child(year).child(month).child(uid)
        // Thanksポイントのインクリメント
        incrementPoint("thanks", thanksData, "")
    }

    // ThankedポイントとThanksポイントのインクリメント処理
    // トランザクション データの保存
    private  fun incrementPoint(actionKbn: String, pointData: HashMap<String, String>, comment: String) {
        mThankedPointRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                //Log.d("ThanksPoint", "ポイント追加=" + mThankedPointRef.toString())
                val pointMutableData = mutableData.getValue(PointsForTransaction::class.java)

                val pointMap = HashMap<String, Any>()

                // レコード追加
                if(pointMutableData == null){
                    pointMap.put("name",pointData["name"].toString())
                    // comment追加
                    val comments = ArrayList<String>()
                    comments.add(comment ?: "" )
                    pointMap.put("comments",comments)
                    //Log.d("ThanksPoint", "Pointレコード追加=${pointMap["comments"]}")
                // レコード更新
                }else{
                    // name変更
                    pointMap.put("name", pointData["name"].toString())
                    // comment追加
                    val comments = pointMutableData!!.comments
                    if(!comment.isNullOrEmpty()) {
                        comments!!.add(comment ?: "" )
                    }
                    pointMap.put("comments", comments)
                    //Log.d("ThanksPoint", "Pointレコード更新=${pointMap["comments"]}")
                }

                //DBのポイント値を取得
                val pointOfThanked = pointMutableData?.thankedPoint ?: null
                val pointOfThanks = pointMutableData?.thanksPoint ?: null

                //処理区分が"thanked"の場合Thankyouされた人のthankedPointをインクリメント
                if(actionKbn.equals("thanked")){
                    if (pointOfThanked.isNullOrEmpty()) {
                        //＋１
                        pointMap.put("thankedPoint", "1")
                        //DBのフォーマットに合わせないとdoTransactionでエラーとなるので取得した値そのまま
                        pointMap.put("thanksPoint", pointOfThanks ?: "0")
                    } else {
                        //インクリメント
                        val incrementPoint = Integer.parseInt(pointOfThanked) + 1
                        pointMap.put("thankedPoint", incrementPoint.toString())
                        //DBのフォーマットに合わせないとdoTransactionでエラーとなる。
                        pointMap.put("thanksPoint", pointOfThanks ?: "0")
                    }
                    //Log.d("ThanksPoint", "thankedPointポイント=${pointMap["thankedPoint"]}")
                }
                //処理区分が"thanks"の場合Thankyouした人のthanksPointをインクリメント
                if(actionKbn.equals("thanks")){
                    if (pointOfThanks.isNullOrEmpty()) {
                        //＋１
                        pointMap.put("thanksPoint", "1")
                        //DBのフォーマットに合わせないとdoTransactionでエラーとなるので取得した値そのまま
                        pointMap.put("thankedPoint", pointOfThanked ?: "0")
                    } else {
                        //インクリメント
                        val incrementPoint = Integer.parseInt(pointOfThanks) + 1
                        pointMap.put("thanksPoint", incrementPoint.toString())
                        //DBのフォーマットに合わせないとdoTransactionでエラーとなるので取得した値そのまま
                        pointMap.put("thankedPoint", pointOfThanked ?: "0")
                    }
                    //Log.d("ThanksPoint", "thankedPointポイント=${pointMap["thanksPoint"]}")
                }

                mutableData.setValue(pointMap)
                return Transaction.success(mutableData)
            }
            override fun onComplete(err: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                if (err == null) {
                    Log.d("ThanksPoint", "インクリメントDB処理成功=${dataSnapshot?.toString()}")
                } else {
                    Log.d("ThanksPoint", "インクリメントDB処理失敗=${err?.toString()}")
                }
            }
        })
    }

    // ポイントデータベースのイベントリスナー
    private val pointEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            if(!month.isNullOrEmpty()){
                //monthの指定があった場合は月の集計
                val map = dataSnapshot.value as Map<String, String>
                val uid = dataSnapshot.key.toString()
                val name = map["name"] ?: ""
                val thanksPoint = map["thanksPoint"] ?: ""
                val thankedPoint = map["thankedPoint"] ?: ""
                val comments = map["comments"] as ArrayList<String>?
                val point = Point(uid,name, Integer.parseInt(thankedPoint), Integer.parseInt(thanksPoint), comments )
                //Log.d("ThanksPoint","ポイントAdd uid=[${uid}], name=[${name}], thanksPoint=[${thanksPoint}] thankedPoint=[${thankedPoint}]")
                //for(comment in comments!!){
                //    Log.d("ThanksPoint","ポイントAdd comment=[${comment}]")
                //}
                mPointArrayList.add(point)
                // ソート
                if(pointKind.equals(ThankedPATH)){
                    Collections.sort(mPointArrayList, ThankedPointSorter());
                }else{
                    Collections.sort(mPointArrayList, ThanksPointSorter());
                }
                // ソート e
                mPointsListAdapter.notifyDataSetChanged()
            }else{
                //monthの指定がなかった場合は年の集計
                val map = dataSnapshot.value as Map<String, String>
                val month = dataSnapshot.key.toString()
                for(key in map.keys){
                    val uid = key
                    val pointMap = map[uid] as Map<String, String>
                    val name = pointMap["name"] ?: ""
                    var thanksPoint = pointMap["thanksPoint"] ?: ""
                    var thankedPoint = pointMap["thankedPoint"] ?: ""
                    var comments = pointMap["comments"] as ArrayList<String>?
                    var point = Point(uid,name, Integer.parseInt(thankedPoint),Integer.parseInt(thanksPoint), comments )
                    //Log.d("ThanksPoint","ポイントAdd uid=[${uid}], name=[${name}], thanksPoint=[${thanksPoint}] thankedPoint=[${thankedPoint}]")
                    //for(comment in comments!!){
                    //    Log.d("ThanksPoint","ポイントAdd comment=[${comment}]")
                    //}

                    // 月ごとのポイントを合算　s
                    //var tempArrayList = mPointArrayList.clone() as ArrayList<Point>
                    var removePoint: Any = ""
                    for (tempPoint in mPointArrayList){
                        // すでにリストにあるユーザーはポイントとコメントを合算する。
                        if(uid.equals(tempPoint.uid)){
                            thankedPoint = (tempPoint.thankedPoint + Integer.parseInt(thankedPoint)).toString()
                            thanksPoint = (tempPoint.thanksPoint + Integer.parseInt(thanksPoint)).toString()
                            tempPoint.comments!!.addAll(comments!!)
                            point = Point(uid,name, Integer.parseInt(thankedPoint),Integer.parseInt(thanksPoint), tempPoint.comments )
                            removePoint = tempPoint
                            break
                        // リストにまだないユーザーはそのままリ
                        }else{
                            continue
                        }
                    }
                    // すでにリストにあるユーザーは削除してから追加する。
                    if (removePoint != ""){
                        mPointArrayList.remove(removePoint)
                    }
                    mPointArrayList.add(point)
                    // 月ごとのポイントを合算　e

                    // ソート s
                    if(pointKind.equals(ThankedPATH)){
                        Collections.sort(mPointArrayList, ThankedPointSorter());
                    }else{
                        Collections.sort(mPointArrayList, ThanksPointSorter());
                    }
                    // ソート e
                    mPointsListAdapter.notifyDataSetChanged()
                }
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        override fun onCancelled(p0: DatabaseError) {}
    }
    // --------- データベースのイベントリスナー　end --------------------------

    // ポイントデータベースの処理結果リスナー（このクラス自体がDatabaseReference.CompletionListener）
    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
        if (databaseError == null) {
            Log.d("ThanksPoint","ポイントDB処理＿正常終了")
        } else {
            Log.d("ThanksPoint","ポイントDB処理＿異常終了")
        }
    }

    // ポイント種別をset
    public fun setPointKind(pointKind: String){
        this.pointKind = pointKind
    }

}