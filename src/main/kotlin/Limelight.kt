package cshcyberhawks.swolight.limelight

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import kotlin.math.tan


abstract class Limelight(name: String, ledMode: LedMode = LedMode.Pipeline, cameraMode: CameraMode = CameraMode.VisionProcessor, pipeline: Int = 0, streamMode: StreamMode = StreamMode.Standard, snapshotMode: SnapshotMode = SnapshotMode.Reset, crop: Array<Number> = arrayOf(0, 0, 0, 0)) {
    private val limelight: NetworkTable

    init {
        if (pipeline < 0 || pipeline > 9)
            error("Invalid pipeline value")
        else if (crop.size != 4)
            error("Invalid crop array")

        limelight = NetworkTableInstance.getDefault().getTable(name)
        limelight.getEntry("ledMode").setNumber(ledMode.ordinal)
        limelight.getEntry("camMode").setNumber(cameraMode.ordinal)
        limelight.getEntry("pipeline").setNumber(pipeline)
        limelight.getEntry("stream").setNumber(streamMode.ordinal)
        limelight.getEntry("snapshot").setNumber(snapshotMode.ordinal)
        limelight.getEntry("crop").setNumberArray(crop)
    }

    /**
     * @return Whether the limelight has any valid targets.
     */
    fun hasTarget(): Boolean = limelight.getEntry("tv").getDouble(0.0) == 1.0

    /**
     * @return Horizontal Offset From Crosshair To Target (-27 degrees to 27 degrees).
     */
    fun getHorizontalOffset(): Double = limelight.getEntry("tx").getDouble(0.0)

    /**
     * @return Vertical Offset From Crosshair To Target (-20.5 degrees to 20.5 degrees)
     */
    fun getVerticalOffset(): Double = limelight.getEntry("ty").getDouble(0.0)

    /**
     * @return Target Area (0% of image to 100% of image)
     */
    fun getArea(): Double = limelight.getEntry("ta").getDouble(0.0)

    fun getRotation(): Double = limelight.getEntry("ts").getDouble(0.0)

    fun getLatency(): Double = limelight.getEntry("tl").getDouble(0.0)

    fun getShortest(): Double = limelight.getEntry("tshort").getDouble(0.0)

    fun getLongest(): Double = limelight.getEntry("tlong").getDouble(0.0)

    fun getHorizontalLength(): Double = limelight.getEntry("thor").getDouble(0.0)

    fun getVerticalLength(): Double = limelight.getEntry("tvert").getDouble(0.0)

    fun getCurrentPipeline(): Double = limelight.getEntry("getpipe").getDouble(0.0)

    fun getTarget3D(): Array<Number> = limelight.getEntry("camtran").getNumberArray(arrayOf<Number>())

    fun getTargetID(): Double = limelight.getEntry("tid").getDouble(0.0)

    fun getJSON(): ByteArray = limelight.getEntry("json").getRaw(byteArrayOf())

    fun getCamPose(): Pose3d? {
        val default: Array<Double> = arrayOf(0.0,0.0,0.0,0.0,0.0,0.0)
        val data = camPose.getDoubleArray(default)
        var pose: Pose3d? = null
        if (!data.contentEquals(default)) {
            val translation = Translation3d(data[0], data[1], data[2])
            val rotation = Rotation3d(data[3], data[4], data[5])
            pose = Pose3d(translation, rotation)
        }
        return pose
    }
    fun getBotPose(): Pose3d? {
        val default: Array<Double> = arrayOf(0.0,0.0,0.0,0.0,0.0,0.0)
        val data = botpose.getDoubleArray(default)
        var pose: Pose3d? = null
        if (!data.contentEquals(default)) {
            val translation = Translation3d(data[0], data[1], data[2])
            val rotation = Rotation3d(data[3], data[4], data[5])
            pose = Pose3d(translation, rotation)
        }
        return pose
    }
    fun getDetectorClass(): Double = limelight.getEntry("tclass").getDouble(0.0)

    fun getColorUnderCrosshair(): Array<Number> = limelight.getEntry("tc").getNumberArray(arrayOf<Number>())

    /**
     * @return Distance from target (meters).
     */
    fun findTargetDistance(cameraHeight: Double, cameraAngle: Double, ballHeight: Double): Double =
            if (hasTarget()) (cameraHeight - ballHeight) * tan(Math.toRadians(getVerticalOffset() + cameraAngle)) else -1.0

    fun getColor(): Array<Number> = limelight.getEntry("tc").getNumberArray(arrayOf(-1))
}
