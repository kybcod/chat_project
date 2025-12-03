function connect() {
    var socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {

        // 알림 수신
        stompClient.subscribe("/user/queue/alarm", function(message) {
            const alarm = JSON.parse(message.body);
            toastAlert(alarm);
        });
    });
}


connect();

// 알람 기능
function sendAlarmToUser(roomId, content) {

    $.ajax({
        url: `/chatRoom/findRoom`,
        type: "GET",
        data: { userId: loginUser.loginId,
                roomId: roomId,
        },
        success: function(room) {
            const alarmMessage = {
                receiver: room.userId,
                content: content,
                senderName : loginUser.name,
                senderProfileImage : loginUser.profileImage
            };
            stompClient.send("/pub/alarm", {}, JSON.stringify(alarmMessage));
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

                // 🔹 프로필 이미지
                const img = document.createElement("img");
                img.src = fri.profileImage || "/images/orgProfile.png";
                img.alt = fri.name;
                img.classList.add("friends-profile-img");

                // 🔹 이름
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


function showChattingList(){
    $.ajax({
        url: "/chatRoom/list",
        type: "GET",
        data: { userId: loginUser.loginId },
        success: function (chattingRooms) {
            const container = document.querySelector(".chatting-list");
            container.innerHTML = ""; // 초기화

            chattingRooms.forEach(room => {
                const div = document.createElement("div");
                div.classList.add("chatting-item");
                div.textContent = room.roomName;
                div.addEventListener("dblclick", function() {
                    enterRoom(room.id);
                });

                container.appendChild(div);
            });
        },
        error: function (xhr, status, err) {
            basicAlert({ icon: 'error', text: err.responseJSON?.msg || err.responseText });
        }
    });


}
