package dao;

import entity.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class CategoryDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        try {
            return sessionFactory.getCurrentSession();
        } catch (Exception e) {
            return sessionFactory.openSession();
        }
    }
    
    public Category save(Category category) {
        getCurrentSession().saveOrUpdate(category);
        return category;
    }
    
    public Category findById(Integer categoryId) {
        return getCurrentSession().get(Category.class, categoryId);
    }
    
    public Category findByName(String categoryName) {
        Query<Category> query = getCurrentSession().createQuery(
            "FROM Category WHERE categoryName = :name", Category.class);
        query.setParameter("name", categoryName);
        return query.uniqueResult();
    }
    
    public List<Category> findAll() {
        Session session = sessionFactory.openSession();
        try {
            Query<Category> query = session.createQuery(
                "FROM Category ORDER BY categoryName", Category.class);
            return query.getResultList();
        } finally {
            session.close();
        }
    }
    
    public List<Category> findTopLevelCategories() {
        Query<Category> query = getCurrentSession().createQuery(
            "FROM Category WHERE parentCategory IS NULL AND isActive = true ORDER BY categoryName", Category.class);
        return query.getResultList();
    }
    
    public List<Category> findSubcategories(Integer parentCategoryId) {
        Query<Category> query = getCurrentSession().createQuery(
            "FROM Category WHERE parentCategory.categoryId = :parentId AND isActive = true ORDER BY categoryName", Category.class);
        query.setParameter("parentId", parentCategoryId);
        return query.getResultList();
    }
    
    public List<Category> findActiveCategories() {
        Query<Category> query = getCurrentSession().createQuery(
            "FROM Category WHERE isActive = true ORDER BY categoryName", Category.class);
        return query.getResultList();
    }
    
    public List<Category> searchCategories(String searchTerm) {
        Query<Category> query = getCurrentSession().createQuery(
            "FROM Category WHERE categoryName LIKE :search OR description LIKE :search", Category.class);
        query.setParameter("search", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public Long getProductCount(Integer categoryId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId", Long.class);
        query.setParameter("categoryId", categoryId);
        return query.uniqueResult();
    }
    
    public void delete(Category category) {
        getCurrentSession().delete(category);
    }
    
    public void deleteById(Integer categoryId) {
        Category category = findById(categoryId);
        if (category != null) {
            delete(category);
        }
    }
    
    public Category update(Category category) {
        return (Category) getCurrentSession().merge(category);
    }
    
    public boolean existsByName(String categoryName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE c.categoryName = :name", Long.class);
            query.setParameter("name", categoryName);
            return query.uniqueResult() > 0;
        } finally {
            session.close();
        }
    }
    
    public List<Category> getCategoryHierarchy(Integer categoryId) {
        Query<Category> query = getCurrentSession().createQuery(
            "FROM Category WHERE categoryId = :id OR parentCategory.categoryId = :id", Category.class);
        query.setParameter("id", categoryId);
        return query.getResultList();
    }
}