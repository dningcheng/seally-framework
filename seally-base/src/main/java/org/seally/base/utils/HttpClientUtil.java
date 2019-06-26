package org.seally.base.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * @Description http/https请求工具类
 * @Date 2019年6月14日
 * @author 邓宁城
 * 相关依赖如下
 * <!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient -->
	<dependency>
	    <groupId>org.apache.httpcomponents</groupId>
	    <artifactId>httpclient</artifactId>
	    <version>4.5.8</version>
	</dependency>
	
	<!-- https://mvnrepository.com/artifact/log4j/log4j -->
	<dependency>
	    <groupId>log4j</groupId>
	    <artifactId>log4j</artifactId>
	    <version>1.2.17</version>
	</dependency>
 * 
 */
public class HttpClientUtil {
	/**
     * HTTP协议
     */
    private static final String HTTPS_PROTOCOL = "https://";
    /**
     * 默认编码格式
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 日志
     */
    private static Logger logger = Logger.getLogger(HttpClientUtil.class);

    
    /**
     * @Description 发送GET请求
     * @Date 2019年6月25日
     * @author 邓宁城
     * @param url
     * @param charset
     * @return
     */
    public static String get(final String url, String charset) {
        if (null == charset){
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpClient = url.toLowerCase().startsWith(HTTPS_PROTOCOL) ? createSSLClientDefault() : HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(url);
        try {
        	
            // 提交请求并以指定编码获取返回数据
            HttpResponse httpResponse = httpClient.execute(get);
            int statuscode = httpResponse.getStatusLine().getStatusCode();
            if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) || (statuscode == HttpStatus.SC_MOVED_PERMANENTLY)
                    || (statuscode == HttpStatus.SC_SEE_OTHER) || (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                Header header = httpResponse.getFirstHeader("location");
                if (header != null) {
                    String newuri = header.getValue();
                    if ((newuri == null) || (newuri.equals(""))){
                        newuri = "/";
                    }
                    try {
                        httpClient.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        httpClient = null;
                    }
                    logger.info("重定向地址：" + newuri);
                    return get(newuri, null);
                }
            }
            logger.info("请求地址：" + url + "；响应状态：" + httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, charset);
        }
        catch (ClientProtocolException e) {
            logger.error("协议异常,堆栈信息如下", e);
            e.printStackTrace();
        }
        catch (IOException e) {
            logger.error("网络异常,堆栈信息如下", e);
        }
        finally {
            // 关闭连接，释放资源
            try {
                httpClient.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                httpClient = null;
            }
        }
        return null;
    }

