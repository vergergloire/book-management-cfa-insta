package alien.com.alien.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_BOOKS")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    //@Lob
    //@Column(columnDefinition = "TEXT")
    @Column(columnDefinition = "TEXT")
    private String content;
    private int wordCount;

    public Book() {
    }

    public Book(Long id, String title, String author, String content, int wordCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.wordCount = wordCount;
    }

    public Book(String title, String author, String content, int wordCount) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.wordCount = wordCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public int getWordCount() {
        return wordCount;
    }
}
