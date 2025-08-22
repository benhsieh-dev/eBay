package entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Message entity representing individual messages within conversations
 */
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    
    @Column(name = "message_content", columnDefinition = "TEXT", nullable = false)
    private String messageContent;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType = MessageType.TEXT;
    
    @Column(name = "sent_at")
    private Timestamp sentAt;
    
    @Column(name = "delivered_at")
    private Timestamp deliveredAt;
    
    @Column(name = "read_at")
    private Timestamp readAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status = MessageStatus.SENT;
    
    @Column(name = "edited_at")
    private Timestamp editedAt;
    
    @Column(name = "is_edited")
    private Boolean isEdited = false;
    
    @Column(name = "reply_to_message_id")
    private Integer replyToMessageId;
    
    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;
    
    @Column(name = "attachment_name", length = 255)
    private String attachmentName;
    
    @Column(name = "attachment_type", length = 50)
    private String attachmentType;
    
    @Column(name = "attachment_size")
    private Long attachmentSize;
    
    @Column(name = "is_system_message")
    private Boolean isSystemMessage = false;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON for additional data
    
    // Relationships
    @OneToMany(mappedBy = "replyToMessageId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> replies;
    
    // Enums
    public enum MessageType {
        TEXT,           // Plain text message
        IMAGE,          // Image attachment
        FILE,           // File attachment
        SYSTEM,         // System-generated message
        OFFER,          // Price offer/negotiation
        QUICK_REPLY,    // Pre-defined quick reply
        LOCATION,       // Location sharing
        CONTACT         // Contact information
    }
    
    public enum MessageStatus {
        SENT,           // Message sent
        DELIVERED,      // Message delivered to recipient
        READ,           // Message read by recipient
        FAILED,         // Message failed to send
        DELETED,        // Message deleted
        EDITED          // Message edited
    }
    
    // Constructors
    public Message() {
        this.sentAt = new Timestamp(System.currentTimeMillis());
    }
    
    public Message(Conversation conversation, User sender, User recipient, String messageContent) {
        this();
        this.conversation = conversation;
        this.sender = sender;
        this.recipient = recipient;
        this.messageContent = messageContent;
    }
    
    public Message(Conversation conversation, User sender, User recipient, String messageContent, MessageType messageType) {
        this(conversation, sender, recipient, messageContent);
        this.messageType = messageType;
    }
    
    // Getters and Setters
    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }
    
    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }
    
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    
    public Timestamp getSentAt() { return sentAt; }
    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }
    
    public Timestamp getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Timestamp deliveredAt) { this.deliveredAt = deliveredAt; }
    
    public Timestamp getReadAt() { return readAt; }
    public void setReadAt(Timestamp readAt) { this.readAt = readAt; }
    
    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }
    
    public Timestamp getEditedAt() { return editedAt; }
    public void setEditedAt(Timestamp editedAt) { this.editedAt = editedAt; }
    
    public Boolean getIsEdited() { return isEdited; }
    public void setIsEdited(Boolean isEdited) { this.isEdited = isEdited; }
    
    public Integer getReplyToMessageId() { return replyToMessageId; }
    public void setReplyToMessageId(Integer replyToMessageId) { this.replyToMessageId = replyToMessageId; }
    
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    
    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
    
    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }
    
    public Long getAttachmentSize() { return attachmentSize; }
    public void setAttachmentSize(Long attachmentSize) { this.attachmentSize = attachmentSize; }
    
    public Boolean getIsSystemMessage() { return isSystemMessage; }
    public void setIsSystemMessage(Boolean isSystemMessage) { this.isSystemMessage = isSystemMessage; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public List<Message> getReplies() { return replies; }
    public void setReplies(List<Message> replies) { this.replies = replies; }
    
    // Utility methods
    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.trim().isEmpty();
    }
    
    public boolean isImageAttachment() {
        return messageType == MessageType.IMAGE || 
               (attachmentType != null && attachmentType.startsWith("image/"));
    }
    
    public boolean isFileAttachment() {
        return messageType == MessageType.FILE && hasAttachment();
    }
    
    public boolean isRead() {
        return readAt != null;
    }
    
    public boolean isDelivered() {
        return deliveredAt != null;
    }
    
    public void markAsDelivered() {
        if (deliveredAt == null) {
            deliveredAt = new Timestamp(System.currentTimeMillis());
            status = MessageStatus.DELIVERED;
        }
    }
    
    public void markAsRead() {
        if (readAt == null) {
            readAt = new Timestamp(System.currentTimeMillis());
            status = MessageStatus.READ;
            if (deliveredAt == null) {
                markAsDelivered();
            }
        }
    }
    
    public void editMessage(String newContent) {
        this.messageContent = newContent;
        this.editedAt = new Timestamp(System.currentTimeMillis());
        this.isEdited = true;
        this.status = MessageStatus.EDITED;
    }
    
    public String getFormattedSentTime() {
        if (sentAt == null) return "";
        
        long now = System.currentTimeMillis();
        long sent = sentAt.getTime();
        long diff = now - sent;
        
        // Less than 1 minute
        if (diff < 60000) {
            return "Just now";
        }
        // Less than 1 hour
        else if (diff < 3600000) {
            int minutes = (int) (diff / 60000);
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        }
        // Less than 24 hours
        else if (diff < 86400000) {
            int hours = (int) (diff / 3600000);
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        }
        // More than 24 hours - show date
        else {
            return sentAt.toString().substring(0, 16); // YYYY-MM-DD HH:mm
        }
    }
    
    public String getShortContent(int maxLength) {
        if (messageContent == null) return "";
        if (messageContent.length() <= maxLength) return messageContent;
        return messageContent.substring(0, maxLength) + "...";
    }
    
    public String getStatusIcon() {
        switch (status) {
            case SENT: return "✓";
            case DELIVERED: return "✓✓";
            case READ: return "✓✓";
            case FAILED: return "✗";
            case EDITED: return "✏";
            default: return "";
        }
    }
}