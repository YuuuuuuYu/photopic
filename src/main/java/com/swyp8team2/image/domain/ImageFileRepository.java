package com.swyp8team2.image.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findByIdIn(List<Long> bestPickedImageIds);
}
