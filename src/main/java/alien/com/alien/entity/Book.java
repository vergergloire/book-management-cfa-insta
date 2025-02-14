package alien.com.alien.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "T_BOOKS")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int wordCount;

    @Column(columnDefinition = "TEXT")
    private String coverImageUrl;


    public Long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Book() {
    }

    public Book(String title, String author, String content, int wordCount, String coverImageUrl) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.wordCount = wordCount;
        this.coverImageUrl = coverImageUrl;
    }
}
