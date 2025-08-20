package dao;

import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public User save(User user) {
        getCurrentSession().saveOrUpdate(user);
        return user;
    }
    
    public User findById(Integer userId) {
        return getCurrentSession().get(User.class, userId);
    }
    
    public User findByUsername(String username) {
        Query<User> query = getCurrentSession().createQuery(
            "FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }
    
    public User findByEmail(String email) {
        Query<User> query = getCurrentSession().createQuery(
            "FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }
    
    public User findByUsernameOrEmail(String usernameOrEmail) {
        Query<User> query = getCurrentSession().createQuery(
            "FROM User WHERE username = :credential OR email = :credential", User.class);
        query.setParameter("credential", usernameOrEmail);
        return query.uniqueResult();
    }
    
    public List<User> findAll() {
        Query<User> query = getCurrentSession().createQuery("FROM User", User.class);
        return query.getResultList();
    }
    
    public List<User> findByUserType(User.UserType userType) {
        Query<User> query = getCurrentSession().createQuery(
            "FROM User WHERE userType = :userType", User.class);
        query.setParameter("userType", userType);
        return query.getResultList();
    }
    
    public List<User> findActiveUsers() {
        Query<User> query = getCurrentSession().createQuery(
            "FROM User WHERE accountStatus = :status", User.class);
        query.setParameter("status", User.AccountStatus.ACTIVE);
        return query.getResultList();
    }
    
    public boolean existsByUsername(String username) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class);
        query.setParameter("username", username);
        return query.uniqueResult() > 0;
    }
    
    public boolean existsByEmail(String email) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.uniqueResult() > 0;
    }
    
    public void delete(User user) {
        getCurrentSession().delete(user);
    }
    
    public void deleteById(Integer userId) {
        User user = findById(userId);
        if (user != null) {
            delete(user);
        }
    }
    
    public User update(User user) {
        return (User) getCurrentSession().merge(user);
    }
    
    public List<User> searchUsers(String searchTerm) {
        Query<User> query = getCurrentSession().createQuery(
            "FROM User WHERE username LIKE :search OR email LIKE :search OR " +
            "firstName LIKE :search OR lastName LIKE :search", User.class);
        query.setParameter("search", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<User> findTopSellers(int limit) {
        Query<User> query = getCurrentSession().createQuery(
            "FROM User WHERE userType IN ('SELLER', 'BOTH') " +
            "ORDER BY sellerRating DESC, totalSalesCount DESC", User.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public void updateLastLogin(Integer userId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE User SET lastLogin = CURRENT_TIMESTAMP WHERE userId = :userId");
        query.setParameter("userId", userId);
        query.executeUpdate();
    }
}