package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book")
public class BookEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String code;
  private String name;
  private String description;
  private Integer authorId;
  //  @JoinColumn(name = "author_id")
  //  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  //  private AuthorEntity author;
  //
  //  @Default
  //  @OneToMany(
  //      fetch = FetchType.LAZY,
  //      cascade = CascadeType.ALL,
  //      orphanRemoval = true,
  //      mappedBy = "book")
  //  private Set<ReviewEntity> reviews = new HashSet<>();
}
