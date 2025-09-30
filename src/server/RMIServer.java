package server;

import shared.BioService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {

    public static final int PORT = 1099;                          // RMI registry port
    public static final String HOST = "localhost";                // RMI host
    public static final String SERVICE_NAME = "BioService";       // Remote service name

    public static void main(String[] args) {
        System.setProperty("java.util.logging.config.file", "src/server/logging.properties");

        try {
            System.out.println("SERVER: Registering BioService...");

            LocateRegistry.createRegistry(PORT); // Creates registry on given port
            Registry registry = LocateRegistry.getRegistry(PORT); // Gets the registry reference

            BioService remoteComponent = new BioServiceImpl(); // RMI implementation

            // Optional cleaner version:
            // java.rmi.Naming.rebind("rmi://localhost:1099/BioService", remoteComponent);
            registry.rebind("rmi://" + HOST + ":" + PORT + "/" + SERVICE_NAME, remoteComponent);

            System.out.println("SERVER: Ready...");
        } catch (Exception e) {
            System.out.println("SERVER: Failed to register BioService: " + e);
        }
    }
}
