package com.fnexternal.panel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import rikka.shizuku.Shizuku

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val requestCode = 1001
    private val permissionListener = Shizuku.OnRequestPermissionResultListener { code, _ ->
        if (code == requestCode) runOnUiThread { updateStatus() }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32, 48, 32, 32); setBackgroundColor(0xff10131d.toInt()) }
        fun label(text: String): TextView = TextView(this).apply { this.text = text; textSize = 17f; setTextColor(-1); setPadding(0, 12, 0, 12) }
        fun button(text: String, click: () -> Unit) { root.addView(Button(this).apply { this.text = text; setOnClickListener { click() } }) }
        root.addView(label("FN EXTERNAL PANEL • V1"))
        status = label("Shizuku: verificando..."); root.addView(status)
        root.addView(label("Painel externo de diagnóstico. Não lê jogadores nem controla o Fortnite."))
        button("Solicitar permissão Shizuku") {
            try {
                if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) Shizuku.requestPermission(requestCode)
                else updateStatus()
            } catch (e: Exception) { status.text = "Shizuku indisponível: ${e.javaClass.simpleName}" }
        }
        button("Autorizar sobreposição") {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        }
        button("Abrir painel flutuante") {
            if (Settings.canDrawOverlays(this)) startService(Intent(this, OverlayService::class.java))
            else Toast.makeText(this, "Autorize a sobreposição primeiro", Toast.LENGTH_SHORT).show()
        }
        button("Fechar painel flutuante") { stopService(Intent(this, OverlayService::class.java)) }
        setContentView(root)
        Shizuku.addRequestPermissionResultListener(permissionListener)
        updateStatus()
    }
    private fun updateStatus() {
        status.text = try {
            if (!Shizuku.pingBinder()) "Shizuku: serviço não iniciado"
            else if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) "Shizuku: conectado e autorizado"
            else "Shizuku: conectado, aguardando autorização"
        } catch (_: Exception) { "Shizuku: indisponível" }
    }
    override fun onResume() { super.onResume(); if (::status.isInitialized) updateStatus() }
    override fun onDestroy() { Shizuku.removeRequestPermissionResultListener(permissionListener); super.onDestroy() }
}
