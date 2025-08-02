/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
// PostgreSQL function to update search vector automatically
CREATE OR REPLACE FUNCTION update_article_search_vector() RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector := to_tsvector('english', COALESCE(NEW.title, '') || ' ' || COALESCE(NEW.content, ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER article_search_vector_update
BEFORE INSERT OR UPDATE ON articles
FOR EACH ROW EXECUTE FUNCTION update_article_search_vector();
*/

@Repository
public interface PostgreSQLFullTextSearchTableArticleRepo extends JpaRepository<PostgreSQLFullTextSearchTableArticle, Long>{

    //query = database performance
    /*
    @Query(value = "SELECT a FROM PostgreSQLFullTextSearchTableArticle a  " +
           " WHERE to_tsvector('english', a.title || ' ' || a.content) @@ plainto_tsquery('english', :searchVector) " +
           "ORDER BY ts_rank(to_tsvector('english', a.title || ' ' || a.content), plainto_tsquery('english', :searchVector)) DESC")
            // */
    List<PostgreSQLFullTextSearchTableArticle> getArticlesBySearchVector(@Param("searchVector") String searchVector);    
    
    /*
    PostgreSQL Index Recommendation
        -- Add generated column
        ALTER TABLE articles ADD COLUMN search_vector tsvector
        GENERATED ALWAYS AS (to_tsvector('english', coalesce(title, '') || ' ' || coalesce(content, ''))) STORED;

        -- Create index
        CREATE INDEX idx_articles_search ON articles USING gin(search_vector);
    Then the optimized query becomes:    
    */
    
    /*
    @Query(value = "SELECT a FROM PostgreSQLFullTextSearchTableArticle a  " +
           " WHERE search_vector @@ plainto_tsquery('english', :searchVector) " +
           " ORDER BY ts_rank(search_vector, plainto_tsquery('english', :searchVector)) DESC ")
            // */
    List<PostgreSQLFullTextSearchTableArticle> searchArticlesBySearchVector(@Param("searchVector") String searchVector);    
}

/*
Query Components Explained

1. Full-Text Search Components
to_tsvector('english', text) - Creates a search document in TSVECTOR format:
    Parses and normalizes text (lowercases, removes stopwords, stems words)
    'english' specifies the text search configuration (language rules)
    a.title || ' ' || a.content concatenates title and content with a space
plainto_tsquery('english', :query) - Creates a search query in TSQUERY format:
    Parses the user's search term (:query parameter)
    Handles whitespace and logical operators
    'english' applies English language rules
    @@ - The match operator that checks if the TSVECTOR matches the TSQUERY

2. Ranking Function
ts_rank(tsvector, tsquery) - Calculates a relevance score (0-1) based on:
    How often query terms appear
    How close together the terms are
    Whether terms appear in important positions (like titles)

3. Practical Example
If you search for "database performance":
The query will:
    Stem words ("performance" → "perform")
    Remove stopwords if any
    Search for documents containing both "databas" and "perform"

Results will be ordered by:
    Higher rank if terms appear in title
    Higher rank if terms appear close together
    Higher rank if terms appear multiple times
*/