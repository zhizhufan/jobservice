

function job_check(){
	//职位名称
	var name = $('#name').val();
	if(name == null || name == ''){
		alert('姓名不能为空!');
		$('#name').focus();
		return false;
	}
	//招聘人数
	var reg_number = /^[0-9]{1,4}/;
	var hireNumber = $('#hireNumber').val();
	if(!reg_number.test(hireNumber)){
        alert('招聘人数不能为空, 且只能为数字!');
		$('#hireNumber').focus();
		return false;
	}
	//学历
	var education = $('#education').val();
	if(education == null || education == ''){
		alert('请选择最低学历!');
		$('#education').focus();
		return false;
	}
	//工作经验
	var experience = $('#experience').val();
	if(experience == null || experience == ''){
		alert('请选择最低工作经验年限!');
		$('#experience').focus();
		return false;
	}
	//岗位性质
	var nature = $('#nature').val();
	if(nature == null || nature == ''){
		alert('请选择岗位性质!');
		$('#nature').focus();
		return false;
	}
	//有效时间
	var effectiveTime = $('#effectiveTime').val();
	if(effectiveTime == null || effectiveTime == ''){
		alert('请选择职位有效期!');
		$('#effectiveTime').focus();
		return false;
	}
	//地区
	var area_lv3 = $('#area_lv3').val();
	if(area_lv3 == null || area_lv3 == ''){
		alert('请选择具体的期望工作地!');
		$('#area_lv1').focus();
		return false;
	}
	//职位类别
	var jobCategory_lv3 = $('#jobCategory_lv3').val();
	if(jobCategory_lv3 == null || jobCategory_lv3 == ''){
		alert('请选择具体的工作类别!');
		$('#jobCategory_lv1').focus();
		return false;
	}
	//联系人
	var contactPerson = $('#contactPerson').val();
	if(contactPerson == null || contactPerson == ''){
		alert('请选择联系人!');
		$('#contactPerson').focus();
		return false;
	}
	//电话/手机
	var contactTel = $('#contactTel').val();
	if(!verify.checkTel(contactTel)){
        alert('请检查你输入的电话/手机号码!');
		$('#contactTel').focus();
		return false;
	}
	return true;
}