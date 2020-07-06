package com.example.demo.repository;

import com.example.demo.entity.ReviewEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

  Set<ReviewEntity> findAllByBookIdIn(Set<Integer> bookIds);
}
