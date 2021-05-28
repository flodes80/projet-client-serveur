import fr.clientserveur.common.rmi.interfaces.InterfaceServeurCentral;
import fr.clientserveur.serveurmagasin.utils.FactureMakerGenerator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Ignore
public class TestRMIMagasinCentral {

    private static InterfaceServeurCentral server;

    @BeforeClass
    public static void whenRunServer_thenServerStarts(){
        /*try{
            Config.getInstance().loadOrCreateConfig();
            ServerCentral serverCentral = new ServerCentral();
            serverCentral.createStubAndBind();
            Registry registry = LocateRegistry.getRegistry(10000);
            server = (InterfaceServeurCentral) registry.lookup("ServeurCentral");
        }catch (RemoteException | NotBoundException e){
            fail("Exception occured during server start");
        }*/
    }

    @Test
    public void whenClientSendRequest_thenServerRespond(){
        try {
            assertEquals(10, (int) server.add(5, 5));
        } catch (RemoteException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    @After
    @Test
    public void fileTransferTest(){
        File file = new File(TestFactureMaker.FACTURE_FILE_PATH);
        byte[] fileBytes = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(fileBytes, 0, fileBytes.length);
            server.uploadFileToServer(FactureMakerGenerator.getLastFileNameGenerated(), fileBytes, (int) file.length());
            in.close();
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }


}
