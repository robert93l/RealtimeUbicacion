package com.example.realtimeubicacion

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.realtimeubicacion.pojos.UbicacionPojo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private var ubicacion: FusedLocationProviderClient? = null
    private var btn_dameubi: Button? = null
    private var database: FirebaseDatabase? = null
    private var refubicacion: DatabaseReference? = null



    companion object{
        const val MY_CHANNEL_ID ="myChannel"
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = FirebaseDatabase.getInstance()
        refubicacion = database!!.getReference("ubicacion")
        btn_dameubi = findViewById(R.id.btn_dameubi)
        createChannel()
        btn_dameubi?.setOnClickListener(View.OnClickListener {  ejecutar() })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dameubicacion() {
        if (ContextCompat.checkSelfPermission(  
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "tENEMOS PERMISOS", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
        }
        ubicacion = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        ubicacion!!.lastLocation.addOnSuccessListener(this@MainActivity) { location: Location? ->
            if (location != null) {
                val latitud = location.latitude
                val longitud = location.longitude
                val dateTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss a"))

                val ubi = UbicacionPojo(latitud, longitud,dateTime)
                refubicacion!!.push().setValue(ubi)

                Toast.makeText(this@MainActivity, "ubicacion agregada", Toast.LENGTH_SHORT).show()
                Toast.makeText(
                    this@MainActivity,
                    "latitud " + latitud + "Longitud " + longitud,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "SUSCRIBITE"
            }

            val notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    fun createSimpleNotification(){
        var builder = NotificationCompat.Builder(this, MY_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_delete)
            .setContentTitle("MyLocation")
            .setContentText("Se Agrego tu ubicacion a Firebase")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(1,builder.build())
        }
    }

    private fun ejecutar() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                dameubicacion()
                createSimpleNotification()//llamamos nuestro metodo
                handler.postDelayed(this, 10000) //se ejecutara cada 10 segundos
            }
        }, 5000) //empezara a ejecutarse después de 5 milisegundos
    }
}