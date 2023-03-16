package com.fitness.motionsensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.fitness.motionsensor.ui.theme.MotionSensorTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig


class MainActivity : ComponentActivity() {
    private lateinit var mSensorManager: SensorManager
    private var mGLSurfaceView: GLSurfaceView? = null
    private var mRenderer: MyRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Get an instance of the SensorManager
        // Get an instance of the SensorManager
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // Create our Preview view and set it as the content of our
        // Activity
        // Create our Preview view and set it as the content of our
        // Activity
        mRenderer = MyRenderer()
        mGLSurfaceView = GLSurfaceView(this)
        mGLSurfaceView!!.setRenderer(mRenderer)
        setContentView(mGLSurfaceView)

//        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

//        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val sensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        setContent {
            MotionSensorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                }
            }
        }
    }
}

internal class MyRenderer : GLSurfaceView.Renderer, SensorEventListener {
    private val mCube: Cube
    private val mRotationVectorSensor: Sensor
    private val mRotationMatrix = FloatArray(16)

    init {
        // find the rotation-vector sensor
        mRotationVectorSensor = mSensorManager.getDefaultSensor(
            Sensor.TYPE_ROTATION_VECTOR
        )
        mCube = Cube()
        // initialize the rotation matrix to identity
        mRotationMatrix[0] = 1f
        mRotationMatrix[4] = 1f
        mRotationMatrix[8] = 1f
        mRotationMatrix[12] = 1f
    }

    fun start() {
        // enable our sensor when the activity is resumed, ask for
        // 10 ms updates.
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000)
    }

    fun stop() {
        // make sure to turn our sensor off when the activity is paused
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            SensorManager.getRotationMatrixFromVector(
                mRotationMatrix, event.values
            )
        }
    }

    override fun onDrawFrame(gl: GL10) {
        // clear screen
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
        // set-up modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -3.0f)
        gl.glMultMatrixf(mRotationMatrix, 0)
        // draw our object
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
        mCube.draw(gl)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // set view-port
        gl.glViewport(0, 0, width, height)
        // set projection matrix
        val ratio = width.toFloat() / height
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 1f, 10f)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        // dither is enabled by default, we don't need it
        gl.glDisable(GL10.GL_DITHER)
        // clear screen in white
        gl.glClearColor(1f, 1f, 1f, 1f)
    }

    internal inner class Cube {
        // initialize our cube
        private val mVertexBuffer: FloatBuffer
        private val mColorBuffer: FloatBuffer
        private val mIndexBuffer: ByteBuffer

        init {
            val vertices = floatArrayOf(
                -1f,
                -1f,
                -1f,
                1f,
                -1f,
                -1f,
                1f,
                1f,
                -1f,
                -1f,
                1f,
                -1f,
                -1f,
                -1f,
                1f,
                1f,
                -1f,
                1f,
                1f,
                1f,
                1f,
                -1f,
                1f,
                1f
            )
            val colors = floatArrayOf(
                0f,
                0f,
                0f,
                1f,
                1f,
                0f,
                0f,
                1f,
                1f,
                1f,
                0f,
                1f,
                0f,
                1f,
                0f,
                1f,
                0f,
                0f,
                1f,
                1f,
                1f,
                0f,
                1f,
                1f,
                1f,
                1f,
                1f,
                1f,
                0f,
                1f,
                1f,
                1f
            )
            val indices = byteArrayOf(
                0, 4, 5, 0, 5, 1,
                1, 5, 6, 1, 6, 2,
                2, 6, 7, 2, 7, 3,
                3, 7, 4, 3, 4, 0,
                4, 7, 6, 4, 6, 5,
                3, 0, 1, 3, 1, 2
            )
            val vbb: ByteBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            vbb.order(ByteOrder.nativeOrder())
            mVertexBuffer = vbb.asFloatBuffer()
            mVertexBuffer.put(vertices)
            mVertexBuffer.position(0)
            val cbb: ByteBuffer = ByteBuffer.allocateDirect(colors.size * 4)
            cbb.order(ByteOrder.nativeOrder())
            mColorBuffer = cbb.asFloatBuffer()
            mColorBuffer.put(colors)
            mColorBuffer.position(0)
            mIndexBuffer = ByteBuffer.allocateDirect(indices.size)
            mIndexBuffer.put(indices)
            mIndexBuffer.position(0)
        }

        fun draw(gl: GL10) {
            gl.glEnable(GL10.GL_CULL_FACE)
            gl.glFrontFace(GL10.GL_CW)
            gl.glShadeModel(GL10.GL_SMOOTH)
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer)
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer)
            gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}