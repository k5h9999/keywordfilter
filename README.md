基于分词原理修改写的一个过滤敏感词库，可以改成动态，支持返回敏感词，高亮敏感词，替换敏感词等操作，本敏感词收集了4W多个违法词，几十个矫正词。

调用方法：
```java

//状态标识：正常：1；不正常：0

public static void main(String[] args) {
		String str = "一个网站就像一个人，存在一个从小到大白粉的过程。养一个网站和养一个人一样，不同时期漂白粉需要不同的方法，口交--不同的方法下有共24口交换机同的原则。";

		Long startTime = System.currentTimeMillis();
		WordFilter wf = new WordFilter();
		System.out.println(wf.filter_search(str));
		System.out
				.println(wf
						.filter_jk_info("一个网站就像一个人"));
		Long endTime = System.currentTimeMillis();

		System.out.println("时间：" + (endTime - startTime));
	}
```
