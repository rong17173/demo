package hrzhao.process.base;

import net.sf.json.JSONObject;
import hrzhao.beans.ProcessBean;
import hrzhao.beans.ReqMessageBean;

public interface ProcessInterface {
	public String doProcess(ReqMessageBean msgBean);
	public void setProcessId(Integer processId);
	public String getTips();
	
	public ProcessBean getProcessData();
	public JSONObject getParam();

}
