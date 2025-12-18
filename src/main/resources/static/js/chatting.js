var roomId= null;
const typingUsers = new Map(); // { sender: timestamp }
const typingTimeout = 1000; // 2초 동안 타이핑이 없으면 멈춘 것으로 간주

$(document).ready(function() {

    $(document).on('keydown', '#messageInput', function(event) {
        if (event.key === 'Enter' && !event.shiftKey && !event.ctrlKey) {
            event.preventDefault();
            sendMessage();
        }
    });

    // 입력 이벤트: 자동 높이 조절 + 스크롤 + 타이핑 이벤트 전송
    $(document).on('input', '#messageInput', function() {
        // 자동 높이 조절
        this.style.height = 'auto';
        const scrollHeight = this.scrollHeight;
        this.style.height = scrollHeight + 'px';

        const lineHeight = parseFloat($(this).css('line-height'));
        const heightLimit = lineHeight * 4; // 4줄 기준

        if (scrollHeight > heightLimit) {
            $(this).css('overflow-y', 'auto');
        } else {
            $(this).css('overflow-y', 'hidden');
        }

        // 채팅박스 스크롤
        scrollToBottom();

        // 타이핑 이벤트 전송
        sendTyping();
    });

    // 주기적으로 타이핑 타임아웃 체크
    setInterval(checkTypingTimeouts, 1000);
});

// 타이핑 이벤트 전송
function sendTyping() {
    if (!roomId || !stompClient) return;

    stompClient.send("/pub/chat/typing", {}, JSON.stringify({
        type: 'TYPING',
        roomId: roomId,
        sender: loginUser.loginId
    }));
}

// '입력 중' 버블 추가
function addTypingBubble(sender) {
    // 이미 버블이 있으면 추가하지 않음
    if ($(`#typing-bubble-${sender}`).length > 0) return;

    let msgDiv = $('<div>')
        .addClass('chat-message other')
        .attr('id', `typing-bubble-${sender}`);

    let contentDiv = $('<div>').addClass('msg-content');
    let loaderDiv = $('<div>').addClass('typing-bubble');

    contentDiv.append(loaderDiv);
    msgDiv.append(contentDiv);

    $('#chatBox').append(msgDiv);
    scrollToBottom();
}

// '입력 중' 버블 제거
function removeTypingBubble(sender) {
    const bubble = $(`#typing-bubble-${sender}`);
    if (bubble.length > 0) {
        bubble.remove();
        typingUsers.delete(sender);
    }
}

// 타임아웃된 '입력 중' 버블을 확인하고 제거
function checkTypingTimeouts() {
    const now = Date.now();
    typingUsers.forEach((timestamp, sender) => {
        if (now - timestamp > typingTimeout) {
            removeTypingBubble(sender);
        }
    });
}


// 메세지 전송
function sendMessage() {

    if (!roomId){
        basicAlert({ icon: 'error', text: "친구 또는 채팅을 선택하세요." });
        return;
    }

    const msgInputVal = $('#messageInput').val();
    if (msgInputVal.trim() === ""){
        return;
    }

    var message = {
        type: 'TALK',
        roomId: roomId,
        sender: loginUser.loginId,
        senderName: loginUser.name,
        message: msgInputVal
    };


    stompClient.send("/pub/chat/message", {}, JSON.stringify(message));
    $('#messageInput').val("");
    $('#messageInput').css('height', '46px');
    showChattingList();
    sendAlarmToUser(roomId, msgInputVal)

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

    sendAlarmToUser(roomId, "파일을 보냈습니다.")
}

function showMessage(message) {

    switch (message.type) {
        case 'LEAVE':
        case 'INVITE':
            renderEventMsg(message);
            break;

        case 'FILE':
            renderChatMsg(message, renderFileContent);
            break;

        default:
            renderChatMsg(message, renderTextContent);
            break;
    }
}


function renderChatMsg(message, contentRenderer) {
    const isSelf = message.sender === loginUser.loginId;

    const containerDiv = $('<div>').addClass('chat-message-container');

    if (!isSelf) {
        containerDiv.append(
            $('<div>')
                .addClass('sender-name')
                .text(message.senderName)
        );
    }

    const msgDiv = $('<div>')
        .addClass('chat-message')
        .addClass(isSelf ? 'self' : 'other');

    const contentDiv = $('<div>').addClass('msg-content');

    contentRenderer(message, contentDiv); // TALK, FILE 따로 렌더링

    msgDiv.append(contentDiv);
    containerDiv.append(msgDiv);


    $('#chatBox').append(containerDiv);
    scrollToBottom();
}

// LEAVE, INVITE 일 때 채팅창 표시
function renderEventMsg(message) {
    if (message.roomType === "PRIVATE"){
        return;
    }
    const eventDiv = $('<div>')
        .addClass('chat-event-message')
        .text(message.message);

    $('#chatBox').append(eventDiv);
    scrollToBottom();
}

// 기본 TALK 타입
function renderTextContent(message, contentDiv) {
    contentDiv.text(message.message);
}

// FILE 타입 일 때 렌더링
function renderFileContent(message, contentDiv) {
    if (!message.fileUrl) return;

    if (message.fileType?.startsWith('image/')) {
        const img = $('<img>')
            .attr('src', message.fileUrl)
            .addClass('chat-image')
            .css({
                maxWidth: '200px',
                cursor: 'pointer',
                backgroundColor: 'white'
            })
            .on('click', () => window.open(message.fileUrl, '_blank'))
            .on('load', scrollToBottom);

        contentDiv.append(img);
    } else {
        const link = $('<a>')
            .attr({
                href: message.fileUrl,
                target: '_blank'
            })
            .text(message.fileName || '파일 다운로드')
            .addClass('file-link');

        contentDiv.append(link);
    }
}


function scrollToBottom() {
    const chatBox = $('#chatBox');
    chatBox.scrollTop(chatBox[0].scrollHeight);
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
        const msg = JSON.parse(messageOutput.body);
        const sender = msg.sender;

        if (sender === loginUser.loginId && msg.type === 'TYPING') {
            return;
        }

        if (msg.type === 'TYPING') {
            addTypingBubble(sender);
            typingUsers.set(sender, Date.now());
        } else {
            removeTypingBubble(sender);
            showMessage(msg);
            showChattingList();
        }
    });

    messageOutput(roomId)

}

function messageOutput(roomId) {

    $('#chatBox').empty();

    $.ajax({
        url: "/chat/messages",
        type: "POST",
        contentType: "application/json",
        data : JSON.stringify({
            roomId,
            sender: loginUser.loginId,
        }),
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

function exitRoom(room){

    $.ajax({
        url: "/chatRoom",
        type: "DELETE",
        contentType: "application/json",
        data : JSON.stringify({
            roomId: room.id,
            userId: loginUser.loginId,
            type : room.type
        }),
        success: function() {
            exitAndInviteMessage("LEAVE", loginUser.name, room.type)
        },
        error: function(err) {
            console.error("메시지 불러오기 실패", err);
        }
    });
}

// 메세지 전송
function exitAndInviteMessage(type, name, roomType) {

    var message = {
        type: type,
        roomId: roomId,
        sender: loginUser.loginId,
        senderName: name,
        roomType,
        message: type === "LEAVE" ? `${name} 님이 채팅방에 나가셨습니다.` : `${name} 님이 채팅방에 초대되었습니다.`,
    };

    stompClient.send("/pub/chat/message", {}, JSON.stringify(message));
    showChattingList();

}