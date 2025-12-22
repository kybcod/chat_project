/**
 * user 리스트 가져오기
 */
function showFriendList() {
  $.ajax({
    url: "/users",
    type: "GET",
    success: function (friends) {
      const $containerFri = $(".friends-list");
      $containerFri.empty(); // 기존 내용 삭제

      friends
          .filter(fri => fri.loginId !== loginUser.loginId)
          .forEach(fri => {
            const friendHTML = `
                        <div class="friends-item">
                            <img src="${fri.profileImage || '/images/orgProfile.png'}" alt="${fri.name}" class="friends-profile-img" />
                            <span class="friends-name">${fri.name}</span>
                        </div>
                    `;
            const $friendDiv = $(friendHTML);

            // jQuery로 이벤트 연결
            $friendDiv.on("dblclick", function() {
              openChatWith(fri.loginId);
            });

            $containerFri.append($friendDiv);
          });
    },
    error: function (xhr, status, err) {
      basicAlert({ icon: 'error', text: err.responseJSON?.msg || err.responseText });
    }
  });
}


/**
 * 내가 속한 채팅방 리스트
 */
function showChattingList() {
  $.ajax({
    url: "/chatRoom/list",
    type: "GET",
    data: { userId: loginUser.loginId },
    success: function (chattingRooms) {
      const $container = $(".chatting-list");
      $container.empty(); // 초기화

      chattingRooms.forEach(room => {
        const $item = $(`
                    <div class="chatting-item chatting-display">
                        <span class="room-name">${room.roomName}</span>
                        <i class="bi bi-trash trash-icon"></i>
                    </div>
                `);

        // 현재 입장한 방이면 selected 클래스 추가
        if (room.id === roomId) {
          $item.addClass("selected-room");
        }

        $item.on("dblclick", function () {
          $(".chatting-item").removeClass("selected-room");
          $(this).addClass("selected-room");
          enterRoom(room.id);
        });

        $item.find(".trash-icon").on("click", function (e) {
          e.stopPropagation();
          e.preventDefault();
          exitRoom(room);
        });

        $container.append($item);
      });
    },
    error: function (xhr, status, err) {
      basicAlert({
        icon: 'error',
        text: err.responseJSON?.msg || err.responseText
      });
    }
  });
}


/**
 * 친구 리시트에서 친구 더블 클릭 시 채팅방 찾기
 */
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


/**
 * 채팅방 입장
 */
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
      drawMessage(msg);
      showChattingList();
    }
  });

  messageOutput(roomId)

}


/**
 * 채팅방 나가기
 */
function exitRoom(room){
  confirmAlert({ icon: 'success', text: "정말로 채팅방을 나가시겠습니까?" })
      .then(result => {
        if (result.isConfirmed) {
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
              // 1. 본인 화면에 메시지 먼저 보여주기
              exitAndInviteMessage("LEAVE", loginUser.name, room.type);

              // 2. 채팅방 UI 업데이트
              $('#chatPlaceholder').show();
              $('#chatBox').hide();
              $('#chat-input-area').hide();

              // 3. 구독 끊기
              if (chatSubscription) {
                chatSubscription.unsubscribe();
                chatSubscription = null;
              }

              roomId = null;
            },
            error: function(err) {
              console.error("메시지 불러오기 실패", err);
            }
          });
        }
      })
      .catch(error => console.error(error));

}

/**
 *  채팅방 초대하기
 */
function inviteToChat() {
  console.log("채팅방 초대 클릭");
}

/**
 * 나가기/초대하기 메세지 전송
 */
function exitAndInviteMessage(type, name, roomType) {

  stompClient.send("/pub/chat/message", {}, JSON.stringify({
    type: type,
    roomId: roomId,
    sender: loginUser.loginId,
    senderName: name,
    roomType,
    message: type === "LEAVE" ? `${name} 님이 채팅방에 나가셨습니다.` : `${name} 님이 채팅방에 초대되었습니다.`,
  }));

  showChattingList();

}