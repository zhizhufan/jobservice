/*********************************************/
/************ 找回用户名/密码js脚本 ***********/
/*********************************************/

//向邮箱发送用户名/密码功能
function setGetBack(type,email){
	//异步提交发送邮箱的方法
	$.ajax({
		url: server.url + 'getBack',
		type:'POST',
		dataType:'json',
		data:{'email':email,'type':type},
		success:function(json){
			if(json.notice == 'success'){
				alert('发送成功!');
			}else{
				alert(json.notice);
			}
		},
		error:function(){
			alert('操作失败!');
		}
	});
}


function getBack(type){
	if(type == null || type == '' || type==undefined){
		alert('传递的发送类型参数为空, 请联系管理员.');
		return;
	}
	//校验email
	var email = $('#email').val();
	if(!verify.isEmail(email)){
		alert('您输入的邮箱不符合规则, 请重新输入.');
		return false;
	}else{
		$.ajax({
			url: server.url + 'user/checkEmail',
			type:'POST',
			dataType:'json',
			data:{'email':email},
			success:function(json){
				if(json.notice == 'success'){
					setGetBack(type,email);
					return true;
				}else{
					alert('该邮箱不存在.');
					return false;
				}
			},
			error:function(){
				alert('操作失败!');
				return false;
			}
		});
	}
}

