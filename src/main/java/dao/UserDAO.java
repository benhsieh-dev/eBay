package dao;

import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public User save(User user) {
        if (user.getUserId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
        return user;
    }
    
    public User findById(Integer userId) {
        return entityManager.find(User.class, userId);
    }
    
    public User findByUsername(String username) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public User findByEmail(String email) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public User findByUsernameOrEmail(String usernameOrEmail) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "FROM User WHERE username = :credential OR email = :credential", User.class);
            query.setParameter("credential", usernameOrEmail);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery("FROM User", User.class);
        return query.getResultList();
    }
    
    public List<User> findByUserType(User.UserType userType) {
        TypedQuery<User> query = entityManager.createQuery(
            "FROM User WHERE userType = :userType", User.class);
        query.setParameter("userType", userType);
        return query.getResultList();
    }
    
    public List<User> findActiveUsers() {
        TypedQuery<User> query = entityManager.createQuery(
            "FROM User WHERE accountStatus = :status", User.class);
        query.setParameter("status", User.AccountStatus.ACTIVE);
        return query.getResultList();
    }
    
    public boolean existsByUsername(String username) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }
    
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
    
    public void delete(User user) {
        if (entityManager.contains(user)) {
            entityManager.remove(user);
        } else {
            entityManager.remove(entityManager.merge(user));
        }
    }
    
    public void deleteById(Integer userId) {
        User user = findById(userId);
        if (user != null) {
            delete(user);
        }
    }
    
    public User update(User user) {
        return entityManager.merge(user);
    }
    
    public List<User> searchUsers(String searchTerm) {
        TypedQuery<User> query = entityManager.createQuery(
            "FROM User WHERE username LIKE :search OR email LIKE :search OR " +
            "firstName LIKE :search OR lastName LIKE :search", User.class);
        query.setParameter("search", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<User> findTopSellers(int limit) {
        TypedQuery<User> query = entityManager.createQuery(
            "FROM User WHERE userType IN ('SELLER', 'BOTH') " +
            "ORDER BY sellerRating DESC, totalSalesCount DESC", User.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public void updateLastLogin(Integer userId) {
        entityManager.createQuery(
            "UPDATE User SET lastLogin = CURRENT_TIMESTAMP WHERE userId = :userId")
            .setParameter("userId", userId)
            .executeUpdate();
    }
}