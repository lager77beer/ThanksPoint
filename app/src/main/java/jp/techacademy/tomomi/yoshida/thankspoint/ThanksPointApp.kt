package jp.techacademy.tomomi.yoshida.thankspoint

import android.app.Application
import android.content.Context

class ThanksPointApp: Application() {
    // Java言語のStaticのような宣言
    companion object {
        // お気に入りグローバルリスト
        private lateinit var favoriteGList: ArrayList<Favorite>
        // お気に入りグローバルインスタンス
        private lateinit var favorites: Favorites
        // お気に入りグローバルインスタンス
        private lateinit var thisContext: Context

        // お気に入りグローバルインスタンスset
        fun setFavorites() {
            // お気に入りグローバルインスタンスの生成
            favorites = Favorites(thisContext)
            // お気に入りグローバルリストの生成
            favoriteGList = ArrayList<Favorite>()
        }
        // お気に入りグローバルインスタンスget
        fun getFavorites(): Favorites {
            return favorites
        }
        // お気に入りグローバルリストのset
        fun setFavoriteGList(favoriteList: ArrayList<Favorite>) {
            favoriteGList = favoriteList
        }
        // お気に入りグローバルリストのget
        fun getFavoriteGList(): ArrayList<Favorite> {
            return favoriteGList
        }
        // お気に入りグローバルリストのget
        fun clearFavoriteGList() {
            favoriteGList.clear()
        }
    }

    override fun onCreate() {
        thisContext = this
        super.onCreate()
    }


}