package server;

import shared.BioService;
import shared.BioDetails;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import mq.MQProducer;

public class BioServiceImpl extends UnicastRemoteObject implements BioService {

    private final List<BioDetails> bios;

    public BioServiceImpl() throws RemoteException {
        super();
        bios = new ArrayList<>();

        // Preload entries
        bios.add(new BioDetails("Elena", "Works in digital marketing."));
        bios.add(new BioDetails("John", "Specialist in client communications."));
    }

    @Override
    public BioDetails addBio(BioDetails newBio) throws RemoteException {
        System.out.println("addBio() invoked: " + newBio);
        newBio.setTimestamp(LocalDateTime.now());
        bios.add(newBio);

        // MQ Producer
        try (MQProducer producer = new MQProducer()) {
            producer.send(newBio);
        } catch (Exception e) {
            System.out.println("⚠ Could not send to MQ (add):");
            e.printStackTrace();
        }

        return newBio;
    }

    @Override
    public BioDetails updateBio(BioDetails updatedBio) throws RemoteException {
        System.out.println("updateBio() invoked: " + updatedBio);
        for (BioDetails bio : bios) {
            if (bio.getName().equalsIgnoreCase(updatedBio.getName())) {
                bio.setContent(updatedBio.getContent());
                bio.setTimestamp(LocalDateTime.now());

                // MQProducer
                try (MQProducer producer = new MQProducer()) {
                    producer.send(bio);
                } catch (Exception e) {
                    System.out.println("⚠ Could not send to MQ (update):");
                    e.printStackTrace();
                }

                return bio;
            }
        }
        return null; // Not found
    }

    @Override
    public List<BioDetails> listBios() throws RemoteException {
        System.out.println("listBios() invoked");
        return new ArrayList<>(bios); // Return a copy
    }
}
