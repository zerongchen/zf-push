package com.aotain.zongfen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	private static final Log log = LogFactory.getLog(HttpUtils.class);
	
	public static final String UTF8 = "UTF-8"; // 定义编码格式 UTF-8
	public static final String GBK = "GBK"; // 定义编码格式 GBK
	private static final String EMPTY = "";

	/**
	 * HttpClient连接SSL url: https://localhost:8443/myDemo/Ajax/serivceJ.action
	 * password : "123456".toCharArray()
	 */
	public static String ssl(String url, String password) {
		String sslContent = EMPTY;
		CloseableHttpClient httpclient = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			FileInputStream instream = new FileInputStream(new File("/tomcat.keystore"));
			try {
				// 加载keyStore d:\\tomcat.keystore
				trustStore.load(instream, password.toCharArray());
			} catch (CertificateException e) {
				log.info("the certificate is incorrect!", e);
			} finally {
				try {
					instream.close();
				} catch (Exception ignore) {
				}
			}
			// 相信自己的CA和所有自签名的证书
			SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
			// 只允许使用TLSv1协议
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			// 创建http请求(get方式)
			HttpGet httpget = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					sslContent = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
				}
			} finally {
				response.close();
			}
		} catch (ParseException e) {
			log.info("Parse the http entity failed!", e);
		} catch (IOException e) {
			log.info("IO Exception!", e);
		} catch (KeyManagementException e) {
			log.info("load trust material failed!", e);
		} catch (NoSuchAlgorithmException e) {
			log.info("There is no such algorithm!", e);
		} catch (KeyStoreException e) {
			log.info("Obtain key store file failed!", e);
		} finally {
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					log.info("Close the http client failed!", e);
				}
			}
		}
		return sslContent;
	}

	/**
	 * post方式提交表单（模拟用户登录请求）
	 * url:http://localhost:8080/myDemo/Ajax/serivceJ.action
	 * encodeCharset:"UTF-8" formparams:form表单数据
	 */
	public static String postForm(String url, List<NameValuePair> formparams, String encodeCharset) {
		String postFormContent = EMPTY;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(url);
		// 创建参数队列
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, encodeCharset);
			httppost.setEntity(uefEntity);
			log.info("executing request " + httppost.getURI());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					postFormContent = EntityUtils.toString(entity, encodeCharset);
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			log.info("Client Protocol Exception!", e);
		} catch (UnsupportedEncodingException e) {
			log.info("The encode charset is Unsupported!", e);
		} catch (IOException e) {
			log.info("IO Exception!", e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				log.info("Close the http client failed!", e);
			}
		}
		return postFormContent;
	}

	// 重载，增加cookie选项
	public static String postForm(String url, List<NameValuePair> formparams, String username, String encodeCharset) {
		String postFormContent = EMPTY;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Cookie", "username=" + username);
		// 创建参数队列
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, encodeCharset);
			httppost.setEntity(uefEntity);
			log.info("executing request " + httppost.getURI());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					postFormContent = EntityUtils.toString(entity, encodeCharset);
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			log.info("Client Protocol Exception!", e);
		} catch (UnsupportedEncodingException e) {
			log.info("The encode charset is Unsupported!", e);
		} catch (IOException e) {
			log.info("IO Exception!", e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				log.info("Close the http client failed!", e);
			}
		}
		return postFormContent;
	}

	public static String post(String url, String jsonText) {
		String responseBody = null; 
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httpPost
		HttpPost httpPost = new HttpPost(url);
		// 创建参数队列
		StringEntity myEntity;
		try {
			myEntity = new StringEntity(jsonText, ContentType.APPLICATION_JSON);
			httpPost.setEntity(myEntity);
			
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				// 对访问结果进行处理
				public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						if (null != entity) {
							String postContent = EntityUtils.toString(entity);
							return postContent;
						} else {
							return null;
						}
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}
			};
			//返回的json对象  
            responseBody = httpclient.execute(httpPost, responseHandler);
		} catch (ClientProtocolException e) {
			log.info("Client Protocol Exception!", e);
		} catch (IOException e) {
			log.info("IO Exception!", e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				log.info("Close the http client failed!", e);
			}
		}
		return responseBody;
	}
	
	/**
	 * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
	 * url:"http://182.254.218.140:8080/in_user/user_query"
	 * encodeCharset:"UTF-8"
	 */
	public static String post(String url, List<NameValuePair> formparams, String encodeCharset) {
		String postContent = EMPTY;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(url);
		// 创建参数队列
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, encodeCharset);
			httppost.setEntity(uefEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					postContent = EntityUtils.toString(entity, encodeCharset);
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			log.info("Client Protocol Exception!", e);
		} catch (UnsupportedEncodingException e) {
			log.info("The encode charset is Unsupported!", e);
		} catch (IOException e) {
			log.info("IO Exception!", e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				log.info("Close the http client failed!", e);
			}
		}
		return postContent;
	}

	public static String post(String userName, String url, List<NameValuePair> formparams, String encodeCharset) {
		String postContent = EMPTY;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Cookie", "username=" + userName);
		// 创建参数队列
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, encodeCharset);
			httppost.setEntity(uefEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					postContent = EntityUtils.toString(entity, encodeCharset);
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			log.info("Client Protocol Exception!", e);
		} catch (UnsupportedEncodingException e) {
			log.info("The encode charset is Unsupported!", e);
		} catch (IOException e) {
			log.info("IO Exception!", e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				log.info("Close the http client failed!", e);
			}
		}
		return postContent;

	}

	/**
	 * 发送 get请求 url:http://182.254.218.140:8080/in_user/user_query
	 * username=bobby password=123456xyzp
	 */
	public static String get(String userName, String url) {
		String getContent = EMPTY;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// 创建HttpGet
			HttpGet httpget = new HttpGet(url);
			httpget.setHeader("Cookie", "username=" + userName);
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					getContent = EntityUtils.toString(entity);//响应内容
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			log.info("Client Protocol Exception!", e);
		} catch (UnsupportedEncodingException e) {
			log.info("The encode charset is Unsupported!", e);
		} catch (IOException e) {
			log.info("IO Exception!", e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				log.info("Close the http client failed!", e);
			}
		}
		return getContent;
	}

	/**
	 * 套餐类型账户查询
	 * 
	 * @param userName
	 * @param url
	 * @return
	 */
	public static String get(String url) {
		String getContent = EMPTY;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// 创建HttpGet
			HttpGet httpget = new HttpGet(url);
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					getContent = EntityUtils.toString(entity);// 响应内容
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			log.info("Client Protocol Exception!", e);
		} catch (UnsupportedEncodingException e) {
			log.info("The encode charset is Unsupported!", e);
		} catch (IOException e) {
			log.info("IO Exception!", e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				log.info("Close the http client failed!", e);
			}
		}
		return getContent;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param formparams
	 * @param encodeCharset
	 * @return
	 * @throws IOException
	 */
	public static String postList(String url, List<NameValuePair> formparams, String encodeCharset)
			throws IOException {
		String postContent = EMPTY;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(url);
		// 创建参数队列
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, encodeCharset);
			httppost.setEntity(uefEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					postContent = EntityUtils.toString(entity, encodeCharset);
				}
			} finally {
				response.close();
			}
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				log.error("post请求失败", e);
			}
		}
		return postContent;

	}
	
}
