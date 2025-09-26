package service;

import entity.Conversation;
import entity.Message;
import entity.User;
import entity.Product;
import entity.Order;
import dao.ConversationDAO;
import dao.MessageDAO;
import dao.UserDAO;
import dao.ProductDAO;
import dao.OrderDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class MessagingService {
    
    @Autowired
    private ConversationDAO conversationDAO;
    
    @Autowired
    private MessageDAO messageDAO;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private ProductDAO productDAO;
    
    @Autowired
    private OrderDAO orderDAO;
    
    @Autowired
    private EmailService emailService;
    
    // Conversation Management
    
    /**
     * Create a new conversation between two users
     */
    public Conversation createConversation(Integer user1Id, Integer user2Id, 
                                          Conversation.ConversationType type, String subject) {
        User user1 = userDAO.findById(user1Id);
        User user2 = userDAO.findById(user2Id);
        
        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("Invalid user IDs");
        }
        
        // Check if conversation already exists
        Conversation existing = conversationDAO.findBetweenUsers(user1Id, user2Id);
        if (existing != null) {
            return existing;
        }
        
        Conversation conversation = new Conversation(user1, user2, type, subject);
        return conversationDAO.save(conversation);
    }
    
    /**
     * Create conversation about a specific product
     */
    public Conversation createProductInquiry(Integer buyerId, Integer productId, String subject) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        User buyer = userDAO.findById(buyerId);
        User seller = product.getSeller();
        
        if (buyer == null) {
            throw new IllegalArgumentException("Buyer not found");
        }
        
        // Check if conversation already exists for this product
        Conversation existing = conversationDAO.findByUsersAndProduct(buyerId, seller.getUserId(), productId);
        if (existing != null) {
            return existing;
        }
        
        Conversation conversation = new Conversation(buyer, seller, product, subject);
        return conversationDAO.save(conversation);
    }
    
    /**
     * Create conversation about a specific order
     */
    public Conversation createOrderDiscussion(Integer orderId, String subject) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        
        User buyer = order.getBuyer();
        User seller = order.getSeller();
        
        // Check if conversation already exists for this order
        Conversation existing = conversationDAO.findByUsersAndOrder(
            buyer.getUserId(), seller.getUserId(), orderId);
        if (existing != null) {
            return existing;
        }
        
        Conversation conversation = new Conversation(buyer, seller, order, subject);
        return conversationDAO.save(conversation);
    }
    
    /**
     * Get all conversations for a user
     */
    public List<Conversation> getUserConversations(Integer userId) {
        return conversationDAO.findActiveByUser(userId);
    }
    
    /**
     * Get archived conversations for a user
     */
    public List<Conversation> getUserArchivedConversations(Integer userId) {
        return conversationDAO.findArchivedByUser(userId);
    }
    
    /**
     * Archive/Unarchive a conversation for a user
     */
    public void setConversationArchived(Integer conversationId, Integer userId, boolean archived) {
        Conversation conversation = conversationDAO.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found");
        }
        
        User user = userDAO.findById(userId);
        if (!conversation.isParticipant(user)) {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
        
        conversation.setArchivedBy(user, archived);
        conversationDAO.update(conversation);
    }
    
    /**
     * Close a conversation
     */
    public void closeConversation(Integer conversationId, Integer userId) {
        Conversation conversation = conversationDAO.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found");
        }
        
        User user = userDAO.findById(userId);
        if (!conversation.isParticipant(user)) {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
        
        conversation.setStatus(Conversation.ConversationStatus.CLOSED);
        conversationDAO.update(conversation);
        
        // Create system message
        createSystemMessage(conversation, "Conversation closed by " + user.getFullName());
    }
    
    // Message Management
    
    /**
     * Send a message in a conversation
     */
    public Message sendMessage(Integer conversationId, Integer senderId, String content) {
        return sendMessage(conversationId, senderId, content, Message.MessageType.TEXT);
    }
    
    /**
     * Send a message with specific type
     */
    public Message sendMessage(Integer conversationId, Integer senderId, String content, 
                              Message.MessageType messageType) {
        Conversation conversation = conversationDAO.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found");
        }
        
        User sender = userDAO.findById(senderId);
        if (sender == null || !conversation.isParticipant(sender)) {
            throw new IllegalArgumentException("Sender is not a participant in this conversation");
        }
        
        User recipient = conversation.getOtherParticipant(sender);
        
        Message message = new Message(conversation, sender, recipient, content, messageType);
        message = messageDAO.save(message);
        
        // Update conversation activity
        conversation.updateLastActivity();
        conversation.incrementUnreadCount(recipient);
        conversationDAO.update(conversation);
        
        // Send email notification if enabled
        sendMessageNotification(message);
        
        return message;
    }
    
    /**
     * Send message with attachment
     */
    public Message sendMessageWithAttachment(Integer conversationId, Integer senderId, 
                                           String content, String attachmentUrl, 
                                           String attachmentName, String attachmentType, Long attachmentSize) {
        Message message = sendMessage(conversationId, senderId, content, 
                                    attachmentType.startsWith("image/") ? Message.MessageType.IMAGE : Message.MessageType.FILE);
        
        message.setAttachmentUrl(attachmentUrl);
        message.setAttachmentName(attachmentName);
        message.setAttachmentType(attachmentType);
        message.setAttachmentSize(attachmentSize);
        
        return messageDAO.update(message);
    }
    
    /**
     * Reply to a specific message
     */
    public Message replyToMessage(Integer originalMessageId, Integer senderId, String content) {
        Message originalMessage = messageDAO.findById(originalMessageId);
        if (originalMessage == null) {
            throw new IllegalArgumentException("Original message not found");
        }
        
        Message reply = sendMessage(originalMessage.getConversation().getConversationId(), 
                                  senderId, content);
        reply.setReplyToMessage(originalMessage);
        
        return messageDAO.update(reply);
    }
    
    /**
     * Edit a message
     */
    public Message editMessage(Integer messageId, Integer userId, String newContent) {
        Message message = messageDAO.findById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("Message not found");
        }
        
        if (!message.getSender().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Only the sender can edit this message");
        }
        
        // Can only edit within 15 minutes
        long timeSinceCreation = System.currentTimeMillis() - message.getSentAt().getTime();
        if (timeSinceCreation > 15 * 60 * 1000) { // 15 minutes
            throw new IllegalArgumentException("Message can only be edited within 15 minutes of sending");
        }
        
        message.editMessage(newContent);
        return messageDAO.update(message);
    }
    
    /**
     * Delete a message
     */
    public void deleteMessage(Integer messageId, Integer userId) {
        Message message = messageDAO.findById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("Message not found");
        }
        
        if (!message.getSender().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Only the sender can delete this message");
        }
        
        messageDAO.delete(message);
    }
    
    /**
     * Get messages in a conversation
     */
    public List<Message> getConversationMessages(Integer conversationId, Integer userId) {
        Conversation conversation = conversationDAO.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found");
        }
        
        User user = userDAO.findById(userId);
        if (!conversation.isParticipant(user)) {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
        
        return messageDAO.findByConversation(conversationId);
    }
    
    /**
     * Get messages with pagination
     */
    public List<Message> getConversationMessages(Integer conversationId, Integer userId, int page, int pageSize) {
        Conversation conversation = conversationDAO.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found");
        }
        
        User user = userDAO.findById(userId);
        if (!conversation.isParticipant(user)) {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
        
        return messageDAO.findByConversationPaginated(conversationId, page, pageSize);
    }
    
    /**
     * Mark conversation as read
     */
    public void markConversationAsRead(Integer conversationId, Integer userId) {
        Conversation conversation = conversationDAO.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found");
        }
        
        User user = userDAO.findById(userId);
        if (!conversation.isParticipant(user)) {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
        
        // Mark all unread messages as read
        messageDAO.markConversationAsRead(conversationId, userId);
        
        // Reset unread count
        conversation.resetUnreadCount(user);
        conversationDAO.update(conversation);
    }
    
    /**
     * Search messages
     */
    public List<Message> searchMessages(Integer userId, String searchTerm) {
        return messageDAO.searchByUser(userId, searchTerm);
    }
    
    /**
     * Search within a conversation
     */
    public List<Message> searchInConversation(Integer conversationId, Integer userId, String searchTerm) {
        Conversation conversation = conversationDAO.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found");
        }
        
        User user = userDAO.findById(userId);
        if (!conversation.isParticipant(user)) {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
        
        return messageDAO.searchInConversation(conversationId, searchTerm);
    }
    
    // Utility Methods
    
    /**
     * Get user's unread message count
     */
    public Long getUnreadMessageCount(Integer userId) {
        return messageDAO.getUnreadCountForUser(userId);
    }
    
    /**
     * Get conversation statistics for a user
     */
    public Map<String, Object> getMessagingStats(Integer userId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Conversation> activeConversations = conversationDAO.findActiveByUser(userId);
        List<Conversation> archivedConversations = conversationDAO.findArchivedByUser(userId);
        Long unreadCount = conversationDAO.getTotalUnreadCount(userId);
        
        stats.put("activeConversations", activeConversations.size());
        stats.put("archivedConversations", archivedConversations.size());
        stats.put("totalUnreadMessages", unreadCount);
        stats.put("conversationStats", conversationDAO.getConversationStatsByUser(userId));
        
        return stats;
    }
    
    /**
     * Create system message
     */
    private Message createSystemMessage(Conversation conversation, String content) {
        Message systemMessage = new Message();
        systemMessage.setConversation(conversation);
        systemMessage.setMessageContent(content);
        systemMessage.setMessageType(Message.MessageType.SYSTEM);
        systemMessage.setIsSystemMessage(true);
        systemMessage.setStatus(Message.MessageStatus.DELIVERED);
        
        return messageDAO.save(systemMessage);
    }
    
    /**
     * Send email notification for new message
     */
    private void sendMessageNotification(Message message) {
        try {
            User recipient = message.getRecipient();
            User sender = message.getSender();
            
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("message", message);
            emailData.put("conversation", message.getConversation());
            emailData.put("sender", sender);
            emailData.put("recipient", recipient);
            
            // Send notification email
            String subject = "New message from " + sender.getFullName() + " - " + 
                           message.getConversation().getDisplayTitle();
            String emailContent = buildMessageNotificationContent(message);
            
            emailService.sendPaymentStatusUpdateEmail(recipient.getEmail(), subject, emailContent);
            
        } catch (Exception e) {
            System.err.println("Error sending message notification: " + e.getMessage());
        }
    }
    
    private String buildMessageNotificationContent(Message message) {
        StringBuilder content = new StringBuilder();
        content.append("You have received a new message from ")
               .append(message.getSender().getFullName())
               .append("\n\n");
        content.append("Subject: ").append(message.getConversation().getDisplayTitle()).append("\n");
        content.append("Message: ").append(message.getShortContent(200)).append("\n\n");
        content.append("Reply to this message by visiting your eBay messages.");
        
        return content.toString();
    }
    
    /**
     * Clean up old conversations and messages
     */
    public void cleanupOldData(int daysToKeep) {
        Timestamp cutoffDate = new Timestamp(System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000));
        
        // Delete old messages
        messageDAO.deleteOldMessages(cutoffDate);
        
        // Find stale conversations
        List<Conversation> staleConversations = conversationDAO.findStaleConversations(cutoffDate);
        for (Conversation conversation : staleConversations) {
            if (conversation.getStatus() == Conversation.ConversationStatus.ACTIVE) {
                conversation.setStatus(Conversation.ConversationStatus.ARCHIVED);
                conversationDAO.update(conversation);
            }
        }
    }
}