/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity.arts;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1bsv/artists")
public class ArtistByServiceController {

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

	private final ArtistService artistService;

	public ArtistByServiceController(ArtistService artistService) {
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
	public Artist createArtistByService(@RequestBody ArtistRequest artistRequest) {
		return artistService.createArtist(artistRequest);
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Read GET 200 OK Resource
	 * fetched successfully. 404 Not Found Resource not found.
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Artist> getAllArtistsByService() {
		return artistService.getAllArtists();
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Read GET 200 OK Resource
	 * fetched successfully. 404 Not Found Resource not found.
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Artist getArtistByIdByService(@PathVariable Long id) {
		return artistService.getArtistById(id);
	}

	/*
	 * Operation HTTP Method Common Status Codes Description Delete DELETE 204 No Content
	 * Resource deleted successfully. No response body. 404 Not Found Resource not found.
	 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteArtistByService(@PathVariable Long id) {
		artistService.deleteArtist(id);
	}

}
