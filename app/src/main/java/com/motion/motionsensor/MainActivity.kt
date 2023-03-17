package com.motion.motionsensor

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log

/**
 * Documentation: https://developer.android.com/guide/topics/sensors/sensors_motion#sensors-motion-rotate
 * Wrapper activity demonstrating the use of the new
 * [rotation vector sensor][SensorEvent.values]
 * ([TYPE_ROTATION_VECTOR][Sensor.TYPE_ROTATION_VECTOR]).
 *
 * @see Sensor
 * @see SensorEvent
 * @see SensorManager
 */
class RotationVectorDemo : Activity(), SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private var mRotationVectorSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get an instance of the SensorManager
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mRotationVectorSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        mSensorManager!!.registerListener(this, mRotationVectorSensor, 10000)
    }

    override fun onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume()
    }

    override fun onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause()
        // disable sensor
        mSensorManager?.unregisterListener(this)
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9) // move this to top level declaration
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            // The tilt angle in radians is given by the first element of the orientationAngles array.
            val tiltAngleRadians = orientationAngles[0]
            // Convert to degrees if desired.
            // Convert to degrees if desired.
            val tiltAngleDegrees = Math.toDegrees(tiltAngleRadians.toDouble()).toFloat()
            // Do something with the tilt angle.
            Log.d("++tiltAngleDegrees,",tiltAngleDegrees.toString())

        }

        // we received a sensor event. it is a good practice to check
        // that we received the proper event
//            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
//                // convert the rotation-vector to a 4x4 matrix. the matrix
//                // is interpreted by Open GL as the inverse of the
//                // rotation-vector, which is what we want.
//                SensorManager.getRotationMatrixFromVector(
//                    mRotationMatrix, event.values
//                )
//
////                Log.d("++eventValues", event.values.map {
////                    it.toString()
////                }.toString()
////                )
//
//
////                Log.d("++AxisX,",event.values[0].toString()) // 1
//                Log.d("++Axisy,",event.values[1].toString()) // 1
////                Log.d("++Axisz,",event.values[2].toString()) // 1
//
//            }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {} // not needed
}