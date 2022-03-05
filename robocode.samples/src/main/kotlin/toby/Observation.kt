package toby

import robocode.ScannedRobotEvent
import java.awt.Point

// RobotObservation stores basic information about a robot that we see from ScannedRobotEvents
class RobotObservation(private val r: TobyGymBot, val event: ScannedRobotEvent){
    var name: String
    var location: Point
    var velocity: Double
    var energy: Double

    init{
        name = r.name
        location = r.radar.enemyLocationFromEvent(event)
        velocity= event.velocity
        energy = event.energy
    }
}

// MapObservation is the list of all robot observations.
data class MapObservation(val robotObservations: HashMap<String,RobotObservation>) {

}