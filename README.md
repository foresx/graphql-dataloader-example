# This is a java-implementation graphql data loader example for demo

## Why using graphql data loader?

### 场景假设

试想这样一个场景，我们需要查询一个 book list，包含其 review 的信息。

1. 首先我们查询出所有的 book 信息

```sql
select * from book order by id;
```

2. 针对每一个 book 我们单独去查询出所有的 review

```sql
select * from review where book_id = $bookId;
```

假如我们现在这个 list 的长度是 10，那么我们总的 sql 查询就会有 11 条. 10 条是由于这些外部资源带来的额外查询。

> 现在看起来是还好，但是假如我们现在 book 下我们又需要查询出另外一个资源呢比如 author，那么是不是现在查询次数会变成了 2N + 1？
> 并且这样的查询，会浪费大量的数据库连接，可能花在本身 sql 查询的时间都不及建立一次数据库连接花费大。



### What is N+1 problem

如上所出现的问题我们就称之为 N+1 problem，那么 N 代表什么？ 1 代表什么呢？

1 代表的是我们查询 book 的一次 query
N 代表的就是我们查询 review 的 10 次 additional query



## data loader 是什么？

- data loader 是 facebook 的一个工具库. 主要功能是 batching 和 caching。将多次查询合并为一个，加载过的数据可以直接从 dataloader 的缓存空间中获取到。

- data loader 通过批量获取外部资源并且在返回之前添加到资源上去，从而使 n+1 问题中的 n 变成 1. 

**N + 1 => 2**

## 相关背景知识

Graphql resolver and data loader registry

### Graphql resolver

Graphql Resolver 是由 graphql-java-kickstart-tools中提供的一个类，作为所有 graphql resolver 的父类。子类有 **GraphQLQueryResolver, GraphQLMutationResolver and GraphQLSubscriptionResolver** 提供最基础的 graphql 的 query, mutation and subscription 功能。

Graphql  resolver 的用处，就是自动帮我们把 **java object model** 和我们的 **graphql schema** 定义中的类型对应起来。不用像原生的 graphql-java一样， 通过 **runtime wiring** 和 **typeDefinitionRegistry** 把 model 和 graphql type 对应起来。



原生如图所示: 

![Creating GraphQL](https://www.graphql-java.com/images/graphql_creation.png)



用了 Graphql resolver 如图所示:

![img](https://www.altexsoft.com/media/2019/03/word-image-4.png)



### DataLoaderRegistry

Dataloader Registry 是存放所有 dataloader 的一个中心. 提供 通过 name 去获取 dataloader 的方法。

```java
* This allows data loaders to be registered together into a single place so
* they can be dispatched as one.  It also allows you to retrieve data loaders by
* name from a central place
```

## data loader 怎么去用？

总体来说，分成三个步骤(包括 js 也是类似的步骤):

- 提供 batchGet 的接口, 用 sql 来表示的话就类似于

```sql
select * from review where book_id in (?, ?, ?)
```

- 将 batchGet 的接口(异步的)，通过 data loader registry 注册到graphql servlet context 中

```java
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
```

- 实现 GraphqlResolver 接口，并提供对应的 get 接口

```java
public CompletableFuture<List<ReviewEntity>> getReviews(
      BookEntity source, DataFetchingEnvironment env) {
    DataLoader<Integer, List<ReviewEntity>> dataLoader = env.getDataLoader(REVIEWS_LOADER);
    return dataLoader.load(source.getId());
  }
```

> 这样我们就能够在 graphql object 返回之前，把外部资源添加到我们的对象中去，从而返回正确的信息



在这里说一下为什么需要提供异步的接口:

1. 性能更加快
2. 不会阻塞到正常的进程
3. 我们只负责方法的定义，框架进行消费(只有框架才知道什么时候所有未完成的数据提供完了，才能决定什么时候是最佳的调用时间)，在resolver 返回之前，获取到异步查询的结果，并赋到返回的信息中来

> The only execution that works with DataLoader is `graphql.execution.AsyncExecutionStrategy`. This is because this execution strategy knows then the most optimal time to dispatch() your load calls is. It does this by deeply tracking how many fields are outstanding and whether they are list values and so on.

## Dependency

- graphql
- graphql data loader
- spring boot
- spring data jpa
- flyway migration
- junit5