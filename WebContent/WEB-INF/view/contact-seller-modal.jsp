<!-- Contact Seller Modal -->
<div id="contact-seller-modal" class="modal" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Contact Seller</h3>
            <span class="close" onclick="closeContactSellerModal()">&times;</span>
        </div>
        <div class="modal-body">
            <form id="contact-seller-form">
                <input type="hidden" id="seller-id" value="">
                <input type="hidden" id="product-id" value="">
                
                <div class="form-group">
                    <label for="message-subject">Subject</label>
                    <select id="message-subject" onchange="updateSubjectField()">
                        <option value="PRODUCT_INQUIRY">General Question</option>
                        <option value="NEGOTIATION">Price Negotiation</option>
                        <option value="SHIPPING_INQUIRY">Shipping Question</option>
                        <option value="PRODUCT_CONDITION">Product Condition</option>
                        <option value="BULK_ORDER">Bulk Order Inquiry</option>
                        <option value="CUSTOM">Custom Subject</option>
                    </select>
                </div>
                
                <div class="form-group" id="custom-subject-group" style="display: none;">
                    <label for="custom-subject">Custom Subject</label>
                    <input type="text" id="custom-subject" placeholder="Enter your subject">
                </div>
                
                <div class="form-group">
                    <label for="message-content">Message</label>
                    <textarea id="message-content" rows="5" placeholder="Type your message to the seller..."></textarea>
                </div>
                
                <div class="quick-messages">
                    <p>Quick messages:</p>
                    <div class="quick-message-buttons">
                        <button type="button" onclick="setQuickMessage('Is this item still available?')">Still available?</button>
                        <button type="button" onclick="setQuickMessage('What is the condition of this item?')">Item condition?</button>
                        <button type="button" onclick="setQuickMessage('Do you accept offers?')">Accept offers?</button>
                        <button type="button" onclick="setQuickMessage('What are the shipping options?')">Shipping options?</button>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" onclick="closeContactSellerModal()">Cancel</button>
            <button type="button" class="btn btn-primary" onclick="sendContactMessage()">Send Message</button>
        </div>
    </div>
</div>

<style>
.modal {
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0,0,0,0.5);
    display: flex;
    align-items: center;
    justify-content: center;
}

.modal-content {
    background-color: white;
    border-radius: 8px;
    width: 90%;
    max-width: 500px;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.modal-header {
    padding: 20px;
    border-bottom: 1px solid #e5e5e5;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h3 {
    margin: 0;
    color: #333;
}

.close {
    font-size: 24px;
    cursor: pointer;
    color: #999;
}

.close:hover {
    color: #333;
}

.modal-body {
    padding: 20px;
}

.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 600;
    color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    font-family: inherit;
}

.form-group textarea {
    resize: vertical;
    min-height: 100px;
}

.quick-messages {
    margin-top: 15px;
    padding-top: 15px;
    border-top: 1px solid #e5e5e5;
}

.quick-messages p {
    margin-bottom: 10px;
    font-size: 14px;
    color: #666;
}

.quick-message-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.quick-message-buttons button {
    padding: 6px 12px;
    background: #f8f9fa;
    border: 1px solid #dee2e6;
    border-radius: 20px;
    cursor: pointer;
    font-size: 12px;
    transition: all 0.2s;
}

.quick-message-buttons button:hover {
    background: #e9ecef;
    border-color: #adb5bd;
}

.modal-footer {
    padding: 20px;
    border-top: 1px solid #e5e5e5;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.2s;
}

.btn-secondary {
    background: #6c757d;
    color: white;
}

.btn-secondary:hover {
    background: #5a6268;
}

.btn-primary {
    background: #007bff;
    color: white;
}

.btn-primary:hover {
    background: #0056b3;
}

.btn:disabled {
    background: #ccc;
    cursor: not-allowed;
}
</style>

