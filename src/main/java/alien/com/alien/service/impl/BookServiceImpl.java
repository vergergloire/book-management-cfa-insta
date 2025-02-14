package alien.com.alien.service.impl;

import alien.com.alien.dao.BookRepository;
import alien.com.alien.entity.Book;
import alien.com.alien.service.BookService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookDownloaderServiceImpl downloaderService;
    private final IndexingServiceImpl indexingServiceImpl;

    public BookServiceImpl(BookRepository bookRepository, BookDownloaderServiceImpl downloaderService, IndexingServiceImpl indexingServiceImpl) {
        this.bookRepository = bookRepository;
        this.downloaderService = downloaderService;
        this.indexingServiceImpl = indexingServiceImpl;
    }

    @Override
    @Transactional
    @PostConstruct
    public void loadBooks() {
        try {
            List<Book> bookList = downloaderService.fetchBooks((1700));
            int booksAdded = 0;

            for (Book book : bookList) {
                if (booksAdded >= 1700) break;

                if (bookRepository.existsByTitleAndAuthor(book.getTitle(), book.getAuthor())) {
                    System.out.println("Livre déjà en base (double vérification) : " + book.getTitle());
                    continue;
                }

                bookRepository.save(book);
                bookRepository.flush();
                booksAdded++;

                // Indexer le livre après enregistrement
                indexingServiceImpl.indexBook(book);

                System.out.println("Livre ajouté en base : " + book.getTitle());
            }
            System.out.println("Importation terminée : " + booksAdded + " livres ajoutés.");
        } catch (IOException e) {
            throw new RuntimeException("Erreur de récupération des livres", e);
        }
    }

}
