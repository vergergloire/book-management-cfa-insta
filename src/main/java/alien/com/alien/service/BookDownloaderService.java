package alien.com.alien.service;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class BookDownloaderService {

    private static final String GUTENBERG_TOP_URL = "https://www.gutenberg.org/browse/scores/top";


    /**
     * Récupère une liste d'URLs de livres à partir du site Project Gutenberg.
     */
    public List<String> getBookUrls() throws IOException {
        List<String> bookUrls = new ArrayList<>();
        var doc = Jsoup.connect(GUTENBERG_TOP_URL)
                .header("Accept-Encoding", "identity")
                .get();

        doc.select("a[href]").forEach(element -> {
            String href = element.attr("href");
            if (href.startsWith("/ebooks/")) {
                String bookId = href.replace("/ebooks/", "").trim();
                bookUrls.add("https://www.gutenberg.org/files/" + bookId + "/" + bookId + "-0.txt");
            }
        });

        return bookUrls.stream().distinct().limit(3000).toList();
    }

    /**
     * Télécharge un livre et extrait uniquement le texte entre les marqueurs Gutenberg.
     */
    public String downloadAndExtractBook(String url) throws IOException {
        String content = new String(new URL(url).openStream().readAllBytes(), StandardCharsets.UTF_8);

        // Extraire uniquement le contenu utile
        return extractGutenbergContent(content);
    }

    /**
     * Extrait uniquement le texte entre les marqueurs de début et de fin de Gutenberg.
     */
    private String extractGutenbergContent(String content) {
        String[] lines = content.split("\n");
        StringBuilder extractedContent = new StringBuilder();
        boolean isInsideBook = false;

        for (String line : lines) {
            if (line.startsWith("*** START OF THIS PROJECT GUTENBERG EBOOK")) {
                isInsideBook = true;
                continue; // On ignore cette ligne
            }
            if (line.startsWith("*** END OF THIS PROJECT GUTENBERG EBOOK")) {
                break; // Fin du livre
            }
            if (isInsideBook) {
                extractedContent.append(line).append("\n");
            }
        }

        return extractedContent.toString().trim();
    }

    /**
     * Extrait le titre du livre.
     */
    public String extractTitle(String content) {
        if (content == null || content.isEmpty()) return "Titre inconnu";

        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // On suppose que le titre est la première ligne pertinente
            if (!line.isEmpty() && !line.toLowerCase().contains("gutenberg") && !line.toLowerCase().contains("ebook")) {
                return line;
            }
        }
        return "Titre inconnu";
    }

    /**
     * Extrait l'auteur du livre.
     */
    public String extractAuthor(String content) {
        if (content == null || content.isEmpty()) return "Auteur inconnu";

        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.toLowerCase().startsWith("by ") || line.toLowerCase().startsWith("par ")) {
                return line.replace("by", "").replace("par", "").trim();
            }
        }
        return "Auteur inconnu";
    }

    /**
     * Extrait la date de publication si disponible.
     */
    public String extractPublicationDate(String content) {
        if (content == null || content.isEmpty()) return "Date inconnue";

        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().contains("release date") || line.toLowerCase().contains("publication date")) {
                return line.replace("Release Date:", "").replace("Publication Date:", "").trim();
            }
        }
        return "Date inconnue";
    }

    /**
     * Extrait le type de l'œuvre (roman, essai, poésie...).
     */
    public String extractBookType(String content) {
        if (content.toLowerCase().contains("novel") || content.toLowerCase().contains("roman")) {
            return "Roman";
        }
        if (content.toLowerCase().contains("poem") || content.toLowerCase().contains("poetry")) {
            return "Poésie";
        }
        if (content.toLowerCase().contains("essay")) {
            return "Essai";
        }
        return "Type inconnu";
    }

    /**
     * Compte les mots d'un texte.
     */
    public int countWords(String text) {
        return text == null ? 0 : text.split("\\s+").length;
    }
}
