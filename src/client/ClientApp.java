package client;

import shared.BioService;
import shared.BioDetails;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class ClientApp {

    public static final int PORT = 1099;
    public static final String HOST = "localhost";
    public static final String SERVICE_NAME = "BioService";

    public static void main(String[] args) {
        String registryName = "rmi://" + HOST + ":" + PORT + "/" + SERVICE_NAME;
        System.out.println("CLIENT: Looking up " + registryName + " ...");

        try {
            Registry registry = LocateRegistry.getRegistry(PORT);
            BioService remoteObj = (BioService) registry.lookup(registryName);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nChoose an option:");
                System.out.println("1. Add new bio");
                System.out.println("2. Update existing bio");
                System.out.println("3. List all bios");
                System.out.println("E. Exit");
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine().trim();

                switch (choice.toUpperCase()) {
                    case "1" -> {
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter bio content: ");
                        String content = scanner.nextLine();
                        BioDetails added = remoteObj.addBio(new BioDetails(name, content));
                        System.out.println("CLIENT: Added: " + added);
                    }
                    case "2" -> {
                        System.out.print("Enter name to update: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter new content: ");
                        String content = scanner.nextLine();
                        BioDetails updated = remoteObj.updateBio(new BioDetails(name, content));
                        System.out.println("CLIENT: Updated: " + updated);
                    }
                    case "3" -> {
                        List<BioDetails> bios = remoteObj.listBios();
                        System.out.println("CLIENT: Bios list:");
                        for (BioDetails bio : bios) {
                            System.out.println(" - " + bio);
                        }
                    }
                    case "E" -> {
                        System.out.println("CLIENT: Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
