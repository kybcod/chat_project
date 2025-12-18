var stompClient = null;
var chatSubscription = null;
var alarmSubscription = null;
var roomId= null;
const typingUsers = new Map(); // { sender: timestamp }
const typingTimeout = 1000;


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

      showChattingList(); // TODO: 더 좋은 방향이 있을지 생각해 봐야 함
    });
  });
}

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
  setInterval(() => {
    const now = Date.now();
    typingUsers.forEach((t, s) => {
      if (now - t > typingTimeout) removeTypingBubble(s);
    });
  }, 1000);

});
