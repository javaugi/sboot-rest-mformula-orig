/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

// import io.hypersistence.utils.hibernate.type.basic.PostgreSQLTSVectorType;
// import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

// import org.hibernate.annotations.Type;
// import org.hibernate.annotations.TypeDef;

/*
Advanced PostgreSQL Questions
7. How would you implement full-text search in PostgreSQL with Spring Boot?
Detailed Description:
    PostgreSQL provides powerful full-text search capabilities using tsvector and tsquery types, with support for ranking,
    highlighting, and various search operators.
 */
@Data
@Builder(toBuilder = true)
@Entity
// @Table(name = "ts_articles")
// @TypeDef(name = "tsvector", typeClass = PostgreSQLTSVectorType.class)
public class PostgreSQLFullTextSearchTableArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    // @Column(columnDefinition = "tsvector")
    // @Type(type = "tsvector")
    private String searchVector; // used by tsquery
}
/*
Yes, tsvector and tsquery are PostgreSQL data types specifically designed to support full-text search.
1. tsvector: Represents a document or text in a format optimized for searching. It's a sorted list of unique lexemes
    (normalized words) with optional position and weight information.
2. tsquery: Represents a text search query, containing lexemes combined using Boolean operators like & (AND), | (OR), and ! (NOT),
    as well as the phrase search operator <-> (FOLLOWED BY).
These data types are fundamental to PostgreSQL's full-text search functionality, allowing efficient and flexible text searches
    within your database. You can use functions like to_tsvector() and to_tsquery() to convert text strings into these data types
    and then use the @@ operator to perform matches between them.
 */
 /*
In PostgreSQL, tsvector and tsquery are specialized data types designed to facilitate efficient full-text searching.
tsvector:
    1. Represents a document in a format optimized for text search.
    2. It's a sorted list of distinct lexemes, which are normalized forms of words (e.g., "cats," "watched," and "watching" all become
        the lexeme "watch").
    3. tsvector values can optionally include the position of the lexeme in the original document and a weight to denote importance
        (like if a word was in the title or body).
    4. To convert raw text into a tsvector, you typically use the to_tsvector function, which performs normalization and removes
        common "stop words".
tsquery:
    1. Represents a search query for full-text searches.
    2. It stores lexemes that are to be searched for within a tsvector.
    3. tsquery supports Boolean operators (& for AND, | for OR, ! for NOT) and the phrase search operator <-> (FOLLOWED BY) to
        define the search logic.
    4. You use the to_tsquery function (or similar functions like plainto_tsquery, phraseto_tsquery, websearch_to_tsquery) to convert
        user-supplied text into a tsquery.
    5. These functions perform normalization and stop word removal, similar to to_tsvector.
In summary:
    1. tsvector is the optimized representation of the text document you want to search,
    2. tsquery is the optimized representation of the user's search query.
    3. You use the @@ operator to check if a tsvector matches a tsquery. This comparison efficiently determines if a document
        contains the specified search terms, taking into account normalized word forms and the defined search operators
 */
