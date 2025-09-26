package entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Conversation entity representing a chat thread between two users
 * Usually related to a specific product or order
 */
@Entity
@Table(name = "conversations")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Integer conversationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1; // Usually the buyer/inquirer
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2; // Usually the seller/responder
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // Related product (optional)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // Related order (optional)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type")
    private ConversationType conversationType = ConversationType.PRODUCT_INQUIRY;
    
    @Column(name = "subject", length = 200)
    private String subject;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ConversationStatus status = ConversationStatus.ACTIVE;
    
    @Column(name = "created_at")
    private Timestamp createdAt;
    
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    
    @Column(name = "last_message_at")
    private Timestamp lastMessageAt;
    
    @Column(name = "unread_count_user1")
    private Integer unreadCountUser1 = 0;
    
    @Column(name = "unread_count_user2")
    private Integer unreadCountUser2 = 0;
    
    @Column(name = "archived_by_user1")
    private Boolean archivedByUser1 = false;
    
    @Column(name = "archived_by_user2")
    private Boolean archivedByUser2 = false;
    
    // Relationships
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sentAt ASC")
    private List<Message> messages;
    
    // Enums
    public enum ConversationType {
        PRODUCT_INQUIRY,    // Questions about a product
        ORDER_DISCUSSION,   // Discussion about an order
        GENERAL_SUPPORT,    // General customer service
        NEGOTIATION,        // Price or terms negotiation
        COMPLAINT,          // Issues or complaints
        FEEDBACK_REQUEST    // Requesting feedback/review
    }
    
    public enum ConversationStatus {
        ACTIVE,     // Normal active conversation
        CLOSED,     // Closed by one of the parties
        RESOLVED,   // Issue resolved
        ARCHIVED,   // Archived by both parties
        BLOCKED     // Communication blocked
    }
    
    // Constructors
    public Conversation() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = this.createdAt;
    }
    
    public Conversation(User user1, User user2, ConversationType type, String subject) {
        this();
        this.user1 = user1;
        this.user2 = user2;
        this.conversationType = type;
        this.subject = subject;
    }
    
    public Conversation(User user1, User user2, Product product, String subject) {
        this(user1, user2, ConversationType.PRODUCT_INQUIRY, subject);
        this.product = product;
    }
    
    public Conversation(User user1, User user2, Order order, String subject) {
        this(user1, user2, ConversationType.ORDER_DISCUSSION, subject);
        this.order = order;
    }
    
    // Getters and Setters
    public Integer getConversationId() { return conversationId; }
    public void setConversationId(Integer conversationId) { this.conversationId = conversationId; }
    
    public User getUser1() { return user1; }
    public void setUser1(User user1) { this.user1 = user1; }
    
    public User getUser2() { return user2; }
    public void setUser2(User user2) { this.user2 = user2; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public ConversationType getConversationType() { return conversationType; }
    public void setConversationType(ConversationType conversationType) { this.conversationType = conversationType; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public ConversationStatus getStatus() { return status; }
    public void setStatus(ConversationStatus status) { this.status = status; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public Timestamp getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(Timestamp lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    
    public Integer getUnreadCountUser1() { return unreadCountUser1; }
    public void setUnreadCountUser1(Integer unreadCountUser1) { this.unreadCountUser1 = unreadCountUser1; }
    
    public Integer getUnreadCountUser2() { return unreadCountUser2; }
    public void setUnreadCountUser2(Integer unreadCountUser2) { this.unreadCountUser2 = unreadCountUser2; }
    
    public Boolean getArchivedByUser1() { return archivedByUser1; }
    public void setArchivedByUser1(Boolean archivedByUser1) { this.archivedByUser1 = archivedByUser1; }
    
    public Boolean getArchivedByUser2() { return archivedByUser2; }
    public void setArchivedByUser2(Boolean archivedByUser2) { this.archivedByUser2 = archivedByUser2; }
    
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    
    // Utility methods
    public User getOtherParticipant(User currentUser) {
        if (currentUser.getUserId().equals(user1.getUserId())) {
            return user2;
        } else if (currentUser.getUserId().equals(user2.getUserId())) {
            return user1;
        }
        return null;
    }
    
    public Integer getUnreadCount(User user) {
        if (user.getUserId().equals(user1.getUserId())) {
            return unreadCountUser1;
        } else if (user.getUserId().equals(user2.getUserId())) {
            return unreadCountUser2;
        }
        return 0;
    }
    
    public void incrementUnreadCount(User user) {
        if (user.getUserId().equals(user1.getUserId())) {
            unreadCountUser1++;
        } else if (user.getUserId().equals(user2.getUserId())) {
            unreadCountUser2++;
        }
    }
    
    public void resetUnreadCount(User user) {
        if (user.getUserId().equals(user1.getUserId())) {
            unreadCountUser1 = 0;
        } else if (user.getUserId().equals(user2.getUserId())) {
            unreadCountUser2 = 0;
        }
    }
    
    public boolean isArchivedBy(User user) {
        if (user.getUserId().equals(user1.getUserId())) {
            return archivedByUser1;
        } else if (user.getUserId().equals(user2.getUserId())) {
            return archivedByUser2;
        }
        return false;
    }
    
    public void setArchivedBy(User user, boolean archived) {
        if (user.getUserId().equals(user1.getUserId())) {
            archivedByUser1 = archived;
        } else if (user.getUserId().equals(user2.getUserId())) {
            archivedByUser2 = archived;
        }
    }
    
    public boolean isParticipant(User user) {
        return user.getUserId().equals(user1.getUserId()) || user.getUserId().equals(user2.getUserId());
    }
    
    public String getDisplayTitle() {
        if (subject != null && !subject.trim().isEmpty()) {
            return subject;
        }
        if (product != null) {
            return "About: " + product.getTitle();
        }
        if (order != null) {
            return "Order #" + order.getOrderId();
        }
        return conversationType.name().replace("_", " ");
    }
    
    public void updateLastActivity() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.lastMessageAt = this.updatedAt;
    }
}