$(document).ready(function () {
    $('#message_confirmed').click(function(e){
    	e.preventDefault();

    	$.ajax({
        	type: "post",
        	url: "/myMessage/addMessage",
        	dataType: "json",
        	contentType:'application/json',
        	async: false,
        	data: JSON.stringify({
        		"title": $('#message_title').val(),
        		"question": $('#message').val()
        	}),
        	success: function (data) {
            	sweetAlert('提交留言', "提交成功", 'success');
        		$('#send_message_modal').modal('toggle');
        		location.reload();
        	},
        	error: function (XMLHttpRequest, textStatus, errorThrown) {
            	sweetAlert('错误', JSON.parse(XMLHttpRequest.responseText).ErrorMessage, 'error');
        	}
    	});
    })
});