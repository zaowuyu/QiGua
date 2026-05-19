package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import cn.hutool.core.date.ChineseDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : ComponentActivity() {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取控件
        val buttonTimeDivination: Button = findViewById(R.id.buttonTimeDivination)
        val buttonOneNumberDivination: Button = findViewById(R.id.buttonOneNumberDivination)
        val buttonTwoNumbersDivination: Button = findViewById(R.id.buttonTwoNumbersDivination)
        val buttonThreeNumbersDivination: Button = findViewById(R.id.buttonThreeNumbersDivination)
        val buttonSelfSelectDivination: Button = findViewById(R.id.buttonSelfSelect)
        val buttonWeb: Button = findViewById(R.id.btn_open_webpage)
        val resultText: TextView = findViewById(R.id.resultText)
        resultText.movementMethod = ScrollingMovementMethod()

        // 年月日时起卦
        buttonTimeDivination.setOnClickListener {
            showDateTimePickerDialog{ selectedDateTime ->

                // 或者用 TextView 显示
                resultText.text = "年月日时起卦：\n$selectedDateTime"
                resultText.visibility = TextView.VISIBLE
            }
        }

        // 一个数字起卦
        buttonOneNumberDivination.setOnClickListener {
            showNumberInputDialog(1) { numbers ->
                resultText.text = "一个数字起卦[输入数字：${numbers[0]}]\n" + onceMethod(numbers[0])
                Log.d("Divination", "text: $resultText.text")
                resultText.visibility = TextView.VISIBLE
            }
        }

        // 两个数字起卦
        buttonTwoNumbersDivination.setOnClickListener {
            showNumberInputDialog(2) { numbers ->
                resultText.text = "两个数字起卦[输入数字：${numbers[0]},${numbers[1]}]\n" + twiceMethod(numbers[0],numbers[1])
                resultText.visibility = TextView.VISIBLE
            }
        }

        // 三个数字起卦
        buttonThreeNumbersDivination.setOnClickListener {
            showNumberInputDialog(3) { numbers ->
                resultText.text = "三个数字起卦[输入数字：${numbers[0]},${numbers[1]},${numbers[2]}]\n" +
                        thriceMethod(numbers[0],numbers[1],numbers[2])
                resultText.visibility = TextView.VISIBLE
            }
        }

        buttonSelfSelectDivination.setOnClickListener{
            showSelfSelectDialog{ selectedDateTime ->
                // 或者用 TextView 显示
                resultText.text = "自选卦起卦：\n" + selectedDateTime
                resultText.visibility = TextView.VISIBLE
            }
        }

        buttonWeb.setOnClickListener{
            startActivity(Intent(this, WebPageActivity::class.java))
        }
    }

    private fun getCurrentHour12(): Int {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("hh", Locale.getDefault()) // "hh" 表示 12 小时制
        return formatter.format(calendar.time).toInt()
    }

    @SuppressLint("MissingInflatedId")
    fun showSelfSelectDialog(onDateTimeSelected: (String) -> Unit){
        val dialogView = layoutInflater.inflate(R.layout.selfselect_picker, null)

        // 获取 NumberPicker
        val picker1 = dialogView.findViewById<NumberPicker>(R.id.picker1)
        val picker2 = dialogView.findViewById<NumberPicker>(R.id.picker2)
        val picker3 = dialogView.findViewById<NumberPicker>(R.id.picker3)
        val btnSetCurrentTime = dialogView.findViewById<Button>(R.id.btnSetCurrentTime)
        val dizhiLabels = arrayOf("初爻", "二爻", "三爻", "四爻", "五爻", "上爻")
        val baguaLabels = arrayOf("乾", "兑", "离", "震", "巽", "坎", "艮","坤")

        // 配置 NumberPicker 范围
        picker1.minValue = 1
        picker1.maxValue = baguaLabels.size
        picker1.displayedValues = baguaLabels
        picker2.minValue = 1
        picker2.maxValue = baguaLabels.size
        picker2.displayedValues = baguaLabels

        picker3.minValue = 1
        picker3.maxValue = dizhiLabels.size
        picker3.displayedValues = dizhiLabels



        btnSetCurrentTime.setOnClickListener {
            val random = kotlin.random.Random // Kotlin内置随机类，无需额外导入

            // 1. 给第一个滑动条设随机值（替换picker1为你的实际变量名）
            val randomValue1 = random.nextInt(picker1.minValue, picker1.maxValue + 1)
            picker1.value = randomValue1

            // 2. 给第二个滑动条设随机值（替换picker2为你的实际变量名）
            val randomValue2 = random.nextInt(picker2.minValue, picker2.maxValue + 1)
            picker2.value = randomValue2

            // 3. 给第三个滑动条设随机值（替换原本的当前时间逻辑）
            val randomValue3 = random.nextInt(picker3.minValue, picker3.maxValue + 1)
            picker3.value = randomValue3
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("请选择")
            .setView(dialogView)
            .setPositiveButton("确认") { _, _ ->
                val top = picker1.value
                val down = picker2.value
                val hour = picker3.value
                var change = hour%6

                if(change==0) change=6;

                val selectedDateTime = showGua(top, down, change)
                //Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                onDateTimeSelected(selectedDateTime)
            }
            .setNegativeButton("取消", null)
            .show()
    }


    val dizhi = listOf("子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥")

    @SuppressLint("MissingInflatedId")
    fun showDateTimePickerDialog(onDateTimeSelected: (String) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_time_picker, null)

        val pickerYear = dialogView.findViewById<NumberPicker>(R.id.pickerYear)
        val pickerMonth = dialogView.findViewById<NumberPicker>(R.id.pickerMonth)
        val pickerDay = dialogView.findViewById<NumberPicker>(R.id.pickerDay)
        val pickerHour = dialogView.findViewById<NumberPicker>(R.id.pickerHour)
        val btnSetCurrentTime = dialogView.findViewById<Button>(R.id.btnPickerCurrentTime)

        val dizhiLabels = arrayOf("子", "丑", "寅", "卯", "辰", "巳",
            "午", "未", "申", "酉", "戌", "亥")

        // 设置年份范围（假设1-12）
        pickerYear.minValue = 1
        pickerYear.maxValue = dizhiLabels.size
        pickerYear.displayedValues = dizhiLabels
        // 设置月份范围（1-12）
        pickerMonth.minValue = 1
        pickerMonth.maxValue = dizhiLabels.size
        pickerMonth.displayedValues = dizhiLabels

        // 设置日期范围（1-30）
        pickerDay.minValue = 1
        pickerDay.maxValue = 30

        // 设置小时范围（1-12）
        pickerHour.minValue = 1
        pickerHour.maxValue = dizhiLabels.size
        pickerHour.displayedValues = dizhiLabels

        btnSetCurrentTime.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val chineseDate = ChineseDate(currentTime.time) // 转换为农历
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY) // 获取当前时间（12小时制）
            val pre_down = (Math.round((currentHour / 2 + 1).toDouble())).toInt()
            // 获取农历年月日
            val lunchYear = (currentTime.get(Calendar.YEAR) - 40) % 12 + 1
            val lunarMonth = chineseDate.month   // 农历月
            val lunarDay = chineseDate.day       // 农历日

            pickerYear.value = lunchYear
            pickerMonth.value = lunarMonth
            pickerDay.value = lunarDay
            pickerHour.value = pre_down

