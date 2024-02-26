package myapp.controller;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import myapp.entities.User;
import myapp.repository.UserRepository;
import myapp.services.UserService;
import myapp.model.UserResponse;

@RestController
@CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
@RequestMapping("/api/user")
public class UserController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserController(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		try {
			// Hash the password before saving
	        user.setPassword(passwordEncoder.encode(user.getPassword()));

			User _user = userRepository.save(user);
			return ResponseEntity.ok(_user);
		} catch (DataIntegrityViolationException e) {
			// Throw a custom exception with a specific message
			throw new DuplicateKeyException("Duplicate entry for username or email");
		}
	}

	@PostMapping("/{identifier}/{password}")
	public ResponseEntity<UserResponse> login(@PathVariable String identifier, @PathVariable String password) {
		User user = userRepository.findByEmail(identifier).orElse(userRepository.findByUsername(identifier).orElseThrow(
				() -> new ResourceNotFoundException("User not found with username or email: " + identifier)));

		if (!passwordEncoder.matches(password, user.getPassword())) {
		    throw new BadCredentialsException("Incorrect password");
		}


	    return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
	            user.getFirstname(), user.getLastname()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody User userDetails) {

		User user = userRepository.findById(id)

				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id + "fdf"));
		if (!passwordEncoder.matches(userDetails.getPassword(), user.getPassword())) {
		    throw new BadCredentialsException("Incorrect password");
		}
		try { // Handle potential duplicate entry exception during update
			user.setUsername(userDetails.getUsername());
			user.setEmail(userDetails.getEmail());
			user.setFirstname(userDetails.getFirstname());
			user.setLastname(userDetails.getLastname());
			User updatedUser = userRepository.save(user);
			
		
			UserResponse userResponse = new UserResponse(updatedUser.getId(), updatedUser.getUsername(),
					updatedUser.getEmail(), updatedUser.getFirstname(), updatedUser.getLastname());
			return ResponseEntity.ok(userResponse);
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateKeyException("Duplicate entry for username or email");
		}
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

		userRepository.delete(user);
		return ResponseEntity.ok().build();
	}
}
