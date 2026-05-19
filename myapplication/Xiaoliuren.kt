package com.example.myapplication

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class Xiaoliuren: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xiaoliuren)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)


        val randomButton = findViewById<Button>(R.id.randomButton)
        val startButton = findViewById<Button>(R.id.startButton)

        val input1 = findViewById<EditText>(R.id.input1)
        val input2 = findViewById<EditText>(R.id.input2)
        val input3 = findViewById<EditText>(R.id.input3)

        val result1 = findViewById<TextView>(R.id.result1)
        val result2 = findViewById<TextView>(R.id.result2)
        val result3 = findViewById<TextView>(R.id.result3)

        val numberMap = mapOf(
            1 to "大安(东方木)", 2 to "留连(四方土)", 3 to "速喜(南方火)",
            4 to "赤口(西方金)", 5 to "小吉(北方水)", 6 to "空亡(中央土)"
        )

        randomButton.setOnClickListener {
            val num1 = Random.nextInt(1, 7) // 生成 1~6 之间的随机数
            val num2 = Random.nextInt(1, 7)
            val num3 = Random.nextInt(1, 7)

            input1.setText(num1.toString())
            input2.setText(num2.toString())
            input3.setText(num3.toString())
        }

        startButton.setOnClickListener {
            var num1 = input1.text.toString().toIntOrNull() ?: 0
            var num2 = input2.text.toString().toIntOrNull() ?: 0
            var num3 = input3.text.toString().toIntOrNull() ?: 0

            num2 += num1 - 1
            num3 += num2 - 1

            num1 = (num1 - 1) % 6 + 1
            num2 = (num2 - 1) % 6 + 1
            num3 = (num3 - 1) % 6 + 1

            // 根据数字显示大写汉字，若超出 1~6，显示 "无效"
            result1.text = numberMap[num1] ?: "无效"
            result2.text = numberMap[num2] ?: "无效"
            result3.text = numberMap[num3] ?: "无效"
        }
    }

}