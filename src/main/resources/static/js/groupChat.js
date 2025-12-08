// 선택된 사용자 정보를 담을 배열
let selectedUsers = [];

// 팝업을 여는 함수
function groupPopup() {
    // 팝업 열기 전, 이전 상태 초기화
    resetGroupPopup();

    // 사용자 목록 로드
    loadUsersForPopup();

    // Bootstrap Modal 인스턴스 생성 및 표시
    const groupChatModal = new bootstrap.Modal(document.getElementById('groupChatModal'));
    groupChatModal.show();
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
                    <div class="user-item" data-user-id="${user.id}" data-user-name="${user.name}">
                        <input class="form-check-input" type="checkbox" value="${user.loginId}" id="user-checkbox-${user.id}" onchange="selectChoose(this)">
                        <img src="${user.profileImage}" alt="${user.name}" class="friends-profile-img">
                        <label class="form-check-label" for="user-checkbox-${user.id}">
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
    const displayContainer = $('#selected-users-display');
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
    $('#user-search-input').val('');
    $('#user-list-container').empty();
}

// 친구 검색 이벤트 리스너 (keyup)
function searchFriends(input){
    const searchTerm = $(input).val().toLowerCase();
    $('.user-item').each(function() {
        const userName = $(this).data('user-name').toLowerCase();
        $(this).toggle(userName.includes(searchTerm));
    });
}


// 사용자 선택/해제 이벤트 리스너 (checkbox change)
function selectChoose(checkbox){
    const loginId = $(checkbox).val(); // 그대로 loginId
    const userId = $(checkbox).closest('.user-item').data('user-id'); // 숫자 id
    const userName = $(checkbox).closest('.user-item').data('user-name');
    const isChecked = $(checkbox).is(':checked');

    if (isChecked) {
        if (!selectedUsers.some(user => user.id === userId)) {
            selectedUsers.push({ id: userId, loginId: loginId, name: userName });
        }
    } else {
        selectedUsers = selectedUsers.filter(user => user.id !== userId);
    }

    updateSelectedUsersDisplay();
}


// 선택된 사용자 태그의 X 버튼 클릭 이벤트 리스너
$(document).on('click', '#selected-users-display .btn-close', function() {
    const userIdToRemove = $(this).closest('.selected-user-tag').data('user-id');

    // 1. selectedUsers 배열에서 사용자 제거
    selectedUsers = selectedUsers.filter(user => user.id !== userIdToRemove);

    // 2. UI 업데이트
    updateSelectedUsersDisplay();

    // 3. 아래쪽 체크박스 해제
    $(`#user-checkbox-${userIdToRemove}`).prop('checked', false);
});

function groupCreatePopup(){
    console.log("생성", selectedUsers)

    // selectUsers를 가지고 조회 한 번 때려야 함
    // 만약 해당 chatRoom이 있다면 중복 팝업
    // 업다면 바로 채팅방 설정 팝업
    // 선택한 사람 중에 중복 채팅방이 있다면
}