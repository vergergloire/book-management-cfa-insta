package alien.com.alien.service.impl;

import alien.com.alien.dao.BookRepository;
import alien.com.alien.entity.Book;
import alien.com.alien.service.BookDownloaderService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class BookDownloaderServiceImpl implements BookDownloaderService {
    private static final String GUTENDEX_API_URL = "https://gutendex.com/books";
    private static final int TIMEOUT_MS = 3600000;
    private static final int MAX_RETRIES = 100;

    private final BookRepository bookRepository;

    public BookDownloaderServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Récupère une liste de livres depuis l'API Gutendex.
     */
    @Override
    public List<Book> fetchBooks(int limit) throws IOException {
        List<Book> books = new ArrayList<>();
        String nextPage = GUTENDEX_API_URL;
        ExecutorService executor = Executors.newFixedThreadPool(5); // 5 téléchargements en parallèle

        while (nextPage != null && books.size() < limit) {
            try {
                Document doc = Jsoup.connect(nextPage)
                        .ignoreContentType(true)
                        .timeout(TIMEOUT_MS)
                        .get();

                String json = doc.body().text();
                JSONObject jsonObject = new JSONObject(json);
                nextPage = jsonObject.optString("next", null);
                JSONArray results = jsonObject.getJSONArray("results");

                List<Future<Book>> futureBooks = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    if (books.size() >= limit) break;

                    JSONObject bookJson = results.getJSONObject(i);
                    String title = bookJson.getString("title");
                    String author = bookJson.getJSONArray("authors").length() > 0 ?
                            bookJson.getJSONArray("authors").getJSONObject(0).getString("name") : "Auteur inconnu";

                    // Vérifier si le livre est déjà en base AVANT de le télécharger
                    if (bookRepository.existsByTitleAndAuthor(title, author)) {
                        System.out.println("Livre déjà en base : " + title);
                        continue;
                    }

                    String coverImage = bookJson.getJSONObject("formats").optString("image/jpeg", "");
                    String htmlUrl = bookJson.getJSONObject("formats").optString("text/html", "");

                    if (!htmlUrl.isEmpty()) {
                        Future<Book> future = executor.submit(() -> {
                            String content = fetchBookContent(htmlUrl);
                            int wordCount = countWords(content);

                            if (!content.isEmpty() && wordCount >= 10000) {
                                return new Book(title, author, content, wordCount, coverImage);
                            }
                            return null;
                        });
                        futureBooks.add(future);
                    }
                }

                for (Future<Book> future : futureBooks) {
                    Book book = future.get();
                    if (book != null) {
                        books.add(book);
                        System.out.println("Livre téléchargé : " + book.getTitle() + " | Mots : " + book.getWordCount());
                    }
                }

            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération des livres : " + e.getMessage());
                break;
            }
        }
        executor.shutdown();
        return books;
    }



    /**
     * Télécharge le contenu HTML du livre et extrait le texte utile.
     */
    @Override
    public String fetchBookContent(String htmlUrl) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                Document doc = Jsoup.connect(htmlUrl)
                        .timeout(TIMEOUT_MS)
                        .get();

                StringBuilder extractedContent = new StringBuilder();
                boolean isInsideBook = false;

                for (var element : doc.body().children()) {
                    String text = element.text().trim();

                    if (text.startsWith("Contents")) {
                        isInsideBook = true;
                        continue;
                    }
                    if (text.contains("*** END OF THE PROJECT GUTENBERG EBOOK")) {
                        break;
                    }
                    if (isInsideBook) {
                        extractedContent.append(text).append("\n");
                    }
                }
                return extractedContent.toString().trim();
            } catch (IOException e) {
                attempt++;
                System.err.println("Tentative " + attempt + "/" + MAX_RETRIES + " échouée pour " + htmlUrl);
                if (attempt >= MAX_RETRIES) {
                    System.err.println("Échec définitif pour " + htmlUrl);
                    return "";
                }
            }
        }
        return "";
    }

    /**
     * Compte les mots dans un texte.
     */
    @Override
    public int countWords(String text) {
        return text == null ? 0 : text.split("\\s+").length;
    }
}
