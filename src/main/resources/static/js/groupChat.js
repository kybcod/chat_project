// 선택된 사용자 정보를 담을 배열
let selectedUsers = [];
let selectFriModal;
let dupliChatModal;
let teamChatModal;

// 팝업을 여는 함수
function groupPopup() {
    // 팝업 열기 전, 이전 상태 초기화
    resetGroupPopup();

    // 사용자 목록 로드
    loadUsersForPopup();

    selectFriModal = new bootstrap.Modal(document.getElementById('selectFriModal'));
    selectFriModal.show();

    dupliChatModal = new bootstrap.Modal(document.getElementById('dupliChatModal'));
    teamChatModal = new bootstrap.Modal(document.getElementById('teamChatModal'));
}

// 사용자 목록을 불러와 팝업에 렌더링하는 함수
function loadUsersForPopup() {
    $.ajax({
        url: "/users",
        type: "GET",
        success: function(users) {
            const userListContainer = $('#user-list-container');
            userListContainer.empty(); // 기존 목록 삭제

            users.forEach(user => {
                if (user.loginId === loginUser.loginId) {
                    return;
                }
                const userItemHTML = `
                    <div class="user-item">
                        <input class="form-check-input" type="checkbox" id="user-checkbox-${user.id}"
                            onchange='selectChoose(this, ${JSON.stringify(user)})'>
                        <img src="${user.profileImage}" alt="${user.name}" class="friends-profile-img">
                        <label class="form-check-label user-name-label" for="user-checkbox-${user.id}">
                            ${user.name}
                        </label>
                    </div>
                `;

                userListContainer.append(userItemHTML);
            });
        },
        error: function(xhr, status, error) {
            console.error("사용자 목록 로드 실패:", error);
            basicAlert({icon: 'error', text: '사용자 목록을 불러오는 데 실패했습니다.'});
        }
    });
}

// 선택된 사용자 UI를 업데이트하는 함수
function updateSelectedUsersDisplay() {
    const displayContainer = $('#selected-users');
    displayContainer.empty(); // 기존 내용 삭제

    selectedUsers.forEach(user => {
        const userTagHTML = `
            <span class="selected-user-tag" data-user-id="${user.id}">
                ${user.name}
                <button type="button" class="btn-close btn-close-sm" aria-label="Close"></button>
            </span>
        `;
        displayContainer.append(userTagHTML);
    });
}

// 팝업 상태를 초기화하는 함수
function resetGroupPopup() {
    selectedUsers = [];
    updateSelectedUsersDisplay();
    $('#user-search').val('');
    $('#user-list-container').empty();
}

// 친구 검색 이벤트 리스너 (keyup)
function searchFriends(input){
    const searchTerm = $(input).val().toLowerCase();
    $('.user-item').each(function() {
        let userName = $(this).find('.user-name-label').text().toLowerCase();
        $(this).toggle(userName.includes(searchTerm));
    });
}


// 사용자 선택/해제 이벤트 리스너 (checkbox change)
function selectChoose(checkbox, user) {
    const isChecked = $(checkbox).is(':checked');

    if (isChecked) {
        if (!selectedUsers.some(u => u.id === user.id)) {
            selectedUsers.push({ id: user.id, loginId: user.loginId, name: user.name, profileImage: user.profileImage });
        }
    } else {
        selectedUsers = selectedUsers.filter(u => u.id !== user.id);
    }

    updateSelectedUsersDisplay();
}



// 선택된 사용자 태그의 X 버튼 클릭 이벤트 리스너
$(document).on('click', '#selected-users .btn-close', function() {
    const userIdToRemove = $(this).closest('.selected-user-tag').data('user-id');

    // 1. selectedUsers 배열에서 사용자 제거
    selectedUsers = selectedUsers.filter(user => user.id !== userIdToRemove);

    // 2. UI 업데이트
    updateSelectedUsersDisplay();

    // 3. 아래쪽 체크박스 해제
    $(`#user-checkbox-${userIdToRemove}`).prop('checked', false);
});

function groupRoom(){

    // selectUsers를 가지고 조회 한 번 때려야 함
    $.ajax({
        url: "/chatRoom/userIds",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            userIds: selectedUsers.map(u => u.loginId),
            userId : loginUser.loginId,
        }),
        success: function (room) {
            if (room && room.length > 0) {
                groupDupliChatModal(room);
            }else {
                // 업다면 바로 채팅방 설정 팝업
                groupTeamChatModal();
            }
        },
        error: function (xhr) {
            let msg = xhr.responseJSON ? xhr.responseJSON.msg : xhr.responseText;
            basicAlert({icon: 'error', text: msg});
        }
    });

}

// 중복 채팅방 생성 안내 모달
function groupDupliChatModal(rooms) {

    selectFriModal.hide();
    dupliChatModal.show();

    const chatListContainer = $('#chat-list-container');
    chatListContainer.empty();

    rooms.forEach(room => {
        const users = room.users;
        const memberCount = room.memberCount;
        const roomName = room.roomName;
        const roomId = room.roomId;

        // 프로필 이미지 (최대 4개)
        const userCount = users.length > 4 ? 4 : users.length;
        let profileImagesHTML = `<div class="profile-images WH52 user-count-${userCount}">`;
        users.slice(0, 4).forEach(user => {
            profileImagesHTML += `<img src="${user.profileImage || '/images/orgProfile.png'}" alt="${user.name}">`;
        });
        profileImagesHTML += '</div>';

        const chatItemHTML = `
            <div class="dupli-chat-item">
                ${profileImagesHTML}
                <div class="room-info">
                    <span class="room-name" title="${roomName}">${roomName}</span>
                    <span class="member-count">${memberCount}명</span>
                </div>
                <button class="btn btn-primary go-chat-btn" onclick="enterRoomAndCloseModal(${roomId})">채팅방으로 이동</button>
            </div>
        `;
        chatListContainer.append(chatItemHTML);
    });
}

function enterRoomAndCloseModal(roomId) {
    dupliChatModal.hide();
    enterRoom(roomId);
}

// 새로운 팀 채팅 방 모달
function groupTeamChatModal() {
    selectFriModal.hide()
    dupliChatModal.hide()
    teamChatModal.show();

    const teamImageContainer = $('.team-chat-profileImage');
    teamImageContainer.empty();

    let profileHTML;

    selectedUsers.forEach(user => {

        // 프로필 이미지 (최대 4개)
        const userCount = selectedUsers.length > 4 ? 4 : selectedUsers.length;
        profileHTML = `<div class="profile-images WH100 user-count-${userCount}">`;
        selectedUsers.slice(0, 4).forEach(user => {
            profileHTML += `<img src="${user.profileImage || '/images/orgProfile.png'}" alt="${user.name}">`;
        });
        profileHTML += '</div>';


    });

    teamImageContainer.append(profileHTML);

}

function groupRoomCreate(){

    $.ajax({
        url: "/chatRoom/create",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            roomName : $("#group-name").val(),
            userId: loginUser.loginId,
            userIds: selectedUsers.map(u => u.loginId),
        }),
        success: function(room) {
            teamChatModal.hide();
            enterRoom(room.id);
            showChattingList();
        },
        error: function (xhr, status, err) {
            basicAlert({ icon: 'error', text: err.responseJSON?.msg || err.responseText });
        }
    });


}
