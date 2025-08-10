In Spring Boot, there are multiple ways to pass request parameters to a REST controller method like:

@GetMapping("/products")
public ResponseEntity<Collection<Product>> getProducts(...) { ... }

Each option has a different purpose and use case, depending on:

HTTP method (GET vs POST)
    Parameter source (query string, body, headers, path)
    Expected data structure (simple values, objects, files)

🧠 Categories of Request Parameters
Source               Annotation / Class                          Description
✅ Query Parameters	 @RequestParam                               Simple key-value pairs in the URL
✅ Path Variables	 @PathVariable                               Extract values from URL path
✅ Request Body      @RequestBody                                JSON/XML payload in POST/PUT
✅ All Parameters    HttpServletRequest or RequestEntity<T>      Full raw access
✅ Form Data         @ModelAttribute                             Populates POJO from query/form fields
✅ Header Fields     @RequestHeader                              Read specific HTTP headers
✅ Session/Model     Model, ModelMap,                            Used in MVC view apps (not APIs)
                    @SessionAttribute	

🔍 Detailed Breakdown
1. @RequestParam
    For query strings like /products?type=book&maxPrice=20
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestParam String type, @RequestParam(defaultValue = "100") int maxPrice) { ... }
    ✅ Best for: GET parameters, filters, search criteria

2. @PathVariable
    For URL templates like /products/{id}
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) { ... }
    ✅ Best for: Resource identifiers in RESTful URLs

3. @RequestBody
    For POST/PUT payloads, typically JSON:
    @PostMapping("/products")
    public ResponseEntity<Product> create(@RequestBody Product product) { ... }
    ✅ Best for: Complex objects in request bodies
    ❌ Not used with GET (GET should not have a body)

4. @ModelAttribute
    Used for binding query/form params to an object:
    @GetMapping("/products/search")
    public ResponseEntity<?> search(@ModelAttribute ProductFilter filter) { ... }

    public class ProductFilter {
      private String type;
      private int maxPrice;
    }
    ✅ Best for: Form-like input, even in GET requests
    🔸 Similar to @RequestParam, but for binding multiple fields into a POJO

5. @RequestHeader
    Used to extract HTTP headers:
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestHeader("X-Request-ID") String requestId) { ... }
    ✅ Best for: Custom metadata, tokens, correlation IDs

6. HttpServletRequest
    Gives you raw access to query params, headers, cookies, etc.
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(HttpServletRequest request) {
      String type = request.getParameter("type");
      String header = request.getHeader("X-Token");
      ...
    }
    ✅ Best for: Low-level control, not recommended for clean code

7. RequestEntity<T>
    Wraps the entire request, including headers and body.
    @PostMapping("/products")
    public ResponseEntity<?> create(RequestEntity<Product> request) {
      Product body = request.getBody();
      HttpHeaders headers = request.getHeaders();
      ...
    }
    ✅ Best for: APIs that need both body and headers together

8. @RequestPart / MultipartFile
    For file uploads and mixed form-data:
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestPart("file") MultipartFile file) { ... }
    ✅ Best for: File uploads via multipart/form-data

9. Model / ModelMap / ModelAndView
    Mostly for MVC and Thymeleaf view rendering, not REST APIs.
    @GetMapping("/products")
    public String showProducts(Model model) {
      model.addAttribute("products", productService.findAll());
      return "product-list";
    }
    ✅ Best for: Server-side rendered HTML, not JSON REST

When to Use What
Use Case                    Use This
/products?id=3 (GET param)	@RequestParam
/products/3 (path variable)	@PathVariable
Submit JSON via POST/PUT	@RequestBody
Submit form with fields     @ModelAttribute
Need raw headers/params     HttpServletRequest or RequestEntity
Custom header values        @RequestHeader
File upload                 @RequestPart, MultipartFile
MVC/Thymeleaf templates     Model, ModelMap

✅ Sample Controller with Multiple Parameter Types
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(
        @PathVariable Long id,
        @RequestParam(required = false) String locale,
        @RequestHeader("X-Request-ID") String requestId,
        HttpServletRequest request
    ) {
        log.info("Request ID: {}", requestId);
        String agent = request.getHeader("User-Agent");
        ...
        return ResponseEntity.ok(service.get(id));
    }

Let me know your use case — I can show a full example or a mock test question based on these options.

