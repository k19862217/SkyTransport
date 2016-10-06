$(document).ready(function() {
    $('#payment_confirmed').click(function(e){
    	e.preventDefault();

    	$.ajax({
        	type: "post",
        	url: "/myAccount/charge",
        	dataType: "html",
        	contentType:'application/json',
        	async: false,
        	data: JSON.stringify({
        		"payment_value":$('#payment_value').val()
        	}),
        	success: function (data) {
        	    $('#confirm_payment').html(data);
            	sweetAlert('充值成功', "充值成功", 'success');
        		$('#sendPackage').modal('toggle');
        	},
        	error: function (XMLHttpRequest, textStatus, errorThrown) {
            	sweetAlert('错误', JSON.parse(XMLHttpRequest.responseText).ErrorMessage, 'error');
        	}
    	});
    })
})
