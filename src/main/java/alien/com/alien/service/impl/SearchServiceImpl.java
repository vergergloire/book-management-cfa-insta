package alien.com.alien.service.impl;

import alien.com.alien.dao.BookRepository;
import alien.com.alien.dao.WordIndexRepository;
import alien.com.alien.entity.Book;
import alien.com.alien.entity.WordIndex;
import alien.com.alien.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private final WordIndexRepository wordIndexRepository;
    @Autowired
    private final BookRepository bookRepository;

    public SearchServiceImpl(WordIndexRepository wordIndexRepository, BookRepository bookRepository) {
        this.wordIndexRepository = wordIndexRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Recherche par mot-clé exact
     */
    @Override
    public List<Book> searchByKeyword(String keyword) {
        return wordIndexRepository.findByWord(keyword)
                .stream().map(WordIndex::getBook)
                .distinct()
                .collect(Collectors.toList());
    }


    /**
     * Recherche par expression regulière
     */
    @Override
    public List<Book> searchByRegex(String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        return wordIndexRepository.findAll().stream()
                .filter(index -> pattern.matcher(index.getWord()).matches())
                .map(WordIndex::getBook)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Recherche par phrase exacte
     */
    @Override
    public List<Book> searchByPhrase(String phrase) {
        return bookRepository.findAll().stream()
                .filter(book -> book.getContent().contains(phrase))
                .collect(Collectors.toList());
    }

    /**
     * Recherche "ET" / "OU"
     */
    @Override
    public List<Book> searchWithBooleanQuery(String mustContain, String mayContain) {
        List<Book> result = new ArrayList<>();

        if (mustContain != null) {
            result.addAll(searchByKeyword(mustContain));
        }
        if (mayContain != null) {
            result.addAll(searchByKeyword(mayContain));
        }

        return result.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Recherche par similarité (Fuzzy Search)
     */
    @Override
    public List<Book> searchByFuzzyMatch(String keyword) {
        return wordIndexRepository.findAll().stream()
                .filter(index -> levenshteinDistance(index.getWord(), keyword) <= 2) // Tolérance de 2 erreurs max
                .map(WordIndex::getBook)
                .distinct()
                .collect(Collectors.toList());
    }


    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    /**
     * Recherche pondérée (TF-IDF)
     */
    @Override
    public List<Book> searchByTFIDF(String keyword) {
        return bookRepository.findAll().stream()
                .sorted((b1, b2) -> {
                    int tfidf1 = computeTFIDF(b1, keyword);
                    int tfidf2 = computeTFIDF(b2, keyword);
                    return Integer.compare(tfidf2, tfidf1);
                })
                .collect(Collectors.toList());
    }

    private int computeTFIDF(Book book, String keyword) {
        long totalBooks = bookRepository.count();
        long booksWithWord = wordIndexRepository.countByWord(keyword);
        return booksWithWord == 0 ? 0 : (int) ((book.getWordCount() / totalBooks) * Math.log(totalBooks / booksWithWord));
    }

    /**
     * Recherche contextuelle (NLP)
     */
    @Override
    public List<Book> searchByNLP(String query) {
        return searchByKeyword(query);
    }

    /**
     * Recherche par métadonnées
     */
    @Override
    public List<Book> searchByMetadata(String author, String year, String genre) {
        return bookRepository.findAll().stream()
                .filter(book -> (author == null || book.getAuthor().equalsIgnoreCase(author)))
                .collect(Collectors.toList());
    }

    /**
     * Recherche hybride
     */
    @Override
    public List<Book> searchByHybrid(String query, String metadata) {
        List<Book> keywordResults = searchByKeyword(query);
        List<Book> metadataResults = searchByMetadata(metadata, null, null);
        return keywordResults.stream().filter(metadataResults::contains).collect(Collectors.toList());
    }

    @Override
    public List<Book> suggestSimilarBooks(String query) {
        // 1Récupérer les livres qui correspondent à la recherche
        List<Book> foundBooks = searchByKeyword(query);

        // Si aucun livre trouvé, essayer avec une recherche floue
        if (foundBooks.isEmpty()) {
            foundBooks = searchByFuzzyMatch(query);
        }

        // Récupérer les livres similaires (basés sur mots-clés et contenu)
        Set<Book> recommendations = new HashSet<>();

        for (Book book : foundBooks) {
            // Ajouter des livres ayant des mots-clés similaires
            List<WordIndex> relatedWords = wordIndexRepository.findByBook(book);
            for (WordIndex word : relatedWords) {
                recommendations.addAll(searchByKeyword(word.getWord())); // Recherche des livres contenant ces mots
            }

            // Ajouter les livres avec une similarité de titre (Fuzzy Search)
            recommendations.addAll(searchByFuzzyMatch(book.getTitle()));

            // Ajouter les livres avec une similarité de contenu (TF-IDF)
            recommendations.addAll(searchByTFIDF(book.getTitle()));

            // Ajouter les livres du même genre (si une catégorie est disponible)
        }

        // Retourner une liste sans doublons et limitée à 10 suggestions
        return recommendations.stream().distinct().limit(10).collect(Collectors.toList());
    }

}
