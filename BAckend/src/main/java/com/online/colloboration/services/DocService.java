package com.online.colloboration.services;

import com.online.colloboration.models.Doc;
import com.online.colloboration.repository.DocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DocService {
    @Autowired
    private DocRepository docRepository;


    public Doc createDoc(Doc doc) {
        return docRepository.save(doc);
    }


    public Doc getDocById(Long id) {
        return docRepository.findById(id).orElse(null);
    }

    public void saveDoc(Doc doc) {
        docRepository.save(doc);
    }

    public void deleteDocu(Doc doc) {
        docRepository.delete(doc);
    }
}
