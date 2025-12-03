var roomId= null;
var stompClient = null;
var subscription = null;

function handleEnter(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        sendMessage();
    }
}

// 메세지 전송
function sendMessage() {

    if (!roomId){
        basicAlert({ icon: 'error', text: "친구 또는 채팅을 선택하세요." });
        return;
    }

    var msgInput = $('#messageInput').val().trim();
    var message = {
        type: 'TALK',
        roomId: roomId,
        sender: loginUser.loginId,
        message: msgInput
    };


    stompClient.send("/pub/chat/message", {}, JSON.stringify(message));
    $('#messageInput').val("");

    showChattingList();
    sendAlarmToUser(roomId, msgInput)

}

function showMessage(message) {
    var msgDiv = $('<div>').addClass('chat-message');
    msgDiv.addClass(message.sender === loginUser.loginId ? 'self' : 'other');
    msgDiv.text(message.message);

    $('#chatBox').append(msgDiv);
    $('#chatBox').scrollTop($('#chatBox')[0].scrollHeight);
}

function openChatWith(friendLoginId) {
    $('#chatPlaceholder').hide();
    $('#chat-input-area').show();
    $('#chatBox').show();

    $.ajax({
        url: "/chatRoom/find",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            userId: loginUser.loginId,
            friendId: friendLoginId
        }),
        success: function(room) {
            enterRoom(room.id);
        },
        error: function(xhr) {
            let msg = xhr.responseJSON ? xhr.responseJSON.msg : xhr.responseText;
            basicAlert({ icon: 'error', text: msg });
        }

    });
}

function createPrivateRoom(friendLoginId) {

    $('#chatPlaceholder').hide();
    $('#chat-input-area').show();
    $('#chatBox').show();


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

    $('#chatPlaceholder').hide();
    $('#chat-input-area').show();
    $('#chatBox').show();

    roomId = room_id;

    // 이전 구독 해제
    if(subscription) {
        subscription.unsubscribe();
    }

    // 새로운 구독
    subscription = stompClient.subscribe("/sub/chat/room/" + roomId, function(messageOutput) {
        showMessage(JSON.parse(messageOutput.body));

        showChattingList();
    });

    messageOutput(roomId)
}

function messageOutput(roomId) {

    $('#chatBox').empty();

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