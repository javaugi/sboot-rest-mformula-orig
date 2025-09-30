/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

// import static com.mongodb.client.model.Filters.where;
import com.spring5.entity.Product;
import com.spring5.repository.ProductRepository;
import com.spring5.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * @author javaugi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restproduct")
public class ProductRestController {

    @Value("${spring.ai.deepseek.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.deepseek.openai.api-key}")
    private String apiKey;

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final R2dbcEntityTemplate template;
    private final EntityManager em;

    @Transactional
    @GetMapping("/emproduct")
    public Product getProduct(Long id) {
        return em.find(Product.class, id); // Blocks thread
    }

    /*
  1. Blocking (JPA/Hibernate)
  How It Works:
  Thread-Per-Request Model
      Each HTTP request gets a dedicated thread from the pool (e.g., Tomcat's 200 threads by default).
      Thread blocks until the database responds.
      Database Interaction
      Uses JDBC (Java Database Connectivity), which is synchronous.
      Hibernate translates objects to SQL, executes queries, and maps results.
      Connection Pooling
      Tools like HikariCP manage connections (e.g., 10 connections shared across threads).

  Pros:
  ✅ Mature (20+ years of optimizations)
  ✅ Rich features (caching, lazy loading, dirty checking)
  ✅ Easy to use (declarative @Transactional)

  Cons:
  ❌ Thread starvation under high load
  ❌ Wasted resources (threads idle while waiting for DB)
  ❌ No backpressure support
     */
    @GetMapping("/templateproduct")
    public Mono<Product> getProductByTemplate(Long id) {
        return template
                .select(Product.class)
                .from("products")
                // .matching(where("id").is(id))
                .one(); // Returns Mono (no thread blocking)
    }

    /*
  2. Non-Blocking (R2DBC)
      How It Works:
          Event Loop Model
          Uses a small number of event-loop threads (e.g., 4 cores = 4 threads).
          Never blocks threads; switches tasks while waiting for I/O.

      Database Interaction
          Uses Reactive Streams (Publisher-Subscriber model).
          R2DBC drivers speak the database protocol directly (no JDBC).
          Backpressure
              Subscriber controls data flow (e.g., "send me 10 rows at a time").

  Pros:
  ✅ High scalability (handles 10K+ concurrent connections)
  ✅ Efficient resource usage (no thread starvation)
  ✅ Native reactive integration (WebFlux, WebClient)

  Cons:
  ❌ Limited ORM features (no lazy loading, caching)
  ❌ Steeper learning curve (reactive programming)
  ❌ Fewer database vendors supported

  Key Differences
  Aspect              JPA/Hibernate (Blocking)        R2DBC (Non-blocking)
  Thread Model        1 thread per request            Few threads, non-blocking
  Database Protocol	JDBC (synchronous)              R2DBC (asynchronous)
  Resource Usage      High (threads, memory)          Low (scales with connections)
  Latency             Good for low concurrency        Better for high concurrency
  ORM Features        Full-featured (caching, etc.)	Bare-metal SQL-like experience
  Error Handling      try-catch                       Operators (onErrorResume)
  Transactions        @Transactional                  Declarative reactive pipelines

  When to Use Which?
      Use JPA/Hibernate if:
          You need lazy loading, caching, or complex mappings.
          Your app is CRUD-heavy with moderate traffic.
          Your team knows SQL/ORM but not reactive programming.
      Use R2DBC if:
          You need to handle 10K+ concurrent users (e.g., real-time apps).
          You're already using WebFlux and want end-to-end reactivity.
          Your queries are simple (or you prefer SQL over ORM magic).

  Under the Hood
      JPA Blocking Flow:
          HTTP Request → Tomcat Thread → Hibernate → JDBC → Database
                      (Blocks thread)           (Blocks thread)
      R2DBC Non-blocking Flow:
          HTTP Request → Event Loop → R2DBC → Database
                      (Thread freed)   (Callback when ready)
  Performance Example
      JPA:    100 requests × 50ms DB query = 5s total     (with 10 threads).
      R2DBC:  100 requests × 50ms DB query = ~50ms total  (same 4 threads).

  Hybrid Approach
      You can mix both in one app:

      java
      @Transactional // Blocking JPA
      public void updateInventory() {
         // ...
      }

      @GetMapping("/stream")
      public Flux<Product> streamProducts() { // Non-blocking R2DBC
         return r2dbcTemplate.select(Product.class).all();
      }
      Use @EnableAsync + @Async to bridge blocking calls to reactive flows when needed.

  Final Advice
      Start with JPA for most apps (it’s simpler).
      Use R2DBC only if you measure a scalability bottleneck.
  Reactive shines for:
      Streaming APIs
      Microservices with high I/O parallelism
      Real-time dashboards
     */
    // to display all products at localhost:8080
    // to see database values at localhost:8080/h2-console
    @GetMapping
    public ResponseEntity<Collection<Product>> getAllProducts(HttpServletRequest request) {
        List<Product> products = productService.findAll();
        String name = request.getParameter("name");
        String header = request.getHeader("X-Token");

        if (name == null || name.isEmpty()) {
            return ResponseEntity.ok(products);
        }

        products
                = products.stream().filter(p -> name.contains(p.getName())).collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    // Browser Caching: Leverage HTTP caching headers.   (Handled server-side via headers)
    @GetMapping("/cached")
    public ResponseEntity<Collection<Product>> getProducts(
            @RequestHeader(value = "name", required = false) List<String> productNames,
            HttpServletRequest request) {

        List<Product> products = productService.findAll();

        // Apply filtering if name headers exist
        if (productNames != null && !productNames.isEmpty()) {
            products
                    = products.stream()
                            .filter(p -> productNames.contains(p.getName()))
                            .collect(Collectors.toList());
        }

        // Create response headers with caching directives
        HttpHeaders headers = new HttpHeaders();

        // Determine cache strategy based on request
        if (isStaticAssetRequest(request)) {
            // CSS/JS/images - long-term caching
            headers.setCacheControl("public, max-age=31536000, immutable");
            headers.setPragma("cache"); // For HTTP/1.0 backwards compatibility
        } else if (isApiRequest(request)) {
            // API responses - short-term caching
            headers.setCacheControl("public, max-age=60");
            headers.set("Vary", "Accept-Encoding"); // Differentiate compressed responses
        } else {
            // HTML pages - no caching
            headers.setCacheControl("no-cache");
            headers.setPragma("no-cache");
            headers.setExpires(0);
        }

        return ResponseEntity.ok().headers(headers).body(products);
    }

    private boolean isStaticAssetRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.endsWith(".js")
                || path.endsWith(".css")
                || path.endsWith(".png")
                || path.endsWith(".webp");
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }

