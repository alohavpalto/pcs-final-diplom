import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  private static final int PORT = 8989;
  private static final String HOST = "localHost";

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    Gson gson = new Gson();
    while (true) {
      try (Socket clientSocket = new Socket(HOST, PORT);
          PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
          BufferedReader in = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()))) {
        System.out.println("Введите слово для поиска:");
        String input = scanner.nextLine();
        out.println(input);
        String responseJson = in.readLine();
        PageEntry[] results = gson.fromJson(responseJson, PageEntry[].class);
        for (PageEntry result : results) {
          System.out.println("{");
          System.out.println("\t\"pdfName\": \"" + result.getFileName() + "\",");
          System.out.println("\t\"page\": " + result.getPageNumber() + ",");
          System.out.println("\t\"count\": " + result.getCount());
          System.out.println("},");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}