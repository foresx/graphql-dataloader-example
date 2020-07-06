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
@Table(name = "author")
public class AuthorEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;
  private Integer age;

  //  @Default
  //  @OneToMany(
  //      fetch = FetchType.LAZY,
  //      cascade = CascadeType.ALL,
  //      orphanRemoval = true,
  //      mappedBy = "author")
  //  private Set<BookEntity> books = new HashSet<>();
}
