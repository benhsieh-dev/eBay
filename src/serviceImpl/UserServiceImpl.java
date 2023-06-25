package serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dto.UserDTO;
import entity.User;
import repository.UserRepo;
import service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Override
	public String addUser(UserDTO userDTO) {

		User user = new User(

				userDTO.getUserId(), userDTO.getUsername(), userDTO.getAddress(), userDTO.getMobile()

		);

		userRepo.save(user);

		return user.getUsername();
	}

}
