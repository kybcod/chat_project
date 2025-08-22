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

const chattingRooms = [
    { id: 1, name: "채팅1" },
    { id: 2, name: "채팅2" },
    { id: 3, name: "채팅3" }
];

const chatbotList = [
    { id: "c1", name: "챗봇1" },
    { id: "c2", name: "챗봇2" },
    { id: "c3", name: "챗봇3" }
];

// 채팅방 리스트 보여주기
function showChattingList() {
    loadContent('/chatting')

    const container = document.querySelector(".chatting-list");
    container.innerHTML = ""; // 초기화

    chattingRooms.forEach(room => {
        const div = document.createElement("div");
        div.classList.add("chatting-room-item");
        div.textContent = room.name;
        container.appendChild(div);
    });
}

// 챗봇 리스트 보여주기
function showChatbotList() {
    loadContent('/chatbot')

    const container = document.querySelector(".chatting-list");
    container.innerHTML = ""; // 초기화

    chatbotList.forEach(bot => {
        const div = document.createElement("div");
        div.classList.add("chatbot-item");
        div.textContent = bot.name;
        container.appendChild(div);
    });
}