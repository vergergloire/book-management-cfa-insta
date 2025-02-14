package alien.com.alien.dao;

import alien.com.alien.entity.InvertedIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvertedIndexRepository extends JpaRepository<InvertedIndex, Long> {
}

