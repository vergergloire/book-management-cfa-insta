package alien.com.alien.service;

import alien.com.alien.dao.BookRepository;
import alien.com.alien.dao.InvertedIndexRepository;
import alien.com.alien.dao.WordIndexRepository;
import alien.com.alien.domain.entity.Book;
import alien.com.alien.domain.entity.InvertedIndex;
import alien.com.alien.domain.entity.WordIndex;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class IndexingService {

    private final WordIndexRepository wordIndexRepository;
    private final InvertedIndexRepository invertedIndexRepository;

    public IndexingService(WordIndexRepository wordIndexRepository, InvertedIndexRepository invertedIndexRepository, BookRepository bookRepository) {
        this.wordIndexRepository = wordIndexRepository;
        this.invertedIndexRepository = invertedIndexRepository;
    }

    @Transactional
    public void indexBook(Book book) {
        String[] words = book.getContent().toLowerCase().split("\\W+");
        Map<String, Integer> wordCounts = new HashMap<>();

        for (String word : words) {
            if (!word.trim().isEmpty()) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
        }

        // Stocker dans `word_index`
        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            wordIndexRepository.save(new WordIndex(null, entry.getKey(), book, entry.getValue()));
        }

        // Stocker dans `inverted_index`
        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.setBook(book);
        invertedIndex.setWords(new ArrayList<>(wordCounts.keySet()));
        invertedIndexRepository.save(invertedIndex);

        System.out.println("📌 Indexation terminée pour : " + book.getTitle());
    }
}