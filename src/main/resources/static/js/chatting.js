var roomId= null;
var stompClient = null;
var subscription = null;

function sendMessage() {

    var msgInput = document.getElementById('messageInput');
    var message = {
        type: 'TALK',
        roomId: roomId,
        sender: loginUser.loginId,
        message: msgInput.value
    };

    stompClient.send("/pub/chat/message", {}, JSON.stringify(message));
    msgInput.value = '';
}

function showMessage(message) {
    var chatBox = document.getElementById('chatBox');
    var msgDiv = document.createElement('div');
    msgDiv.classList.add('chat-message');

    if(message.sender === loginUser.loginId) {
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
        contentType: "application/json",
        data: JSON.stringify({
            userId: loginUser.loginId,
            friendId: friendLoginId
        }),
        success: function(room) {
            if (room) {
                enterRoom(room.id);
            } else {
                createPrivateRoom(friendLoginId);
            }
        },
        error: function(xhr) {
            let msg = xhr.responseJSON ? xhr.responseJSON.msg : xhr.responseText;
            basicAlert({ icon: 'error', text: msg });
        }

    });
}

function createPrivateRoom(friendLoginId) {
    $.ajax({
        url: "/chatRoom/create",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            userId: loginUser.loginId,
            friendId: friendLoginId
        }),
        success: function(room) {
            enterRoom(room.id);
        },
        error: function (xhr, status, err) {
            basicAlert({ icon: 'error', text: err.responseJSON?.msg || err.responseText });
        }
    });
}

function enterRoom(room_id) {
    roomId = room_id;

    // 이전 구독 해제
    if(subscription) {
        subscription.unsubscribe();
    }

    // 새로운 구독
    subscription = stompClient.subscribe("/sub/chat/room/" + roomId, function(messageOutput) {
        showMessage(JSON.parse(messageOutput.body));
    });

    messageOutput(roomId)
}

function messageOutput(roomId) {

    document.getElementById('chatBox').innerHTML = '';


    $.ajax({
        url: "/chat/messages/" + roomId,
        type: "GET",
        success: function(messages) {
            messages.forEach(function(message) {
                showMessage(message);
            });
        },
        error: function(err) {
            console.error("메시지 불러오기 실패", err);
        }
    });
}