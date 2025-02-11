package alien.com.alien.service;

import alien.com.alien.dao.BookRepository;
import alien.com.alien.domain.entity.Book;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookDownloaderService downloaderService;
    private final IndexingService indexingService;

    public BookService(BookRepository bookRepository, BookDownloaderService downloaderService, IndexingService indexingService) {
        this.bookRepository = bookRepository;
        this.downloaderService = downloaderService;
        this.indexingService = indexingService;
    }

    @Transactional
    @PostConstruct
    public void loadBooks() {
        try {
            List<String> bookUrls = downloaderService.getBookUrls();
            int booksAdded = 0;

            for (String url : bookUrls) {
                if (booksAdded >= 7) break; // 📌 Stop après 5 livres ajoutés

                try {
                    String content = downloaderService.downloadAndExtractBook(url);
                    int wordCount = downloaderService.countWords(content);

                    if (wordCount >= 10000) { // Filtrer les livres trop courts
                        String title = downloaderService.extractTitle(content);
                        String author = downloaderService.extractAuthor(content);

                        // 📌 Vérifier si le livre existe déjà
                        if (bookRepository.existsByTitleAndAuthor(title, author)) {
                            System.out.println("⚠ Livre déjà existant : " + title + " - " + author);
                            continue; // 🚫 Ignorer l'ajout du livre
                        }

                        String publicationDate = downloaderService.extractPublicationDate(content);
                        String bookType = downloaderService.extractBookType(content);

                        Book book = new Book(title, author, content, wordCount);
                        bookRepository.save(book);
                        bookRepository.flush(); // 💾 Sauvegarde en base de données
                        booksAdded++;

                        // 🔍 Indexer le livre après enregistrement
                        indexingService.indexBook(book);

                        System.out.println("✔ Livre ajouté : " + title + " 📖 Auteur : " + author + " 📅 Date : " + publicationDate + " 🏷 Type : " + bookType + " 📝 Nombre de mots : " + wordCount);
                    } else {
                        System.out.println("⚠ Livre ignoré (trop court) : " + url);
                    }
                } catch (Exception e) {
                    System.err.println("🚨 Erreur lors du traitement du livre : " + url);
                    e.printStackTrace();
                }
            }
            System.out.println("✅ Importation terminée : " + booksAdded + " livres ajoutés.");
        } catch (IOException e) {
            throw new RuntimeException("❌ Erreur de récupération des livres", e);
        }
    }
}

/*@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookDownloaderService downloaderService;
    private final IndexingService indexingService;

    public BookService(BookRepository bookRepository, BookDownloaderService downloaderService, IndexingService indexingService) {
        this.bookRepository = bookRepository;
        this.downloaderService = downloaderService;
        this.indexingService = indexingService;
    }

    @Transactional
    @PostConstruct
    public void loadBooks() {
        try {
            List<String> bookUrls = downloaderService.getBookUrls();
            int booksAdded = 1;

            for (String url : bookUrls) {
                if (booksAdded >= 5) break;

                try {
                    String content = downloaderService.downloadAndExtractBook(url);
                    int wordCount = downloaderService.countWords(content);

                    if (wordCount >= 10000) {
                        String title = downloaderService.extractTitle(content);
                        String author = downloaderService.extractAuthor(content);
                        String publicationDate = downloaderService.extractPublicationDate(content);
                        String bookType = downloaderService.extractBookType(content);

                        Book book = new Book(title, author, content, wordCount);
                        bookRepository.save(book);
                        bookRepository.flush();
                        booksAdded++;

                        // 🔍 Indexer le livre après enregistrement
                        indexingService.indexBook(book);

                        System.out.println("✔ Livre ajouté : " + title + " 📖 Auteur : " + author + " 📅 Date : " + publicationDate + " 🏷 Type : " + bookType + " 📝 Nombre de mots : " + wordCount);
                    }

                } catch (Exception e) {
                    System.err.println("⚠ Erreur lors du téléchargement : " + url);
                }
            }
            System.out.println("📚 Chargement terminé : " + booksAdded + " livres ajoutés.");
        } catch (IOException e) {
            throw new RuntimeException("Erreur de récupération des livres", e);
        }
    }*/

