package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import entity.User;

@EnableJpaRepositories
@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

}
