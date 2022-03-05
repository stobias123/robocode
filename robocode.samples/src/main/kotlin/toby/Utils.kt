package toby

import robocode.ScannedRobotEvent
import robocode.util.Utils
import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import kotlin.math.cos
import kotlin.math.sin


// Store a movementvector, with directionAngle as radians
data class MovementVector(val startPoint: Point, val directionAngle: Double) {

}

class RobotCommands(val r: TobyGymBot) {
    fun shootAt() {
        throw Throwable("This hasn't been implemented yet.")
    }
}

class DrawUtils constructor() {
    fun drawProbableRobotRadius(g: Graphics2D, obs: RobotObservation) {
        g.color = Color.red
        g.stroke = BasicStroke(2f);
        val radius = 18 // This is half the size of a robot.
        //println("Trying to draw robot at ${obs.location.x}, ${obs.location.y}")
        g.fillOval(obs.location.x, obs.location.y, radius, radius)
    }

    fun drawSelf(g: Graphics2D, x: Double, y: Double) {
        g.color = Color.blue
        g.stroke = BasicStroke(2f)
        val radius = 18
        g.fillOval(x.toInt(), y.toInt(), radius, radius)
    }

    fun drawWave(g: Graphics2D, wave: EnemyWave) {
        // Draw on the absolute bearing oif the line, this is where the bullet is going to go.
        g.color = Color.red
        val waveAbsoluteDirection = Utils.normalAbsoluteAngle(wave.direction)
        val estimatedBulletPosition = wave.project(wave.fireLocation, waveAbsoluteDirection, wave.distanceTraveled())
        val waveEndPoints = wave.project(estimatedBulletPosition, waveAbsoluteDirection, 20.0)
        g.color = Color.red
        g.drawLine(estimatedBulletPosition.x.toInt(), estimatedBulletPosition.y.toInt(), waveEndPoints.x.toInt(), waveEndPoints.y.toInt())
        //println("Estimating bullet has moved ${wave.distanceTraveled()}")
    }
    //fun drawRobot(g: Graphics2D, x: Int, y: Int)

}

class AdvancedRadar(val r: TobyGymBot) {
    init {
        val mapHeight = r.battleFieldHeight
        val mapWidth = r.battleFieldWidth
    }

    // Takes a ScannedRobotEvent and returns the location of the robot (x,y)
    fun enemyLocationFromEvent(e: ScannedRobotEvent): Point {
        val angle = Math.toRadians((this.r.heading + e.bearing) % 360)
        val scannedX = (r.x + sin(angle) * e.distance).toInt()
        val scannedY = (r.y + cos(angle) * e.distance).toInt()
        return Point(scannedX, scannedY)
    }

    // Returns a line attempting to show the enemy velocity vector.
    fun enemyVelocityVectorFromEvent(e: ScannedRobotEvent): Pair<Point, Point> {
        val scale = 10
        val enemyLocation = enemyLocationFromEvent(e)
        val endX = enemyLocation.x + (sin(e.headingRadians) * e.velocity * scale).toInt()
        val endY = enemyLocation.y + (cos(e.headingRadians) * e.velocity * scale).toInt()
        return Pair(enemyLocation, Point(endX, endY))
    }

    // checkBulletFire returns > 0 if a bullet has been fired.
    fun getEnergyDelta(lastEvent: ScannedRobotEvent, currentEvent: ScannedRobotEvent): Double {
        return lastEvent.energy - currentEvent.energy
    }
}

class FastRGB {
    companion object {
        @JvmStatic
        fun convertTo2DWithoutUsingGetRGB(image: BufferedImage): Array<IntArray>? {
            val pixels = (image.raster.dataBuffer as DataBufferByte).data
            val width = image.width
            val height = image.height
            val hasAlphaChannel = image.alphaRaster != null
            val result = Array(height) { IntArray(width) }
            if (hasAlphaChannel) {
                val pixelLength = 4
                var pixel = 0
                var row = 0
                var col = 0
                while (pixel + 3 < pixels.size) {
                    var argb = 0
                    argb += pixels[pixel].toInt() and 0xff shl 24 // alpha
                    argb += pixels[pixel + 1].toInt() and 0xff // blue
                    argb += pixels[pixel + 2].toInt() and 0xff shl 8 // green
                    argb += pixels[pixel + 3].toInt() and 0xff shl 16 // red
                    result[row][col] = argb
                    col++
                    if (col == width) {
                        col = 0
                        row++
                    }
                    pixel += pixelLength
                }
            } else {
                val pixelLength = 3
                var pixel = 0
                var row = 0
                var col = 0
                while (pixel + 2 < pixels.size) {
                    var argb = 0
                    argb += -16777216 // 255 alpha
                    argb += pixels[pixel].toInt() and 0xff // blue
                    argb += pixels[pixel + 1].toInt() and 0xff shl 8 // green
                    argb += pixels[pixel + 2].toInt() and 0xff shl 16 // red
                    result[row][col] = argb
                    col++
                    if (col == width) {
                        col = 0
                        row++
                    }
                    pixel += pixelLength
                }
            }
            println(result.size)
            return result
        }
    }
}