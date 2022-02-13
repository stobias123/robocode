package net.sf.robocode.battle;

import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.grpc.grpcServer.BattleServer;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.recording.BattlePlayer;
import net.sf.robocode.settings.ISettingsManager;

import co.bird.battlebots.toby.proto.ActionReply;
import co.bird.battlebots.toby.proto.GymGrpc;
import co.bird.battlebots.toby.proto.StepRequest;
import co.bird.battlebots.toby.proto.Observation;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import sun.awt.Mutex;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static net.sf.robocode.io.Logger.logMessage;

// This server will annoyingly pause after _every turn_
// It also starts a GRPC server.
public class GymBattle extends Battle {
    private Server server;

    public GymBattle(ISettingsManager properties, IBattleManager battleManager, IHostManager hostManager, ICpuManager cpuManager, BattleEventDispatcher eventDispatcher) throws IOException {
        super(properties,battleManager,hostManager,cpuManager,eventDispatcher);
        startServer();
    }

    @Override
    protected void runTurn() {
        super.runTurn();
        this.pause();
    }


    public void startServer() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new ServerImpl())
                .build()
                .start();
        logMessage("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    GymBattle.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }



    static class ServerImpl extends GymGrpc.GymImplBase {
        @Override
        public void step(StepRequest req, StreamObserver<Observation> responseObserver) {
            logMessage("stepping.");
            Observation.Builder obs = Observation.newBuilder();
            obs.setDone(false);
            Observation returnObs = obs.build();

            responseObserver.onNext(returnObs);
        }
    }
}
