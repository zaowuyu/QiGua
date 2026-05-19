package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class Money : AppCompatActivity() {
    // 声明按钮
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button_reset: Button
    private lateinit var button_query: Button
    private lateinit var button_start: Button
    private lateinit var display1: TextView
    private lateinit var display2: TextView
    private lateinit var display3: TextView
    private lateinit var yao1: TextView
    private lateinit var yao2: TextView
    private lateinit var yao3: TextView
    private lateinit var yao4: TextView
    private lateinit var yao5: TextView
    private lateinit var yao6: TextView
    private lateinit var resultT: TextView
    private val handler = android.os.Handler()
    private var isRunning = false
    private var updateRunnable: Runnable? = null
    var dnum1 = 0
    var dnum2 = 0
    var dnum3 = 0
    var nyao1 = 0
    var nyao2 = 0
    var nyao3 = 0
    var nyao4 = 0
    var nyao5 = 0
    var nyao6 = 0
    // 当前激活按钮的索引
    private var currentActiveIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.money)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // 获取按钮的 ID
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)
        button_reset = findViewById(R.id.button_reset)
        button_query = findViewById(R.id.button_query)
        button_start = findViewById(R.id.button_start)
        display1 = findViewById(R.id.text_on_circle1)
        display2 = findViewById(R.id.text_on_circle2)
        display3 = findViewById(R.id.text_on_circle3)
        yao1 = findViewById(R.id.display1)
        yao2 = findViewById(R.id.display2)
        yao3 = findViewById(R.id.display3)
        yao4 = findViewById(R.id.display4)
        yao5 = findViewById(R.id.display5)
        yao6 = findViewById(R.id.display6)
        resultT = findViewById(R.id.text_output)
        resultT.movementMethod = ScrollingMovementMethod()

        // 定义定时更新的 Runnable
        updateRunnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    // 随机生成0或1，并更新显示框
                    dnum1 = (Math.random() < 0.5).toInt()
                    dnum2 = (Math.random() < 0.5).toInt()
                    dnum3 = (Math.random() < 0.5).toInt()
                    display1.text = if (dnum1 == 1) "正" else "反"
                    display2.text = if (dnum2 == 1) "正" else "反"
                    display3.text = if (dnum3 == 1) "正" else "反"

                    // 每0.5秒更新一次
                    handler.postDelayed(this, 100)
                }
            }
        }
        // 初始化按钮状态，所有按钮文本都为“开始”，第一个按钮激活
        val buttons = arrayOf(button1, button2, button3, button4, button5, button6)
        val yaos = arrayOf(yao1, yao2, yao3, yao4, yao5, yao6)
        val nyaos = arrayOf(nyao1, nyao2, nyao3, nyao4, nyao5, nyao6)
        button_start.isEnabled = false
        buttons[currentActiveIndex].isEnabled = true
        buttons[currentActiveIndex].text = "摇\n卦"  // 第一个按钮文本设置为 "开始"
        for (i in 1 until buttons.size) {
            buttons[i].isEnabled = false
            buttons[i].text = "摇\n卦"  // 其他按钮文本为 "开始"
        }

        // 设置按钮点击监听
        for (i in buttons.indices) {
            buttons[i].setOnClickListener {

                if (buttons[i].text == "摇\n卦"&& !isRunning) {
                    // 如果当前按钮是“开始”，则将其文本改为“停止”
                    isRunning = true
                    buttons[i].text = "停\n止"
                    handler.post(updateRunnable as Runnable)
                } else {
                    // 如果当前按钮是“停止”，则将其文本改为“开始”
                    isRunning = false
                    buttons[i].text = "摇\n卦"
                    nyaos[i] = dnum1 + dnum2 + dnum3 + 6
                    yaos[i].text = nyaos[i].toString()
                    if (nyaos[i] == 6 || nyaos[i] == 9) {
                        yaos[i].setTextColor(Color.BLUE)  // 设置蓝色
                    }else {
                        yaos[i].setTextColor(Color.WHITE) // 恢复默认颜色（可修改）
                    }

                    // 转移激活状态到下一个按钮
                    if (i + 1 < buttons.size) {
                        buttons[i + 1].isEnabled = true
                        buttons[i + 1].text = "摇\n卦"  // 下一个按钮文本设置为“开始”
                    }
                    buttons[i].isEnabled = false  // 禁用当前按钮
                }
                var k = 0
                for(i in buttons.indices)
                {
                    if(buttons[i].isEnabled == false) k++
                }
                if(k==6) button_start.isEnabled = true
            }
        }

        // 设置重置按钮点击监听
        button_reset.setOnClickListener {
            // 重置按钮状态，第一个按钮激活，其他按钮不可点击
            button_start.isEnabled = false
            isRunning = false
            display1.text = "正"
            display2.text = "正"
            display3.text = "正"
            yao1.text = "0"
            yao1.setTextColor(Color.WHITE)
            yao2.text = "0"
            yao2.setTextColor(Color.WHITE)
            yao3.text = "0"
            yao3.setTextColor(Color.WHITE)
            yao4.text = "0"
            yao4.setTextColor(Color.WHITE)
            yao5.text = "0"
            yao5.setTextColor(Color.WHITE)
            yao6.text = "0"
            yao6.setTextColor(Color.WHITE)
            currentActiveIndex = 0
            buttons[currentActiveIndex].isEnabled = true
            buttons[currentActiveIndex].text = "摇\n卦"  // 第一个按钮文本为“开始”
            for (i in 1 until buttons.size) {
                buttons[i].isEnabled = false
                buttons[i].text = "摇\n卦"  // 其他按钮文本为“开始”
            }
        }

        button_start.setOnClickListener{
            resultT.text = "解卦结果\n" + showGua(nyaos)
            resultT.visibility = TextView.VISIBLE
        }

        // 设置卦象查询按钮的点击监听
        button_query.setOnClickListener {
            startActivity(Intent(this, WebPageActivity::class.java))
            // 处理卦象查询逻辑
            // 在这里你可以执行任何你需要的操作
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        updateRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun Boolean.toInt() = if (this) 1 else 0


}

