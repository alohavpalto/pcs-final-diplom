import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class BooleanSearchEngine implements SearchEngine {

  private final Map<String, List<PageEntry>> index = new HashMap<>();

  public BooleanSearchEngine(File pdfsDir) throws IOException {
    File[] files = pdfsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
    if (files == null) {
      throw new IllegalArgumentException("Invalid directory: " + pdfsDir.getPath());
    }
    for (File file : files) {
      PdfDocument pdfDoc = new PdfDocument(new PdfReader(file));
      int numPages = pdfDoc.getNumberOfPages();
      for (int pageNum = 1; pageNum <= numPages; pageNum++) {
        String pageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(pageNum));
        String[] words = pageText.toLowerCase().split("[^\\p{L}\\p{Digit}]+");
        for (String word : words) {
          List<PageEntry> entries = index.computeIfAbsent(word, k -> new ArrayList<>());
          if (!entries.isEmpty() && entries.get(entries.size() - 1).getFileName()
              .equals(file.getName())
              && entries.get(entries.size() - 1).getPageNumber() == pageNum) {
            entries.get(entries.size() - 1).incrementCount();
          } else {
            entries.add(new PageEntry(file.getName(), pageNum, 1));
          }
        }
      }
      pdfDoc.close();
    }
    for (List<PageEntry> entries : index.values()) {
      entries.sort(Collections.reverseOrder());
    }
  }

  @Override
  public List<PageEntry> search(String query) {
    Set<String> stopWords = readStopWords(); // считываем список стоп-слов
    String[] words = query.toLowerCase().split("[^\\p{L}\\p{Nd}]+"); // разбиваем запрос на слова
    Map<PageEntry, Integer> pageCounts = new HashMap<>();
    for (String word : words) {
      if (stopWords.contains(word)) {
        continue; // игнорируем стоп-слова
      }
      List<PageEntry> entries = index.getOrDefault(word, Collections.emptyList());
      for (PageEntry entry : entries) {
        int count = pageCounts.getOrDefault(entry, 0);
        pageCounts.put(entry, count + 1);
        entry.incrementCount();
      }
    }
    List<PageEntry> result = new ArrayList<>(pageCounts.keySet());
    result.sort(Collections.reverseOrder(Comparator.comparing(
        PageEntry::getCount))); // сортируем по убыванию суммарного количества вхождений
    return result;
  }

  private Set<String> readStopWords() {
    Set<String> stopWords = new HashSet<>();
    try (Scanner scanner = new Scanner(new File("stop-ru.txt"))) {
      while (scanner.hasNextLine()) {
        stopWords.add(scanner.nextLine());
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return stopWords;
  }
}
