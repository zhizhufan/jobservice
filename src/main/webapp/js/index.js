$(function () {
	/*首页、找工作鼠标效果*/
	$("#Mainnav").find("ul").find("li").find("a:not(.Nav_sy)").hover(function () {
		$(this).css("color", "yellow");
	}, function () { $(this).css("color", "white"); });
	/*设置热门搜索字体颜色*/
	$(".VerticalLineUL").find("a").css("color", "#20a4fe");
	
	/*******  个人注册/企业注册   ********/
	$("#PersonalRegister").bind("click", function () {
    	window.location.href = server.url + 'regP'; //个人注册
	});
	$("#BusinessRegister").bind("click", function () {
		window.location.href = server.url + 'regC'; //企业注册
	});
	
	/*******  光标focus效果   ********/
	$('#loginName').focus(function(){
		if(this.value == '请输入用户名'){
			this.value = '';
		}
	}).blur(function(){
		if(this.value == '' || this.value == null || this.value == undefined){
			this.value = '请输入用户名';
		}
	});
	
	/*******  个人单位切换   ********/
	$("#PersonalTab").bind("click", function () {
		$('#identity').val('person');
		$(this).removeClass().addClass("BlueFirst"); 
		$("#BusinessTab").removeClass().addClass("WhiteLast"); 
		$(".loginmain").removeClass("loginmain2"); 
		$(".LoginRight").removeClass("LoginRight2"); 
		$("#LoginBtn").removeClass("loginbtnimg2"); 
		$("#LinkHrefBtn").attr("href", ""); 
		$("#loginName").val("请输入用户名");
	}).mouseover(function () { 
		this.style.cursor = "pointer"; 
	}).mouseout(function () { 
		this.style.cursor = "auto"; 
	});

	$("#BusinessTab").bind("click", function () {
		$('#identity').val('company');
		$(this).removeClass().addClass("BlueLast"); 
		$("#PersonalTab").removeClass().addClass("WhiteFirst "); 
		$(".loginmain").addClass("loginmain2"); 
		$(".LoginRight").addClass("LoginRight2"); 
		$("#LoginBtn").addClass("loginbtnimg2"); 
		$("#LinkHrefBtn").attr("href", ""); 
		$("#loginName").val("请输入用户名");
	}).mouseover(function () {
		this.style.cursor = "pointer";
	}).mouseout(function () {
		this.style.cursor = "auto";
	});
	
	 /*******  鼠标mouseover效果   ********/
	$("#LoginBtn").mouseover(function () { this.src = "/images/HomePageImage/LoginImage/HoverLogin.gif"; this.style.cursor = "pointer"; }).mouseout(function () { this.src = "images/HomePageImage/LoginImage/LoginButtom.gif"; this.style.cursor = "pointer"; })
	$("#PersonalRegister").mouseover(function () { 
		this.style.background = 'url(images/HomePageImage/LoginImage/HoverPersonalRegister.gif)'; 
		this.style.cursor = "pointer"; 
	}).mouseout(function () { 
		this.style.background = 'url(images/HomePageImage/LoginImage/PersonalRegister.gif)'; 
		this.style.cursor = "auto"; 
	})
	$("#BusinessRegister").mouseover(function () { 
		this.style.background = 'url(images/HomePageImage/LoginImage/HoverBusinessRegister.gif)'; 
		this.style.cursor = "pointer"; 
	}).mouseout(function () { 
		this.style.background = 'url(images/HomePageImage/LoginImage/BusinessRegister.gif)'; 
		this.style.cursor = "auto"; 
	})
	
	 /*******  点击登陆   ********/
	$("#LoginBtn").click(function(){
		$('form:first').submit();
	});
	
	//初始化职位种类切换效果...
	easyTab(".TabCss").click(function(data){
	//	alert(data);
	});
});

/*******  不同种类职位轮换效果   ********/
function changeShowJob(jobCategory,obj){
	//异步请求数据
	$.ajax({
		url : 'job/getByCategory/' + jobCategory,
		type : 'post',
		dataType : 'json',
		success : function(data){
			//如果有数据返回, 则遍历数据进行显示
			if(data.jobByCategoryList != null && data.jobByCategoryList.length > 0){
				$('#TabsMainShow').find('ul').children().remove();
				var str ='';
				$.each(data.jobByCategoryList, function(i, item){
					str += '<li class="">';
					str += '<a href="company/getOneForShow?id=' + item.companyId+ '">' + item.companyName + '</a>';
					str += '<a href="job/getOneForShow?id=' + item.jobId + '" style="color:#0868C8" class="position">' + item.jobName + '</a>';
					str += '</li>';
				});
				//先清除当前TabsMainShow div框中的所有li子元素, 然后添加上新的招聘信息
				$('#TabsMainShow').find('ul').html(str);
			}
		},
		error : function(){
			alert("请求数据发生错误, 请联系管理员.");
		}
	});
}
/*******  登陆用户名和密码校验   ********/
function check(){
	//账号
	var loginName = $('#loginName').val();
	if(!verify.checkname(loginName)){
		alert('用户名只能为5-20为的英文, 数字或下划线_组成.');
		$('#loginName').focus();
		return false;
	}
	//密码
	var passWord = $('#passWord').val();
	if(!verify.checkname(passWord)){
		alert('密码只能为5-20为的英文, 数字或下划线_组成.');
		$('#passWord').focus();
		return false;
	}
	return true;
}

/*******  登陆   ********/
function blur_loginName(){
	var loginName = $('#loginName').val();
	$.ajax({
		url : server.url + 'user/',
		dataType : 'json',
		type : 'GET',
		async : false,
		success : function(json) {
			//存在时
			if(json.notice == 'success'){
				$('#loginNameNotice').html('√');
				//选中时
				$('#btn-submit').removeClass('gray').addClass('blue').removeAttr('disabled');
				return false;
			}
			if(json.notice == 'failure'){
				$('#loginNameNotice').html('该用户名已经存在, 请重新填写一个.');
				//按钮变灰
				$('#btn-submit').removeClass('blue').addClass('gray').attr('disabled','disabled');
				return false;
			}
		},
	});
}