fun change_yao(nyaos: Array<Int>): Int{
    var cnum = 0
    var i = 0
    var k = 0
    var numbers = intArrayOf(0, 0, 0, 0, 0, 0)
    for(nyao in nyaos){
        if(nyao == 6 || nyao == 9){
            cnum++
            numbers[i] = 1
        }
        i++
    }
    i = 0
    if(cnum == 0) return 0
    else if(cnum == 1){
        for(j in numbers){
            i++
            if(j == 1) return i
        }
    }
    else if(cnum == 2 || cnum == 3){
        for(j in numbers){
            i++
            if(j == 1){
                k++
                if(k==2) return i
            }
        }
    }
    else if(cnum == 4 || cnum == 5){
        for(j in numbers){
            i++
            if(j == 0) return i
        }
    }
    else if(cnum == 6){
        return 7
    }
    return 0
}

fun getGua(nyaos: Array<Int>): String{
    var Gua = intArrayOf(0, 0, 0, 0, 0, 0)
    var i = 0
    for(nyao in nyaos){
        if(nyao == 6 || nyao == 8) Gua[i] = 0
        else if(nyao == 7 || nyao == 9) Gua[i]= 1
        else return "数值异常"
        i++
    }
    return Gua.joinToString("")
}

fun getChange(x:Int,nyaos: Array<Int>): String{
    var k = 0
    var j = 0
    var str = ""
    for(i in nyaos){
        if(i==6 || i==9) k++
    }
    if(k == 0) return "无变爻，用本卦的卦辞解卦"
    else if(k == 1){
         return "第${x}爻变，用该变爻的爻辞解卦"
    }
    else if(k == 2){
        for (i in nyaos){
            j++
            if (i == 6 || i==9)  str += "第${j}爻 "
        }
        return "${str}变，以上变爻即第${x}爻的爻辞为主"
    }
    else if(k == 3){
        for (i in nyaos){
            j++
            if (i == 6 || i==9)  str += "第${j}爻 "
        }
        return "${str}变，以中间爻即第${x}爻的爻辞为主"
    }
    else if(k == 4){
        for (i in nyaos){
            j++
            if (i == 6 || i==9)  str += "第${j}爻 "
        }
        return "${str}变，以下定爻即第${x}爻的爻辞为主"
    }
    else if(k == 5){
        for (i in nyaos){
            j++
            if (i == 6 || i==9)  str += "第${j}爻 "
        }
        return "${str}变，以定爻即第${x}爻的爻辞解卦"
    }
    else if(k == 6){
        for (i in nyaos){
            j++
            if (i == 6 || i==9)  str += "第${j}爻 "
        }
        return "六爻变，乾、坤两卦用“用九”和“用六”的爻辞；其他卦则用变卦的卦辞解卦"
    }
    return ""
}

