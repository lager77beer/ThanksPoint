package jp.techacademy.tomomi.yoshida.thankspoint

import java.io.Serializable

class Point(var uid: String, var name: String, var thankedPoint: Int, var thanksPoint: Int, var comments: ArrayList<String>? ) : Serializable