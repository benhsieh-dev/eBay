package service;

import dto.UserDTO;
import jakarta.validation.Valid;
import model.User;

public interface UserService {

	String addUser(UserDTO userDTO);

	void registerUser(@Valid User user);

}
