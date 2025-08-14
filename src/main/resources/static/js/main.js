function loadContent(url) {
    if (!isAuthenticated) {
        window.location.href = '/login';
    } else {
        $.ajax({
            url: url,
            type: "GET",
            success: function (html) {
                $("#main-content").html(html);
            },
            error: function (xhr, status, error) {
                console.error("AJAX Error:", status, error);
            }
        });
    }
}

// 사이드바 토글
$('.menu-toggle').on('click', function() {
    $('nav').toggleClass('collapsed');

    if ($('nav').hasClass('collapsed')) {
        $('.user-info-name').hide();
    } else {
        $('.user-info-name').show();
    }
});