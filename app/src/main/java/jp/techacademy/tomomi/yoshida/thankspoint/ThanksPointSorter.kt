package jp.techacademy.tomomi.yoshida.thankspoint

class ThanksPointSorter: Comparator<Point> {

    override fun compare(o1: Point, o2: Point): Int {
        // リファレンスによるとこのcompareメソッドで負の値を返すと1番目の引数の方が2番目より小さい、
        // 0を返すと両方同じ値、正なら1番目の方が2番目より大きいと判断するようです。
        // 降順にしたいので　マイナス”-”を付けた。
        return -(o1.thanksPoint.compareTo(o2.thanksPoint));
    }
}