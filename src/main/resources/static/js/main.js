// 사이드바 토글
$('.menu-toggle').on('click', function() {
    $('nav').toggleClass('collapsed');

    if ($('nav').hasClass('collapsed')) {
        $('.user-info-name').hide();
    } else {
        $('.user-info-name').show();
    }
});