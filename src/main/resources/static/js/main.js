// 사이드바 토글
$('.menu-toggle').on('click', function() {
    $('nav').toggleClass('collapsed');

    if ($('nav').hasClass('collapsed')) {
        $('.user-info-name').hide();
    } else {
        $('.user-info-name').show();
    }
});


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