fun ChangeGua(nyaos: Array<Int>): Array<Int>{
    var k = 0
    for(i in nyaos){
        if(i==6) nyaos[k] = 7
        if(i==9) nyaos[k] = 8
        k++
    }
    return nyaos
}

fun showGua(nyaos: Array<Int>): String {

    val baguaBi = listOf("000", "111", "110", "101", "100", "011", "010", "001")
    val bagua = listOf("坤", "乾", "兑", "离", "震", "巽", "坎", "艮")
    val gua64 = listOf(
        "坤为地", "地天泰", "地泽临", "地火明夷", "地雷复", "地风升", "地水师", "地山谦",
        "天地否", "乾为天", "天泽履", "天火同人", "天雷无妄", "天风姤", "天水讼", "天山遁",
        "泽地萃", "泽天夬", "兑为泽", "泽火革", "泽雷随", "泽风大过", "泽水困", "泽山咸",
        "火地晋", "火天大有", "火泽睽", "离为火", "火雷噬磕", "火风鼎", "火水未济", "火山旅",
        "雷地豫", "雷天大壮", "雷泽归妹", "雷火丰", "震为雷", "雷风恒", "雷水解", "雷山小过",
        "风地观", "风天小畜", "风泽中孚", "风火家人", "风雷益", "巽为风", "风水涣", "风山渐",
        "水地比", "水天需", "水泽节", "水火既济", "水雷屯", "水风井", "坎为水", "水山蹇",
        "山地剥", "山天大畜", "山泽损", "山火贲", "山雷颐", "山风蛊", "山水蒙", "艮为山"
    )
    var x = change_yao(nyaos)
    var GuaBi = getGua(nyaos)
    var Change = getChange(x, nyaos)
    var ChangeGua = ChangeGua(nyaos)
    var ChangeGuaBi = getGua(ChangeGua)
    val yi64 = Array(8) { Array(8) { "" } }
    var k = 0
    for (i in 0 until 8) {
        for (j in 0 until 8) {
            yi64[i][j] = gua64[k]
            k++
        }
    }

    var downbi = GuaBi.substring(0, 3)
    var topbi = GuaBi.substring(3, 6)
    var cdownbi = ChangeGuaBi.substring(0, 3)
    var ctopbi = ChangeGuaBi.substring(3, 6)
    var top = 0
    var down = 0
    var ctop = 0
    var cdown = 0
    for(i in 0 until 8){
        if(baguaBi[i] == topbi) top = i
        if(baguaBi[i] == downbi) down = i
        if(baguaBi[i] == ctopbi) ctop = i
        if(baguaBi[i] == cdownbi) cdown = i
    }

    val outputText = StringBuilder()
    outputText.append("--------------------------------------\n")
    outputText.append("卦象：上${bagua[top]}下${bagua[down]}，${yi64[top][down]}\n")
    outputText.append("卦爻：$GuaBi \n")
    outputText.append("变爻：$Change \n")
    outputText.append("变卦：上${bagua[ctop]}下${bagua[cdown]}，${yi64[ctop][cdown]}\n")
    outputText.append("变卦卦爻：$ChangeGuaBi \n")
    outputText.append("--------------------------------------\n")
    Log.d("Divination", "result: $outputText")
    // 将生成的文本设置到 TextView 中
    return outputText.toString()
}
