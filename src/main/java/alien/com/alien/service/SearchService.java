package alien.com.alien.service;

import alien.com.alien.domain.entity.Book;

import java.util.List;

public interface SearchService {
    List<Book> searchByKeyword(String keyword);
    List<Book> searchByRegex(String regex);
    List<Book> searchByPhrase(String phrase);
    List<Book> searchWithBooleanQuery(String mustContain, String mayContain);
    List<Book> searchByFuzzyMatch(String keyword);
    List<Book> searchByTFIDF(String keyword);
    List<Book> searchByNLP(String query);
    List<Book> searchByMetadata(String author, String year, String genre);
    List<Book> searchByHybrid(String query, String metadata);
}
