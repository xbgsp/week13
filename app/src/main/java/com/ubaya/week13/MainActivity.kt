package com.ubaya.week13

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var accelerometerReading = FloatArray(3)

    private var previousV:Float ?= null
    private var stepCount:Int = 0

    private var magneticReading = FloatArray(3)

    override fun onSensorChanged(p0: SensorEvent?) {
        if(p0!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelerometerReading = p0.values
            var x = p0.values[0]
            var y = p0.values[1]
            var z = p0.values[2]
            txtAccelerometerReading.text = "x : $x, y : $y; z : $z"

            var v = sqrt(x.pow(2) + y.pow(2) + z.pow(2))
            if (previousV != null) {
                var dif = v - previousV!!
                if (dif > 6) {
                    stepCount++
                    txtAccelerometerReading.text = "$stepCount steps"
                }
            }
            previousV = v
        }

        if (p0!!.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticReading = p0.values
        }
        if (p0!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelerometerReading = p0.values
        }
        if (magneticReading != null && accelerometerReading != null) {
            var rotationMatrix = FloatArray(9)
            var orientationAngles = FloatArray(3)

            SensorManager.getRotationMatrix(
                rotationMatrix,null, accelerometerReading, magneticReading
            )
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            var azimuth = (Math.toDegrees(orientationAngles[0].toDouble()) + 360.0) % 360.0
            var pitch = (Math.toDegrees(orientationAngles[1].toDouble()) + 360.0) % 360.0
            var roll = (Math.toDegrees(orientationAngles[2].toDouble()) + 360.0)%360.0

            txtGyroReading.text = "Az = " + (azimuth*100)/100 + "\nPitch = " +
                    (pitch*100)/100 + "\nRoll = " + (roll*100)/100
        }

        if(p0.sensor.type == Sensor.TYPE_LIGHT) {
            txtLightReading.text = p0.values[0].toString()
        }

        if(p0!!.sensor.type == Sensor.TYPE_PROXIMITY) {
            txtLightReading.text = p0.values[0].toString()
            if(p0.values[0] <= 0.0) {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()

        // ACCELEROMETER SENSOR
        val accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if(accelerometerSensor!=null) {
            Toast.makeText(this, "Accelerometer Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager?.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Toast.makeText(this, "No Accelerometer sensor detected", Toast.LENGTH_SHORT).show()
        }

        // GEOMAGNETIC SENSOR
        val geomagneticSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if(geomagneticSensor != null) {
            Toast.makeText(this, "Magnetic Field Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager?.registerListener(this, geomagneticSensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Toast.makeText(this, "No Magnetic Field Sensor detected",Toast.LENGTH_SHORT).show()
        }

        //LIGHT SENSOR
        val lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        if(lightSensor != null) {
            Toast.makeText(this, "Light Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager?.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Toast.makeText(this, "No Light Sensor detected", Toast.LENGTH_SHORT).show()
        }

        // PROXIMITY SENSOR
        val proximitySensor= sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        if(proximitySensor != null) {
            Toast.makeText(this, "Proximity Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager?.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Toast.makeText(this, "No ProximitySensor detected", Toast.LENGTH_SHORT).show()
        }
    }
}