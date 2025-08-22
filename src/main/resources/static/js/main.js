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