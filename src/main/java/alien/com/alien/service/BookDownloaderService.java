package alien.com.alien.service;

import alien.com.alien.entity.Book;

import java.io.IOException;
import java.util.List;

public interface BookDownloaderService {
    List<Book> fetchBooks(int limit) throws IOException;
    String fetchBookContent(String htmlUrl) throws IOException;
    int countWords(String text);
}
