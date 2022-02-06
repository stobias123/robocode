package net.sf.robocode.grpc.grpcServer;

import co.bird.battlebots.toby.proto.ActionReply;
import co.bird.battlebots.toby.proto.ObservationRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import co.bird.battlebots.toby.proto.ObserverGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BattleServer {
    private static final Logger logger = Logger.getLogger(BattleServer.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new ServerImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    BattleServer.this.stop();
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

    /**
     * Main launches the server from the command line.
     public static void main(String[] args) throws IOException, InterruptedException {
     final HelloWorldServer server = new HelloWorldServer();
     server.start();
     server.blockUntilShutdown();
     }
     */

    static class ServerImpl extends ObserverGrpc.ObserverImplBase {

        @Override
        public void sendObservation(ObservationRequest req, StreamObserver<ActionReply> responseObserver) {
            ActionReply reply = ActionReply.newBuilder().setActionChoice(1).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}