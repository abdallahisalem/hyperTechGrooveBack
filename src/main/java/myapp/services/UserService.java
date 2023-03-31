package myapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import myapp.entities.User;
import myapp.repository.UserRepository;

@Service
public class UserService {
	private UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User login(String identifier, String password) {
		User user = userRepository.findByUsername(identifier)
				.orElse(userRepository.findByEmail(identifier)
				.orElseThrow(
				() -> new ResourceNotFoundException(
						"User not found with username or email: " + identifier)));
		if (password == user.getPassword()) {
			return user;
		} else {
			throw new BadCredentialsException("Incorrect password");
		}
	}
	
	public User createUser(User user) {
		return userRepository.save(user);
	}
}
