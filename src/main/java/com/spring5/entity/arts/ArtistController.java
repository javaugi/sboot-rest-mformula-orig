/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity.arts;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/artists")
public class ArtistController {

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

	public ArtistController(ArtistRepository artistRepository) {
		this.artistRepository = artistRepository;
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Create POST 201 Created
	 * Resource created successfully. Include Location header with URI of new resource.
	 * 400 Bad Request Malformed input or validation failure. 409 Conflict Resource
	 * already exists (e.g., duplicate key).
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Artist createArtist(@RequestBody ArtistRequest artistRequest) {
		Artist artist = new Artist(artistRequest.getFirstName(), artistRequest.getLastName());
		return artistRepository.save(artist);
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Read GET 200 OK Resource
	 * fetched successfully. 404 Not Found Resource not found.
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Artist> getAllArtists() {
		return artistRepository.findAll();
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Read GET 200 OK Resource
	 * fetched successfully. 404 Not Found Resource not found.
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Artist getArtistById(@PathVariable Long id) {
		return artistRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Delete DELETE 204 No Content
	 * Resource deleted successfully. No response body. 404 Not Found Resource not found.
	 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteArtist(@PathVariable Long id) {
		if (!artistRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
		}
		artistRepository.deleteById(id);
	}

}
