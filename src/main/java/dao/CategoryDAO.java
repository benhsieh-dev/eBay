package dao;

import entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class CategoryDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Category save(Category category) {
        if (category.getCategoryId() == null) {
            entityManager.persist(category);
        } else {
            entityManager.merge(category);
        }
        return category;
    }
    
    public Category findById(Integer categoryId) {
        return entityManager.find(Category.class, categoryId);
    }
    
    public Category findByName(String categoryName) {
        try {
            TypedQuery<Category> query = entityManager.createQuery(
                "FROM Category WHERE categoryName = :name", Category.class);
            query.setParameter("name", categoryName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<Category> findAll() {
        TypedQuery<Category> query = entityManager.createQuery(
            "FROM Category ORDER BY categoryName", Category.class);
        return query.getResultList();
    }
    
    public List<Category> findTopLevelCategories() {
        TypedQuery<Category> query = entityManager.createQuery(
            "FROM Category WHERE parentCategory IS NULL AND isActive = true ORDER BY categoryName", Category.class);
        return query.getResultList();
    }
    
    public List<Category> findSubcategories(Integer parentCategoryId) {
        TypedQuery<Category> query = entityManager.createQuery(
            "FROM Category WHERE parentCategory.categoryId = :parentId AND isActive = true ORDER BY categoryName", Category.class);
        query.setParameter("parentId", parentCategoryId);
        return query.getResultList();
    }
    
    public List<Category> findActiveCategories() {
        TypedQuery<Category> query = entityManager.createQuery(
            "FROM Category WHERE isActive = true ORDER BY categoryName", Category.class);
        return query.getResultList();
    }
    
    public List<Category> searchCategories(String searchTerm) {
        TypedQuery<Category> query = entityManager.createQuery(
            "FROM Category WHERE categoryName LIKE :search OR description LIKE :search", Category.class);
        query.setParameter("search", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public Long getProductCount(Integer categoryId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId", Long.class);
        query.setParameter("categoryId", categoryId);
        return query.getSingleResult();
    }
    
    public void delete(Category category) {
        if (entityManager.contains(category)) {
            entityManager.remove(category);
        } else {
            entityManager.remove(entityManager.merge(category));
        }
    }
    
    public void deleteById(Integer categoryId) {
        Category category = findById(categoryId);
        if (category != null) {
            delete(category);
        }
    }
    
    public Category update(Category category) {
        return entityManager.merge(category);
    }
    
    public boolean existsByName(String categoryName) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM Category c WHERE c.categoryName = :name", Long.class);
        query.setParameter("name", categoryName);
        return query.getSingleResult() > 0;
    }
    
    public List<Category> getCategoryHierarchy(Integer categoryId) {
        TypedQuery<Category> query = entityManager.createQuery(
            "FROM Category WHERE categoryId = :id OR parentCategory.categoryId = :id", Category.class);
        query.setParameter("id", categoryId);
        return query.getResultList();
    }
}