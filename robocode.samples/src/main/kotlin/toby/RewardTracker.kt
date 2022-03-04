package toby

class RewardTracker() {

    var reward: Double = 0.0
    var lastEnergy: Double = 0.0
    var currentEnergy: Double = 0.0

    fun receiveBulletHitEvent(){
        this.reward += .02
    }

    fun receiveHitByBulletEvent(){
        this.reward -= .01
    }

    fun turnTick() {
        this.reward += .005
    }
}