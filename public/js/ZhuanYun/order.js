var options = [];
function pad(n, width, z) {
  z = z || '0';
  n = n + '';
  return n.length >= width ? n : new Array(width - n.length + 1).join(z) + n;
}
var jtable_options = {
		title: '操作一览表',
		paging: true,
		actions: {
			listAction:	'/transaction/get'
		},
		fields: {
			id: {
				title: '天空网订单号',
				key: true,
				list:true,
				display: function (data) {
                    return 'TK' + pad(data.record.id, 8);
                }
			},
			orderNumber: {
				title: '发送单号',
				key: false,
				create: true
			},
			logisticMethod: {
				title: '发送方式',
				key: false,
				create: true,
				options: {'t1' : 'EMS原箱', 't2' : 'EMS特快', 't3' : 'SAL空陆联运', 't4' : '海运', 't5' : 'AIR航空'}
    		},
			needInsurance: {
				title: '需要保险',
				key: false,
				create: true,
				options: {'0' : '否', '1' : '是'}
			},
            needFastDelivery: {
             	title: '闪电发货',
             	key: false,
             	create: true,
				options: {'0' : '否', '1' : '是'}
            },
            status: {
            	title: '订单状态',
            	key: false,
            	create: false,
            	edit: false,
				options: {'0' : '未付款', '1' : '已付款'}
            },
            createTime: {
            	title: '创建时间',
             	create: true
            },
            detail: {
            	title: '订单详情',
            	key: false,
            	edit: false,
            	create: false,
            	display: function (data) {
                	return '<a href="/myOrderDetail/' + data.record.id + '" target="_blank"> 订单详情</a>';
                }
            }
		}
	}
$(document).ready(function () {
    //Prepare jTable
	$('#OrderTableContainer').jtable(jtable_options);
	//Load person list from server
	$('#OrderTableContainer').jtable('load');
});