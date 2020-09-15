package jp.techacademy.tomomi.yoshida.thankspoint

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

class MyMenu {

    // Java言語のStaticのような宣言
    companion object {

        // onOptionsItemSelectedの共通化
        fun selected(applicationContext: Context,item: MenuItem): Boolean {
            val id = item.itemId

            when (id) {
                R.id.action_settings -> {
                    val intent = Intent(applicationContext, SettingActivity::class.java)
                    applicationContext.startActivity(intent)
                    return true
                }
                R.id.action_say_thanks -> {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    applicationContext.startActivity(intent)
                    return true
                }
                R.id.action_totalization -> {
                    val intent = Intent(applicationContext, TotalizationActivity::class.java)
                    applicationContext.startActivity(intent)
                    return true
                }
                R.id.action_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    applicationContext.startActivity(intent)
                    return true
                }
                else -> {
                    // エラーを表示する
                    Log.d("ThanksPoint", "プログラムエラー" + id)
                    return false
                }
            }
        }
    }

}