package toby

/*
  Radio is how we communicate with our RL agent (python / grpc)
 */
class Radio(val robot: TobyGymBot) {
    // Remember - our gym expects a shape of (CHANNELS, HEIGHT, WIDTH)
    // https://pytorch.org/tutorials/intermediate/mario_rl_tutorial.html#preprocess-environment
    init {
        GameInfo(
            robot.battleFieldHeight,
            robot.battleFieldHeight,
            20.0,
        )
    }


}

data class GameInfo(
    val battleFieldHeight: Double,
    val battleFieldWidth: Double,
    val robotSize: Double,
)

