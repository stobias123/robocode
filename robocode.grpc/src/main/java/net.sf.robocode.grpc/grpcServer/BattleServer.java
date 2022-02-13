package net.sf.robocode.grpc.grpcServer;

import co.bird.battlebots.toby.proto.ActionReply;
import co.bird.battlebots.toby.proto.GymGrpc;
import co.bird.battlebots.toby.proto.StepRequest;
import co.bird.battlebots.toby.proto.Observation;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static net.sf.robocode.io.Logger.logMessage;

public class BattleServer {
    private static final Logger logger = Logger.getLogger(BattleServer.class.getName());

    private Server server;
    private int port;

    GymGrpc.GymImplBase svc = new GymGrpc.GymImplBase() {
        @Override
        public StreamObserver<StepRequest> step(StepRequest request, StreamObserver<Observation> responseObserver) {
            super.step(request, responseObserver);
        }
    }
    }
}
