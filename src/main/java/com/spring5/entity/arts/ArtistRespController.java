/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity.arts;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1r/artists")
public class ArtistRespController {

	/*
	 * REST CRUD Operations and HTTP Status Codes
	 * 
	 * Operation HTTP Method Common Status Codes Description Create POST 201 Created
	 * Resource created successfully. Include Location header with URI of new resource.
	 * 400 Bad Request Malformed input or validation failure. 409 Conflict Resource
	 * already exists (e.g., duplicate key). Read GET 200 OK Resource fetched
	 * successfully. 404 Not Found Resource not found. Update PUT/PATCH 200 OK / 204 No
	 * Content Update successful. Use 200 if response body is returned; 204 if not. 400
	 * Bad Request Malformed data. 404 Not Found Resource does not exist. Delete DELETE
	 * 204 No Content Resource deleted successfully. No response body. 404 Not Found
	 * Resource not found.
	 */

	private final ArtistRepository artistRepository;

	private final ArtistService artistService;

	public ArtistRespController(ArtistRepository artistRepository, ArtistService artistService) {
		this.artistRepository = artistRepository;
		this.artistService = artistService;
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Create POST 201 Created
	 * Resource created successfully. Include Location header with URI of new resource.
	 * 400 Bad Request Malformed input or validation failure. 409 Conflict Resource
	 * already exists (e.g., duplicate key).
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Artist> createArtistByResp(@RequestBody ArtistRequest artistRequest) {
		// Validate request
		/*
		 * if (artistRequest.getFirstName() == null || artistRequest.getLastName() ==
		 * null) { System.out.
		 * println("createArtistByResp throws ResponseStatusException HttpStatus.BAD_REQUEST "
		 * ); throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
		 * "First name and last name are required"); }
		 * 
		 * // Check for duplicate artist if
		 * (artistService.existsByFirstNameAndLastName(artistRequest.getFirstName(),
		 * artistRequest.getLastName())) { System.out.
		 * println("createArtistByResp throws ResponseStatusException HttpStatus.CONFLICT "
		 * ); throw new ResponseStatusException(HttpStatus.CONFLICT,
		 * "Artist already exists"); } //
		 */
		// Create and save artist
		Artist artist = new Artist(artistRequest.getFirstName(), artistRequest.getLastName());
		return new ResponseEntity<>(artistRepository.save(artist), HttpStatus.CREATED);
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Read GET 200 OK Resource
	 * fetched successfully. 404 Not Found Resource not found.
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<Artist>> getAllArtistsByResp() {
		return new ResponseEntity<>(artistRepository.findAll(), HttpStatus.OK);
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Read GET 200 OK Resource
	 * fetched successfully. 404 Not Found Resource not found.
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Artist> getArtistByIdByResp(@PathVariable Long id) {
		return ResponseEntity.ok(artistRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found")));
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Delete DELETE 204 No Content
	 * Resource deleted successfully. No response body. 404 Not Found Resource not found.
	 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> deleteArtistByResp(@PathVariable Long id) {
		if (!artistRepository.existsById(id)) {
			// new ResponseEntity<>(ResponseEntity.noContent().build(),
			// HttpStatus.NOT_FOUND);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
		}
		artistRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
