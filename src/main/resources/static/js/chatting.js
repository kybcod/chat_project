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
    msgDiv.textContent = message.sender + ": " + message.message;
    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}
