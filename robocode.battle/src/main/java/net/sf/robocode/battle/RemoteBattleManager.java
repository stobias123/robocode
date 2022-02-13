package net.sf.robocode.battle;

import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import net.sf.robocode.recording.BattlePlayer;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.grpc.grpcServer.BattleServer;
import robocode.control.RandomFactory;
import robocode.control.RobotSpecification;

import static net.sf.robocode.io.Logger.logMessage;

public class RemoteBattleManager extends BattleManager {
    CountDownLatch startSignal = new CountDownLatch(1);

    public RemoteBattleManager(ISettingsManager properties, IRepositoryManager repositoryManager, IHostManager hostManager, ICpuManager cpuManager, BattleEventDispatcher battleEventDispatcher, IRecordManager recordManager) {
        super(properties, repositoryManager, hostManager, cpuManager, battleEventDispatcher, recordManager);
    }
    public void startNewBattle(BattleProperties battleProperties, boolean waitTillOver, boolean enableCLIRecording) {
        this.battleProperties = battleProperties;
        final RobotSpecification[] robots = repositoryManager.loadSelectedRobots(battleProperties.getSelectedRobots());

        startNewBattleImpl(robots, waitTillOver, enableCLIRecording);
    }

    @Override
    public void startNewBattle(BattleProperties battleProperties, boolean waitTillOver, boolean enableCLIRecording) {
        BattleServer server = new BattleServer();
        super.startNewBattle(battleProperties, waitTillOver, enableCLIRecording);
    }

    private void startNewBattleImpl(RobotSpecification[] battlingRobotsList, boolean waitTillOver, boolean enableRecording) {
        stop(true);

        logMessage("Preparing battle...");

        final boolean recording = (properties.getOptionsCommonEnableReplayRecording()
                && System.getProperty("TESTING", "none").equals("none"))
                || enableRecording;

        if (recording) {
            recordManager.attachRecorder(battleEventDispatcher);
        } else {
            recordManager.detachRecorder();
        }

        // resets seed for deterministic behavior of Random
        final String seed = System.getProperty("RANDOMSEED", "none");

        if (!seed.equals("none")) {
            // init soon as it reads random
            cpuManager.getCpuConstant();

            RandomFactory.resetDeterministic(Long.valueOf(seed));
        }

        // TODO: I need to copypasta this whole fuckin method, but change out for gymbattle here.
        GymBattle realBattle = Container.createComponent(GymBattle.class);
        realBattle.setup(battlingRobotsList, battleProperties, isPaused());

        battle = realBattle;

        battleThread = new Thread(Thread.currentThread().getThreadGroup(), realBattle);
        battleThread.setPriority(Thread.NORM_PRIORITY);
        battleThread.setName("Battle Thread");
        realBattle.setBattleThread(battleThread);

        if (RobocodeProperties.isSecurityOn()) {
            hostManager.addSafeThread(battleThread);
        }

        // Start the realBattle thread
        battleThread.start();

        // Wait until the realBattle is running and ended.
        // This must be done as a new realBattle could be started immediately after this one causing
        // multiple realBattle threads to run at the same time, which must be prevented!
        realBattle.waitTillStarted();
        if (waitTillOver) {
            realBattle.waitTillOver();
        }
    }

}
