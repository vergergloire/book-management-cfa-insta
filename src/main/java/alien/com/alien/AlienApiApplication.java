package alien.com.alien;

import alien.com.alien.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlienApiApplication  {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(AlienApiApplication.class, args);
    }
}
