package com.example.myapplication


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class Menu : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)


        button1.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        button2.setOnClickListener{
            startActivity(Intent(this, Xiaoliuren::class.java))
        }

        button3.setOnClickListener{
            startActivity(Intent(this, Money::class.java))
            /*val builder = AlertDialog.Builder(this)
            builder.setTitle("提示")
            builder.setMessage("功能开发中，敬请期待！")
            builder.setPositiveButton("确定") { dialog, which ->
                // 处理用户点击“确定”按钮的事件
                dialog.dismiss()
            }
            // 显示对话框
            builder.create().show()*/
        }
    }


}