var roomId= null;

$(document).ready(function() {
    const messageInput = $('#messageInput');

    if (messageInput.length) { // Check if element exists
        messageInput.on('keydown', function(event) {
            if (event.key === 'Enter' && !event.shiftKey && !event.ctrlKey) {
                event.preventDefault();
                sendMessage();
            }
        });

        messageInput.on('input', function() {
            this.style.height = 'auto';
            let scrollHeight = this.scrollHeight;
            this.style.height = (scrollHeight) + 'px';
            $('#chatBox').scrollTop($('#chatBox')[0].scrollHeight);
        });
    }
});

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
    $('#messageInput').css('height', '46px');
    showChattingList();
    sendAlarmToUser(roomId, msgInput)

}

// 파일 전송
function handleFileUpload(event) {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("chatFile", file);
    formData.append("roomId", roomId);
    formData.append("sender", loginUser.loginId);

    $.ajax({
        url: '/chat/upload',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function(messageDto) {
            // WebSocket 전송
            stompClient.send("/pub/chat/message", {}, JSON.stringify(messageDto));
        },
        error: function(err) {
            basicAlert({ icon: 'error', text: err.responseJSON?.msg || err.responseText });
        }
    });

    event.target.value = ""; // 초기화
}


function showMessage(message) {
    let msgDiv = $('<div>').addClass('chat-message');
    msgDiv.addClass(message.sender === loginUser.loginId ? 'self' : 'other');

    // 메시지 내용
    let contentDiv = $('<div>').addClass('msg-content');

    if (message.fileUrl) {
        if (message.fileType && message.fileType.startsWith('image/')) {
            // 이미지 파일이면 미리보기
            let img = $('<img>')
                .attr('src', message.fileUrl)
                .addClass('chat-image')
                .css({ maxWidth: '200px', cursor: 'pointer', backgroundColor: 'white' })
                .on('click', function() {
                    window.open(message.fileUrl, '_blank');
                });
            contentDiv.append(img);
        } else {
            // 이미지 외 파일은 다운로드 링크
            let link = $('<a>')
                .attr('href', message.fileUrl)
                .attr('target', '_blank')
                .text(message.fileName || '파일 다운로드')
                .addClass('file-link');
            contentDiv.append(link);
        }
    } else {
        // 일반 텍스트 메시지
        contentDiv.text(message.message);
    }

    msgDiv.append(contentDiv);

    // 채팅박스에 추가 후 스크롤
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
    if(chatSubscription) {
        chatSubscription.unsubscribe();
    }

    // 새로운 구독
    chatSubscription = stompClient.subscribe("/sub/chat/room/" + roomId, function(messageOutput) {
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