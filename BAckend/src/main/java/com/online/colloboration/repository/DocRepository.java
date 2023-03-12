package com.online.colloboration.repository;

import com.online.colloboration.models.Doc;
import com.online.colloboration.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocRepository extends JpaRepository<Doc, Long> {
}
