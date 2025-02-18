package alien.com.alien.dao;

import alien.com.alien.entity.Book;
import alien.com.alien.entity.WordIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordIndexRepository extends JpaRepository<WordIndex, Long> {
    List<WordIndex> findByWord(String word);

    long countByWord(String keyword);

    List<WordIndex> findByBook(Book book);
}

