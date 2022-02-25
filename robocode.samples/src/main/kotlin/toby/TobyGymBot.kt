package toby

import robocode.*

//import net.
import robocode.util.Utils

import java.awt.BasicStroke

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintStream
import java.util.*
import javax.imageio.ImageIO
import javax.swing.Action
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TobyGymBot : GymRobot() {

    // Radar needs some initialized values from the first run call - so we initialize late.
    lateinit var radar: AdvancedRadar
    lateinit var commander: RobotCommands
    var draw = DrawUtils()
    var enemies: HashMap<String, Point> = HashMap()
    var initialized = false
    var mapObs = HashMap<String,RobotObservation>()
    private var radarTurns = 1
    var lastChosenAction  = GymRobotAction();


    // Surfing vars
    var _fieldRect = Rectangle2D.Double(18.toDouble(), 18.toDouble(), 764.toDouble(), 564.toDouble())
    var WALL_STICK = 160.0
    var _enemyWaves = ArrayList<EnemyWave>()


    override fun run() {
        if(! initialized) {
            // Radar and commander = Internal commands.
            radar = AdvancedRadar(this)
            commander = RobotCommands(this)
            initialized = true
        }
        while (true) {
            this.isAdjustRadarForGunTurn = true
            this.turnRadarLeft(Double.POSITIVE_INFINITY )
            radarTurns += 1
            act(lastChosenAction)
            execute()
        }
    }

    override fun onDeath(event: DeathEvent?) {
        super.onDeath(event)
    }

    override fun onScannedRobot(e: ScannedRobotEvent) {
        print("Scanned Robot")
        val time = getTime() - 1
        if(mapObs[e.name] != null){
          val lastScannedEvent = mapObs[e.name]!!.event
            val energyDelta = this.radar.getEnergyDelta(lastScannedEvent,e)
          if(energyDelta >0){
             //MakeWave/
              val enemyLocation = this.radar.enemyLocationFromEvent(e)
              val wave = EnemyWave(_fieldRect, fireTime = time, fireLocation = enemyLocation, robot=this)
              wave.bulletVelocity = Rules.getBulletSpeed(energyDelta)
              // Assume they shot at us.
              // Get absolute bearing from target to us
              wave.direction = wave.absoluteBearing(enemyLocation,Point(this.x.toInt(), this.y.toInt()))
              this._enemyWaves.add(wave)
          }
        }
        mapObs[e.name] = RobotObservation(this, e )
    }

    override fun onPaint(g: Graphics2D?) {
        super.onPaint(g)
    }

    fun getView(): String {
                // this is our view. We use this image to send RGB Array over radio.
        this.others
        val image = BufferedImage(battleFieldWidth.toInt(),  battleFieldHeight.toInt(), BufferedImage.TYPE_3BYTE_BGR)
        val g2 = image.createGraphics()
        g2.color = Color.blue
        this.mapObs.forEach { (k, obs) ->
            draw.drawProbableRobotRadius(g2,obs)
            draw.drawSelf(g2,this.x,this.y)
            val source = Point(this.x.toInt(),this.y.toInt())
            val target = obs.location
        }

        this._enemyWaves.forEach { wave ->
            draw.drawWave(g2, wave)
        }

        val baos = ByteArrayOutputStream()
        ImageIO.write(image,"png",baos)
        val bytes = baos.toByteArray()
        return Base64.getEncoder().encodeToString(bytes);
    }


    override fun step(action: GymRobotAction): GymRobotObservation {
        val info = mutableMapOf("foo" to "Bar")
        lastChosenAction = action
        return TobyBotObservation(observation = getView(), 2.0, false, info)
    }

    // actOnMessage will take an action from a discrete space of length 16.
    // It will then perform bitwise and to find if the chosen action (from a binary representation of the action) was included in the chosen action.
    // I'm fucking clever.
    override fun act(action: GymRobotAction) {
        if(action.actionChoice and Keys.FORWARD.int > 0) { this.ahead(8.0)}
        if(action.actionChoice and Keys.BACK.int > 0) { this.back(8.0)}
        if(action.actionChoice and Keys.LEFT.int > 0) { this.turnLeft(20.0)}
        if(action.actionChoice and Keys.RIGHT.int > 0) { this.turnRight(20.0)}
    }

    /*
    override fun onStatus(e: StatusEvent?) {
        super.onStatus(e)
        val mapper = jacksonObjectMapper()
        val statusString = out.println(mapper.writeValueAsString(e))
        this.w?.println(statusString)
    }*/
}

data class TrainBotAction(val actionChoice: Int): GymRobotAction() {}

class TobyBotObservation(observation: String, reward: Double, done: Boolean, info: MutableMap<String, String>?) : GymRobotObservation(observation, reward, done, info){
}