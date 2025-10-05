package dao;

import entity.Message;
import entity.Conversation;
import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class MessageDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Message save(Message message) {
        if (message.getMessageId() == null) {
            entityManager.persist(message);
        } else {
            entityManager.merge(message);
        }
        return message;
    }
    
    public Message findById(Integer messageId) {
        return entityManager.find(Message.class, messageId);
    }
    
    public List<Message> findByConversation(Integer conversationId) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("conversationId", conversationId);
        return query.getResultList();
    }
    
    public List<Message> findByConversationPaginated(Integer conversationId, int page, int pageSize) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("conversationId", conversationId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
    
    public Message getLatestMessageInConversation(Integer conversationId) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("conversationId", conversationId);
        query.setMaxResults(1);
        List<Message> results = query.getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }
    
    public List<Message> findBySender(Integer senderId) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.sender.userId = :senderId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("senderId", senderId);
        return query.getResultList();
    }
    
    public List<Message> findByRecipient(Integer recipientId) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.recipient.userId = :recipientId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("recipientId", recipientId);
        return query.getResultList();
    }
    
    public List<Message> findUnreadByRecipient(Integer recipientId) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.recipient.userId = :recipientId " +
            "AND m.readAt IS NULL AND m.status != 'DELETED' " +
            "ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("recipientId", recipientId);
        return query.getResultList();
    }
    
    public List<Message> findUnreadInConversation(Integer conversationId, Integer recipientId) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.recipient.userId = :recipientId AND m.readAt IS NULL " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("recipientId", recipientId);
        return query.getResultList();
    }
    
    public Long getUnreadCountForUser(Integer userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM Message m WHERE m.recipient.userId = :userId " +
            "AND m.readAt IS NULL AND m.status != 'DELETED'", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }
    
    public Long getUnreadCountInConversation(Integer conversationId, Integer recipientId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.recipient.userId = :recipientId AND m.readAt IS NULL " +
            "AND m.status != 'DELETED'", Long.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("recipientId", recipientId);
        return query.getSingleResult();
    }
    
    public List<Message> findByType(Message.MessageType messageType) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.messageType = :messageType " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("messageType", messageType);
        return query.getResultList();
    }
    
    public List<Message> findWithAttachments() {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.attachmentUrl IS NOT NULL " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        return query.getResultList();
    }
    
    public List<Message> findByStatus(Message.MessageStatus status) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.status = :status ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Message> findByDateRange(Timestamp startDate, Timestamp endDate) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.sentAt BETWEEN :startDate AND :endDate " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Message> searchInConversation(Integer conversationId, String searchTerm) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND LOWER(m.messageContent) LIKE LOWER(:searchTerm) " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Message> searchByUser(Integer userId, String searchTerm) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE (m.sender.userId = :userId OR m.recipient.userId = :userId) " +
            "AND LOWER(m.messageContent) LIKE LOWER(:searchTerm) " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("userId", userId);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Message> findReplies(Integer messageId) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.replyToMessageId = :messageId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("messageId", messageId);
        return query.getResultList();
    }
    
    public List<Message> findSystemMessages(Integer conversationId) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.isSystemMessage = true AND m.status != 'DELETED' " +
            "ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("conversationId", conversationId);
        return query.getResultList();
    }
    
    public List<Message> findRecentByUser(Integer userId, int limit) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE (m.sender.userId = :userId OR m.recipient.userId = :userId) " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Message> findFailedMessages() {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.status = 'FAILED' ORDER BY m.sentAt ASC", Message.class);
        return query.getResultList();
    }
    
    public void markAsRead(Integer messageId) {
        Message message = findById(messageId);
        if (message != null && message.getReadAt() == null) {
            message.markAsRead();
            save(message);
        }
    }
    
    public void markConversationAsRead(Integer conversationId, Integer userId) {
        TypedQuery<Message> query = entityManager.createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.recipient.userId = :userId AND m.readAt IS NULL", Message.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("userId", userId);
        
        List<Message> unreadMessages = query.getResultList();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        for (Message message : unreadMessages) {
            message.setReadAt(now);
            message.setStatus(Message.MessageStatus.READ);
        }
    }
    
    public void markAsDelivered(Integer messageId) {
        Message message = findById(messageId);
        if (message != null && message.getDeliveredAt() == null) {
            message.markAsDelivered();
            save(message);
        }
    }
    
    public Long getMessageCountInConversation(Integer conversationId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.status != 'DELETED'", Long.class);
        query.setParameter("conversationId", conversationId);
        return query.getSingleResult();
    }
    
    public List<Object[]> getMessageStatsByUser(Integer userId) {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT m.messageType, COUNT(m), SUM(CASE WHEN m.readAt IS NULL THEN 1 ELSE 0 END) " +
            "FROM Message m WHERE (m.sender.userId = :userId OR m.recipient.userId = :userId) " +
            "AND m.status != 'DELETED' GROUP BY m.messageType", Object[].class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public void deleteOldMessages(Timestamp cutoffDate) {
        entityManager.createQuery(
            "UPDATE Message m SET m.status = 'DELETED' WHERE m.sentAt < :cutoffDate " +
            "AND m.status != 'DELETED'")
            .setParameter("cutoffDate", cutoffDate)
            .executeUpdate();
    }
    
    public Message update(Message message) {
        return entityManager.merge(message);
    }
    
    public void delete(Message message) {
        message.setStatus(Message.MessageStatus.DELETED);
        update(message);
    }
    
    public void deleteById(Integer messageId) {
        Message message = findById(messageId);
        if (message != null) {
            delete(message);
        }
    }
    
    public Long getMessageCount() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM Message m WHERE m.status != 'DELETED'", Long.class);
        return query.getSingleResult();
    }
    
    public Long getTotalMessageCount() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM Message m", Long.class);
        return query.getSingleResult();
    }
}