package hrzhao.pcs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import hrzhao.beans.AcountBean;
import hrzhao.beans.OrdersBean;
import hrzhao.beans.ReqMessageBean;
import hrzhao.dao.AcountBeanDao;
import hrzhao.dao.OrdersBeanDao;
import hrzhao.pcs.base.PcsBase;

public class PcsConfirm extends PcsBase {

	public PcsConfirm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String doProcess(ReqMessageBean msgBean) {
		// TODO Auto-generated method stub
		String content = msgBean.getContent();
		if(content == null || content.equals("")){
			goThisProcess();
			return "请输入选项";
		}
		int selectedId = -1;
		try{
			selectedId = Integer.parseInt(content);
		}catch(Exception e){
			goThisProcess();
			return "请输入数字";
		}
		if(selectedId <=0){
			goThisProcess();
			return "请输入有正确的选项";
		}
		int orderId = -1;
		try{
			JSONObject processData = this.getProcessData();
			if(processData != null){
				JSONArray orderSelection = (JSONArray )processData.get("Selection");
				@SuppressWarnings("unchecked")
				Iterator<JSONObject> it = orderSelection.iterator();
				if(it == null || !it.hasNext()){
					goNegativeId();
					return "系统错误";
				}
				while(it.hasNext()){
					JSONObject item = it.next();
					if((int)item.get("key") == selectedId){
						orderId = (int)item.get("value");
						break;
					}
				}
			}
		}catch(Exception e){
			goNegativeId();
			return "系统错误";
		}
		if(orderId<0){
			goNegativeId();
			return "系统错误";
		}
		OrdersBeanDao orderDao = new OrdersBeanDao();
		OrdersBean order = orderDao.getOrderById(orderId);
		if(order.getStatus() < 2){
			goThisProcess();
			return "此订单尚未完成配送";
		}else if(order.getStatus() >2 ){
			goThisProcess();
			return "此订单尚状态不正常";
		}else{
			order.setStatus(3);
			String msg = dataFormate.format(order.getIntime())+"，"+order.getProduct().getName()
					+order.getAmount() +"桶,确认成功";
			goNextId();
			orderDao.updateOrder(order);
			return msg;
		}
		
	}
	private DateFormat dataFormate = new SimpleDateFormat("MM/dd hh:mm");
	private String[] status = OrdersBean.STATUS;

	@Override
	public String getTips() {
		// TODO Auto-generated method stub
		OrdersBeanDao orderDao = new OrdersBeanDao();
		List<OrdersBean> list = orderDao.getOrderByCustomerName(getCustomer().getName());
		if(list == null || list.size()<=0){
			//没有订单
			return "没有未完成的订单，欢迎下单，请输入 # 返回";
		}else{
			JSONObject processData = new JSONObject();
			List<HashMap<String, Integer>> selectList = new ArrayList<HashMap<String,Integer>>(); 
			String msg = "";
			Iterator<OrdersBean> it = list.iterator();
			int i = 1;
			while(it.hasNext()){
				OrdersBean order = it.next();
				msg += i+"、"+ order.getProduct().getName() 
						+ order.getAmount() +"桶，"
						+ status[order.getStatus()] +"，"
						+ dataFormate.format(order.getIntime())+"\n";
				HashMap<String, Integer > hm = new HashMap<String,Integer>();
				hm.put("key", i);
				hm.put("value", order.getId());
				selectList.add(hm);
				processData.element("Selection", selectList);
				setNextProcessData(processData);
				i++;
			}
			msg += super.getTips();
			return msg;
		}
		//"请输入序号确认订单"
	}
	

}