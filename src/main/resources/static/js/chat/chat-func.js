/**
 * 알람
 */
function sendAlarmToUser(roomId, content) {

  $.ajax({
    url: `/chatRoom/findRoom`,
    type: "GET",
    data: { userId: loginUser.loginId,
      roomId: roomId,
    },
    success: function(users) {  // users는 리스트
      if(users.length === 0) return; // 안전장치

      users.forEach(user => {
        const alarmMessage = {
          receiver: user.userId,
          content: content,
          senderName: loginUser.name,
          senderProfileImage: loginUser.profileImage,
          roomId: roomId,
        };

        stompClient.send("/pub/alarm", {}, JSON.stringify(alarmMessage));
      });
    },
    error: function(xhr) {
      let msg = xhr.responseJSON ? xhr.responseJSON.msg : xhr.responseText;
      basicAlert({ icon: 'error', text: msg });
    }

  });

}



/**
 * 타이핑
 */
function sendTyping() {
  if (!roomId || !stompClient) return;

  stompClient.send("/pub/chat/typing", {}, JSON.stringify({
    type: 'TYPING',
    roomId,
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