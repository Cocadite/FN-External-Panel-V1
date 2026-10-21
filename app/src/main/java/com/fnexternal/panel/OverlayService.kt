package com.fnexternal.panel

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.*

class OverlayService : Service() {
    private lateinit var manager: WindowManager
    private var panel: LinearLayout? = null
    private var params: WindowManager.LayoutParams? = null
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (panel != null) return START_NOT_STICKY
        manager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(22, 14, 22, 18); setBackgroundColor(Color.rgb(19, 22, 38)) }
        val header = TextView(this).apply { text = "FN PANEL   ≡  [fechar]"; textSize = 17f; setTextColor(Color.CYAN); setPadding(8, 8, 8, 16) }
        layout.addView(header)
        val tabs = LinearLayout(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        fun showTab(name: String) {
            content.removeAllViews()
            val note = TextView(this).apply { text = "$name • parâmetros locais; sem integração ao Fortnite"; setTextColor(Color.WHITE) }
            content.addView(note)
            val items = if (name == "AIMBOT") listOf("Ativar configuração", "FOV", "Suavização") else listOf("Ativar configuração", "Boxes", "Skeleton", "Snaplines")
            val prefs = getSharedPreferences("panel", MODE_PRIVATE)
            items.forEach { item ->
                if (item == "FOV" || item == "Suavização") {
                    content.addView(TextView(this).apply { text = item; setTextColor(Color.WHITE) })
                    content.addView(SeekBar(this).apply { max = 100; progress = prefs.getInt(item, 50); setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { if (fromUser) prefs.edit().putInt(item, progress).apply() }
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    }) })
                } else content.addView(CheckBox(this).apply { text = item; setTextColor(Color.WHITE); val key = "$name:$item"; isChecked = prefs.getBoolean(key, false); setOnCheckedChangeListener { _, checked -> prefs.edit().putBoolean(key, checked).apply() } })
            }
        }
        listOf("AIMBOT", "ESP").forEach { name -> tabs.addView(Button(this).apply { text = name; setOnClickListener { showTab(name) } }) }
        layout.addView(tabs); layout.addView(content); showTab("AIMBOT")
        params = WindowManager.LayoutParams(700, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.START; x = 30; y = 180 }
        header.setOnTouchListener(object : android.view.View.OnTouchListener {
            var sx = 0; var sy = 0; var tx = 0f; var ty = 0f
            override fun onTouch(v: android.view.View, e: MotionEvent): Boolean {
                val p = params ?: return false
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> { sx = p.x; sy = p.y; tx = e.rawX; ty = e.rawY; return true }
                    MotionEvent.ACTION_MOVE -> { p.x = sx + (e.rawX - tx).toInt(); p.y = sy + (e.rawY - ty).toInt(); manager.updateViewLayout(layout, p); return true }
                    MotionEvent.ACTION_UP -> { return true }
                }
                return false
            }
        })
        header.setOnLongClickListener { stopSelf(); true }
        manager.addView(layout, params); panel = layout
        return START_NOT_STICKY
    }
    override fun onDestroy() { panel?.let { manager.removeView(it) }; panel = null; super.onDestroy() }
}
