package net.sf.robocode.battle.gym;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.robocode.battle.Battle;
import net.sf.robocode.battle.BattleManager;
import net.sf.robocode.battle.GymBattle;
import net.sf.robocode.battle.peer.RobotPeer;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.rq.RqPrint;
import org.takes.rs.RsText;
import robocode.GymRobot;
import robocode.GymRobotAction;
import robocode.GymRobotObservation;
import robocode.Robot;

import java.io.IOException;
import java.util.HashMap;

import static net.sf.robocode.io.Logger.logMessage;


public final class GymProxy implements Take, Runnable {
    BattleManager bm;
    Battle battle;
    GymRobot gymBot;

    public GymProxy(BattleManager bm) {
        logMessage("We're in the gymbot proxy!");
        this.bm = bm;
    }

    public GymProxy(BattleManager bm, GymRobot gymBot) {
        this.bm = bm;
        this.gymBot = gymBot;
    }

    public void setBattle(Battle b) {
        logMessage("Resetting Battle");
        this.battle = b;
    }

    public void setGymBot(GymRobot gymBot) {
        this.gymBot = gymBot;
    }

    @Override
    public Response act(final Request req) throws Exception {
        String json = new RqPrint(req).printBody();
        GymRobotAction action = new ObjectMapper().readValue(json, GymRobotAction.class);
        if (gymBot == null) {
            for (RobotPeer robot : this.battle.robots) {
                Robot testBot = (Robot) robot.getRobotObject();
                if (testBot != null && robot.toString().contains("Gym")) {
                    gymBot = (GymRobot) robot.getRobotObject();
                    if (gymBot == null) {
                        logMessage("Bot isnull");
                    } else {
                        break;
                    }
                }
            }
        }
        GymRobotObservation obs;
        if (gymBot != null) {
            obs = gymBot.step(action);
            // TODO: Refactor this into BattleManager.
            if (this.battle.isRoundOver()) {
                logMessage("Round over!");
            }
            if (gymBot.getEnergy() <= 0) {
                obs.setDone(true);
            }
            this.battle.resume();
            return obs.toResponse();
        }
        // We want to call gym.step.
        this.battle.resume();
        obs = new GymRobotObservation("", 0, false, new HashMap<String, String>());
        return obs.toResponse();
    }

    @Override
    public void run() {
        int port = 8000;
        logMessage("Port is " + port);
        try {
            new FtBasic(
                    new TkFork(
                            new FkRegex("/step", new TkFork(new FkMethods("POST", this))),
                            new FkRegex("/reset", new TkFork(new FkMethods("GET", new GymProxyReset(this.bm))))
                    ), port).start(Exit.NEVER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final class GymProxyReset implements Take {
        BattleManager bm;
        public GymProxyReset(BattleManager bm){
            this.bm = bm;
        }
        @Override
        public Response act(final Request req) throws Exception {
            this.bm.restart();
            return new RsText("Hello servlet!");
        }
    }
}
