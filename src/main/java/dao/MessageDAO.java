package dao;

import entity.Message;
import entity.Conversation;
import entity.User;
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
public class MessageDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public Message save(Message message) {
        getCurrentSession().saveOrUpdate(message);
        return message;
    }
    
    public Message findById(Integer messageId) {
        return getCurrentSession().get(Message.class, messageId);
    }
    
    public List<Message> findByConversation(Integer conversationId) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("conversationId", conversationId);
        return query.getResultList();
    }
    
    public List<Message> findByConversationPaginated(Integer conversationId, int page, int pageSize) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("conversationId", conversationId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
    
    public Message getLatestMessageInConversation(Integer conversationId) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("conversationId", conversationId);
        query.setMaxResults(1);
        List<Message> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<Message> findBySender(Integer senderId) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.sender.userId = :senderId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("senderId", senderId);
        return query.getResultList();
    }
    
    public List<Message> findByRecipient(Integer recipientId) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.recipient.userId = :recipientId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("recipientId", recipientId);
        return query.getResultList();
    }
    
    public List<Message> findUnreadByRecipient(Integer recipientId) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.recipient.userId = :recipientId " +
            "AND m.readAt IS NULL AND m.status != 'DELETED' " +
            "ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("recipientId", recipientId);
        return query.getResultList();
    }
    
    public List<Message> findUnreadInConversation(Integer conversationId, Integer recipientId) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.recipient.userId = :recipientId AND m.readAt IS NULL " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("recipientId", recipientId);
        return query.getResultList();
    }
    
    public Long getUnreadCountForUser(Integer userId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(m) FROM Message m WHERE m.recipient.userId = :userId " +
            "AND m.readAt IS NULL AND m.status != 'DELETED'", Long.class);
        query.setParameter("userId", userId);
        return query.uniqueResult();
    }
    
    public Long getUnreadCountInConversation(Integer conversationId, Integer recipientId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(m) FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.recipient.userId = :recipientId AND m.readAt IS NULL " +
            "AND m.status != 'DELETED'", Long.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("recipientId", recipientId);
        return query.uniqueResult();
    }
    
    public List<Message> findByType(Message.MessageType messageType) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.messageType = :messageType " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("messageType", messageType);
        return query.getResultList();
    }
    
    public List<Message> findWithAttachments() {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.attachmentUrl IS NOT NULL " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        return query.getResultList();
    }
    
    public List<Message> findByStatus(Message.MessageStatus status) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.status = :status ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Message> findByDateRange(Timestamp startDate, Timestamp endDate) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.sentAt BETWEEN :startDate AND :endDate " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Message> searchInConversation(Integer conversationId, String searchTerm) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND LOWER(m.messageContent) LIKE LOWER(:searchTerm) " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Message> searchByUser(Integer userId, String searchTerm) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE (m.sender.userId = :userId OR m.recipient.userId = :userId) " +
            "AND LOWER(m.messageContent) LIKE LOWER(:searchTerm) " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("userId", userId);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Message> findReplies(Integer messageId) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.replyToMessageId = :messageId " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("messageId", messageId);
        return query.getResultList();
    }
    
    public List<Message> findSystemMessages(Integer conversationId) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.isSystemMessage = true AND m.status != 'DELETED' " +
            "ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("conversationId", conversationId);
        return query.getResultList();
    }
    
    public List<Message> findRecentByUser(Integer userId, int limit) {
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE (m.sender.userId = :userId OR m.recipient.userId = :userId) " +
            "AND m.status != 'DELETED' ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Message> findFailedMessages() {
        Query<Message> query = getCurrentSession().createQuery(
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
        Query<Message> query = getCurrentSession().createQuery(
            "FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.recipient.userId = :userId AND m.readAt IS NULL");
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
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(m) FROM Message m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.status != 'DELETED'", Long.class);
        query.setParameter("conversationId", conversationId);
        return query.uniqueResult();
    }
    
    public List<Object[]> getMessageStatsByUser(Integer userId) {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT m.messageType, COUNT(m), SUM(CASE WHEN m.readAt IS NULL THEN 1 ELSE 0 END) " +
            "FROM Message m WHERE (m.sender.userId = :userId OR m.recipient.userId = :userId) " +
            "AND m.status != 'DELETED' GROUP BY m.messageType", Object[].class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public void deleteOldMessages(Timestamp cutoffDate) {
        Query<?> query = getCurrentSession().createQuery(
            "UPDATE Message m SET m.status = 'DELETED' WHERE m.sentAt < :cutoffDate " +
            "AND m.status != 'DELETED'");
        query.setParameter("cutoffDate", cutoffDate);
        query.executeUpdate();
    }
    
    public Message update(Message message) {
        return (Message) getCurrentSession().merge(message);
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
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(m) FROM Message m WHERE m.status != 'DELETED'", Long.class);
        return query.uniqueResult();
    }
    
    public Long getTotalMessageCount() {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(m) FROM Message m", Long.class);
        return query.uniqueResult();
    }
}