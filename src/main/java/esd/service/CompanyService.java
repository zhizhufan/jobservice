package esd.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import esd.bean.Area;
import esd.bean.Company;
import esd.bean.Job;
import esd.bean.Record;
import esd.bean.Resume;
import esd.controller.Constants;
import esd.controller.SecureResumeController;
import esd.dao.CompanyDao;
import esd.dao.RecordDao;

@Service
public class CompanyService<T> {

	private static Logger log = Logger.getLogger(SecureResumeController.class);

	@Autowired
	private CompanyDao dao;

	@Autowired
	private RecordDao recordDao;

	@Autowired
	private KitService kitService;

	@Autowired
	private JobService jobService;

	@Autowired
	private ParameterService pService;

	// 保存一个对象
	public boolean save(Company company) {
		boolean bl = pService.getSwitchStatus(Constants.Switch.COMPANY
				.toString(), company.getArea().getCode());
		if (company != null) {
			// 如果user审核开关打开的话, 则将user设置为 待审核 状态
			if (bl) {
				company.setCheckStatus(Constants.CheckStatus.DAISHEN.toString());
			} else {
				// //如果user审核开关没有打开的话, 则将user设置为 ok 状态
				company.setCheckStatus(Constants.CheckStatus.OK.toString());
			}
		}
		return dao.save(company);
	}

	// 删除一个对象
	public boolean delete(int id) {
		return dao.delete(id);
	}

	// 更新一个对象
	public boolean update(Company company) {
		return dao.update(company);
	}

	// 按id查询一个对象,以供前台显示
	public Company getOneForShow(int id) {
		Company company = (Company) dao.getById(id);
		company = kitService.getForShow(company);
		return company;
	}

	// 按id查询一个对象
	public Company getById(int id) {
		return (Company) dao.getById(id);
	}

	// 按公司对象本身属性条件查询
	public List<Company> check(Company company) {
		return dao.getByObj(company);
	}

