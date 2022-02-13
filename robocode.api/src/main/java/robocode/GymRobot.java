package robocode;

import robocode.AdvancedRobot;

/**
 * A more advanced type of robot than Robot that allows non-blocking calls,
 * An implementation of AdvancedRobot that implements a key step method used in OpenAI Gym environments
 * <p>
 * If you have not already, you should create a {@link Robot} first.
 *
 * @see <a target="_top" href="https://robocode.sourceforge.io">
 *      robocode.sourceforge.net</a>
 * @see <a href="https://robocode.sourceforge.io/myfirstrobot/MyFirstRobot.html">
 *      Building your first robot</a>
 * @see <a href="https://gym.openai.com/">OpenAI Gym</a>
 *
 * @see AdvancedRobot
 * @see Robot
 *
 * @author Steven Tobias (contributor?)
 *
 *
 * **/

public abstract class GymRobot extends AdvancedRobot {

    abstract IGymRobotObservation observe();
    abstract void act(IGymRobotAction action);

}

