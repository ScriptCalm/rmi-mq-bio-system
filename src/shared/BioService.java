package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BioService extends Remote {
    BioDetails addBio(BioDetails newBio) throws RemoteException;
    BioDetails updateBio(BioDetails updatedBio) throws RemoteException;
    List<BioDetails> listBios() throws RemoteException;
}
