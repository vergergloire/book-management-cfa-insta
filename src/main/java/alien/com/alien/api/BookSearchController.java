package alien.com.alien.api;

import alien.com.alien.entity.Book;
import alien.com.alien.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books/")
public class BookSearchController {

    private final SearchService searchService;

    public BookSearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Recherche par mot-clé exact
     */
    @GetMapping("/search")
    public List<Book> searchByKeyword(@RequestParam String keyword) {
        return searchService.searchByKeyword(keyword);
    }


    /**
     * Recherche par mot-clé exact
     */
    @GetMapping("/search/regex")
    public List<Book> searchByRegex(@RequestParam String regex) {
        return searchService.searchByRegex(regex);
    }

    /**
     * Recherche par phrase exacte
     */
    @GetMapping("/search/phrase")
    public List<Book> searchByPhrase(@RequestParam String phrase) {
        return searchService.searchByPhrase(phrase);
    }

    /**
     * Recherche "ET" / "OU"
     */
    @GetMapping("/search/boolean")
    public List<Book> searchWithBooleanQuery(
            @RequestParam(required = false) String mustContain,
            @RequestParam(required = false) String mayContain
    ) {
        return searchService.searchWithBooleanQuery(mustContain, mayContain);
    }

    /**
     * Recherche par similarité (fuzzy search)
     */
    @GetMapping("/search/fuzzy")
    public List<Book> searchByFuzzyMatch(@RequestParam String keyword) {
        return searchService.searchByFuzzyMatch(keyword);
    }

    /**
     * Recherche pondérée (TF-IDF)
     */
    @GetMapping("/search/tfidf")
    public List<Book> searchByTFIDF(@RequestParam String keyword) {
        return searchService.searchByTFIDF(keyword);
    }

    /**
     * Recherche contextuelle (NLP)
     */
    @GetMapping("/search/nlp")
    public List<Book> searchByNLP(@RequestParam String query) {
        return searchService.searchByNLP(query);
    }

    /**
     * Recherche par métadonnées
     */
    @GetMapping("/search/metadata")
    public List<Book> searchByMetadata(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String genre
    ) {
        return searchService.searchByMetadata(author, year, genre);
    }

    /**
     * Recherche hybride (mixte)
     */
    @GetMapping("/search/hybrid")
    public List<Book> searchByHybrid(
            @RequestParam String query,
            @RequestParam(required = false) String metadata
    ) {
        return searchService.searchByHybrid(query, metadata);
    }

    @GetMapping("/search/suggestions")
    public List<Book> getSuggestions(@RequestParam String query) {
        return searchService.suggestSimilarBooks(query);
    }
}