// 设置 NumberPicker
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("请选择年月日时[农历]")
            .setView(dialogView)
            .setPositiveButton("确认") { _, _ ->
                val year = pickerYear.value
                val month = pickerMonth.value
                val day = pickerDay.value
                val hour = pickerHour.value

                val top = (year+month+day)%8
                val down = (year+month+day+hour)%8
                var change = (year+month+day+hour)%6
                if(change==0) change=6;

                val result = "选择的是：${dizhi[year-1]}年 ${dizhi[(month+1)%12]}月 ${day}日 ${dizhi[hour-1]}时"
                val selectedDateTime = showGua(top, down, change)
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                onDateTimeSelected(selectedDateTime)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    // 显示数字输入对话框
    @SuppressLint("SetTextI18n")
    private fun showNumberInputDialog(count: Int, callback: (List<Int>) -> Unit) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("请输入$count 个数字")
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL

        val inputFields = Array(count) { android.widget.EditText(this) }
        for (i in inputFields.indices) {
            val horizontalLayout = android.widget.LinearLayout(this)
            horizontalLayout.orientation = android.widget.LinearLayout.HORIZONTAL

            inputFields[i].hint = "第 ${i + 1} 个数字"
            inputFields[i].layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val randomButton = android.widget.Button(this).apply {
                text = "随机"
                setOnClickListener {
                    val maxRange = if (count == 3) 48 else 64
                    val randomNum = (1..maxRange).random()
                    inputFields[i].setText(randomNum.toString())
                }
            }

            horizontalLayout.addView(inputFields[i])
            horizontalLayout.addView(randomButton)
            layout.addView(horizontalLayout)
        }

        builder.setView(layout)
        builder.setPositiveButton("确定") { _, _ ->
            try {
                val numbers = inputFields.map { it.text.toString().toInt() }
                callback(numbers)
            } catch (e: NumberFormatException) {
                showErrorDialog("请输入有效的数字")
            }
        }
        builder.setNegativeButton("取消", null)
        builder.show()
    }

    // 错误提示对话框
    private fun showErrorDialog(message: String = "请输入有效的数字") {
        AlertDialog.Builder(this)
            .setTitle("错误")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }


    private fun onceMethod(num: Int): String {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val pre_down = (Math.round((hour / 2 + 1).toDouble())).toInt()
        val top = num % 8
        val down = (top + pre_down) % 8
        val change = ((num + hour) % 6).let { if (it == 0) 6 else it }
        Log.d("Divination", "top: $top, down: $down, change: $change")
        return showGua(top, down, change)
    }

    private fun twiceMethod(num1: Int,num2: Int): String {
        val top=num1%8;
        val down=num2%8;
        var change=(num1+num2)%6;

        if(change==0) change=6;
        Log.d("Divination", "top: $top, down: $down, change: $change")
        return showGua(top, down, change)
    }

    private fun thriceMethod(num1: Int,num2: Int,num3: Int): String {
        val top=num1%8;
        val down=num2%8;
        var change=num3%6;

        if(change==0) change=6;
        Log.d("Divination", "top: $top, down: $down, change: $change")
        return showGua(top, down, change)
    }

    fun showGua(top: Int, down: Int, change: Int): String {
        var downC = down
        var topC = top

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

        val yi64 = Array(8) { Array(8) { "" } }
        var k = 0
        for (i in 0 until 8) {
            for (j in 0 until 8) {
                yi64[i][j] = gua64[k]
                k++
            }
        }

        val guaBi = baguaBi[down] + baguaBi[top]

        val downHubi = guaBi.substring(1, 4)
        val topHubi = guaBi.substring(2, 5)

        var downHu = -1
        var topHu = -1

         for (i in 0 until 8) {
             if (baguaBi[i] == downHubi) downHu = i
             if (baguaBi[i] == topHubi) topHu = i
         }

        // 检查是否找到有效的索引
         if (downHu == -1 || topHu == -1) {
             return "错误：无法匹配卦象。guaBi:${guaBi}\ndownHubi:${downHubi}\ntopHubi:${topHubi}"
         }

        val self: Int
        val use: Int
        val useHu: Int
        val selfHu: Int

        if (change in 1..3) {
            downC = changeT(down, change)
            self = top
            use = down
            useHu = downHu
            selfHu = topHu
        } else if (change in 4..6) {
            topC = changeT(top, change)
            self = down
            use = top
            useHu = topHu
            selfHu = downHu
        } else {
            use = 0
            self = 0
            selfHu = 0
            useHu = 0
        }
         val HuguaBi = baguaBi[downHu] + baguaBi[topHu]
         val useC = changeT(use, change)
         val currentTime = Calendar.getInstance()
         val hour = currentTime.get(Calendar.HOUR_OF_DAY)

         val pre_down = (Math.round((hour / 2 + 1).toDouble())).toInt()
         val dHour = dizhi[pre_down-1]
         val outputText = StringBuilder()
         outputText.append("当前系统时辰为：${hour}${dHour}时\n")
         outputText.append("--------------------------------------\n")
         outputText.append("*本卦：上${bagua[top]}下${bagua[down]}，${yi64[top][down]}\n")
         outputText.append("卦爻：${guaBi},动爻：第$change 爻\n")
         outputText.append("体用：体卦为${bagua[self]}卦，用卦为${bagua[use]}卦。\n")
         outputText.append(shengke(self, use))
         outputText.append("--------------------------------------\n")
         outputText.append("*互卦：上${bagua[topHu]}下${bagua[downHu]}，${yi64[topHu][downHu]}\n")
         outputText.append("卦爻：${HuguaBi}\n")
         outputText.append("体互为${bagua[selfHu]}卦，用互为${bagua[useHu]}卦。\n")
         outputText.append(Hushengke(self, use, selfHu, useHu))
         outputText.append("--------------------------------------\n")
         outputText.append("*变卦：上${bagua[topC]}下${bagua[downC]}，${yi64[topC][downC]}\n")
         outputText.append("体用：体卦为${bagua[self]}卦，用卦为${bagua[useC]}卦。\n")
         outputText.append(shengke(self, useC))
         outputText.append("--------------------------------------\n")
         outputText.append("*本卦：本卦代表事物初始、开始阶段的信息，或目前的情况。它是占卜的起点，反映了求测者所问之事的当前状态或初始条件。\n")
         outputText.append("*互卦：互卦反映了事物发展过程中的内在关系和具体细节，是预测人心里的想法的关键。它揭示了事物发展过程中的交互、互动以及潜在的影响因素。\n")
         outputText.append("*变卦：变卦代表着事物发展变化的最终结果，也就是求测者想要得到的最终结果、结局。在预测时，事物的最终吉凶结果往往由变卦来作最后定夺。\n")
         outputText.append("\n")
         Log.d("Divination", "result: $outputText")
         // 将生成的文本设置到 TextView 中
         return outputText.toString()
    }

    fun changeT(value: Int, change: Int): Int {
        var cGua = value
        var changeValue = change

        if (changeValue > 3) changeValue -= 3

        when (value) {
            0 -> {
                when (changeValue) {
                    1 -> cGua = 4
                    2 -> cGua = 6
                    3 -> cGua = 7
                }
            }
            1 -> {
                when (changeValue) {
                    1 -> cGua = 5
                    2 -> cGua = 3
                    3 -> cGua = 2
                }
            }
            2 -> {
                when (changeValue) {
                    1 -> cGua = 6
                    2 -> cGua = 4
                    3 -> cGua = 1
                }
            }
            3 -> {
                when (changeValue) {
                    1 -> cGua = 7
                    2 -> cGua = 1
                    3 -> cGua = 4
                }
            }
            4 -> {
                when (changeValue) {
                    1 -> cGua = 0
                    2 -> cGua = 2
                    3 -> cGua = 3
                }
            }
            5 -> {
                when (changeValue) {
                    1 -> cGua = 1
                    2 -> cGua = 7
                    3 -> cGua = 6
                }
            }
            6 -> {
                when (changeValue) {
                    1 -> cGua = 2
                    2 -> cGua = 0
                    3 -> cGua = 5
                }
            }
            7 -> {
                when (changeValue) {
                    1 -> cGua = 3
                    2 -> cGua = 5
                    3 -> cGua = 0
                }
            }
            else -> println("error!")
        }
        return cGua
    }

    fun Hushengke(self: Int, use: Int, huself: Int, huuse: Int): String {
        val wuxing = listOf("土", "金", "金", "火", "木", "木", "水", "土")
        val result = StringBuilder()

        if (wuxing[huself] == wuxing[self]) {
            result.append("->体互和体卦比和，五行属${wuxing[self]}，互相得到增强和补益\n")
        }else{
            when (self) {
                0, 7 -> {
                    result.append("->体卦属土，")
                    when (huself) {
                        1, 2 -> result.append("体互属金，体卦生体互，体卦得到积极信号\n")
                        3 -> result.append("体互属火，体互生体卦，体卦得到助力和额外支持\n")
                        4, 5 -> result.append("体互属木，体互克体卦，体卦受到阻碍和遇到挑战\n")
                        6 -> result.append("体互属水，体卦克体互，体卦受到负面的影响\n")
                    }
                }
                1, 2 -> {
                    result.append("->体卦属金，")
                    when (huself) {
                        6 -> result.append("体互属水，体卦生体互，体卦得到积极的信号\n")
                        0, 7 -> result.append("体互属土，体互生体卦，体卦得到助力和额外支持\n")
                        3 -> result.append("体互属火，体互克体卦，体卦受到阻碍和遇到挑战\n")
                        4, 5 -> result.append("体互属木，体卦克体互，体卦受到负面的影响\n")
                    }
                }
                3 -> {
                    result.append("->体卦属火，")
                    when (huself) {
                        0, 7 -> result.append("体互属土，体卦生体互，体卦得到积极信号\n")
                        4, 5 -> result.append("体互属木，体互生体卦，体卦得到助力和额外支持\n")
                        6 -> result.append("体互属水，体互克体卦，体卦受到阻碍和遇到挑战\n")
                        1, 2 -> result.append("体互属金，体卦克体互，体卦受到负面影响\n")
                    }
                }
                4, 5 -> {
                    result.append("->体卦属木，")
                    when (huself) {
                        3 -> result.append("体互属火，体卦生体互，体卦得到积极的信号\n")
                        6 -> result.append("体互属水，体互生体卦，体卦得到助力和额外支持\n")
                        1, 2 -> result.append("体互属金，体互克体卦，体卦受到阻碍和遇到挑战\n")
                        0, 7 -> result.append("体互属土，体卦克体互，体卦代表事物受到负面影响\n")
                    }
                }
                6 -> {
                    result.append("->体卦属水，")
                    when (huself) {
                        4, 5 -> result.append("体互属木，体卦生体互，体卦得到积极信号\n")
                        1, 2 -> result.append("体互属金，体互生体卦，体卦得到助力和额外支持\n")
                        0, 7 -> result.append("体互属土，体互克体卦，体卦受到阻碍和遇到挑战\n")
                        3 -> result.append("体互属火，体卦克体互，体卦受到负面影响\n")
                    }
                }
                else -> result.append("error!\n")
            }
        }

        if (wuxing[huuse] == wuxing[use]) {
            result.append("->用互和用卦比和，五行属${wuxing[self]}，互相得到增强和补益\n")
        }else{
            when (use) {
                0, 7 -> {
                    result.append("->用卦属土，")
                    when (huuse) {
                        1, 2 -> result.append("用互属金，用卦生用互，用卦得到积极信号\n")
                        3 -> result.append("用互属火，用互生用卦，用卦得到增强和补益\n")
                        4, 5 -> result.append("用互属木，用互克用卦，用卦受到减弱和损耗\n")
                        6 -> result.append("用互属水，用卦克用互，用卦受到负面影响\n")
                    }
                }
                1, 2 -> {
                    result.append("->用卦属金，")
                    when (huuse) {
                        6 -> result.append("用互属水，用卦生用互，用卦得到积极信号\n")
                        0, 7 -> result.append("用互属土，用互生用卦，用卦得到增强和补益\n")
                        3 -> result.append("用互属火，用互克用卦，用卦受到减弱和损耗\n")
                        4, 5 -> result.append("用互属木，用卦克用互，用卦受到负面的影响\n")
                    }
                }
                3 -> {
                    result.append("->用卦属火，")
                    when (huuse) {
                        0, 7 -> result.append("用互属土，用卦生用互，用卦得到积极信号\n")
                        4, 5 -> result.append("用互属木，用互生用卦，用卦得到增强和补益\n")
                        6 -> result.append("用互属水，用互克用卦，用卦受到减弱和损耗\n")
                        1, 2 -> result.append("用互属金，用卦克用互，用卦受到负面的影响\n")
                    }
                }
                4, 5 -> {
                    result.append("->用卦属木，")
                    when (huuse) {
                        3 -> result.append("用互属火，用卦生用互，用卦得到积极信号\n")
                        6 -> result.append("用互属水，用互生用卦，用卦得到增强和补益\n")
                        1, 2 -> result.append("用互属金，用互克用卦，用卦受到减弱和损耗\n")
                        0, 7 -> result.append("用互属土，用卦克用互，用卦受到负面的影响\n")
                    }
                }
                6 -> {
                    result.append("->用卦属水，")
                    when (huuse) {
                        4, 5 -> result.append("用互属木，用卦生用互，用卦得到补益\n")
                        1, 2 -> result.append("用互属金，用互生用卦，用卦得到增强和补益\n")
                        0, 7 -> result.append("用互属土，用互克用卦，用卦受到减弱和损耗\n")
                        3 -> result.append("用互属火，用卦克用互，用卦受到负面的影响\n")
                    }
                }
                else -> result.append("error!\n")
            }
        }
        return result.toString()
    }

    fun shengke(self: Int, use: Int): String {
        val wuxing = listOf("土", "金", "金", "火", "木", "木", "水", "土")
        val result = StringBuilder()

        if (wuxing[self] == wuxing[use]) {
            result.append("->体用比和，五行属${wuxing[self]}，大吉[和谐互利]！\n")
            return result.toString()
        }

        when (self) {
            0, 7 -> {
                result.append("->体卦属土，")
                when (use) {
                    1, 2 -> result.append("用卦属金，体生用，小凶[消耗或不利]\n")
                    3 -> result.append("用卦属火，用生体，大吉[得到助力]\n")
                    4, 5 -> result.append("用卦属木，用克体，大凶[遇到阻碍]\n")
                    6 -> result.append("用卦属水，体克用，小吉[进展顺利]\n")
                }
            }
            1, 2 -> {
                result.append("->体卦属金，")
                when (use) {
                    6 -> result.append("用卦属水，体生用，小凶[消耗或不利]\n")
                    0, 7 -> result.append("用卦属土，用生体，大吉[得到助力]\n")
                    3 -> result.append("用卦属火，用克体，大凶[遇到阻碍]\n")
                    4, 5 -> result.append("用卦属木，体克用，小吉[进展顺利]\n")
                }
            }
            3 -> {
                result.append("->体卦属火，")
                when (use) {
                    0, 7 -> result.append("用卦属土，体生用，小凶[消耗或不利]\n")
                    4, 5 -> result.append("用卦属木，用生体，大吉[得到助力]\n")
                    6 -> result.append("用卦属水，用克体，大凶[遇到阻碍]\n")
                    1, 2 -> result.append("用卦属金，体克用，小吉[一切顺利]\n")
                }
            }
            4, 5 -> {
                result.append("->体卦属木，")
                when (use) {
                    3 -> result.append("用卦属火，体生用，小凶[消耗或不利]\n")
                    6 -> result.append("用卦属水，用生体，大吉[得到助力]\n")
                    1, 2 -> result.append("用卦属金，用克体，大凶[遇到阻碍]\n")
                    0, 7 -> result.append("用卦属土，体克用，小吉[一切顺利]\n")
                }
            }
            6 -> {
                result.append("->体卦属水，")
                when (use) {
                    4, 5 -> result.append("用卦属木，体生用，小凶[消耗或不利]\n")
                    1, 2 -> result.append("用卦属金，用生体，大吉[得到助力]\n")
                    0, 7 -> result.append("用卦属土，用克体，大凶[遇到阻碍]\n")
                    3 -> result.append("用卦属火，体克用，小吉[一切顺利]\n")
                }
            }
            else -> result.append("error!\n")
        }
        return result.toString()
    }


}