<script>
function showContactSellerModal(sellerId, productId, productTitle) {
    document.getElementById('seller-id').value = sellerId;
    document.getElementById('product-id').value = productId;
    
    // Set default subject based on product
    const subjectSelect = document.getElementById('message-subject');
    const messageContent = document.getElementById('message-content');
    
    // Pre-fill with product context
    messageContent.placeholder = `Ask about "${productTitle}"...`;
    
    // Show modal
    document.getElementById('contact-seller-modal').style.display = 'flex';
    
    // Focus on message content
    setTimeout(() => messageContent.focus(), 100);
}

function closeContactSellerModal() {
    document.getElementById('contact-seller-modal').style.display = 'none';
    
    // Reset form
    document.getElementById('contact-seller-form').reset();
    document.getElementById('custom-subject-group').style.display = 'none';
}

function updateSubjectField() {
    const subjectSelect = document.getElementById('message-subject');
    const customGroup = document.getElementById('custom-subject-group');
    
    if (subjectSelect.value === 'CUSTOM') {
        customGroup.style.display = 'block';
        document.getElementById('custom-subject').focus();
    } else {
        customGroup.style.display = 'none';
    }
}

function setQuickMessage(message) {
    document.getElementById('message-content').value = message;
}

async function sendContactMessage() {
    const sellerId = document.getElementById('seller-id').value;
    const productId = document.getElementById('product-id').value;
    const subjectSelect = document.getElementById('message-subject');
    const customSubject = document.getElementById('custom-subject').value;
    const messageContent = document.getElementById('message-content').value;
    
    // Validate form
    if (!messageContent.trim()) {
        alert('Please enter a message');
        return;
    }
    
    let subject;
    if (subjectSelect.value === 'CUSTOM') {
        if (!customSubject.trim()) {
            alert('Please enter a custom subject');
            return;
        }
        subject = customSubject;
    } else {
        const subjectMap = {
            'PRODUCT_INQUIRY': 'General Question',
            'NEGOTIATION': 'Price Negotiation',
            'SHIPPING_INQUIRY': 'Shipping Question',
            'PRODUCT_CONDITION': 'Product Condition',
            'BULK_ORDER': 'Bulk Order Inquiry'
        };
        subject = subjectMap[subjectSelect.value] || 'Product Inquiry';
    }
    
    // Disable send button
    const sendBtn = document.querySelector('.btn-primary');
    sendBtn.disabled = true;
    sendBtn.textContent = 'Sending...';
    
    try {
        // Create conversation
        const formData = new URLSearchParams();
        formData.append('otherUserId', sellerId);
        formData.append('productId', productId);
        formData.append('subject', subject);
        formData.append('type', 'PRODUCT_INQUIRY');
        
        const conversationResponse = await fetch('/eBay/messages/api/conversations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        });
        
        const conversationData = await conversationResponse.json();
        
        if (!conversationData.success) {
            throw new Error(conversationData.message);
        }
        
        // Send first message
        const messageFormData = new URLSearchParams();
        messageFormData.append('conversationId', conversationData.conversation.conversationId);
        messageFormData.append('content', messageContent);
        
        const messageResponse = await fetch('/eBay/messages/api/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: messageFormData
        });
        
        const messageData = await messageResponse.json();
        
        if (messageData.success) {
            closeContactSellerModal();
            
            // Show success message
            alert('Message sent successfully! You can view the conversation in your messages.');
            
            // Optionally redirect to messages
            if (confirm('Would you like to view the conversation now?')) {
                window.location.href = '/eBay/messages/conversation/' + conversationData.conversation.conversationId;
            }
        } else {
            throw new Error(messageData.message);
        }
        
    } catch (error) {
        console.error('Error sending message:', error);
        alert('Failed to send message: ' + error.message);
    } finally {
        // Re-enable send button
        sendBtn.disabled = false;
        sendBtn.textContent = 'Send Message';
    }
}

// Close modal when clicking outside
document.getElementById('contact-seller-modal')?.addEventListener('click', function(e) {
    if (e.target === this) {
        closeContactSellerModal();
    }
});

// Close modal with Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape' && document.getElementById('contact-seller-modal').style.display === 'flex') {
        closeContactSellerModal();
    }
});
</script>