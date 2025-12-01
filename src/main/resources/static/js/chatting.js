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

    msgDiv.textContent = message.message;
    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function openChatWith(friendLoginId) {
    $.ajax({
        url: "/chatRoom/find",
        type: "POST",
        data: {
            me: loginUser.loginId,      // 내 로그인 ID
            friendId: friendLoginId       // 친구 로그인 ID
        },
        success: function(room) {
            if (room) {
                enterRoom(room.id);
            } else {
                createPrivateRoom(friendLoginId);
            }
        }
    });
}

function createPrivateRoom(friendLoginId) {
    $.ajax({
        url: "/chatRoom/create",
        type: "POST",
        data: {
            me: loginUser.loginId,
            friend: friendLoginId
        },
        success: function(room) {
            enterRoom(room.id);
        }
    });
}

function enterRoom(roomId) {

    // 그려줘야 함
    window.location.href = "/chat/room/" + roomId;
}
