package com.example.demo;

import static com.example.demo.DataLoadersConstant.AUTHOR_LOADER;
import static com.example.demo.DataLoadersConstant.REVIEWS_LOADER;

import com.example.demo.entity.AuthorEntity;
import com.example.demo.entity.ReviewEntity;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.ReviewRepository;
import graphql.kickstart.execution.context.DefaultGraphQLContext;
import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.servlet.context.DefaultGraphQLWebSocketContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CustomGraphQLContextBuilder implements GraphQLServletContextBuilder {

  private final Executor dataLoaderThreadPoolTaskExecutor;

  private final ReviewRepository reviewRepository;

  private final AuthorRepository authorRepository;

  @Autowired
  public CustomGraphQLContextBuilder(
      @Qualifier("dataLoaderThreadPoolTaskExecutor") Executor dataLoaderThreadPoolTaskExecutor,
      ReviewRepository reviewRepository,
      AuthorRepository authorRepository) {
    this.dataLoaderThreadPoolTaskExecutor = dataLoaderThreadPoolTaskExecutor;
    this.reviewRepository = reviewRepository;
    this.authorRepository = authorRepository;
  }

  @Override
  public GraphQLContext build(HttpServletRequest req, HttpServletResponse response) {
    return DefaultGraphQLServletContext.createServletContext(buildDataLoaderRegistry(), null)
        .with(req)
        .with(response)
        .build();
  }

  @Override
  public GraphQLContext build(Session session, HandshakeRequest handshakeRequest) {
    return DefaultGraphQLWebSocketContext.createWebSocketContext(buildDataLoaderRegistry(), null)
        .with(session)
        .with(handshakeRequest)
        .build();
  }

  @Override
  public GraphQLContext build() {
    return new DefaultGraphQLContext(buildDataLoaderRegistry(), null);
  }

  private DataLoaderRegistry buildDataLoaderRegistry() {
    DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
    MappedBatchLoader<Integer, List<ReviewEntity>> reviewMappedBatchLoader =
        bookIds ->
            CompletableFuture.supplyAsync(
                () ->
                    reviewRepository.findAllByBookIdIn(bookIds).stream()
                        .collect(Collectors.groupingBy(ReviewEntity::getBookId)),
                dataLoaderThreadPoolTaskExecutor);
    // register to data loader
    dataLoaderRegistry.register(
        REVIEWS_LOADER, DataLoader.newMappedDataLoader(reviewMappedBatchLoader));

    MappedBatchLoader<Integer, AuthorEntity> authorMappedBatchLoader =
        authorIds ->
            CompletableFuture.supplyAsync(
                () ->
                    authorRepository.findAllById(authorIds).stream()
                        .collect(Collectors.toMap(AuthorEntity::getId, Function.identity())),
                dataLoaderThreadPoolTaskExecutor);
    // register author data loader
    dataLoaderRegistry.register(
        AUTHOR_LOADER, DataLoader.newMappedDataLoader(authorMappedBatchLoader));
    return dataLoaderRegistry;
  }
}
