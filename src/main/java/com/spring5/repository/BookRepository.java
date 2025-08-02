/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.entity.Book;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// 2. Using native query with query hints
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByAuthor(@Param("author") String author);
    
    List<BookTitleProjection> findBy();
    
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "50"))
    @Query("SELECT b FROM Book b WHERE b.author = :author")
    List<Book> findByAuthorWithHint(@Param("author") String author);
    
    @Query(value = "SELECT * FROM get_books_by_author(:author, :page, :size)",  nativeQuery = true)
    List<Book> findBooksByAuthorWithPagination(@Param("author") String author, @Param("page") int page, @Param("size") int size);
}
