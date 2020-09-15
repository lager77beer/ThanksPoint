package jp.techacademy.tomomi.yoshida.thankspoint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.material.snackbar.Snackbar

class TotalizationActivity : AppCompatActivity() {

    // ListViewの準備
    private lateinit var mListView: ListView
    private lateinit var selectYear: String
    private lateinit var selectMonth: String
    private lateinit var selectPoint: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_totalization)

        // UIの初期設定
        title = "集計"

        // ListViewのレイアウト定義
        mListView = findViewById(R.id.listView)

        // スピナー（プルダウンメニュー）
        // 年の選択肢
        val spinnerYearItems = arrayOf("2019", "2020", "2021", "2022", "2023","2024")
        // 月の選択肢
        val spinnerMonthItems = arrayOf("all","01", "02", "03", "04", "05","06","07","08","09","10","11","12")
        // 集計対象の選択肢
        val spinnerPointItems = arrayOf("ThankedPoint（ありがとうを言われた数）", "ThanksPoint（ありがとうを言った数）")
        // Spinnerの取得
        val spinnerYear = findViewById<Spinner>(R.id.spinnerYear)
        val spinnerMonth = findViewById<Spinner>(R.id.spinnerMonth)
        val spinnerPoint = findViewById<Spinner>(R.id.spinnerPoint)
        // Adapterの生成
        val adapterYear = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerYearItems)
        val adapterMonth = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerMonthItems)
        val adapterPont = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerPointItems)
        // 選択肢の各項目のレイアウト
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterPont.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // AdapterをSpinnerのAdapterとして設定
        spinnerYear.adapter = adapterYear
        spinnerMonth.adapter = adapterMonth
        spinnerPoint.adapter = adapterPont
        // 選択肢の初期表示
        when (Common.getTodayYear()) {
            "2020" -> { spinnerYear.setSelection(1) }
            "2021" -> { spinnerYear.setSelection(2) }
            "2022" -> { spinnerYear.setSelection(3) }
            "2023" -> { spinnerYear.setSelection(4) }
            "2024" -> { spinnerYear.setSelection(5) }
            else -> { spinnerYear.setSelection(0)   }
        }
        // 選択肢の初期表示
        when (Common.getTodayMonth()) {
            "01" -> { spinnerMonth.setSelection(1) }
            "02" -> { spinnerMonth.setSelection(2) }
            "03" -> { spinnerMonth.setSelection(3) }
            "04" -> { spinnerMonth.setSelection(4) }
            "05" -> { spinnerMonth.setSelection(5) }
            "06" -> { spinnerMonth.setSelection(6) }
            "07" -> { spinnerMonth.setSelection(7) }
            "08" -> { spinnerMonth.setSelection(8) }
            "09" -> { spinnerMonth.setSelection(9) }
            "10" -> { spinnerMonth.setSelection(10) }
            "11" -> { spinnerMonth.setSelection(11) }
            "12" -> { spinnerMonth.setSelection(11) }
            else -> { spinnerMonth.setSelection(0)   }
        }

        // スピナーのリスナーを登録
        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                selectYear = spinnerParent.selectedItem as String
            }
            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                selectMonth = spinnerParent.selectedItem as String
            }
            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerPoint.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                selectPoint = spinnerParent.selectedItem as String
            }
            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 集計ボタンのリスナーを登録
        val ButtonTotalization = findViewById<Button>(R.id.ButtonTotalization)
        ButtonTotalization.setOnClickListener {v ->
            doTotalization(v)
        }

    }

    // 集計処理
    private fun doTotalization(v: View) {
        //Log.d("ThanksPoint", "集計処理 選択肢=[${selectYear}],[${selectMonth}],[${selectPoint}]")
        // -------- ListViewの準備 start ----------------------
        var workSelectMonth = ""
        if(selectMonth.equals("all")){
            selectMonth = ""
            workSelectMonth = "全"
        }else{
            workSelectMonth = selectMonth
        }
        // Ponitsのインスタンス生成
        val points = Points(this,selectYear,selectMonth)
        // Adapterの定義（pontsのインスタンスから取得）
        val mAdapter = points.getAdapter()
        // PonitsのインスタンスとAdapterにポイント種別をset
        if(selectPoint.startsWith("Thanked")){
            points.setPointKind(ThankedPATH)
            mAdapter.setPointKind(ThankedPATH)
        }else{
            points.setPointKind(ThanksPATH)
            mAdapter.setPointKind(ThanksPATH)
        }
        // モデルのリストの定義（favoritesのインスタンスから取得）
        val mPointsArrayList = points.getArrayList()
        // モデルのリストをAdapterにセット
        mAdapter.setPointsArrayList(mPointsArrayList)
        // Adapterの更新を周知
        mAdapter.notifyDataSetChanged()
        // AdapterをListViewに関連づけ
        mListView.adapter = mAdapter

        // メッセージ
        Snackbar.make(v, "${selectYear}年${workSelectMonth}月の「${selectPoint}」の集計をしました。", Snackbar.LENGTH_LONG).show()
    }

    // メニュー
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    // メニュー処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  MyMenu.selected(applicationContext,item)
    }

}
