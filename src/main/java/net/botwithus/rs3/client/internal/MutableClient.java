package net.botwithus.rs3.client.internal;

import net.botwithus.rs3.client.Client;
import net.botwithus.rs3.client.StateTransition;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class MutableClient extends Client {

    public static final Queue<StateTransition> STATE_CHANGES = new ConcurrentLinkedQueue<>();

    public static int CLIENT_CYCLE = 0;

    public static int SERVER_TICK = 0;

    public static int CLIENT_STATE = 0;

}
