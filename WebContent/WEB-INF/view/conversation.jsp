<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Conversation - eBay Messages</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .conversation-container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            min-height: calc(100vh - 120px);
            display: flex;
            flex-direction: column;
        }
        
        .conversation-header {
            padding: 20px;
            border-bottom: 1px solid #e5e5e5;
            background: #f8f9fa;
        }
        
        .back-button {
            color: #007bff;
            text-decoration: none;
            margin-bottom: 15px;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            font-size: 14px;
        }
        
        .back-button:hover {
            text-decoration: underline;
        }
        
        .conversation-info {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .participant-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: linear-gradient(45deg, #007bff, #0056b3);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 20px;
            font-weight: bold;
        }
        
        .conversation-details h2 {
            margin: 0 0 5px 0;
            font-size: 18px;
            color: #333;
        }
        
        .conversation-subject {
            color: #666;
            font-size: 14px;
            margin-bottom: 5px;
        }
        
        .conversation-meta {
            font-size: 12px;
            color: #999;
        }
        
        .messages-section {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            background: #f8f9fa;
        }
        
        .message {
            margin-bottom: 20px;
            display: flex;
            align-items: flex-start;
            gap: 10px;
        }
        
        .message.sent {
            flex-direction: row-reverse;
        }
        
        .message-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background: linear-gradient(45deg, #28a745, #20c997);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 14px;
            font-weight: bold;
            flex-shrink: 0;
        }
        
        .message.sent .message-avatar {
            background: linear-gradient(45deg, #007bff, #0056b3);
        }
        
        .message-bubble {
            max-width: 70%;
            background: white;
            border-radius: 18px;
            padding: 12px 16px;
            box-shadow: 0 1px 2px rgba(0,0,0,0.1);
            position: relative;
        }
        
        .message.sent .message-bubble {
            background: #007bff;
            color: white;
        }
        
        .message-content {
            margin: 0;
            line-height: 1.5;
            word-wrap: break-word;
        }
        
        .message-attachment {
            margin-top: 10px;
            padding: 10px;
            background: rgba(0,0,0,0.05);
            border-radius: 8px;
        }
        
        .message.sent .message-attachment {
            background: rgba(255,255,255,0.2);
        }
        
        .attachment-image {
            max-width: 100%;
            border-radius: 8px;
            cursor: pointer;
        }
        
        .attachment-file {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .file-icon {
            font-size: 24px;
        }
        
        .file-info {
            flex: 1;
        }
        
        .file-name {
            font-weight: 600;
            margin-bottom: 2px;
        }
        
        .file-size {
            font-size: 12px;
            opacity: 0.7;
        }
        
        .download-btn {
            padding: 6px 12px;
            background: rgba(0,0,0,0.1);
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
        }
        
        .message.sent .download-btn {
            background: rgba(255,255,255,0.3);
            color: white;
        }
        
        .message-meta {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-top: 8px;
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
        
        .system-message {
            text-align: center;
            margin: 20px 0;
        }
        
        .system-content {
            display: inline-block;
            padding: 8px 16px;
            background: rgba(0,0,0,0.1);
            border-radius: 20px;
            font-size: 13px;
            color: #666;
        }
        
        .message-input-section {
            padding: 20px;
            background: white;
            border-top: 1px solid #e5e5e5;
        }
        
        .input-wrapper {
            display: flex;
            align-items: flex-end;
            gap: 10px;
            background: #f8f9fa;
            border-radius: 25px;
            padding: 10px 15px;
            border: 1px solid #e5e5e5;
            transition: all 0.2s;
        }
        
        .input-wrapper:focus-within {
            border-color: #007bff;
            background: white;
            box-shadow: 0 0 0 3px rgba(0,123,255,0.1);
        }
        
        .attachment-button {
            background: none;
            border: none;
            font-size: 20px;
            color: #666;
            cursor: pointer;
            padding: 5px;
            border-radius: 50%;
            transition: background-color 0.2s;
        }
        
        .attachment-button:hover {
            background: rgba(0,0,0,0.1);
        }
        
        .message-textarea {
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
        
        .send-button {
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
        
        .send-button:hover {
            background: #0056b3;
        }
        
        .send-button:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
        
        .typing-indicator {
            padding: 10px 0;
            font-size: 12px;
            color: #666;
            font-style: italic;
            display: none;
        }
        
        .conversation-actions {
            display: flex;
            gap: 10px;
            margin-left: auto;
        }
        
        .action-button {
            padding: 8px 16px;
            background: white;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            cursor: pointer;
            font-size: 12px;
            color: #666;
            text-decoration: none;
            transition: all 0.2s;
        }
        
        .action-button:hover {
            background: #f8f9fa;
            text-decoration: none;
            color: #333;
        }
        
        .no-messages {
            text-align: center;
            padding: 60px 20px;
            color: #666;
        }
        
        .no-messages-icon {
            font-size: 48px;
            margin-bottom: 20px;
            opacity: 0.5;
        }
        
        @media (max-width: 768px) {
            .conversation-container {
                margin: 0;
                min-height: calc(100vh - 60px);
            }
            
            .conversation-header {
                padding: 15px;
            }
            
            .messages-section {
                padding: 15px;
            }
            
            .message-input-section {
                padding: 15px;
            }
            
            .message-bubble {
                max-width: 85%;
            }
            
            .conversation-actions {
                margin-top: 10px;
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>
    
    <div class="conversation-container">
        <!-- Header -->
        <div class="conversation-header">
            <a href="/eBay/messages" class="back-button">← Back to Messages</a>
            
            <div style="display: flex; align-items: center; justify-content: space-between;">
                <div class="conversation-info">
                    <c:set var="otherParticipant" value="${messages[0].conversation.getOtherParticipant(currentUser)}" />
                    <div class="participant-avatar">${otherParticipant.firstName.substring(0,1)}</div>
                    <div class="conversation-details">
                        <h2>${otherParticipant.fullName}</h2>
                        <div class="conversation-subject">
                            <c:choose>
                                <c:when test="${not empty messages[0].conversation.displayTitle}">
                                    ${messages[0].conversation.displayTitle}
                                </c:when>
                                <c:otherwise>
                                    Conversation
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="conversation-meta">
                            Started <fmt:formatDate value="${messages[0].conversation.createdAt}" pattern="MMM dd, yyyy 'at' HH:mm"/>
                        </div>
                    </div>
                </div>
                
                <div class="conversation-actions">
                    <button class="action-button" onclick="archiveConversation()">Archive</button>
                    <button class="action-button" onclick="closeConversation()">Close</button>
                </div>
            </div>
        </div>
        
        <!-- Messages -->
        <div class="messages-section" id="messages-section">
            <c:choose>
                <c:when test="${not empty messages}">
                    <c:forEach var="message" items="${messages}">
                        <c:choose>
                            <c:when test="${message.isSystemMessage}">
                                <div class="system-message">
                                    <div class="system-content">${message.messageContent}</div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="message ${message.sender.userId == currentUser.userId ? 'sent' : ''}">
                                    <div class="message-avatar">${message.sender.firstName.substring(0,1)}</div>
                                    <div class="message-bubble">
                                        <p class="message-content">${message.messageContent}</p>
                                        
                                        <c:if test="${message.hasAttachment()}">
                                            <div class="message-attachment">
                                                <c:choose>
                                                    <c:when test="${message.isImageAttachment()}">
                                                        <img src="${message.attachmentUrl}" 
                                                             alt="${message.attachmentName}" 
                                                             class="attachment-image"
                                                             onclick="openImageModal('${message.attachmentUrl}', '${message.attachmentName}')">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="attachment-file">
                                                            <div class="file-icon">📎</div>
                                                            <div class="file-info">
                                                                <div class="file-name">${message.attachmentName}</div>
                                                                <div class="file-size">
                                                                    <c:choose>
                                                                        <c:when test="${message.attachmentSize != null}">
                                                                            <fmt:formatNumber value="${message.attachmentSize / 1024}" pattern="#,##0" /> KB
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            Unknown size
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </div>
                                                            <button class="download-btn" 
                                                                    onclick="downloadFile('${message.attachmentUrl}', '${message.attachmentName}')">
                                                                Download
                                                            </button>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </c:if>
                                        
                                        <div class="message-meta">
                                            <span class="message-time">
                                                <fmt:formatDate value="${message.sentAt}" pattern="MMM dd, HH:mm"/>
                                            </span>
                                            <span class="message-status">
                                                <c:choose>
                                                    <c:when test="${message.status == 'READ'}">✓✓</c:when>
                                                    <c:when test="${message.status == 'DELIVERED'}">✓✓</c:when>
                                                    <c:when test="${message.status == 'SENT'}">✓</c:when>
                                                    <c:when test="${message.status == 'FAILED'}">✗</c:when>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="no-messages">
                        <div class="no-messages-icon">💬</div>
                        <h3>No messages yet</h3>
                        <p>Start the conversation by sending a message below.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Typing Indicator -->
        <div class="typing-indicator" id="typing-indicator">
            Someone is typing...
        </div>
        
        <!-- Message Input -->
        <div class="message-input-section">
            <div class="input-wrapper">
                <button class="attachment-button" onclick="document.getElementById('file-input').click()" title="Attach file">
                    📎
                </button>
                <textarea class="message-textarea" 
                          id="message-input" 
                          placeholder="Type your message..."
                          onkeypress="handleKeyPress(event)"
                          oninput="handleInput()"></textarea>
                <button class="send-button" id="send-btn" onclick="sendMessage()" title="Send message">
                    ➤
                </button>
            </div>
            <input type="file" id="file-input" style="display: none" onchange="handleFileUpload(event)">
        </div>
    </div>
    
    <jsp:include page="footer.jsp"/>
    
    <script>
        const conversationId = ${conversationId};
        const currentUserId = ${currentUser.userId};
        let messageInput;
        let sendButton;
        
        document.addEventListener('DOMContentLoaded', function() {
            messageInput = document.getElementById('message-input');
            sendButton = document.getElementById('send-btn');
            
            // Auto-resize textarea
            messageInput.addEventListener('input', function() {
                this.style.height = 'auto';
                this.style.height = this.scrollHeight + 'px';
            });
            
            // Scroll to bottom
            scrollToBottom();
            
            // Auto-refresh messages every 10 seconds
            setInterval(refreshMessages, 10000);
        });
        
        function handleKeyPress(event) {
            if (event.key === 'Enter' && !event.shiftKey) {
                event.preventDefault();
                sendMessage();
            }
        }
        
        function handleInput() {
            // Auto-resize textarea
            messageInput.style.height = 'auto';
            messageInput.style.height = messageInput.scrollHeight + 'px';
            
            // Show typing indicator (in real implementation with WebSocket)
            // showTypingIndicator();
        }
        
        function sendMessage() {
            const content = messageInput.value.trim();
            if (!content) return;
            
            sendButton.disabled = true;
            messageInput.disabled = true;
            
            fetch('/eBay/messages/api/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `conversationId=${conversationId}&content=${encodeURIComponent(content)}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    messageInput.value = '';
                    messageInput.style.height = 'auto';
                    appendMessage(data.message);
                    scrollToBottom();
                } else {
                    alert('Failed to send message: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error sending message:', error);
                alert('Error sending message. Please try again.');
            })
            .finally(() => {
                sendButton.disabled = false;
                messageInput.disabled = false;
                messageInput.focus();
            });
        }
        
        function handleFileUpload(event) {
            const file = event.target.files[0];
            if (!file) return;
            
            if (file.size > 10 * 1024 * 1024) {
                alert('File size must be less than 10MB');
                return;
            }
            
            const formData = new FormData();
            formData.append('conversationId', conversationId);
            formData.append('content', `Shared: ${file.name}`);
            formData.append('file', file);
            
            sendButton.disabled = true;
            
            fetch('/eBay/messages/api/send-attachment', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    appendMessage(data.message);
                    scrollToBottom();
                } else {
                    alert('Failed to send attachment: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error sending attachment:', error);
                alert('Error sending attachment. Please try again.');
            })
            .finally(() => {
                sendButton.disabled = false;
                // Clear file input
                event.target.value = '';
            });
        }
        
        function appendMessage(message) {
            const messagesSection = document.getElementById('messages-section');
            const isSent = message.sender.userId == currentUserId;
            const isSystem = message.isSystemMessage;
            
            let messageHtml;
            
            if (isSystem) {
                messageHtml = `
                    <div class="system-message">
                        <div class="system-content">${message.messageContent}</div>
                    </div>
                `;
            } else {
                messageHtml = `
                    <div class="message ${isSent ? 'sent' : ''}">
                        <div class="message-avatar">${message.sender.firstName.charAt(0)}</div>
                        <div class="message-bubble">
                            <p class="message-content">${message.messageContent}</p>
                            ${message.hasAttachment ? renderAttachment(message) : ''}
                            <div class="message-meta">
                                <span class="message-time">${formatTime(message.sentAt)}</span>
                                <span class="message-status">✓</span>
                            </div>
                        </div>
                    </div>
                `;
            }
            
            messagesSection.insertAdjacentHTML('beforeend', messageHtml);
        }
        
        function renderAttachment(message) {
            if (message.isImageAttachment) {
                return `
                    <div class="message-attachment">
                        <img src="${message.attachmentUrl}" alt="${message.attachmentName}" class="attachment-image">
                    </div>
                `;
            } else {
                return `
                    <div class="message-attachment">
                        <div class="attachment-file">
                            <div class="file-icon">📎</div>
                            <div class="file-info">
                                <div class="file-name">${message.attachmentName}</div>
                                <div class="file-size">${formatFileSize(message.attachmentSize)}</div>
                            </div>
                            <button class="download-btn" onclick="downloadFile('${message.attachmentUrl}', '${message.attachmentName}')">
                                Download
                            </button>
                        </div>
                    </div>
                `;
            }
        }
        
        function scrollToBottom() {
            const messagesSection = document.getElementById('messages-section');
            messagesSection.scrollTop = messagesSection.scrollHeight;
        }
        
        function refreshMessages() {
            fetch(`/eBay/messages/api/conversation/${conversationId}/messages`)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Simple refresh - reload page if new messages
                        const currentMessageCount = document.querySelectorAll('.message, .system-message').length;
                        if (data.messages.length > currentMessageCount) {
                            location.reload();
                        }
                    }
                })
                .catch(error => {
                    console.error('Error refreshing messages:', error);
                });
        }
        
        function archiveConversation() {
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
                        window.location.href = '/eBay/messages';
                    } else {
                        alert('Error archiving conversation: ' + data.message);
                    }
                });
            }
        }
        
        function closeConversation() {
            if (confirm('Close this conversation?')) {
                fetch(`/eBay/messages/api/conversation/${conversationId}/close`, {
                    method: 'PUT'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload();
                    } else {
                        alert('Error closing conversation: ' + data.message);
                    }
                });
            }
        }
        
        function downloadFile(url, filename) {
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
        }
        
        function openImageModal(url, alt) {
            // Simple image modal - in production would use a proper modal library
            const modal = document.createElement('div');
            modal.style.cssText = `
                position: fixed; top: 0; left: 0; width: 100%; height: 100%; 
                background: rgba(0,0,0,0.8); display: flex; align-items: center; 
                justify-content: center; z-index: 10000; cursor: pointer;
            `;
            
            const img = document.createElement('img');
            img.src = url;
            img.alt = alt;
            img.style.cssText = 'max-width: 90%; max-height: 90%; border-radius: 8px;';
            
            modal.appendChild(img);
            document.body.appendChild(modal);
            
            modal.onclick = () => document.body.removeChild(modal);
        }
        
        // Utility functions
        function formatTime(timestamp) {
            const date = new Date(timestamp);
            return date.toLocaleString('en-US', {
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
        }
        
        function formatFileSize(bytes) {
            if (!bytes) return 'Unknown size';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
        }
    </script>
</body>
</html>