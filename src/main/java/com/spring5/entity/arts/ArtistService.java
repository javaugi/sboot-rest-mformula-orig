/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity.arts;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;

	public boolean existsByFirstNameAndLastName(String firstName, String lastName) {
		Optional<Artist> opt = artistRepository.findByFirstNameAndLastName(firstName, lastName);
		return opt.isPresent() && opt.get().getId() != null && opt.get().getId() > 0;
	}

	public boolean existsById(Long id) {
		return artistRepository.existsById(id);
	}

	public Artist createArtist(@RequestBody ArtistRequest artistRequest) {
		// Validate input
		/*
		 * if (artistRequest.getFirstName() == null || artistRequest.getLastName() ==
		 * null) { throw new BadRequestException("First name and last name are required");
		 * }
		 * 
		 * // Check for duplicates if
		 * (existsByFirstNameAndLastName(artistRequest.getFirstName(),
		 * artistRequest.getLastName())) { throw new
		 * ConflictException("Artist already exists"); } //
		 */

		// Create and save
		Artist artist = new Artist(artistRequest.getFirstName(), artistRequest.getLastName());
		return artistRepository.save(artist);
	}

	public List<Artist> getAllArtists() {
		return artistRepository.findAll();
	}

	public Artist getArtistById(@PathVariable Long id) {
		return artistRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));
	}

	public void deleteArtist(@PathVariable Long id) {
		if (!artistRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
		}
		artistRepository.deleteById(id);
	}

}
