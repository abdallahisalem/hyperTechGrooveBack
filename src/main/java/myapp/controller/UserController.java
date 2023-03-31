package myapp.controller;

import java.util.List;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
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

@RestController
@CrossOrigin(exposedHeaders="Access-Control-Allow-Origin")	
@RequestMapping("/api/user")
public class UserController {

	private final UserRepository userRepository;
	private final UserService userService;

	public UserController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@GetMapping
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User _user = userService.createUser(user);
		return ResponseEntity.ok(_user);
	}

	@GetMapping("/{username}/{password}")
	public ResponseEntity<User> login(@PathVariable String username, @PathVariable String password) {
		User user = userService.login(username, password);
		return ResponseEntity.ok(user);
	}

	@PutMapping("/{id}")
	public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

		user.setUsername(userDetails.getUsername());
		user.setEmail(userDetails.getEmail());
		user.setPassword(userDetails.getPassword());
		user.setFirstname(userDetails.getFirstname());
		user.setLastname(userDetails.getLastname());

		User updatedUser = userRepository.save(user);
		return updatedUser;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

		userRepository.delete(user);
		return ResponseEntity.ok().build();
	}
}
