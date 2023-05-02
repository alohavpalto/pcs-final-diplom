public class PageEntry implements Comparable<PageEntry> {

  private String fileName; // Имя pdf-файла
  private int pageNumber; // Номер страницы, на которой найдено слово
  private int count; // Количество вхождений слова на странице

  public PageEntry(String fileName, int pageNumber, int count) {
    this.fileName = fileName;
    this.pageNumber = pageNumber;
    this.count = count;
  }

  public String getFileName() {
    return fileName;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public int getCount() {
    return count;
  }

  public void incrementCount() {
    count++;
  }

  public int compareTo(PageEntry other) {
    return Integer.compare(other.getCount(),
        this.getCount()); // Сортировка по убыванию количества вхождений слова
  }

  @Override
  public String toString() {
    return "PageEntry{" +
        "pdfName='" + fileName + '\'' +
        ", page=" + pageNumber +
        ", count=" + count +
        '}';
  }
}