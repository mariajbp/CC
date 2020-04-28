import Structs.PacketLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnonGW {
    private Map<Integer, PacketLog> connectionTable;
    private List<String> peers;

    public AnonGW(List<String> peers) {
        connectionTable = new HashMap<>();
        this.peers=peers;
    }




}
