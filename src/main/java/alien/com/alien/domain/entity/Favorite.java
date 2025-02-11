package alien.com.alien.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_FAVORITE")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Book book;
    private LocalDateTime ceratedAt = LocalDateTime.now();

    public Favorite() {
    }

    public Favorite(Long id, User user, Book book, LocalDateTime ceratedAt) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.ceratedAt = ceratedAt;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public LocalDateTime getCeratedAt() {
        return ceratedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setCeratedAt(LocalDateTime ceratedAt) {
        this.ceratedAt = ceratedAt;
    }
}
