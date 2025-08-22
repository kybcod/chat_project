var roomId = 'test-room';
var stompClient = null;


function sendMessage() {
    var msgInput = document.getElementById('messageInput');
    var message = {
        type: 'TALK',
        roomId: roomId,
        sender: loginUser.name,
        message: msgInput.value
    };

    stompClient.send("/pub/chat/message", {}, JSON.stringify(message));
    msgInput.value = '';
}

function showMessage(message) {
    var chatBox = document.getElementById('chatBox');
    var msgDiv = document.createElement('div');
    msgDiv.classList.add('chat-message');

    if(message.sender === loginUser.name) {
        msgDiv.classList.add('self'); // 내 메시지
    } else {
        msgDiv.classList.add('other'); // 상대 메시지
    }

    msgDiv.textContent = message.sender + ": " + message.message;
    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}

