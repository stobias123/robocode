package net.sf.robocode.battle;

import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.grpc.grpcServer.BattleServer;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.recording.BattlePlayer;
import net.sf.robocode.settings.ISettingsManager;

import java.io.IOException;

// This server will annoyingly pause after _every turn_
// It also starts a GRPC server.
public class GymBattle extends Battle {

    private BattleServer battleServer;

    public GymBattle(ISettingsManager properties, IBattleManager battleManager, IHostManager hostManager, ICpuManager cpuManager, BattleEventDispatcher eventDispatcher) throws IOException {
        super(properties,battleManager,hostManager,cpuManager,eventDispatcher);
        this.battleServer = new BattleServer();
        battleServer.start();
    }

    @Override
    protected void runTurn() {
        super.runTurn();
        this.pause();
    }
}
