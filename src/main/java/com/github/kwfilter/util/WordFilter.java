/*


  审核状态标识：正常：1；不正常：0
    
  filter_in_com		写入状态公司名称
  filter_in_info	写入状态非公司名称
  filter_out_com	公司名称输出
  filter_out_info	数据输出
  filter_jk_com		监控 公司名称;
  filter_jk_info	监控 非公司名称
 
*/
package com.github.kwfilter.util;


public class WordFilter{
	public KeyWordFilter kwf = KeyWordFilter.getInstance();
//	public String filter_in_com(String content){
//		return kwf.filter_in(content, "1","0");
//	}
//	public String filter_in_info(String content){
//		return kwf.filter_in(content, "0","0");
//	}
//	public String filter_jk_com(String content){
//		return kwf.filter_jk(content,"1","0","<font color=#ff0000>","</font>","<font color=#00ff00>","</font>");
//	}
	public String filter_jk_info(String content){
		return kwf.filter_jk(content,"0","0","<font color=#ff0000>","</font>","<font color=#00ff00>","</font>");
	}
//	public String filter_out_com(String content){
//		return kwf.filter_out(content, "1","0");
//	}
//	public String filter_out_info(String content){
//		return kwf.filter_out(content, "0","0");
//	}
	
	public String filter_jk(String content,String sta,String mgcstyle1,String mgcstyle2,String wfcstyle1,String wfcstyle2){
		return kwf.filter_jk(content,sta,"0",mgcstyle1, mgcstyle2, wfcstyle1, wfcstyle2);
	}
	public String filter_search(String content){
		return kwf.filter_search(content);
	}
	
	/*
	 * 重新读取词库文件
	 */
	public boolean reload(){
		return kwf.reload();
    }

}