    /**
     * @Description 发送delete请求
     * @Date 2019年6月25日
     * @author 邓宁城
     * @param url
     * @param charset
     * @return
     */
    public static String delete(String url, String charset) {
        if (null == charset){
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpClient = url.toLowerCase().startsWith(HTTPS_PROTOCOL) ? createSSLClientDefault() : HttpClientBuilder.create().build();
        HttpDelete del = new HttpDelete(url);
        try {
        	
            // 提交请求并以指定编码获取返回数据
            HttpResponse httpResponse = httpClient.execute(del);
            logger.info("请求地址：" + url + "；响应状态：" + httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, charset);
        }
        catch (ClientProtocolException e) {
            logger.error("协议异常,堆栈信息如下", e);
        }
        catch (IOException e) {
            logger.error("网络异常,堆栈信息如下", e);
        }
        finally {
            // 关闭连接，释放资源
            try {
                httpClient.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                httpClient = null;
            }
        }
        return null;
    }
    
    /**
     * @Description 发送put请求
     * @Date 2019年6月25日
     * @author 邓宁城
     * @param url 请求路径
     * @param charset 字符集 默认UTF-8
     * @return 
     */
    public static String put(String url, String charset) {
        if (null == charset){
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpClient = url.toLowerCase().startsWith(HTTPS_PROTOCOL) ? createSSLClientDefault() : HttpClientBuilder.create().build();
        HttpPut put = new HttpPut(url);
        try {
            // 提交请求并以指定编码获取返回数据
            HttpResponse httpResponse = httpClient.execute(put);

            logger.info("请求地址：" + url + "；响应状态：" + httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, charset);
        }
        catch (ClientProtocolException e) {
            logger.error("协议异常,堆栈信息如下", e);
        }
        catch (IOException e) {
            logger.error("网络异常,堆栈信息如下", e);
        }
        finally {
            // 关闭连接，释放资源
            try {
                httpClient.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                httpClient = null;
            }
        }
        return null;
    }
    
    /**
     * @Description 发送put请求
     * @Date 2019年6月25日
     * @author 邓宁城
     * @param url 请求路径
     * @param jsonBody json格式字符串请求体参数
     * @param charset 字符集 默认UTF-8
     * @return 
     */
    public static String put(String url, Map<String, Object> params, String charset) {
        if (null == charset){
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpClient = url.toLowerCase().startsWith(HTTPS_PROTOCOL) ? createSSLClientDefault() : HttpClientBuilder.create().build();
        HttpPut put = new HttpPut(url);

        // 封装请求参数
        List<NameValuePair> pairs = buildNameValuePairs(params);
        try {
            put.setEntity(new UrlEncodedFormEntity(pairs, charset));
            // 提交请求并以指定编码获取返回数据
            HttpResponse httpResponse = httpClient.execute(put);

            logger.info("请求地址：" + url + "；响应状态：" + httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, charset);
        }
        catch (ClientProtocolException e) {
            logger.error("协议异常,堆栈信息如下", e);
        }
        catch (IOException e) {
            logger.error("网络异常,堆栈信息如下", e);
        }
        finally {
            // 关闭连接，释放资源
            try {
                httpClient.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                httpClient = null;
            }
        }
        return null;
    }
    
    /**
     * @Description 发送put请求，参数放入请求体方式
     * @Date 2019年6月25日
     * @author 邓宁城
     * @param url 请求路径
     * @param jsonBody json格式字符串请求体参数
     * @param charset 字符集 默认UTF-8
     * @return 
     */
    public static String put(String url, String jsonBody, String charset) {
        if (null == charset){
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpClient = url.toLowerCase().startsWith(HTTPS_PROTOCOL) ? createSSLClientDefault() : HttpClientBuilder.create().build();
        HttpPut put = new HttpPut(url);

        try {
        	// 封装请求参数
        	put.setEntity(new StringEntity(jsonBody, charset));
            // 提交请求并以指定编码获取返回数据
            HttpResponse httpResponse = httpClient.execute(put);

            logger.info("请求地址：" + url + "；响应状态：" + httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, charset);
        }
        catch (ClientProtocolException e) {
            logger.error("协议异常,堆栈信息如下", e);
        }
        catch (IOException e) {
            logger.error("网络异常,堆栈信息如下", e);
        }
        finally {
            // 关闭连接，释放资源
            try {
                httpClient.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                httpClient = null;
            }
        }
        return null;
    }

    /**
     * @Description 发送POST请求
     * @Date 2019年6月25日
     * @author 邓宁城
     * @param url 请求路径
     * @param params 请求参数
     * @param charset 字符集 默认UTF-8
     * @return
     */
    public static String post(String url, Map<String, ?> params, String charset) {
        if (null == charset){
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpClient = url.toLowerCase().startsWith(HTTPS_PROTOCOL) ? createSSLClientDefault() : HttpClientBuilder.create().build();
        RequestConfig reqConf = RequestConfig.DEFAULT;
        HttpPost httpPost = new HttpPost(url);
        // 封装请求参数
        List<NameValuePair> pairs = buildNameValuePairs(params);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
            // 提交请求并以指定编码获取返回数据
            httpPost.setConfig(reqConf);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statuscode = httpResponse.getStatusLine().getStatusCode();
            if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) || (statuscode == HttpStatus.SC_MOVED_PERMANENTLY)
                    || (statuscode == HttpStatus.SC_SEE_OTHER) || (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                Header header = httpResponse.getFirstHeader("location");
                if (header != null) {
                    String newuri = header.getValue();
                    if ((newuri == null) || (newuri.equals(""))){
                        newuri = "/";
                    }
                    try {
                        httpClient.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        httpClient = null;
                    }
                    return get(newuri, null);
                }
            }

            logger.info("请求地址：" + url + "；响应状态：" + httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, charset);
        }
        catch (UnsupportedEncodingException e) {
            logger.error("不支持当前参数编码格式[" + charset + "],堆栈信息如下", e);
        }
        catch (ClientProtocolException e) {
            logger.error("协议异常,堆栈信息如下", e);
        }
        catch (IOException e) {
            logger.error("网络异常,堆栈信息如下", e);
        }
        finally {
            // 关闭连接，释放资源
            try {
                httpClient.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                httpClient = null;
            }
        }
        return null;
    }

    /**
     * @Description 发送POST请求， 参数放入请求体方式
     * @Date 2019年6月25日
     * @author 邓宁城
     * @param url 请求路径
     * @param jsonBody json字符串格式参数
     * @param charset 字符集 默认UTF-8
     * @return
     */
    public static String post(String url, String jsonBody, String charset) {
        if (null == charset){
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpClient = url.toLowerCase().startsWith(HTTPS_PROTOCOL) ? createSSLClientDefault() : HttpClientBuilder.create().build();
        RequestConfig reqConf = RequestConfig.DEFAULT;
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new StringEntity(jsonBody, charset));
            // 提交请求并以指定编码获取返回数据
            httpPost.setConfig(reqConf);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statuscode = httpResponse.getStatusLine().getStatusCode();
            if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) || (statuscode == HttpStatus.SC_MOVED_PERMANENTLY)
                    || (statuscode == HttpStatus.SC_SEE_OTHER) || (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                Header header = httpResponse.getFirstHeader("location");
                if (header != null) {
                    String newuri = header.getValue();
                    if ((newuri == null) || (newuri.equals(""))){
                        newuri = "/";
                    }
                    try {
                        httpClient.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        httpClient = null;
                    }
                    return get(newuri, null);
                }
            }

            logger.info("请求地址：" + url + "；响应状态：" + httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, charset);
        }
        catch (UnsupportedEncodingException e) {
            logger.error("不支持当前参数编码格式[" + charset + "],堆栈信息如下", e);
        }
        catch (ClientProtocolException e) {
            logger.error("协议异常,堆栈信息如下", e);
        }
        catch (IOException e) {
            logger.error("网络异常,堆栈信息如下", e);
        }
        finally {
            // 关闭连接，释放资源
            try {
                httpClient.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                httpClient = null;
            }
        }
        return null;
    }

    
    /**
     * @Description 创建一个受信任的https请求客户端
     * @Date 2019年6月25日
     * @author 邓宁城
     * @return
     */
    public static CloseableHttpClient createSSLClientDefault(){
        try {
            SSLContext sslContext=new SSLContextBuilder().loadTrustMaterial(
                    null,new TrustStrategy() {
                        //信任所有
                        public boolean isTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslsf=new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }
    

    /**
     * @Description 构建键值对参数
     * @Date 2019年6月25日
     * @author 邓宁城
     * @param params 入参
     * @return
     */
    private static List<NameValuePair> buildNameValuePairs(Map<String, ?> params) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (params == null) {
            return pairs;
        }
        for (String name : params.keySet()) {
            if (params.get(name) == null) {
                continue;
            }
            pairs.add(new BasicNameValuePair(name, params.get(name).toString()));
        }
        return pairs;
    }
    
    
    
    public static void main(String[] args) {
		
    	
//    	String sendGetReq2 = HttpClientUtil.sendGetReq("http://192.168.0.104:9200/_cat/indices", null);
//    	System.out.println(sendGetReq2);
    	
    	//物业云平台系统操作日志映射
//    	String systemLogMapping = "{\"mappings\":{\"pblog\":{\"properties\":{\"logId\":{\"type\":\"integer\"},\"orgId\":{\"type\":\"integer\"},\"userId\":{\"type\":\"integer\"},\"unitId\":{\"type\":\"integer\"},\"moduleCode\":{\"type\":\"integer\"},\"apiCode\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\",\"fields\":{\"kw\":{\"type\":\"keyword\"}}},\"userAccount\":{\"type\":\"keyword\",\"fields\":{\"searchField\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\"},\"prefix\":{\"type\":\"keyword\"}}},\"unitName\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\",\"fields\":{\"kw\":{\"type\":\"keyword\"}}},\"opMethod\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\"},\"opContent\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\"},\"opResult\":{\"type\":\"keyword\"},\"opTime\":{\"type\":\"date\",\"format\":\"strict_date_optional_time||epoch_millis\"},\"moduleParkPlate\":{\"type\":\"keyword\",\"fields\":{\"searchField\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\"},\"prefix\":{\"type\":\"keyword\"}}}}}},\"settings\":{\"index\":{\"number_of_shards\":\"5\",\"number_of_replicas\":\"1\",\"analysis\":{\"analyzer\":{\"ik_smart_pinyin_analyzer\":{\"type\":\"custom\",\"filter\":[\"pinyin_filter\"],\"char_filter\":\"html_strip\",\"tokenizer\":\"ik_smart\"},\"ik_maxword_pinyin_analyzer\":{\"type\":\"custom\",\"filter\":[\"pinyin_filter\"],\"char_filter\":\"html_strip\",\"tokenizer\":\"ik_max_word\"}},\"filter\":{\"pinyin_filter\":{\"type\":\"pinyin\",\"keep_first_letter\":true,\"keep_separate_first_letter\":false,\"limit_first_letter_length\":16,\"keep_full_pinyin\":false,\"keep_joined_full_pinyin\":true,\"keep_none_chinese\":true,\"keep_none_chinese_together\":true,\"keep_original\":true,\"lowercase\":true,\"trim_whitespace\":true,\"remove_duplicated_term\":false},\"length_filter\":{\"type\":\"length\",\"min\":2}}}}}}";
//    	
    	//物业云平台系统工单映射
//    	String workOrderMapping = "{\"mappings\":{\"workorder\":{\"properties\":{\"unitId\":{\"type\":\"integer\"},\"businessType\":{\"type\":\"integer\"},\"businessId\":{\"type\":\"integer\"},\"stateType\":{\"type\":\"integer\"},\"title\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\",\"fields\":{\"kw\":{\"type\":\"keyword\"}}},\"subTitle\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\",\"fields\":{\"kw\":{\"type\":\"keyword\"}}},\"userName\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\",\"fields\":{\"kw\":{\"type\":\"keyword\"}}},\"userType\":{\"type\":\"keyword\"},\"userAddress\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\",\"fields\":{\"kw\":{\"type\":\"keyword\"}}},\"userTel\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\",\"fields\":{\"kw\":{\"type\":\"keyword\"}}},\"briefContent\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\"},\"message\":{\"type\":\"keyword\"},\"createTime\":{\"type\":\"date\",\"format\":\"strict_date_optional_time||epoch_millis\"},\"executeTime\":{\"type\":\"date\",\"format\":\"strict_date_optional_time||epoch_millis\"},\"grabTime\":{\"type\":\"date\",\"format\":\"strict_date_optional_time||epoch_millis\"},\"updateTime\":{\"type\":\"date\",\"format\":\"strict_date_optional_time||epoch_millis\"},\"orderStep\":{\"type\":\"integer\"},\"star\":{\"type\":\"integer\"},\"sendUserName\":{\"type\":\"keyword\"},\"sendAccount\":{\"type\":\"keyword\"},\"doAccount\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\",\"fields\":{\"kw\":{\"type\":\"keyword\"}}},\"doUserName\":{\"type\":\"text\",\"analyzer\":\"ik_maxword_pinyin_analyzer\",\"search_analyzer\":\"ik_max_word\",\"fields\":{\"kw\":{\"type\":\"keyword\"}}},\"comState\":{\"type\":\"integer\"}}}},\"settings\":{\"index\":{\"number_of_shards\":\"5\",\"number_of_replicas\":\"1\",\"analysis\":{\"analyzer\":{\"ik_smart_pinyin_analyzer\":{\"type\":\"custom\",\"filter\":[\"pinyin_filter\"],\"char_filter\":\"html_strip\",\"tokenizer\":\"ik_smart\"},\"ik_maxword_pinyin_analyzer\":{\"type\":\"custom\",\"filter\":[\"pinyin_filter\"],\"char_filter\":\"html_strip\",\"tokenizer\":\"ik_max_word\"}},\"filter\":{\"pinyin_filter\":{\"type\":\"pinyin\",\"keep_first_letter\":true,\"keep_separate_first_letter\":false,\"limit_first_letter_length\":16,\"keep_full_pinyin\":false,\"keep_joined_full_pinyin\":true,\"keep_none_chinese\":true,\"keep_none_chinese_together\":true,\"keep_original\":true,\"lowercase\":true,\"trim_whitespace\":true,\"remove_duplicated_term\":false}}}}}}";
    	
//    	JSONObject parseObject = JSON.parseObject(workOrderMapping);

//    	String response = HttpClientUtil.sendPutReq("http://192.168.0.104:9200/dnc_test_workorder", JSON.toJSONString(parseObject), null);
//    	System.out.println(response);
    	System.out.println(HttpClientUtil.get("https://www.seally.cn:9200/_cat/indices", null));
//    	System.out.println(HttpClientUtil.get("http://192.168.0.104:9200/dnc_test_workorder/_mapping", null));
//    	System.out.println(HttpClientUtil.get("http://192.168.0.104:9200/dnc_test_workorder/_settings", null));
    	
//    	System.out.println(HttpClientUtil.put("http://www.seally.cn:9200/dnc-test2", null));
    	
	}
    
    
    /**=============================业务方法==========================**/
    
    
    /**
     * @Description 创建索引
     * @Date 2019年6月25日
     * @author 邓宁城
     * @param indexUrl 带索引名称的请求端点 ： http://esHost:9200/indexName
     * @param jsonMappingAndSetting 映射和配置json字符串
     * @return
     */
    private static boolean createIndexWithMappingAndSettings(String indexUrl,String jsonMappingAndSetting,boolean overRide) {
    	
    	String response = HttpClientUtil.put("http://192.168.0.104:9200/dnc_test_systemlog", jsonMappingAndSetting, null);
    
    	
    	return true;
    }
    
}
