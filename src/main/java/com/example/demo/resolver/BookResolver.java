package com.example.demo.resolver;

import com.example.demo.entity.BookEntity;
import com.example.demo.repository.BookRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

  private final BookRepository bookRepository;

  public BookResolver(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public BookEntity book(Integer id) {
    return bookRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Can't find book by this id"));
  }

  public List<BookEntity> books(List<Integer> ids) {
    return bookRepository.findAllById(ids);
  }
}
