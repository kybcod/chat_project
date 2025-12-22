/**
 * 메세지 전송
 */
function sendMessage() {

  if (!roomId){
    basicAlert({ icon: 'error', text: "친구 또는 채팅을 선택하세요." });
    return;
  }

  const msgInputVal = $('#messageInput').val().trim();
  if (!msgInputVal) return;

  stompClient.send("/pub/chat/message", {}, JSON.stringify({
    type: 'TALK',
    roomId: roomId,
    sender: loginUser.loginId,
    senderName: loginUser.name,
    message: msgInputVal
  }));

  $('#messageInput').val("").css('height', '46px');
  showChattingList();
  sendAlarmToUser(roomId, msgInputVal)

}


/**
 * 타입별로 메세지 그려주기
 */
function drawMessage(message) {

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
  contentDiv.html(linkify(message.message));
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


// 파일 전송
function openFileSelect() {
  // 여기서는 파일 선택창만 열어줌
  document.getElementById("fileInput").click();
}


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
      sendAlarmToUser(roomId, "파일을 보냈습니다.")
    },
    error: function(err) {
      basicAlert({ icon: 'error', text: err.responseJSON?.msg || err.responseText });
    }
  });

  event.target.value = ""; // 초기화

}


// 링크 표시 url
function linkify(text) {
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  return text.replace(urlRegex, function(url) {
    return `<a href="${url}" target="_blank" class="chat-link">${url}</a>`;
  });
}


/**
 * 해당 채팅방 메세지 전부 가져오기
 */
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
        drawMessage(message);
      });
    },
    error: function(err) {
      console.error("메시지 불러오기 실패", err);
    }
  });
}