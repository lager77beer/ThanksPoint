package jp.techacademy.tomomi.yoshida.thankspoint

import java.text.SimpleDateFormat
import java.util.*

class Common {

    // Java言語のStaticのような宣言
    companion object {

        // 本日の年の取得
        fun getTodayYear(): String {
            val date = Date()
            val format = SimpleDateFormat("yyyy", Locale.getDefault())
            return format.format(date)
        }
        // 本日の月の取得
        fun getTodayMonth(): String {
            val date = Date()
            val format = SimpleDateFormat("MM", Locale.getDefault())
            return format.format(date)
        }

    }

}