package jp.techacademy.tomomi.yoshida.thankspoint

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Favorites : DatabaseReference.CompletionListener {

    private  val currentUid: String
    private  var mDataBaseReference: DatabaseReference
    private  var mFavoritesRef: DatabaseReference
    //リストとアダプター
    private lateinit var mFavoriteArrayList: ArrayList<Favorite>
    private lateinit var mFavoritesListAdapter: FavoritesListAdapter


    // 引数付きコンストラクタ
    constructor(context: Context) {
        // FirebaseのDatabaseReferenceを取得する
        mDataBaseReference = FirebaseDatabase.getInstance().reference
        // データベースのReferenceの定義
        currentUid = FirebaseAuth.getInstance().currentUser!!.uid
        mFavoritesRef = mDataBaseReference.child(FavoritesPATH).child(currentUid)
        if (mFavoritesRef != null) {
            mFavoritesRef.removeEventListener(favoriteEventListener)
        }
        mFavoritesRef.addChildEventListener(favoriteEventListener)

        //リストとアダプター
        mFavoriteArrayList = ArrayList<Favorite>()
        mFavoritesListAdapter = FavoritesListAdapter(context)
        mFavoritesListAdapter.notifyDataSetChanged()
    }

    // リストのget
    public fun getArrayList(): ArrayList<Favorite>{
        return this.mFavoriteArrayList
    }
    // アダプターのget
    public fun getAdapter(): FavoritesListAdapter{
        return this.mFavoritesListAdapter
    }

    // お気に入りＯＮ（DB更新）
    public fun pushFavorite(favoriteUid: String, favoriteName:String){
        mFavoritesRef.child(favoriteUid).setValue(favoriteName,this)
    }
    // お気に入りＯＦＦ（DB更新）
    public fun removeFavorite(favoriteUid: String){
        mFavoritesRef.child(favoriteUid).removeValue(this)
    }

    // お気に入りデータベースのイベントリスナー
    private val favoriteEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            // お気に入りグローバルリストのset
            ThanksPointApp.setFavoriteGList(mFavoriteArrayList)
            mFavoriteArrayList.add(Favorite(dataSnapshot.key.toString(),dataSnapshot.value.toString()))
            mFavoritesListAdapter.notifyDataSetChanged()
            //Log.d("ThanksPoint","お気に入りAdd.FavoriteGList.size＝" + ThanksPointApp.getFavoriteGList().size)
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            // お気に入りグローバルリストのget
            mFavoriteArrayList = ThanksPointApp.getFavoriteGList()
            for(favorite in mFavoriteArrayList){
                if(favorite.uid == dataSnapshot.key){
                    mFavoriteArrayList.remove(favorite)
                    mFavoritesListAdapter.notifyDataSetChanged()
                    break
                }
            }
            //Log.d("ThanksPoint","お気に入りRemove.FavoriteGList.size＝" + ThanksPointApp.getFavoriteGList().size)
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        override fun onCancelled(p0: DatabaseError) {}
    }

    // お気に入りデータベースの処理結果リスナー（このクラス自体がDatabaseReference.CompletionListener）
    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
        if (databaseError == null) {
            Log.d("ThanksPoint","お気に入りDB処理＿正常終了")
        } else {
            Log.d("ThanksPoint","お気に入りDB処理＿異常終了")
        }
    }

}