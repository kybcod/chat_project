function connect() {
    var socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // 구독
        stompClient.subscribe('/sub/chat/room/' + roomId, function(chatMessage){
            showMessage(JSON.parse(chatMessage.body));
        });
    });
}

connect();

function showFriendList() {
    $.ajax({
        url: "/users",
        type: "GET",
        success: function (friends) {
            const containerFri = document.querySelector(".friends-list");
            containerFri.innerHTML = "";

            friends.forEach(fri => {
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

                containerFri.appendChild(div);
            });
        },
        error: function (xhr, status, error) {
            console.error("Error:", status, error);
        }
    });
}