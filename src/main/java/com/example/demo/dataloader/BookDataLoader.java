package com.example.demo.dataloader;

import static com.example.demo.DataLoadersConstant.AUTHOR_LOADER;
import static com.example.demo.DataLoadersConstant.REVIEWS_LOADER;

import com.example.demo.entity.AuthorEntity;
import com.example.demo.entity.BookEntity;
import com.example.demo.entity.ReviewEntity;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookDataLoader implements GraphQLResolver<BookEntity> {
  public CompletableFuture<List<ReviewEntity>> getReviews(
      BookEntity source, DataFetchingEnvironment env) {
    DataLoader<Integer, List<ReviewEntity>> dataLoader = env.getDataLoader(REVIEWS_LOADER);
    return dataLoader.load(source.getId());
  }

  public CompletableFuture<AuthorEntity> getAuthor(BookEntity source, DataFetchingEnvironment env) {
    DataLoader<Integer, AuthorEntity> dataLoader = env.getDataLoader(AUTHOR_LOADER);
    return dataLoader.load(source.getAuthorId());
  }
}
