$(document).ready(function () {
    //Prepare jTable
	$('#AddressTableContainer').jtable({
		title: '操作一览表',
		paging: true,
		toolbar: {
  			items: [{
     			icon: '/assets/jTable/themes/lightcolor/add.png',
        		text: '添加地址',
        		click: function () {
                    $('#address_add').modal('toggle');
					addressInit('Select1', 'Select2', 'Select3');
        		}
    		}]
    	},
    	messages: {
            addNewRecord: '添加地址'
        },
		actions: {
			listAction: '/myAddress/get',
			createAction: function (postData, jtParams) {
                return $.Deferred(function ($dfd) {
                	var city = $('#Select1').val() + '-' + $('#Select2').val() + '-' + $('#Select3').val();
                    $.ajax({
                        url: '/myAddress/add?' + 'city=' + city + '&' + postData,
                        type: 'POST',
                        dataType: "json",
                        success: function (data) {
                            $dfd.resolve(data);
                        },
                        error: function () {
                            $dfd.reject();
                        }
                    });
                });
            },
			deleteAction: '/myAddress/delete'
		},
		fields: {
			id: {
				title: '操作ID',
				key: true,
				create: false,
				edit: false,
				list: false
			},
			country: {
				title: '国家',
				key: false,
				create: true,
				edit: true,
				defaultValue: '中国'
			},
			city: {
				title: '地区',
				create: true,
				edit: true,
				list: true
			},
			address: {
				title: '地址',
				create: true,
				edit: true,
				list: true
			},
			receiver: {
				title: '收件人',
				create: true,
				edit: true,
				list: true
			},
			phone: {
				title: '电话',
				create: true,
				edit: true,
				list: true
			},
			postcode: {
				title: '邮编',
				create: true,
				edit: true,
				list: true
			}
		}
	});
	//Load person list from server
	$('#AddressTableContainer').jtable('load');
	$(".jtable-toolbar-item.jtable-toolbar-item-add-record").hide();
});

