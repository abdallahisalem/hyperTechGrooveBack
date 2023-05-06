package myapp.controller;

import java.util.List;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

	public UserController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User _user = userRepository.save(user);
		return ResponseEntity.ok(_user);
	}

	@PostMapping("/{identifier}/{password}")
	public ResponseEntity<UserResponse> login(@PathVariable String identifier, @PathVariable String password) {
		User user = userRepository.findByEmail(identifier).orElse(userRepository.findByUsername(identifier).orElseThrow(
				() -> new ResourceNotFoundException("User not found with username or email: " + identifier)));

		if (!password.equals(user.getPassword())) {
			throw new BadCredentialsException("Incorrect password");
		}

		UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
				user.getFirstname(), user.getLastname());

		return ResponseEntity.ok(userResponse);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id + "fdf"));
		if (!userDetails.getPassword().equals(user.getPassword())) {
			throw new BadCredentialsException("Incorrect password");
		}
		user.setUsername(userDetails.getUsername());
		user.setEmail(userDetails.getEmail());
		user.setFirstname(userDetails.getFirstname());
		user.setLastname(userDetails.getLastname());
		User updatedUser = userRepository.save(user);
		UserResponse userResponse = new UserResponse(updatedUser.getId(), updatedUser.getUsername(),
				updatedUser.getEmail(), updatedUser.getFirstname(), updatedUser.getLastname());
		return ResponseEntity.ok(userResponse);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

		userRepository.delete(user);
		return ResponseEntity.ok().build();
	}
}
