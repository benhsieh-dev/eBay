package controller;

import entity.Conversation;
import entity.Message;
import entity.User;
import service.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import service.FileUploadService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/messages")
public class MessagingController {
    
    @Autowired
    private MessagingService messagingService;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    // View Controllers
    
    /**
     * Show main messaging interface
     */
    @GetMapping
    public ModelAndView showMessaging(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        List<Conversation> conversations = messagingService.getUserConversations(currentUser.getUserId());
        Map<String, Object> stats = messagingService.getMessagingStats(currentUser.getUserId());
        
        mv.setViewName("messaging");
        mv.addObject("conversations", conversations);
        mv.addObject("stats", stats);
        mv.addObject("currentUser", currentUser);
        
        return mv;
    }
    
    /**
     * Show specific conversation
     */
    @GetMapping("/conversation/{conversationId}")
    public ModelAndView showConversation(@PathVariable Integer conversationId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            List<Message> messages = messagingService.getConversationMessages(conversationId, currentUser.getUserId());
            
            // Mark conversation as read
            messagingService.markConversationAsRead(conversationId, currentUser.getUserId());
            
            mv.setViewName("conversation");
            mv.addObject("messages", messages);
            mv.addObject("conversationId", conversationId);
            mv.addObject("currentUser", currentUser);
            
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/messages");
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
    
    /**
     * Show archived conversations
     */
    @GetMapping("/archived")
    public ModelAndView showArchived(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        List<Conversation> archivedConversations = messagingService.getUserArchivedConversations(currentUser.getUserId());
        
        mv.setViewName("messages-archived");
        mv.addObject("conversations", archivedConversations);
        mv.addObject("currentUser", currentUser);
        
        return mv;
    }
    
    // REST API Endpoints
    
    /**
     * Get conversations for current user
     */
    @GetMapping("/api/conversations")
    @ResponseBody
    public Map<String, Object> getConversations(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            List<Conversation> conversations = messagingService.getUserConversations(currentUser.getUserId());
            Map<String, Object> stats = messagingService.getMessagingStats(currentUser.getUserId());
            
            response.put("success", true);
            response.put("conversations", conversations);
            response.put("stats", stats);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get messages in a conversation
     */
    @GetMapping("/api/conversation/{conversationId}/messages")
    @ResponseBody
    public Map<String, Object> getConversationMessages(@PathVariable Integer conversationId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "50") int pageSize,
                                                      HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            List<Message> messages;
            if (page == 0 && pageSize == 50) {
                messages = messagingService.getConversationMessages(conversationId, currentUser.getUserId());
            } else {
                messages = messagingService.getConversationMessages(conversationId, currentUser.getUserId(), page, pageSize);
            }
            
            response.put("success", true);
            response.put("messages", messages);
            response.put("page", page);
            response.put("pageSize", pageSize);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Send a new message
     */
    @PostMapping("/api/send")
    @ResponseBody
    public Map<String, Object> sendMessage(@RequestParam Integer conversationId,
                                         @RequestParam String content,
                                         @RequestParam(required = false) String messageType,
                                         @RequestParam(required = false) Integer replyToMessageId,
                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            Message message;
            
            if (replyToMessageId != null) {
                message = messagingService.replyToMessage(replyToMessageId, currentUser.getUserId(), content);
            } else {
                Message.MessageType type = messageType != null ? 
                    Message.MessageType.valueOf(messageType.toUpperCase()) : Message.MessageType.TEXT;
                message = messagingService.sendMessage(conversationId, currentUser.getUserId(), content, type);
            }
            
            response.put("success", true);
            response.put("message", message);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Send message with attachment
     */
    @PostMapping("/api/send-attachment")
    @ResponseBody
    public Map<String, Object> sendMessageWithAttachment(@RequestParam Integer conversationId,
                                                        @RequestParam String content,
                                                        @RequestParam("file") MultipartFile file,
                                                        HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            // Upload file using FileUploadService
            Map<String, Object> uploadResult = fileUploadService.uploadFile(file);
            
            if (!(Boolean) uploadResult.get("success")) {
                response.put("success", false);
                response.put("message", "File upload failed");
                return response;
            }
            
            String attachmentUrl = (String) uploadResult.get("url");
            String attachmentType = (String) uploadResult.get("type");
            String originalName = (String) uploadResult.get("originalName");
            Long attachmentSize = (Long) uploadResult.get("size");
            
            Message message = messagingService.sendMessageWithAttachment(
                conversationId, currentUser.getUserId(), content,
                attachmentUrl, originalName, attachmentType, attachmentSize);
            
            response.put("success", true);
            response.put("message", message);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Edit a message
     */
    @PutMapping("/api/message/{messageId}")
    @ResponseBody
    public Map<String, Object> editMessage(@PathVariable Integer messageId,
                                         @RequestParam String content,
                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            Message message = messagingService.editMessage(messageId, currentUser.getUserId(), content);
            
            response.put("success", true);
            response.put("message", message);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Delete a message
     */
    @DeleteMapping("/api/message/{messageId}")
    @ResponseBody
    public Map<String, Object> deleteMessage(@PathVariable Integer messageId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            messagingService.deleteMessage(messageId, currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Message deleted successfully");
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Create new conversation
     */
    @PostMapping("/api/conversations")
    @ResponseBody
    public Map<String, Object> createConversation(@RequestParam Integer otherUserId,
                                                 @RequestParam(required = false) Integer productId,
                                                 @RequestParam(required = false) Integer orderId,
                                                 @RequestParam String subject,
                                                 @RequestParam(required = false) String type,
                                                 HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            Conversation conversation;
            
            if (productId != null) {
                conversation = messagingService.createProductInquiry(currentUser.getUserId(), productId, subject);
            } else if (orderId != null) {
                conversation = messagingService.createOrderDiscussion(orderId, subject);
            } else {
                Conversation.ConversationType conversationType = type != null ?
                    Conversation.ConversationType.valueOf(type.toUpperCase()) : 
                    Conversation.ConversationType.GENERAL_SUPPORT;
                conversation = messagingService.createConversation(
                    currentUser.getUserId(), otherUserId, conversationType, subject);
            }
            
            response.put("success", true);
            response.put("conversation", conversation);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Archive/Unarchive conversation
     */
    @PutMapping("/api/conversation/{conversationId}/archive")
    @ResponseBody
    public Map<String, Object> archiveConversation(@PathVariable Integer conversationId,
                                                  @RequestParam boolean archived,
                                                  HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            messagingService.setConversationArchived(conversationId, currentUser.getUserId(), archived);
            
            response.put("success", true);
            response.put("message", archived ? "Conversation archived" : "Conversation unarchived");
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Close conversation
     */
    @PutMapping("/api/conversation/{conversationId}/close")
    @ResponseBody
    public Map<String, Object> closeConversation(@PathVariable Integer conversationId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            messagingService.closeConversation(conversationId, currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Conversation closed");
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Mark conversation as read
     */
    @PutMapping("/api/conversation/{conversationId}/read")
    @ResponseBody
    public Map<String, Object> markAsRead(@PathVariable Integer conversationId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            messagingService.markConversationAsRead(conversationId, currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Conversation marked as read");
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Search messages
     */
    @GetMapping("/api/search")
    @ResponseBody
    public Map<String, Object> searchMessages(@RequestParam String query,
                                            @RequestParam(required = false) Integer conversationId,
                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            List<Message> messages;
            
            if (conversationId != null) {
                messages = messagingService.searchInConversation(conversationId, currentUser.getUserId(), query);
            } else {
                messages = messagingService.searchMessages(currentUser.getUserId(), query);
            }
            
            response.put("success", true);
            response.put("messages", messages);
            response.put("query", query);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get messaging statistics
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public Map<String, Object> getMessagingStats(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            Map<String, Object> stats = messagingService.getMessagingStats(currentUser.getUserId());
            Long unreadCount = messagingService.getUnreadMessageCount(currentUser.getUserId());
            
            response.put("success", true);
            response.put("stats", stats);
            response.put("unreadMessageCount", unreadCount);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Helper endpoint for getting current user info
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}