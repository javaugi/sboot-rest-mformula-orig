/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

// Controller

import com.spring5.entity.Book;
import com.spring5.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    //Constructor Injection is Generally Preferred   
    //1. Immutability
    //2. Clear Dependencies & Guaranteed Availability
    //3. Improved Testability:
    //4. Avoids Circular Dependencies (Early Detection): 
    //5. Less "Magic": It's a standard Java way of creating objects with their requirements.

    private final BookService service;
    
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(service.findAll());
    }
} 
/*
1. Immutability: Dependencies can be declared as final fields. This means once the bean is constructed, its core dependencies cannot be changed, leading to more robust and thread-safe objects.
    private final MyDependency myDependency;

    // @Autowired // Optional on constructor if only one constructor
    public MyService(MyDependency myDependency) {
        this.myDependency = myDependency;
    }
2. Clear Dependencies & Guaranteed Availability: When you look at the constructor, you immediately see all the required dependencies for the class to function. The object cannot be instantiated by Spring unless all its constructor arguments (dependencies) are resolved and provided. This prevents NullPointerExceptions that could occur if a field-injected dependency isn't available.
3. Improved Testability: It's very easy to unit test a class that uses constructor injection. You can simply instantiate it manually and pass in mock or stub implementations of its dependencies without needing a Spring context or reflection.
    MyDependency mockDependency = Mockito.mock(MyDependency.class);
    MyService serviceToTest = new MyService(mockDependency);
    // ... proceed with test
    4. Avoids Circular Dependencies (Early Detection): Constructor injection helps Spring detect circular dependencies (e.g., Bean A depends on Bean B, and Bean B depends on Bean A) during application startup, leading to a BeanCurrentlyInCreationException. While an error, it's better to catch this early than to encounter unexpected behavior at runtime.
    5. Less "Magic": It's a standard Java way of creating objects with their requirements.
*/