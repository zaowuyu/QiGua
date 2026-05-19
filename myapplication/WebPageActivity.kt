package com.example.myapplication

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.WindowManager
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebPageActivity : AppCompatActivity() {
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 第一步：先加载布局（必须在所有窗口操作之前）
        setContentView(R.layout.activity_web_page)

        // 第二步：再取消全屏（此时DecorView已创建，不会空指针）
        setupNonFullScreen()

        // 第三步：初始化WebView（核心配置不变，确保HTTP图片加载）
        webView = findViewById(R.id.webView)
        val webSettings = webView?.settings ?: return

        // 1. 必须：启用JS（动态HTTP图片依赖）
        webSettings.javaScriptEnabled = true
        // 2. 必须：允许HTTPS页面加载HTTP图片（核心开关）
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        // 3. 必须：自动加载图片
        webSettings.loadsImagesAutomatically = true
        // 4. 必须：启用DOM存储（避免图片加载被拦截）
        webSettings.domStorageEnabled = true

        // 加载目标URL
        val url = "https://www.zhouyi.cc/zhouyi/yijing64/"
        webView?.loadUrl(url)

        // 处理证书+图片错误
        webView?.webViewClient = object : WebViewClient() {
            // 信任不规范证书（测试环境用）
            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }

            // 打印图片加载错误日志
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                val requestUrl = request?.url?.toString() ?: ""
                val isImage = arrayOf(".png", ".jpg", ".jpeg", ".gif")
                    .any { requestUrl.endsWith(it, ignoreCase = true) }
                if (isImage) {
                    android.util.Log.e("ImageError", "HTTP图片加载失败：$requestUrl，错误：${error?.description}")
                }
            }
        }
    }

    // 简化版取消全屏（无版本判断，无空指针风险）
    @Suppress("DEPRECATION") // 消除过时警告
    private fun setupNonFullScreen() {
        // 直接用旧API（兼容所有版本，不会空指针，简单稳定）
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    // 防止内存泄漏
    override fun onDestroy() {
        webView?.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        webView?.destroy()
        webView = null
        super.onDestroy()
    }
}