	// 后台审核用方法
	// 分页查询方法,其中数据已被处理成适合前台展示的
	public List<Company> getListShowForManage(Company company, int start,
			int size) {
		if (company != null) {
			// 处理传进来的地区code, 变成适用于sql语句使用的格式
			if (company.getArea() != null) {
				if (company.getArea().getCode() != null) {
					company.getArea().setCode(
							KitService.areaCodeForSql(company.getArea()
									.getCode()));
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (start < 0) {
			start = 1;
		}
		if (size < 0) {
			size = 50;
		}
		map.put("company", company);
		map.put("start", (start - 1) * size);
		map.put("size", size);
		List<Company> clist = dao.getByPage(map);
		for (Company c : clist) {
			c = kitService.getForShow(c);
		}
		return clist;
	}

	// 分页查询方法,其中数据已被处理成适合前台展示的
	public List<Company> getForListShow(Company company, int start, int size) {
		if (company != null) {
			// 处理传进来的地区code, 变成适用于sql语句使用的格式
			if (company.getArea() != null) {
				if (company.getArea().getCode() != null) {
					company.getArea().setCode(
							KitService.areaCodeForSql(company.getArea()
									.getCode()));
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (start < 0) {
			start = 1;
		}
		if (size < 0) {
			size = 50;
		}
		map.put("company", company);
		map.put("start", (start - 1) * size);
		map.put("size", size);
		List<Company> clist = dao.getByPage(map);
		for (Company c : clist) {
			c = kitService.getForShow(c);
		}
		return clist;
	}

	// 分页查询方法,--标准分页方法
	// @param map中为具体的参数
	// 1-类对象, 名称为对应类的小写!!切记切记!! 字段的值即为查询条件; 2-start: 起始索引; 3-size: 返回条数
	public List<Company> getByPage(Company company, int startPage, int size) {
		if (company != null) {
			// 处理传进来的地区code, 变成适用于sql语句使用的格式
			if (company.getArea() != null) {
				if (company.getArea().getCode() != null) {
					company.getArea().setCode(
							KitService.areaCodeForSql(company.getArea()
									.getCode()));
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("company", company);
		map.put("start", startPage <= 0 ? Constants.START : (startPage - 1)
				* (size <= 0 ? Constants.SIZE : size));
		map.put("size", size <= 0 ? Constants.SIZE : size);
		return dao.getByPage(map);
	}

	// 获得数据总条数
	public int getTotalCount(Company company) {
		if (company != null) {
			// 处理传进来的地区code, 变成适用于sql语句使用的格式
			if (company.getArea() != null) {
				if (company.getArea().getCode() != null) {
					company.getArea().setCode(
							KitService.areaCodeForSql(company.getArea()
									.getCode()));
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("company", company);
		return dao.getTotalCount(map);
	}

	// 得到最新的N个公司
	public List<Company> getByNew(String acode, int size) {
		// 处理传进来的地区code, 变成适用于sql语句使用的格式
		if (acode != null) {
			acode = KitService.areaCodeForSql(acode);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Company company = new Company();
		company.setCheckStatus(Constants.CheckStatus.OK.toString());
		company.setArea(new Area(acode));
		map.put("company", company);
		map.put("start", 0);
		map.put("size", size <= 0 ? Constants.SIZE : size);
		List<Company> list = dao.getByPage(map);
		// 处理为适合前台显示的字段数据
		list = kitService.getForShowCompany(list);
		return list;
	}

	// 根据账号id, 得到公司对象
	public Company getByAccount(int uid) {
		return dao.getByAccount(uid);
	}

	// 查询投递到一家公司的所有简历列表
	public List<Record> getAllGotResume(int companyid, int startPage, int size) {
		Map<String, Object> map = new HashMap<String, Object>();
		Record r = new Record();
		r.setcID(companyid);
		map.put("record", r);
		map.put("start", (startPage - 1) * size);
		map.put("size", size);
		List<Record> list = recordDao.getByPage(map);
		for (Record record : list) {
			record = kitService.getForShow(record);
		}
		return list;

	}

	// 查询投递到一家公司的所有简历总数
	public int getAllGotResumeCount(int companyid) {
		Map<String, Object> map = new HashMap<String, Object>();
		Record r = new Record();
		r.setcID(companyid);
		map.put("record", r);
		return recordDao.getTotalCount(map);

	}

	// 查询投递到某一固定职位的所有简历
	public List<Resume> getResumeToJob(int jobid, int startPage, int size) {
		Map<String, Object> map = new HashMap<String, Object>();
		Record r1 = new Record();
		r1.setjID(jobid);
		map.put("record", r1);
		map.put("start", (startPage - 1) * size);
		map.put("size", size);
		return recordDao.getByPage(map);
	}

	// 根据公司id, 导出该公司和他的招聘信息,返回下载文件的路径
	public String getBuildCompany(int cid, String url) {
		// ①得到数据model
		// 得到公司对象
		Company company = getOneForShow(cid);
		if (company == null) {
			return null;
		}
		// 得到该公司发布的招聘信息
		List<Job> list = jobService.getByCompanyForShow(cid, 1, 5);
		int i = list.size();
		log.info(" company'job number : " + list.size());
		// ②获得对应地区的模板文件路径
		String templatePath = getTemplatePath(company.getArea().getCode(), url);
		// ③生成的目标文件路径
		String destPath = "temp" + "/" + "company.xls";
		// ④向模板中插入原始数据
		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("company", company);
		beans.put("jobList", list);
		InputStream is = null;
		XLSTransformer transformer = null;
		try {
			is = new FileInputStream(templatePath);
			transformer = new XLSTransformer();
			HSSFWorkbook workBook = (HSSFWorkbook) transformer.transformXLS(is,
					beans);
			HSSFSheet sheet = workBook.getSheetAt(0);
			sheet.addMergedRegion(new Region(9, (short) 0, (10 + i), (short) 0));
			OutputStream os = new FileOutputStream(url + destPath);
			workBook.write(os);
			is.close();
			os.flush();
			os.close();
			log.info("生成excel文件成功");
			return destPath;
		} catch (Exception e) {
			log.info("生成excel文件发生异常");
			return null;
		}
	}

	// 根据公司多个公司的id, 返回该公司及其招聘信息的xls文件的路径
	public String getBuildCompany(int[] ids, String url) {
		if (ids == null || ids.length == 0) {
			return null;
		}
		String uuid = UUID.randomUUID().toString();
		// 生成的公司文件所存放的路径
		// 即temp缓存文件加下生成的一个随机文件夹如 context\\temp\\uuid\\
		String tempPath = url + "temp" + File.separator + uuid;
		// 创建文件夹
		File tempFile = new File(tempPath);
		if (!tempFile.exists()) {
			tempFile.mkdir();
		}
		// 循环生成简历
		for (int i = 0; i < ids.length; i++) {
			int cid = ids[i];
			// 得到公司对象
			Company company = getOneForShow(cid);
			if (company == null) {
				continue;
			}
			// 得到该公司发布的招聘信息
			List<Job> list = jobService.getByCompanyForShow(cid, 1, 5);
			int len = 0;
			if (list != null) {
				log.info("list.size() : " + list.size());
				len = list.size();
			}
			log.info(" company'job number : " + list.size());
			Map<String, Object> beans = new HashMap<String, Object>();
			beans.put("company", company);
			beans.put("jobList", list);
			InputStream is = null;
			XLSTransformer transformer = null;
			try {
				// 获得对应地区的模板文件路径
				String templatePath = getTemplatePath(company.getArea()
						.getCode(), url);
				is = new FileInputStream(templatePath);
				transformer = new XLSTransformer();
				HSSFWorkbook workBook = (HSSFWorkbook) transformer
						.transformXLS(is, beans);
				HSSFSheet sheet = workBook.getSheetAt(0);
				sheet.addMergedRegion(new Region(9, (short) 0, (10 + i),
						(short) 0));
				// 保存的excel文件命名为： 公司名+(id).xls
				OutputStream os = new FileOutputStream(tempPath
						+ File.separator + company.getName() + "("
						+ company.getId() + ").xls");
				workBook.write(os);
				is.close();
				os.flush();
				os.close();
				log.info("生成excel文件成功");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("生成excel文件发生异常");
			}
		}
		// 生成的zip压缩文件存放路径,压缩文件名也为上面存放简历文件夹同名zip文件
		// 从简历所在的缓存文件夹tempFile读取所有excel文件, 打包成zip压缩文件
		String destPath = "temp" + "/" + uuid + ".zip";
		// 压缩的目标文件
		File zipFile = new File(url + destPath);
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
					zipFile));
			// 输入流
			InputStream input = null;
			if (tempFile.isDirectory()) {
				File[] files = tempFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					input = new FileInputStream(files[i]);
					zos.putNextEntry(new ZipEntry(files[i].getName()));
					int temp = 0;
					while ((temp = input.read()) != (-1)) {
						zos.write(temp);
					}
					input.close();
				}
			}
			zos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return destPath;
	}

	// 根据地区code, 返回他所使用的模板路径
	private String getTemplatePath(String acode, String url) {
		// 第一次尝试路径
		String filePath = url + "templates" + File.separator + acode
				+ File.separator + "company.xls";
		File file = new File(filePath);
		if (file.exists()) {
			return filePath;
		}
		// 本地模板不存在则搜索它的上级模板--市级
		acode = "20" + acode.substring(2, 6) + "00";
		filePath = url + "templates" + File.separator + acode + File.separator
				+ "company.xls";
		file = new File(filePath);
		if (file.exists()) {
			return filePath;
		}
		// 市级模板不存在则搜索它的上级模板--省级
		acode = "10" + acode.substring(2, 4) + "0000";
		filePath = url + "templates" + File.separator + acode + File.separator
				+ "company.xls";
		file = new File(filePath);
		if (file.exists()) {
			return filePath;
		}
		// 都不存在, 则使用标准模板
		filePath = url + "templates" + File.separator + "company.xls";
		return filePath;
	}
}
