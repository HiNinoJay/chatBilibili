<!--设置-->
var pre;

$(document).on('click', '.chatGPTCharacterMenu .item', function (e) {
    if(pre != null) {
        $(pre).removeClass('active');
    }
    console.log($(this).text());
    if($(this).hasClass('makeNewChatGPTCharacter')) {
        $('.ui.setNewChatGPTCharacterModal').modal('show');
    } else {
        $(this).addClass('active');
        $('.chatGPTCharacterContentSegment').text($(this).text());
        pre = e.currentTarget;
    }

});
