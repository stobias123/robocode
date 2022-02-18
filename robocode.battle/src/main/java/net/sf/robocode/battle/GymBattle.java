package net.sf.robocode.battle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.settings.ISettingsManager;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.fork.TkMethods;
import org.takes.http.Exit;
import org.takes.rq.RqPrint;
import org.takes.http.FtBasic;
import org.takes.rs.*;
import org.takes.rs.RsJson;
import org.takes.rs.xe.XeSource;
import robocode.*;
import robocode.GymRobotAction;
import robocode.GymRobotObservation;
import robocode.control.RobotSpecification;

import java.io.IOException;

import static net.sf.robocode.io.Logger.logMessage;

// This server will annoyingly pause after _every turn_
// It also starts a GRPC server.
public class GymBattle extends Battle {
    GymRobot gymBot = null;

    public GymBattle(ISettingsManager properties, IBattleManager battleManager, IHostManager hostManager, ICpuManager cpuManager, BattleEventDispatcher eventDispatcher) throws IOException {
        super(properties, battleManager, hostManager, cpuManager, eventDispatcher);
    }

    void setup(RobotSpecification[] battlingRobotsList, BattleProperties battleProps, boolean paused) {
        super.setup(battlingRobotsList, battleProps, paused);
        int gymBotCounter = 0;
        for (RobotPeer robot : this.robots) {
            Robot testBot = (Robot) robot.getRobotObject();
            if (robot.toString().contains("Gym")) {
                gymBotCounter++;
                gymBot = (GymRobot) robot.getRobotObject();
            }
        }
        if(gymBotCounter > 1){
            logMessage("[ERROR] - MORE THAN 1 GYMBOT");
        }
        try {
            startServer();
        } catch (IOException e) {
            logMessage("[ERROR] PROBLEM STARTING REMOTE ACTION SERVER");
            e.printStackTrace();
        }
    }


    @Override
    protected void runTurn() {
        if(gymBot == null){
            for (RobotPeer robot : this.robots) {
                logMessage("Checking gymbots");
                Robot testBot = (Robot) robot.getRobotObject();
                if (robot.toString().contains("Gym")) {
                    gymBot = (GymRobot) robot.getRobotObject();
                    if(gymBot == null) {
                        logMessage("Bot isnull");
                    } else {
                        break;
                    }
                }
            }
        }
        super.runTurn();
        this.pause();
    }


    private void startServer() throws IOException {
        GymProxy server = new GymProxy(this);
        Thread serverThread = new Thread(Thread.currentThread().getThreadGroup(), server);
        serverThread.setPriority(Thread.NORM_PRIORITY);
        serverThread.setName("Server Thread");
        serverThread.start();
    }

    public final class GymProxy implements Take, Runnable {
        Battle battle;

        public GymProxy(Battle battle) {
            this.battle = battle;
        }

        @Override
        public Response act(final Request req) throws Exception {
            String json = new RqPrint(req).printBody();
            logMessage("json body was: " + json);
            AgentRequest action = new ObjectMapper().readValue(json, AgentRequest.class);
            GymRobotObservation obs = gymBot.step(action);
            // We want to call gym.step.
            this.battle.resume();

            return obs.toResponse();
        }

        @Override
        public void run() {
            try {
                new FtBasic(
                        new TkFork(
                                new FkRegex("/step",
                                        new TkFork(
                                                new FkMethods("POST", new GymProxy(this.battle)
                                                )))), 8888).start(Exit.NEVER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static final class AgentRequest extends GymRobotAction {
        public int actionChoice;
    }

}
