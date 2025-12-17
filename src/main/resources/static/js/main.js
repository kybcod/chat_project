var stompClient = null;
var chatSubscription = null;
var alarmSubscription = null;

function connect() {
    var socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        // 알림 수신
        alarmSubscription = stompClient.subscribe("/user/queue/alarm", function(message) {
            const alarm = JSON.parse(message.body);
            // 해당 유저가 해당 채팅방에 들어가 있다면 알림X
            if(roomId === alarm.roomId) return;
            toastAlert(alarm);
        });
    });
}


// 알람 기능
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


function showFriendList() {
    $.ajax({
        url: "/users",
        type: "GET",
        success: function (friends) {
            const containerFri = document.querySelector(".friends-list");
            containerFri.innerHTML = "";

            friends
                .filter(fri => fri.loginId !== loginUser.loginId)
                .forEach(fri => {
                const div = document.createElement("div");
                div.classList.add("friends-item");

                // 프로필 이미지
                const img = document.createElement("img");
                img.src = fri.profileImage || "/images/orgProfile.png";
                img.alt = fri.name;
                img.classList.add("friends-profile-img");

                // 이름
                const nameSpan = document.createElement("span");
                nameSpan.textContent = fri.name;
                nameSpan.classList.add("friends-name");

                // div 구성
                div.appendChild(img);
                div.appendChild(nameSpan);

                div.addEventListener("dblclick", function() {
                    openChatWith(fri.loginId);
                });

                containerFri.appendChild(div);
            });
        },
        error: function (xhr, status, err) {
            basicAlert({ icon: 'error', text: err.responseJSON?.msg || err.responseText });
        }
    });
}


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

                $item.on("dblclick", function () {
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

