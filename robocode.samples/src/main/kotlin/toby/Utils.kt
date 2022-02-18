package toby
import robocode.ScannedRobotEvent
import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import kotlin.math.cos
import kotlin.math.sin


// Store a movementvector, with directionAngle as radians
data class MovementVector(val startPoint: Point, val directionAngle: Double){

}

class RobotCommands(val r: TobyGymBot) {
    fun shootAt() {
        throw Throwable("This hasn't been implemented yet.")
    }
}

class DrawUtils constructor(val r: TobyGymBot) {
    fun drawProbableRobotRadius(obs: RobotObservation) {
        r.graphics.stroke = BasicStroke(2f);
        r.graphics.color = Color.red
        val radius = 20
        r.graphics.drawOval(obs.location.x,obs.location.y,radius,radius)
        //val velocityVector = r.radar.enemyVelocityVectorFromEvent(e)
        //r.graphics.drawLine(velocityVector.first.x,velocityVector.first.y,velocityVector.second.x,velocityVector.second.y)
    }
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
    fun enemyVelocityVectorFromEvent(e: ScannedRobotEvent): Pair<Point,Point> {
        val scale = 10
        val enemyLocation = enemyLocationFromEvent(e)
        val endX = enemyLocation.x + (sin(e.headingRadians) * e.velocity * scale).toInt()
        val endY = enemyLocation.y + (cos(e.headingRadians) * e.velocity * scale ).toInt()
        return Pair(enemyLocation,Point(endX,endY))
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