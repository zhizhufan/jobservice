$(document).ready(function(){
		//定义绑定上传按钮事件
		var button = $('#picFileImport');
		/*
		 * 异步 上传图片方法函数
		 */
		new AjaxUpload(button, {
			action: server.url + 'filegags/uploadFile',
			name: 'upload',// 更改上传的文件名
			autoSubmit:true,
			type:'POST',
			data: {},
			onSubmit : function(file , ext){
				button.val('文件上传中ing...');
				/**
				 *	①验证上传文件格式
				 **/
				
		/*		if(!(ext && /^(jpg|xlsx)$/.test(ext))){
					$.messager.alert('提示','您上传的文件格式不对, 或者不是excel文件, 请重新选择','info');
					$('#picfileTitle').val('');
					return false;
				}	*/
				/**
				 *	②设置上传参数
				 **/
				this.setData({
					'userid':$('#userid').val(),
					'preFileId':$('#imageid').val(),
					'type':'headimage'
				});
				
			},
			onComplete : function(file,response){
				var notice = response.substring(0,7);
				if(notice != 'success'){
					alert('上传文件失败,'+response);
				}else{
					//刷新新上传的图片
					var fileid = response.substring(7);
					$('#headImage').attr('src','');
				//	alert(fileid + new Date());
					$('#headImage').attr('src',server.url + 'filegags/downloadFile/'+fileid + '?time='+ new Date());
				}
				button.val('上传文件');
				this.enable();
			}
		});
	});