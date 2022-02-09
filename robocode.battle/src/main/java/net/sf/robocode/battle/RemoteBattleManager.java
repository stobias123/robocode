package net.sf.robocode.battle;

import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.grpc.grpcServer.BattleServer;

public class RemoteBattleManager extends BattleManager {

    public RemoteBattleManager(ISettingsManager properties, IRepositoryManager repositoryManager, IHostManager hostManager, ICpuManager cpuManager, BattleEventDispatcher battleEventDispatcher, IRecordManager recordManager) {
        super(properties, repositoryManager, hostManager, cpuManager, battleEventDispatcher, recordManager);
    }

    @Override
    public void startNewBattle(BattleProperties battleProperties, boolean waitTillOver, boolean enableCLIRecording) {
        BattleServer server = new BattleServer();
        super.startNewBattle(battleProperties, waitTillOver, enableCLIRecording);
    }
}
