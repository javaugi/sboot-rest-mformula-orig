
HTTP response codes are used to indicate success, failure, and other properties about the result of an HTTP request. Regardless of the contents of an HTTP response message body, a client will act according to the response status code.

Table of Contents
1xx: Informational
2xx: Success
3xx: Redirection
4xx: Client error
5xx: Server error
Additional status codes
Takeaway
Each HTTP response is accompanied by a status code, and they are broken into five categories. Each of the response status codes is used to convey general information about the outcome of the request.

There are five primary categories of HTTP response codes, each identifiable by the first digit:

1xx: Informational
An informational response code informs the client that the request is continuing.
100 Continue
101 Switching Protocols
102 Processing
103 Early Hints

2xx: Success
A successful response was received, interpreted corrected, and has been accepted.
200 OK
201 Created
202 Accepted
203 Non-Authoritative Information
204 No Content
205 Reset Content
206 Partial Content
207 Multi-Status
208 Already Reported
218 This Is Fine
226 IM Used

3xx: Redirection
A redirection indicates that further action needs to take place before the request is completed.
300 Multiple Choices
301 Moved Permanently
302 Found
303 See Other
304 Not Modified
305 Use Proxy
306 Switch Proxy
307 Temporary Redirect
308 Permanent Redirect

4xx: Client error
A client error indicates that the request cannot be completed because of an issue with the client, or the syntax of the request.
400 Bad Request
401 Unauthorized
402 Payment Required
403 Forbidden
404 Not Found
405 Method Not Allowed
406 Not Acceptable
407 Proxy Authentication Required
408 Request Timeout
409 Conflict
410 Gone
411 Length Required
412 Precondition Failed
413 Payload Too Large
414 URI Too Long
415 Unsupported Media Type
416 Range Not Satisfiable
417 Expectation Failed
418 I'm a Teapot
419 Page Expired
420 Method Failure or Enhance Your Calm
421 Misdirected Request
422 Unprocessable Entity
423 Locked
424 Failed Dependency
425 Too Early
426 Upgrade Required
428 Precondition Required
429 Too Many Requests
430 HTTP Status Code
431 Request Header Fields Too Large
440 Login Time-Out
444 No Response
449 Retry With
450 Blocked by Windows Parental Controls
451 Unavailable For Legal Reasons
460 Client Closed Connection Prematurely
463 Too Many Forwarded IP Addresses
464 Incompatible Protocol
494 Request Header Too Large
495 SSL Certificate Error
496 SSL Certificate Required
497 HTTP Request Sent to HTTPS Port
498 Invalid Token
499 Token Required or Client Closed Request

5xx: Server error
A server error indicates that the request is valid but cannot be completed because of an issue on the server’s side, such as a lack of available resources.
500 Internal Server Error
501 Not Implemented
502 Bad Gateway
503 Service Unavailable
504 Gateway Timeout
505 HTTP Version Not Supported
506 Variant Also Negotiates
507 Insufficient Storage
508 Loop Detected
509 Bandwidth Limit Exceeded
510 Not Extended
511 Network Authentication Required
520 Web Server Is Returning an Unknown Error
521 Web Server Is Down
522 Connection Timed Out
523 Origin Is Unreachable
524 A Timeout Occurred
525 SSL Handshake Failed
526 Invalid SSL Certificate
527 Railgun Listener to Origin
529 The Service Is Overloaded
530 Site Frozen
561 Unauthorized
598 Network Read Timeout Error
599 Network Connect Timeout Error

Additional status codes
In addition to the five primary categories of HTTP status codes mentioned above, the following status codes can also be encountered on the World Wide Web.

110 Response Is Stale
111 Revalidation Failed
112 Disconnected Operation
113 Heuristic Expiration
199 Miscellaneous Warning
214 Transformation Applied
299 Miscellaneous Persistent Warning
999 Unauthorized

Takeaway
HTTP responses are always accompanied by an HTTP response status code. The first digit of a status code indicates the category, which often indicates 
    whether the request succeeded or failed. Each status code can be a valuable clue when troubleshooting problems between a client and server.
