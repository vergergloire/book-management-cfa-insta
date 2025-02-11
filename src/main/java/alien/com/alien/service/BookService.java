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
            List<Book> bookList = downloaderService.fetchBooks(15);
            int booksAdded = 0;

            for (Book book : bookList) {
                if (booksAdded >= 15) break;

                // Vérifier si le livre existe déjà
                if (bookRepository.existsByTitleAndAuthor(book.getTitle(), book.getAuthor())) {
                    System.out.println("⚠ Livre déjà existant : " + book.getTitle());
                    continue;
                }

                bookRepository.save(book);
                bookRepository.flush();
                booksAdded++;

                // Indexer le livre après enregistrement
                indexingService.indexBook(book);

                System.out.println("Livre ajouté en base : " + book.getTitle());
            }
            System.out.println("Importation terminée : " + booksAdded + " livres ajoutés.");
        } catch (IOException e) {
            throw new RuntimeException("Erreur de récupération des livres", e);
        }
    }
}
