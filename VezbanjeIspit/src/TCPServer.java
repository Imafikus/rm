import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ChessPlayer {

    private static Integer currentId = 0;

    Integer id;
    String name;
    Integer elo;

    public ChessPlayer(String name) {
        this.id = currentId;
        currentId++;

        this.name = name;
        this.elo = 1300;
    }

    public String getInfo() {
        return  "Name: " + this.name + "; Elo: " + this.elo.toString();
    }

    public void updateElo(Integer dElo) {
        this.elo += dElo;
    }
}

public class TCPServer {

    private static List<ChessPlayer> allPlayers = Collections.synchronizedList(new ArrayList<ChessPlayer>());

    private static class ServerWorker implements Runnable {
        Socket client;

        public ServerWorker(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
            ) {
                while (true) {
                    String line = in.readLine();

                    if(line == null || line.equals("bye")) {
                        client.close();
                        break;
                    }

                    System.out.println("Received: " + line);

                    var cmd = line.substring(0, 4);
                    try {
                        if(cmd.equals("sel ")) {
                            var splitLine = line.split(" ");
                            if(splitLine.length != 2) {
                                throw new IOException();
                            }

                            var id = Integer.parseInt(splitLine[1]);
                            synchronized (allPlayers) {
                                out.write(allPlayers.get(id).getInfo());
                                out.newLine();
                                out.flush();
                            }
                        }

                        else if (cmd.equals("ins ")) {
                            var splitLine = line.split(" ");
                            if(splitLine.length == 1) {
                                throw new IOException();
                            }

                            synchronized (allPlayers) {
                                var name = line.substring(4);
                                allPlayers.add(new ChessPlayer(name));
                                out.write("ins successful");
                                out.newLine();
                                out.flush();
                            }
                        }

                        else if (cmd.equals("upd ")) {
                            var splitLine = line.split(" ");
                            if(splitLine.length != 3) {
                                throw new IOException();
                            }
                            var id = Integer.parseInt(splitLine[1]);
                            var dElo = Integer.parseInt(splitLine[2]);

                            allPlayers.get(id).updateElo(dElo);
                            out.write("upd successful");
                            out.newLine();
                            out.flush();
                        }
                        else {
                            out.write("Bad command");
                            out.newLine();
                            out.flush();
                        }

                    } catch (IOException | NumberFormatException | IndexOutOfBoundsException e) {
                        out.write("Bad command");
                        out.newLine();
                        out.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(12345)) {
            while(true) {
                new Thread(new ServerWorker(server.accept())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
