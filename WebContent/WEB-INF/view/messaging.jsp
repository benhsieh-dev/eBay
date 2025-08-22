<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Messages - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .messaging-container {
            display: flex;
            height: calc(100vh - 80px);
            background: #f5f5f5;
        }
        
        .sidebar {
            width: 350px;
            background: white;
            border-right: 1px solid #e5e5e5;
            display: flex;
            flex-direction: column;
        }
        
        .sidebar-header {
            padding: 20px;
            border-bottom: 1px solid #e5e5e5;
            background: #f8f9fa;
        }
        
        .sidebar-header h2 {
            margin: 0 0 10px 0;
            font-size: 20px;
            color: #333;
        }
        
        .message-stats {
            display: flex;
            gap: 15px;
            margin-top: 10px;
        }
        
        .stat-item {
            font-size: 12px;
            color: #666;
        }
        
        .stat-number {
            font-weight: bold;
            color: #007bff;
        }
        
        .search-box {
            padding: 15px;
            border-bottom: 1px solid #e5e5e5;
        }
        
        .search-input {
            width: 100%;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 25px;
            font-size: 14px;
            background: #f8f9fa;
        }
        
        .search-input:focus {
            outline: none;
            border-color: #007bff;
            background: white;
        }
        
        .conversation-list {
            flex: 1;
            overflow-y: auto;
        }
        
        .conversation-item {
            padding: 15px 20px;
            border-bottom: 1px solid #f0f0f0;
            cursor: pointer;
            transition: background-color 0.2s;
            position: relative;
        }
        
        .conversation-item:hover {
            background: #f8f9fa;
        }
        
        .conversation-item.active {
            background: #e3f2fd;
            border-right: 3px solid #007bff;
        }
        
        .conversation-item.unread {
            background: #fff3cd;
        }
        
        .conversation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 5px;
        }
        
        .participant-name {
            font-weight: 600;
            color: #333;
        }
        
        .conversation-time {
            font-size: 12px;
            color: #666;
        }
        
        .conversation-subject {
            font-size: 13px;
            color: #666;
            margin-bottom: 5px;
        }
        
        .last-message {
            font-size: 14px;
            color: #333;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .unread-badge {
            position: absolute;
            top: 15px;
            right: 15px;
            background: #dc3545;
            color: white;
            border-radius: 10px;
            padding: 2px 8px;
            font-size: 12px;
            font-weight: bold;
        }
        
        .main-chat {
            flex: 1;
            display: flex;
            flex-direction: column;
            background: white;
        }
        
        .chat-header {
            padding: 20px;
            border-bottom: 1px solid #e5e5e5;
            background: #f8f9fa;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .chat-participant {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .participant-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: linear-gradient(45deg, #007bff, #0056b3);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
        }
        
        .participant-info h3 {
            margin: 0;
            font-size: 16px;
            color: #333;
        }
        
        .participant-info .status {
            font-size: 12px;
            color: #666;
        }
        
        .chat-actions {
            display: flex;
            gap: 10px;
        }
        
        .action-btn {
            padding: 8px 12px;
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
            transition: all 0.2s;
        }
        
        .action-btn:hover {
            background: #e9ecef;
        }
        
        .messages-container {
            flex: 1;
            overflow-y: auto;
            padding: 20px;
            background: #f8f9fa;
        }
        
        .message-group {
            margin-bottom: 20px;
        }
        
        .message {
            margin-bottom: 10px;
            display: flex;
            align-items: flex-start;
            gap: 10px;
        }
        
        .message.sent {
            flex-direction: row-reverse;
        }
        
        .message-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: linear-gradient(45deg, #28a745, #20c997);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 12px;
            font-weight: bold;
            flex-shrink: 0;
        }
        
        .message.sent .message-avatar {
            background: linear-gradient(45deg, #007bff, #0056b3);
        }
        
        .message-content {
            max-width: 70%;
            background: white;
            border-radius: 18px;
            padding: 12px 16px;
            box-shadow: 0 1px 2px rgba(0,0,0,0.1);
            position: relative;
        }
        
        .message.sent .message-content {
            background: #007bff;
            color: white;
        }
        
        .message-text {
            margin: 0;
            line-height: 1.4;
        }
        
        .message-meta {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-top: 5px;
            font-size: 11px;
            color: rgba(0,0,0,0.5);
        }
        
        .message.sent .message-meta {
            color: rgba(255,255,255,0.8);
        }
        
        .message-time {
            margin-right: 5px;
        }
        
        .message-status {
            font-size: 10px;
        }
        
        .message-attachment {
            margin-top: 8px;
            padding: 10px;
            background: rgba(0,0,0,0.05);
            border-radius: 8px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .message.sent .message-attachment {
            background: rgba(255,255,255,0.2);
        }
        
        .attachment-icon {
            font-size: 18px;
        }
        
        .attachment-info {
            flex: 1;
        }
        
        .attachment-name {
            font-size: 12px;
            margin-bottom: 2px;
        }
        
        .attachment-size {
            font-size: 10px;
            opacity: 0.7;
        }
        
        .system-message {
            text-align: center;
            margin: 20px 0;
        }
        
        .system-message-content {
            display: inline-block;
            padding: 6px 12px;
            background: rgba(0,0,0,0.1);
            border-radius: 12px;
            font-size: 12px;
            color: #666;
        }
        
        .message-input-container {
            padding: 20px;
            background: white;
            border-top: 1px solid #e5e5e5;
        }
        
        .message-input-wrapper {
            display: flex;
            align-items: flex-end;
            gap: 10px;
            background: #f8f9fa;
            border-radius: 25px;
            padding: 10px 15px;
            border: 1px solid #e5e5e5;
        }
        
        .message-input-wrapper:focus-within {
            border-color: #007bff;
            background: white;
        }
        
        .attachment-btn {
            background: none;
            border: none;
            font-size: 20px;
            color: #666;
            cursor: pointer;
            padding: 5px;
            border-radius: 50%;
            transition: background-color 0.2s;
        }
        
        .attachment-btn:hover {
            background: rgba(0,0,0,0.1);
        }
        
        .message-input {
            flex: 1;
            border: none;
            background: none;
            outline: none;
            resize: none;
            max-height: 120px;
            min-height: 20px;
            font-family: inherit;
            font-size: 14px;
            line-height: 1.4;
        }
        
        .send-btn {
            background: #007bff;
            border: none;
            color: white;
            border-radius: 50%;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background-color 0.2s;
        }
        
        .send-btn:hover {
            background: #0056b3;
        }
        
        .send-btn:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
        
        .typing-indicator {
            padding: 10px 20px;
            font-size: 12px;
            color: #666;
            font-style: italic;
        }
        
        .no-conversation {
            flex: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            color: #666;
        }
        
        .no-conversation-icon {
            font-size: 64px;
            margin-bottom: 20px;
            opacity: 0.5;
        }
        
        .no-conversation h3 {
            margin-bottom: 10px;
        }
        
        .no-conversation p {
            text-align: center;
            max-width: 300px;
        }
        
        .quick-actions {
            margin-top: 20px;
            display: flex;
            gap: 10px;
        }
        
        .quick-action-btn {
            padding: 10px 20px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            text-decoration: none;
            font-size: 14px;
            transition: background-color 0.2s;
        }
        
        .quick-action-btn:hover {
            background: #0056b3;
            text-decoration: none;
            color: white;
        }
        
        .new-conversation-btn {
            position: absolute;
            bottom: 20px;
            right: 20px;
            width: 56px;
            height: 56px;
            border-radius: 50%;
            background: #007bff;
            color: white;
            border: none;
            font-size: 24px;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(0,123,255,0.3);
            transition: all 0.2s;
        }
        
        .new-conversation-btn:hover {
            background: #0056b3;
            transform: scale(1.1);
        }
        
        @media (max-width: 768px) {
            .messaging-container {
                flex-direction: column;
                height: auto;
            }
            
            .sidebar {
                width: 100%;
                height: 200px;
            }
            
            .conversation-list {
                display: flex;
                overflow-x: auto;
                overflow-y: hidden;
            }
            
            .conversation-item {
                min-width: 200px;
                flex-shrink: 0;
            }
            
            .main-chat {
                height: 60vh;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>
    
    <div class="messaging-container">
        <!-- Sidebar -->
        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Messages</h2>
                <div class="message-stats">
                    <div class="stat-item">
                        <span class="stat-number">${stats.activeConversations}</span>
                        Active
                    </div>
                    <div class="stat-item">
                        <span class="stat-number">${stats.totalUnreadMessages}</span>
                        Unread
                    </div>
                    <div class="stat-item">
                        <span class="stat-number">${stats.archivedConversations}</span>
                        Archived
                    </div>
                </div>
            </div>
            
            <div class="search-box">
                <input type="text" class="search-input" placeholder="Search conversations..." id="conversation-search">
            </div>
            
            <div class="conversation-list" id="conversation-list">
                <c:choose>
                    <c:when test="${not empty conversations}">
                        <c:forEach var="conversation" items="${conversations}">
                            <div class="conversation-item ${conversation.getUnreadCount(currentUser) > 0 ? 'unread' : ''}" 
                                 data-conversation-id="${conversation.conversationId}"
                                 onclick="selectConversation(${conversation.conversationId})">
                                
                                <div class="conversation-header">
                                    <span class="participant-name">
                                        ${conversation.getOtherParticipant(currentUser).fullName}
                                    </span>
                                    <span class="conversation-time">
                                        <c:choose>
                                            <c:when test="${conversation.lastMessageAt != null}">
                                                <fmt:formatDate value="${conversation.lastMessageAt}" pattern="MMM dd"/>
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:formatDate value="${conversation.createdAt}" pattern="MMM dd"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                
                                <div class="conversation-subject">
                                    ${conversation.displayTitle}
                                </div>
                                
                                <div class="last-message">
                                    <c:choose>
                                        <c:when test="${not empty conversation.messages}">
                                            ${conversation.messages[conversation.messages.size()-1].shortContent(50)}
                                        </c:when>
                                        <c:otherwise>
                                            No messages yet
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <c:if test="${conversation.getUnreadCount(currentUser) > 0}">
                                    <div class="unread-badge">${conversation.getUnreadCount(currentUser)}</div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div style="padding: 40px 20px; text-align: center; color: #666;">
                            <p>No conversations yet.</p>
                            <p>Start by messaging a seller about a product!</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <!-- Main Chat Area -->
        <div class="main-chat" id="main-chat">
            <div class="no-conversation">
                <div class="no-conversation-icon">💬</div>
                <h3>Select a conversation</h3>
                <p>Choose a conversation from the sidebar to start messaging, or create a new conversation.</p>
                
                <div class="quick-actions">
                    <a href="/eBay/products" class="quick-action-btn">Browse Products</a>
                    <button onclick="showNewConversationModal()" class="quick-action-btn">New Message</button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- New Conversation Button -->
    <button class="new-conversation-btn" onclick="showNewConversationModal()">+</button>
    
    <jsp:include page="footer.jsp"/>
    
    <script>
        let currentConversationId = null;
        let messagesContainer = null;
        let messageInput = null;
        
        // Initialize messaging interface
        document.addEventListener('DOMContentLoaded', function() {
            setupEventListeners();
            refreshConversations();
            
            // Auto-refresh conversations every 30 seconds
            setInterval(refreshConversations, 30000);
        });
        
        function setupEventListeners() {
            // Search functionality
            document.getElementById('conversation-search').addEventListener('input', function(e) {
                filterConversations(e.target.value);
            });
        }
        
        function selectConversation(conversationId) {
            currentConversationId = conversationId;
            
            // Update active state
            document.querySelectorAll('.conversation-item').forEach(item => {
                item.classList.remove('active');
            });
            document.querySelector(`[data-conversation-id="${conversationId}"]`).classList.add('active');
            
            loadConversation(conversationId);
        }
        
        function loadConversation(conversationId) {
            fetch(`/eBay/messages/api/conversation/${conversationId}/messages`)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        displayConversation(data.messages, conversationId);
                        markConversationAsRead(conversationId);
                    } else {
                        console.error('Error loading conversation:', data.message);
                    }
                })
                .catch(error => {
                    console.error('Error loading conversation:', error);
                });
        }
        
        function displayConversation(messages, conversationId) {
            const mainChat = document.getElementById('main-chat');
            
            // Get conversation info
            const conversationItem = document.querySelector(`[data-conversation-id="${conversationId}"]`);
            const participantName = conversationItem.querySelector('.participant-name').textContent;
            const subject = conversationItem.querySelector('.conversation-subject').textContent;
            
            mainChat.innerHTML = `
                <div class="chat-header">
                    <div class="chat-participant">
                        <div class="participant-avatar">${participantName.charAt(0)}</div>
                        <div class="participant-info">
                            <h3>${participantName}</h3>
                            <div class="status">${subject}</div>
                        </div>
                    </div>
                    <div class="chat-actions">
                        <button class="action-btn" onclick="archiveConversation(${conversationId})">Archive</button>
                        <button class="action-btn" onclick="closeConversation(${conversationId})">Close</button>
                    </div>
                </div>
                
                <div class="messages-container" id="messages-container">
                    ${renderMessages(messages)}
                </div>
                
                <div class="message-input-container">
                    <div class="message-input-wrapper">
                        <button class="attachment-btn" onclick="document.getElementById('file-input').click()">📎</button>
                        <textarea class="message-input" id="message-input" 
                                  placeholder="Type a message..." 
                                  onkeypress="handleMessageInputKeyPress(event)"></textarea>
                        <button class="send-btn" id="send-btn" onclick="sendMessage()">➤</button>
                    </div>
                </div>
                
                <input type="file" id="file-input" style="display: none" onchange="handleFileSelect(event)">
            `;
            
            messagesContainer = document.getElementById('messages-container');
            messageInput = document.getElementById('message-input');
            
            // Scroll to bottom
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
            
            // Auto-resize textarea
            messageInput.addEventListener('input', function() {
                this.style.height = 'auto';
                this.style.height = this.scrollHeight + 'px';
            });
        }
        
        function renderMessages(messages) {
            if (!messages || messages.length === 0) {
                return '<div style="text-align: center; padding: 40px; color: #666;">No messages yet. Start the conversation!</div>';
            }
            
            let html = '';
            let currentUser = '${currentUser.userId}';
            
            messages.forEach(message => {
                const isSent = message.sender.userId == currentUser;
                const isSystem = message.isSystemMessage;
                
                if (isSystem) {
                    html += `
                        <div class="system-message">
                            <div class="system-message-content">${message.messageContent}</div>
                        </div>
                    `;
                } else {
                    html += `
                        <div class="message ${isSent ? 'sent' : ''}">
                            <div class="message-avatar">${message.sender.firstName.charAt(0)}</div>
                            <div class="message-content">
                                <p class="message-text">${message.messageContent}</p>
                                ${message.hasAttachment() ? renderAttachment(message) : ''}
                                <div class="message-meta">
                                    <span class="message-time">${formatMessageTime(message.sentAt)}</span>
                                    <span class="message-status">${message.statusIcon}</span>
                                </div>
                            </div>
                        </div>
                    `;
                }
            });
            
            return html;
        }
        
        function renderAttachment(message) {
            const isImage = message.attachmentType && message.attachmentType.startsWith('image/');
            
            if (isImage) {
                return `
                    <div class="message-attachment">
                        <img src="${message.attachmentUrl}" alt="${message.attachmentName}" 
                             style="max-width: 200px; max-height: 200px; border-radius: 8px;">
                    </div>
                `;
            } else {
                return `
                    <div class="message-attachment">
                        <div class="attachment-icon">📎</div>
                        <div class="attachment-info">
                            <div class="attachment-name">${message.attachmentName}</div>
                            <div class="attachment-size">${formatFileSize(message.attachmentSize)}</div>
                        </div>
                        <button onclick="downloadAttachment('${message.attachmentUrl}', '${message.attachmentName}')">
                            Download
                        </button>
                    </div>
                `;
            }
        }
        
        function handleMessageInputKeyPress(event) {
            if (event.key === 'Enter' && !event.shiftKey) {
                event.preventDefault();
                sendMessage();
            }
        }
        
        function sendMessage() {
            if (!currentConversationId || !messageInput) return;
            
            const content = messageInput.value.trim();
            if (!content) return;
            
            const sendBtn = document.getElementById('send-btn');
            sendBtn.disabled = true;
            messageInput.disabled = true;
            
            fetch('/eBay/messages/api/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `conversationId=${currentConversationId}&content=${encodeURIComponent(content)}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    messageInput.value = '';
                    messageInput.style.height = 'auto';
                    loadConversation(currentConversationId);
                } else {
                    alert('Error sending message: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error sending message:', error);
                alert('Error sending message. Please try again.');
            })
            .finally(() => {
                sendBtn.disabled = false;
                messageInput.disabled = false;
                messageInput.focus();
            });
        }
        
        function handleFileSelect(event) {
            const file = event.target.files[0];
            if (!file || !currentConversationId) return;
            
            if (file.size > 10 * 1024 * 1024) {
                alert('File size must be less than 10MB');
                return;
            }
            
            const formData = new FormData();
            formData.append('conversationId', currentConversationId);
            formData.append('content', `Shared a file: ${file.name}`);
            formData.append('file', file);
            
            fetch('/eBay/messages/api/send-attachment', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    loadConversation(currentConversationId);
                } else {
                    alert('Error sending attachment: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error sending attachment:', error);
                alert('Error sending attachment. Please try again.');
            });
        }
        
        function markConversationAsRead(conversationId) {
            fetch(`/eBay/messages/api/conversation/${conversationId}/read`, {
                method: 'PUT'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Remove unread styling
                    const conversationItem = document.querySelector(`[data-conversation-id="${conversationId}"]`);
                    conversationItem.classList.remove('unread');
                    
                    const unreadBadge = conversationItem.querySelector('.unread-badge');
                    if (unreadBadge) {
                        unreadBadge.remove();
                    }
                }
            })
            .catch(error => {
                console.error('Error marking as read:', error);
            });
        }
        
        function archiveConversation(conversationId) {
            if (confirm('Archive this conversation?')) {
                fetch(`/eBay/messages/api/conversation/${conversationId}/archive`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'archived=true'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload();
                    } else {
                        alert('Error archiving conversation: ' + data.message);
                    }
                });
            }
        }
        
        function closeConversation(conversationId) {
            if (confirm('Close this conversation? You can reopen it later if needed.')) {
                fetch(`/eBay/messages/api/conversation/${conversationId}/close`, {
                    method: 'PUT'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        loadConversation(conversationId);
                    } else {
                        alert('Error closing conversation: ' + data.message);
                    }
                });
            }
        }
        
        function refreshConversations() {
            fetch('/eBay/messages/api/conversations')
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Update conversation list without full page reload
                        // This would require more complex DOM manipulation
                        // For now, we'll just log the data
                        console.log('Conversations refreshed');
                    }
                });
        }
        
        function filterConversations(searchTerm) {
            const conversations = document.querySelectorAll('.conversation-item');
            conversations.forEach(conversation => {
                const text = conversation.textContent.toLowerCase();
                if (text.includes(searchTerm.toLowerCase())) {
                    conversation.style.display = 'block';
                } else {
                    conversation.style.display = 'none';
                }
            });
        }
        
        function showNewConversationModal() {
            // This would show a modal to start a new conversation
            alert('New conversation feature - would open a modal to select recipient and start conversation');
        }
        
        // Utility functions
        function formatMessageTime(timestamp) {
            const date = new Date(timestamp);
            const now = new Date();
            const diff = now - date;
            
            if (diff < 60000) return 'Just now';
            if (diff < 3600000) return Math.floor(diff / 60000) + 'm';
            if (diff < 86400000) return Math.floor(diff / 3600000) + 'h';
            return date.toLocaleDateString();
        }
        
        function formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        }
        
        function downloadAttachment(url, filename) {
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
        }
    </script>
</body>
</html>