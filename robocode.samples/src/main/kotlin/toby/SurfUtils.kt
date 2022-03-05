package toby
import java.awt.Point
import robocode.AdvancedRobot
import robocode.util.Utils
import java.awt.geom.Rectangle2D
import java.time.Instant
import java.time.format.DateTimeFormatter

class SurfUtils {
}

class EnemyWave( val fieldRect: Rectangle2D,
                 val fireLocation: Point,
                 val fireTime: Long = 0,
                 var bulletVelocity: Double = 0.0,
                 var robot: AdvancedRobot) {
    var directAngle = 0.0
    var direction = 0.0
    var WALL_STICK = 160.0

    fun wallSmoothing(botLocation: Point, angle: Double, orientation: Int): Double {
        var angle = angle
        while (!this.fieldRect.contains(project(botLocation, angle, WALL_STICK))) {
            angle += orientation * 0.05
        }
        return angle
    }

    fun distanceTraveled(): Double {
        val time = robot.getTime()
        val delta = time - fireTime

        return this.bulletVelocity * (time - fireTime )
    }

    fun project(
        sourceLocation: Point,
        angle: Double, length: Double
    ): Point {
        return Point(
            (sourceLocation.x + Math.sin(angle) * length).toInt(),
            (sourceLocation.y + Math.cos(angle) * length).toInt()
        )
    }

    fun absoluteBearing(source: Point, target: Point): Double {
        return Math.atan2(target.x.toDouble() - source.x.toDouble(), target.y.toDouble() - source.y.toDouble())
    }

    fun limit(min: Double, value: Double, max: Double): Double {
        return Math.max(min, Math.min(value, max))
    }

    fun bulletVelocity(power: Double): Double {
        return 20.0 - 3.0 * power
    }

    fun maxEscapeAngle(velocity: Double): Double {
        return Math.asin(8.0 / velocity)
    }


    // I think this turns to the target???
    fun setBackAsFront(robot: AdvancedRobot, goAngle: Double) {
        // what's our relative angle to goAngle
        val angle: Double = Utils.normalRelativeAngle(goAngle - robot.getHeadingRadians())
        // turn there
        if (Math.abs(angle) > Math.PI / 2) {
            if (angle < 0) {
                robot.setTurnRightRadians(Math.PI + angle)
            } else {
                robot.setTurnLeftRadians(Math.PI - angle)
            }
            robot.setBack(100.0)
        } else {
            if (angle < 0) {
                robot.setTurnLeftRadians(-1 * angle)
            } else {
                robot.setTurnRightRadians(angle)
            }
            robot.setAhead(100.0)
        }
    }

    companion object {
        @JvmStatic
        fun cleanWaves(waves: ArrayList<EnemyWave>){
            waves.forEach{ wave ->
                // !this.fieldRect.contains(project(botLocation, angle, WALL_STICK
                val waveAbsoluteDirection = Utils.normalAbsoluteAngle(wave.direction)
                val position = wave.project(wave.fireLocation,waveAbsoluteDirection,wave.bulletVelocity)
                if( ! wave.fieldRect.contains(position)) {
                    waves.remove(wave)
                }
            }
        }
    }
}