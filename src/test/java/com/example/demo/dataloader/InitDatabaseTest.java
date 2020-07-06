package com.example.demo.dataloader;

import com.example.demo.DataloaderApplication;
import com.example.demo.entity.AuthorEntity;
import com.example.demo.entity.BookEntity;
import com.example.demo.entity.ReviewEntity;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.ReviewRepository;
import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.Rollback;

@SpringBootTest(classes = DataloaderApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class InitDatabaseTest {
  protected static Faker faker = new Faker();

  @Autowired private AuthorRepository authorRepository;

  @Autowired private BookRepository bookRepository;

  @Autowired private ReviewRepository reviewRepository;

  @Test
  @Rollback(false)
  public void testInit() {
    List<AuthorEntity> authors = mockAuthors();
    List<BookEntity> books = mockBooks(authors);
    List<ReviewEntity> reviews = mockReviews(books);
  }

  private List<ReviewEntity> mockReviews(List<BookEntity> books) {
    int booksCount = books.size();
    List<ReviewEntity> reviews = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      BookEntity bookEntity = books.get(i % booksCount);
      reviews.add(mockReview(bookEntity));
    }
    return reviewRepository.saveAll(reviews);
  }

  private ReviewEntity mockReview(BookEntity bookEntity) {
    return ReviewEntity.builder()
        .content(faker.overwatch().quote())
        .bookId(bookEntity.getId())
        .build();
  }

  private List<BookEntity> mockBooks(List<AuthorEntity> authors) {
    int authorCount = authors.size();
    List<BookEntity> books = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      AuthorEntity authorEntity = authors.get(i % authorCount);
      books.add(mockBook(authorEntity));
    }
    return bookRepository.saveAll(books);
  }

  private BookEntity mockBook(AuthorEntity authorEntity) {
    return BookEntity.builder()
        .code(faker.number().digits(5))
        .name(faker.pokemon().name())
        .description(faker.harryPotter().quote())
        .authorId(authorEntity.getId())
        .build();
  }

  private List<AuthorEntity> mockAuthors() {
    List<AuthorEntity> authors = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      authors.add(mockAuthor());
    }
    return authorRepository.saveAll(authors);
  }

  private AuthorEntity mockAuthor() {
    return AuthorEntity.builder()
        .age(faker.number().numberBetween(0, 100))
        .name(faker.rickAndMorty().character())
        .build();
  }
}
