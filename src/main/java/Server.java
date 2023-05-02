import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

  private int port;

  public Server(int port) {
    this.port = port;
  }

  public void start() {
    System.out.println("Starting server at port " + port + "...");
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
      System.out.println("Server started");
      while (true) {
        //GsonBuilder gsonBuilder = new GsonBuilder();
        //Gson gson = gsonBuilder.create();
        try (Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {
          String clientsRequest = in.readLine();
          List<PageEntry> searchResult = engine.search(clientsRequest);
          List<PageEntry> answer = new ArrayList<>();
          for (PageEntry pageEntry : searchResult) {
            PageEntry result = new PageEntry(pageEntry.getFileName(), pageEntry.getPageNumber(),
                pageEntry.getCount());
            answer.add(result);
          }
          Gson gson = new Gson();
          out.println(gson.toJson(answer));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}