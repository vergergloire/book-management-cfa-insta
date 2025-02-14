package alien.com.alien;

import alien.com.alien.service.impl.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlienApiApplication  {
    @Autowired
    private BookServiceImpl bookServiceImpl;

    public static void main(String[] args) {
        SpringApplication.run(AlienApiApplication.class, args);
    }
}
