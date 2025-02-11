package alien.com.alien.service;

import alien.com.alien.domain.entity.Book;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookDownloaderService {
    private static final String GUTENDEX_API_URL = "https://gutendex.com/books";

    /**
     * Récupère une liste de livres depuis l'API Gutendex.
     */
    public List<Book> fetchBooks(int limit) throws IOException {
        List<Book> books = new ArrayList<>();
        String nextPage = GUTENDEX_API_URL;

        while (nextPage != null && books.size() < limit) {
            Document doc = Jsoup.connect(nextPage).ignoreContentType(true).get();
            String json = doc.body().text();
            JSONObject jsonObject = new JSONObject(json);

            nextPage = jsonObject.optString("next", null);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                if (books.size() >= limit) break;

                JSONObject bookJson = results.getJSONObject(i);
                String title = bookJson.getString("title");
                String author = bookJson.getJSONArray("authors").length() > 0 ?
                        bookJson.getJSONArray("authors").getJSONObject(0).getString("name") : "Auteur inconnu";
                String coverImage = bookJson.getJSONObject("formats").optString("image/jpeg", "");
                String htmlUrl = bookJson.getJSONObject("formats").optString("text/html", "");

                if (!htmlUrl.isEmpty()) {
                    String content = fetchBookContent(htmlUrl);
                    int wordCount = countWords(content);

                    if (!content.isEmpty() && wordCount >= 10000) { // Filtre des livres trop courts
                        books.add(new Book(title, author, content, wordCount, coverImage));
                        System.out.println(" Livre téléchargé : " + title + " | Mots : " + wordCount);
                    }
                }
            }
        }
        return books;
    }

    /**
     * Télécharge le contenu HTML du livre et extrait le texte entre <h>Contents</h> et <div id="pg-end-separator">
     */
    public String fetchBookContent(String htmlUrl) throws IOException {
        Document doc = Jsoup.connect(htmlUrl).get();
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
    }

    /**
     * Compte les mots dans un texte.
     */
    public int countWords(String text) {
        return text == null ? 0 : text.split("\\s+").length;
    }
}