    /*
  Key Improvements:
  Proper Cache Headers:
      Static assets: Cache-Control: public, max-age=31536000, immutable
      API responses: Cache-Control: public, max-age=60
      HTML: Cache-Control: no-cache
  Header Processing:
      Simplified header access with @RequestHeader
      Added null-check for product names
  Cache Strategy:
      Added helper methods to determine request type
      Different caching policies for different resources
      Additional Best Practices:
      Added Vary header for compressed responses

  HTTP/1.0 compatibility with Pragma
  Explicit expiration with Expires

  For Global Configuration (Spring Boot):
      Add this to your configuration class for static resources:

      java
      @Configuration
      public class CacheControlConfig implements WebMvcConfigurer {

          @Override
          public void addResourceHandlers(ResourceHandlerRegistry registry) {
              registry.addResourceHandler("/static/**")
                      .addResourceLocations("classpath:/static/")
                      .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)
                      .resourceChain(true)
                      .addResolver(new PathResourceResolver());
          }
      }
      For Servlet Container (Tomcat/etc):
      Add to application.properties:

      properties
      # Cache static resources
      spring.resources.cache.cachecontrol.max-age=365d
      spring.resources.cache.cachecontrol.public=true
      spring.resources.cache.cachecontrol.immutable=true
  This implementation gives you fine-grained control over caching while maintaining clean separation of concerns. The caching strategy
          automatically adapts to different request types while following HTTP caching best practices.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
        /*
    Optional<Product> productOptional = productRepository.findById(id);
    if (productOptional.isPresent()) {
        return ResponseEntity.ok(productOptional.get());
    } else {
        return ResponseEntity.notFound().build();
        //return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        // 404 Not Found if the product doesn't exist
    }
    // */
        // return ResponseEntity.ok(productOptional.orElse(null));
    }

    @GetMapping("/apiproducts/{id}")
    public ResponseEntity<Product> getProductApiById(@PathVariable Long id) {
        return productRepository
                .findById(id)
                .map(
                        product
                        -> ResponseEntity.ok()
                                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
                                .body(product))
                .orElse(ResponseEntity.notFound().build());
    }

    /*
  1. public ResponseEntity<Product> getProductById(@PathVariable Long id)
  Pros:
  ✅ Full HTTP Control - Customize status codes, headers, and body
  ✅ Explicit API Contract - Clearly communicates possible responses
  ✅ Flexibility - Can return different status codes (404, 403, etc.)
  ✅ Testing - Easier to verify HTTP details in tests
  ✅ Cache Control - Easier to add headers like Cache-Control

  Cons:
  ❌ Verbose - More boilerplate code
  ❌ Less Readable - Clutters simple endpoints
     */
    @GetMapping("/internalproducts/{id}")
    public Product getProductInternalById(@PathVariable Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /*
  2. public Product getProductById(@PathVariable Long id)
  Pros:
  ✅ Concise - Less boilerplate for simple cases
  ✅ Readable - Straightforward for CRUD endpoints
  ✅ Default Status Codes - Spring handles 200 (OK) and 404 (Not Found) automatically

  Cons:
  ❌ Limited Control - Harder to customize headers/status codes
  ❌ Implicit Behavior - Relies on Spring defaults (may surprise developers)
  ❌ No Fine-Grained Error Handling - Can't easily return 403/401 without exceptions
     */
 /*
  Key Differences
      Feature                 ResponseEntity<Product>         Product (Direct Return)
      HTTP Status Control     Full control (200, 404, etc.)	Automatic (200 OK or 404)
      Headers                 Customizable                    Limited (via @ResponseHeader)
      Boilerplate             More                            Less
      Error Handling          Flexible (any HTTP status)      Mostly via exceptions
      Cache Control           Easy                            Requires annotations
  When to Use Each
  Use ResponseEntity when you need:
      Custom HTTP status codes (e.g., 202 ACCEPTED)
      Special headers (e.g., caching, rate limiting)
      Fine-grained error handling (e.g., 403 FORBIDDEN)

  Use direct return (Product) when:
      You're building simple CRUD APIs
      Default HTTP behavior is sufficient
      Readability is a priority
  Best Practice
      For public APIs, prefer ResponseEntity for explicit contracts.
      For internal APIs, direct return is often cleaner.
      Consistency matters most—pick one style per project.
     */
    @GetMapping("/restProducts")
    public ResponseEntity<Collection<Product>> listProducts(
            HttpServletRequest request, ModelMap modelMap) {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/restProducts2")
    public ResponseEntity<Collection<Product>> listProducts2(
            org.springframework.http.RequestEntity<Product> request) {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(RequestEntity<Product> request) {
        Product body = request.getBody();
        HttpHeaders headers = request.getHeaders();
        Product product = productRepository.save(body);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, RequestEntity<Product> request) {
        Product updates = request.getBody();
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            BeanUtils.copyProperties(updates, product, new String[]{"id"});
            product = productRepository.save(product);
            return ResponseEntity.ok(product);
        } else {
            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(
            org.springframework.http.RequestEntity<Product> request) {
        Product body = request.getBody();
        HttpHeaders headers = request.getHeaders();
        Product product = productRepository.save(body);
        return ResponseEntity.ok(product);
    }

    @RequestMapping(
            value = "/heavyresource/{id}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> partialUpdateGeneric(
            @RequestBody Product productUpdates, @PathVariable("id") Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        // *
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            BeanUtils.copyProperties(productUpdates, product, new String[]{"id"});
            product = productRepository.save(product);
            return ResponseEntity.ok(product);
        } else {
            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
        // */
        // return ResponseEntity.ok(productOptional.orElse(null));
    }

    @DeleteMapping("/{id}") // Map DELETE requests to /products/{id}
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            productRepository.deleteById(id); // Use deleteById for deleting by ID
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } else {
            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(
            org.springframework.http.RequestEntity<Product> request) {
        Optional<Product> productOptional = Optional.empty();
        long id = 0;
        if (request.getBody() != null) {
            id = request.getBody().getId();
        }
        if (id > 0) {
            productOptional = productRepository.findById(id);
        }

        if (id > 0 && productOptional.isPresent()) {
            productRepository.deleteById(id); // Use deleteById for deleting by ID
            ResponseEntity.noContent().build();
            ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } else {
            ResponseEntity.notFound().build();
            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
    }

    @GetMapping("/products")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product-list";
    }

    @PostMapping("/uploadfile")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "pdf") String format)
            throws Exception {
        // Step 1: Send image to OpenAI
        String extractedText = callOpenAIVision(file);

        // Step 2: Generate file
        String filename = "converted_" + System.currentTimeMillis();
        Path output;
        if (format.equalsIgnoreCase("docx")) {
            output = generateDocx(extractedText, filename);
        } else {
            output = generatePdf(extractedText, filename);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + output.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(Files.readAllBytes(output));
    }

    private String callOpenAIVision(MultipartFile file) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(baseUrl);
        request.setHeader("Authorization", "Bearer " + apiKey);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(
                "file", file.getBytes(), ContentType.IMAGE_JPEG, file.getOriginalFilename());

        // For GPT-4-vision you structure the request like this:
        String jsonPayload
                = """
        {
          "model": "gpt-4o",
          "messages": [
            {
              "role": "user",
              "content": [
                {
                  "type": "image_url",
                  "image_url": {
                    "url": "data:image/jpeg;base64,"""
                + Base64.getEncoder().encodeToString(file.getBytes())
                + """
                  }
                },
                {
                  "type": "text",
                  "text": "Extract the readable text from this scanned image."
                }
              ]
            }
          ],
          "max_tokens": 2048
        }
    """;

        request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());

        // Parse JSON response
        JSONObject json = new JSONObject(responseBody);
        return json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }

    private Path generateDocx(String text, String filename) throws IOException {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(text);

        Path output = Files.createTempFile(filename, ".docx");
        try (FileOutputStream out = new FileOutputStream(output.toFile())) {
            doc.write(out);
        }
        return output;
    }

    private Path generatePdf(String text, String filename) throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        PDPageContentStream content = new PDPageContentStream(doc, page);
        content.beginText();

        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        content.setFont(font, 12);
        content.setLeading(14.5f);
        content.newLineAtOffset(50, 700);

        for (String line : text.split("\n")) {
            content.showText(line);
            content.newLine();
        }

        content.endText();
        content.close();

        Path output = Files.createTempFile(filename, ".pdf");
        doc.save(output.toFile());
        doc.close();

        return output;
    }

    /*
  @GetMapping("/{id}")
  public ResponseEntity<String> getById(@PathVariable Long id) {
      return ResponseEntity.ok("Product ID: " + id);
  }
  // */
    @GetMapping
    public ResponseEntity<String> getByType(@RequestParam String type) {
        return ResponseEntity.ok("Product type: " + type);
    }

    /*
  @PostMapping
  public ResponseEntity<String> create(@RequestBody ProductDto dto) {
      return ResponseEntity.ok("Created: " + dto.getName());
  }
  // */
    @GetMapping("/headers")
    public ResponseEntity<String> headerTest(@RequestHeader("X-Request-ID") String reqId) {
        return ResponseEntity.ok("Header: " + reqId);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok("Uploaded: " + file.getOriginalFilename());
    }
}

/*
In REST, the PATCH HTTP method is used for partial updates of a resource, while the PUT method is used for complete replacements of a
    resource. PUT replaces the entire resource with the provided data, while PATCH only updates the specific fields specified in the request body.
Elaboration:
PUT:
        Replaces the entire resource with a new version.
        Expects the entire resource data to be provided in the request body.
        Idempotent: Multiple identical PUT requests have the same effect as a single one.
PATCH:
        Applies partial updates to a resource.
        Only the fields to be modified are included in the request body.
        Not necessarily idempotent: Repeated PATCH requests can lead to different results depending on the order of operations.
Example:
    If you want to update the email address of a user, you would use a PATCH request, sending only the new email address. A PUT request
        would require sending the entire user profile, including the unchanged fields, according to GeeksForGeeks.
    In essence, use PUT when you need to completely replace a resource, and PATCH when you only need to modify specific parts of it.
 */
