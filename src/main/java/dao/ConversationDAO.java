package dao;

import entity.Conversation;
import entity.User;
import entity.Product;
import entity.Order;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class ConversationDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public Conversation save(Conversation conversation) {
        getCurrentSession().saveOrUpdate(conversation);
        return conversation;
    }
    
    public Conversation findById(Integer conversationId) {
        return getCurrentSession().get(Conversation.class, conversationId);
    }
    
    public List<Conversation> findByUser(Integer userId) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE (c.user1.userId = :userId OR c.user2.userId = :userId) " +
            "AND c.status != 'BLOCKED' ORDER BY c.lastMessageAt DESC", Conversation.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public List<Conversation> findActiveByUser(Integer userId) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE (c.user1.userId = :userId OR c.user2.userId = :userId) " +
            "AND c.status = 'ACTIVE' AND " +
            "((c.user1.userId = :userId AND c.archivedByUser1 = false) OR " +
            "(c.user2.userId = :userId AND c.archivedByUser2 = false)) " +
            "ORDER BY c.lastMessageAt DESC", Conversation.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public List<Conversation> findArchivedByUser(Integer userId) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE (c.user1.userId = :userId OR c.user2.userId = :userId) " +
            "AND ((c.user1.userId = :userId AND c.archivedByUser1 = true) OR " +
            "(c.user2.userId = :userId AND c.archivedByUser2 = true)) " +
            "ORDER BY c.lastMessageAt DESC", Conversation.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public Conversation findByUsersAndProduct(Integer user1Id, Integer user2Id, Integer productId) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE c.product.productId = :productId AND " +
            "((c.user1.userId = :user1Id AND c.user2.userId = :user2Id) OR " +
            "(c.user1.userId = :user2Id AND c.user2.userId = :user1Id)) " +
            "AND c.status = 'ACTIVE'", Conversation.class);
        query.setParameter("user1Id", user1Id);
        query.setParameter("user2Id", user2Id);
        query.setParameter("productId", productId);
        return query.uniqueResult();
    }
    
    public Conversation findByUsersAndOrder(Integer user1Id, Integer user2Id, Integer orderId) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE c.order.orderId = :orderId AND " +
            "((c.user1.userId = :user1Id AND c.user2.userId = :user2Id) OR " +
            "(c.user1.userId = :user2Id AND c.user2.userId = :user1Id)) " +
            "AND c.status = 'ACTIVE'", Conversation.class);
        query.setParameter("user1Id", user1Id);
        query.setParameter("user2Id", user2Id);
        query.setParameter("orderId", orderId);
        return query.uniqueResult();
    }
    
    public Conversation findBetweenUsers(Integer user1Id, Integer user2Id) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE " +
            "((c.user1.userId = :user1Id AND c.user2.userId = :user2Id) OR " +
            "(c.user1.userId = :user2Id AND c.user2.userId = :user1Id)) " +
            "AND c.status = 'ACTIVE' AND c.product IS NULL AND c.order IS NULL " +
            "ORDER BY c.createdAt DESC", Conversation.class);
        query.setParameter("user1Id", user1Id);
        query.setParameter("user2Id", user2Id);
        query.setMaxResults(1);
        List<Conversation> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<Conversation> findByProduct(Integer productId) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE c.product.productId = :productId " +
            "ORDER BY c.createdAt DESC", Conversation.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public List<Conversation> findByOrder(Integer orderId) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE c.order.orderId = :orderId " +
            "ORDER BY c.createdAt DESC", Conversation.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
    
    public List<Conversation> findUnreadByUser(Integer userId) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE " +
            "((c.user1.userId = :userId AND c.unreadCountUser1 > 0) OR " +
            "(c.user2.userId = :userId AND c.unreadCountUser2 > 0)) " +
            "AND c.status = 'ACTIVE' ORDER BY c.lastMessageAt DESC", Conversation.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public Long getTotalUnreadCount(Integer userId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT SUM(CASE WHEN c.user1.userId = :userId THEN c.unreadCountUser1 " +
            "WHEN c.user2.userId = :userId THEN c.unreadCountUser2 ELSE 0 END) " +
            "FROM Conversation c WHERE (c.user1.userId = :userId OR c.user2.userId = :userId) " +
            "AND c.status = 'ACTIVE'", Long.class);
        query.setParameter("userId", userId);
        Long result = query.uniqueResult();
        return result != null ? result : 0L;
    }
    
    public List<Conversation> findByType(Conversation.ConversationType type) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE c.conversationType = :type " +
            "ORDER BY c.createdAt DESC", Conversation.class);
        query.setParameter("type", type);
        return query.getResultList();
    }
    
    public List<Conversation> findByStatus(Conversation.ConversationStatus status) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE c.status = :status " +
            "ORDER BY c.lastMessageAt DESC", Conversation.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Conversation> searchBySubject(String searchTerm, Integer userId) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE (c.user1.userId = :userId OR c.user2.userId = :userId) " +
            "AND c.subject LIKE :searchTerm ORDER BY c.lastMessageAt DESC", Conversation.class);
        query.setParameter("userId", userId);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Conversation> findRecentByUser(Integer userId, int limit) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE (c.user1.userId = :userId OR c.user2.userId = :userId) " +
            "AND c.status = 'ACTIVE' ORDER BY c.lastMessageAt DESC", Conversation.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Conversation> findStaleConversations(Timestamp cutoffDate) {
        Query<Conversation> query = getCurrentSession().createQuery(
            "FROM Conversation c WHERE c.lastMessageAt < :cutoffDate " +
            "AND c.status = 'ACTIVE'", Conversation.class);
        query.setParameter("cutoffDate", cutoffDate);
        return query.getResultList();
    }
    
    public List<Object[]> getConversationStatsByUser(Integer userId) {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT c.conversationType, COUNT(c), SUM(CASE WHEN c.user1.userId = :userId " +
            "THEN c.unreadCountUser1 WHEN c.user2.userId = :userId THEN c.unreadCountUser2 ELSE 0 END) " +
            "FROM Conversation c WHERE (c.user1.userId = :userId OR c.user2.userId = :userId) " +
            "GROUP BY c.conversationType", Object[].class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public Conversation update(Conversation conversation) {
        conversation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return (Conversation) getCurrentSession().merge(conversation);
    }
    
    public void delete(Conversation conversation) {
        getCurrentSession().delete(conversation);
    }
    
    public void deleteById(Integer conversationId) {
        Conversation conversation = findById(conversationId);
        if (conversation != null) {
            delete(conversation);
        }
    }
    
    public Long getConversationCount() {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(c) FROM Conversation c", Long.class);
        return query.uniqueResult();
    }
    
    public Long getActiveConversationCount() {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(c) FROM Conversation c WHERE c.status = 'ACTIVE'", Long.class);
        return query.uniqueResult();
    }
}