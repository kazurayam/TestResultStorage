package com.kazurayam.materials.view

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.kazurayam.materials.repository.RepositoryRoot
import com.kazurayam.materials.repository.RepositoryVisitor

import groovy.xml.MarkupBuilder

class IndexerConcise extends IndexerBase {

    static Logger logger_ = LoggerFactory.getLogger(IndexerConcise.class)

    @Override
    RepositoryVisitor createRepositoryVisitorGeneratingHtmlDivs(RepositoryRoot repoRoot, MarkupBuilder mb) {
        return new RepositoryVisitorGeneratingHtmlDivsAsModalConcise(repoRoot, mb)
    }

}
