package com.motion.sensor

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import kotlin.math.roundToInt

/**
 * Documentation: https://developer.android.com/guide/topics/sensors/sensors_motion#sensors-motion-rotate
 */
class MainActivity : Activity(), SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private var mRotationVectorSensor: Sensor? = null

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get an instance of the SensorManager
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mRotationVectorSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        mSensorManager!!.registerListener(
            this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            // Convert the rotation-vector to a 3x3 matrix.
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            // Rotates the supplied rotation matrix so it is expressed in a different coordinate system
            // https://developer.android.com/reference/android/hardware/SensorManager#remapCoordinateSystem(float[],%20int,%20int,%20float[])
//            SensorManager.remapCoordinateSystem(
//                rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, rotationMatrix
//
////                rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_X, rotationMatrix
//            )

            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            val inclination = SensorManager.getInclination(rotationMatrix)
            val degree = Math.toDegrees(inclination.toDouble())

            Log.d("++tiltAngleDegrees,", degree.roundToInt().toString())

        }
    }

    override fun onResume() {
        super.onResume()
        mRotationVectorSensor?.let {
            mSensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this) // disable sensor
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {} // not needed
}
