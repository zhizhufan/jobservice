/***********************************************
 *******  后台管理页面框架脚本--用户管理  ********
 ***********************************************/

//收集参数并进入后台查询方法
function query(page,articleType){
	var paramStr = '';	//查询字符串
	var totalPage = $('#totalPage').val();
	if(page == null || page == undefined || page <= 1 || page == ''){
		paramStr += 'page=1'; 
	}else if(page >= totalPage){
		paramStr += 'page=' + totalPage;
	}else{
		paramStr += 'page=' + page;
	}
	var targetName = $('#targetName').val();
	if(targetName != null && targetName != undefined && targetName != ''){
		paramStr += '&targetName=' + targetName; 
	}
	if(articleType == null || articleType == undefined || articleType == ''){
		var articleType = $('#articleType').val();
		paramStr += '&articleType=' + articleType; 
	}else{
		paramStr += '&articleType=' + articleType; 
	}
	// 跳转提交
	window.location.href = getRootPath() + '/manage/news/news_list?' + paramStr;
}

//参数下拉框点击事件
function selectButton(valueButton, nameButton, value, name){
	//给对应控件 赋value值
	$('#'+valueButton).val(value);
	//给对应控件 赋name值
	$('#'+nameButton).text(name);
}

//跳转到新增文章页面
function addEntityGet(){
	var articleType = $('#articleType').val();
	window.location.href = getRootPath() + '/manage/news/add/' + articleType;
}

// 新增, 修改, 返回  综合方法
function updateEntity(submitType,objId){
	//返回
	if(submitType == 'return'){
		window.history.back();
		return;
	}
	// 新增
	if(submitType == 'add'){
		var param = checkObject();
		if(!param){
			return false;
		}
		$.ajax({
			url:server.url + 'manage/news/add',
			type:'post',
			dataType:'json',
			data:{
				'title':param.title,
				'author':param.author,
				'source':param.source,
				'type':param.type,
				'content':param.content
			},
			success:function(data){
				if(data.notice == 'success'){
					alert('保存成功!');
				}else{
					alert(data.notice);
				}
			}
		});
	}
	// 删除
	if(submitType == 'delete'){
		var confirm = window.confirm('此操作不可恢复, 确认删除吗?');
		if(!confirm){
			return;
		}
		$.ajax({
			url:server.url + 'manage/news/delete/' + objId,
			type:'post',
			dataType:'json',
			data:{},
			success:function(data){
				if(data.notice == 'success'){
					alert('删除成功!');
					window.location.reload();
				}else{
					alert(data.notice);
				}
			}
		});
	}
	//更新
	if(submitType == 'update'){
		var param = checkObject();
		if(!param){
			return false;
		}
		$.ajax({
			url:server.url + 'manage/news/edit',
			type:'post',
			dataType:'json',
			data:{
				'id':objId,
				'title':param.title,
				'author':param.author,
				'source':param.source,
				'type':param.type,
				'content':param.content
			},
			success:function(data){
				if(data.notice == 'success'){
					alert('更新成功!');
				}else{
					alert(data.notice);
				}
			}
		});
	}
	return;
}

//校验提交表单中所有控件方法
function checkObject(){
	var param  = new Object();
	var title = $('#title').val();
	if(title == null || title == ''){
		alert('标题不能为空.');
		$('#title').focus();
		return false;
	}
	param.title = title;
	var author = $('#author').val();
	if(author == null || author == ''){
		$('#author').focus();
		return false;
	}
	param.author = author;
	var source = $('#source').val();
	if(source != null && source != ''){
		param.source = source;
	}
	var articleType = $('#articleType').val();
	if(articleType != null && articleType != ''){
		param.type = articleType;
	}
	var content = $('#content').val();
	if(content == null || content == ''){
		alert('文章内容不能为空.');
		$('#content').focus();
		return false;
	}
	param.content = content;
	return param;
}
	
	