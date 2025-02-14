package alien.com.alien.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "T_INVERTED_INDEX")
public class InvertedIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Book book;

    @ElementCollection
    @CollectionTable(name = "inverted_index_words", joinColumns = @JoinColumn(name = "inverted_index_id"))
    private List<String> words;

    public InvertedIndex() {
    }

    public InvertedIndex(Long id, Book book, List<String> words) {
        this.id = id;
        this.book = book;
        this.words = words;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public List<String> getWords() {
        return words;
    }
}
