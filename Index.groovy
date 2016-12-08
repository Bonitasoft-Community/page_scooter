import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.Runtime;

import org.json.simple.JSONObject;
import org.codehaus.groovy.tools.shell.CommandAlias;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;


import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;

import org.apache.commons.lang3.StringEscapeUtils

import org.bonitasoft.engine.identity.User;
import org.bonitasoft.console.common.server.page.PageContext
import org.bonitasoft.console.common.server.page.PageController
import org.bonitasoft.console.common.server.page.PageResourceProvider
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;

import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.IdentityAPI;
import com.bonitasoft.engine.api.PlatformMonitoringAPI;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceSearchDescriptor;
import org.bonitasoft.engine.bpm.flownode.ArchivedActivityInstanceSearchDescriptor;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.flownode.ArchivedFlowNodeInstance;
import org.bonitasoft.engine.bpm.flownode.ArchivedActivityInstance;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;

import org.bonitasoft.engine.command.CommandDescriptor;
import org.bonitasoft.engine.command.CommandCriterion;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;

import com.bonitasoft.qrcode.GeneratorQrcode;	

import java.net.NetworkInterface;
import java.net.InetAddress;



public class Index implements PageController {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response, PageResourceProvider pageResourceProvider, PageContext pageContext) {
	
		Logger logger= Logger.getLogger("org.bonitasoft");
		
		
		try {
			def String indexContent;
			pageResourceProvider.getResourceAsStream("Index.groovy").withStream { InputStream s-> indexContent = s.getText() };
			response.setCharacterEncoding("UTF-8");
			
			String action=request.getParameter("action");
			logger.info("################################ V2 ###### action is["+action+"] !");
			if (action==null || action.length()==0 )
			{
				logger.severe(" RUN Default !");
				
				runTheBonitaIndexDoGet( request, response,pageResourceProvider,pageContext);
				return;
			}
			
			
			if ("getqrcode".equals(action))
			{
			   	logger.info("###################################### showqrcode-1 !");
		        
				
				response.setContentType("application/png");
				OutputStream outSt = response.getOutputStream();
				GeneratorQrcode.generateUriForMobile("http://localhost:8080/bonita", outSt );
			   	logger.info("###################################### showqrcode-2 !");
				
        		outSt.flush();
				outSt.close();
				logger.info("###################################### end showqrcode !");
			
				return;
			}
			if ("geturlmobile".equals(action))
			{
				logger.info("###################################### getUrlMobile getServletPath()"+request.getServletPath()+"] getRequestURL() ["+request.getRequestURL() +"] localname["+request.getLocalName() +"] localport ["+request.getLocalPort() );
				
				
				String completeHeaderUrl = request.getRequestURL();
				logger.info("###################################### getUrlMobile completeHeaderUrl()"+completeHeaderUrl+"]" );
				int pos = completeHeaderUrl.indexOf("/portal/custom-page");

				completeHeaderUrl = completeHeaderUrl.substring(0,pos);
				

				List<Map<String,Object>> listAddresses = new ArrayList<Map<String,Object>>();
				
				
				String allIpAddress="";
				// replace localhost by the IP address
				Enumeration e = NetworkInterface.getNetworkInterfaces();
				while(e.hasMoreElements())
				{
					NetworkInterface n = (NetworkInterface) e.nextElement();
					Enumeration ee = n.getInetAddresses();
					while (ee.hasMoreElements())
					{
						InetAddress i = (InetAddress) ee.nextElement();
						String ipAddressCandidate= i.getHostAddress();
						logger.info(">>>   IPAdresss["+ipAddressCandidate+"]");
						if (ipAddressCandidate.startsWith("127"))
							continue;
						if (ipAddressCandidate.indexOf(":")!=-1)
							continue;
						
						allIpAddress+=ipAddressCandidate+", ";
						String localHeader = completeHeaderUrl.replaceAll("localhost", ipAddressCandidate);
				
						File customPageFile = pageResourceProvider.getPageDirectory();
						String directory = customPageFile.getAbsolutePath();
						String qrCodeName = "img/qrcodeMobilePortal_"+ipAddressCandidate+".png";
						
						logger.info("#####custom directory["+directory+"] localHeader (like http://localhost:8080/bonita) : ["+localHeader+"]"); 
				
						FileOutputStream fileOutput = new FileOutputStream( new File(directory+"/resources/"+qrCodeName) );
				
						GeneratorQrcode.generateUriForMobile(localHeader, fileOutput );
						fileOutput.flush();
						fileOutput.close();
				
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("ipaddress", ipAddressCandidate);
						map.put("urlmobile", localHeader+"/mobile");
						map.put("urlqrcode", "pageResource?page=custompage_scooter&location="+qrCodeName);

						listAddresses.add( map );
						
						
					}
				}
				

				
				
				Map<String,Object> answser= new HashMap<String,Object>();
				answser.put("listaddress", listAddresses);
				answser.put("allipadress", allIpAddress );
				
			   
				String jsonDetailsSt = JSONValue.toJSONString( answser );
	   
				
				PrintWriter out = response.getWriter()
				out.write( jsonDetailsSt );
				out.flush();
				out.close();
	
				logger.info("###################################### getUrlMobile answer=["+jsonDetailsSt+"]");
			
							
				return;				
			}
				logger.info("###################################### getUrlMobile Unknow action");
			
			out.write( "Unknow command" );
			out.flush();
			out.close();
			return;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionDetails = sw.toString();
			logger.severe("Scooter:Exception ["+e.toString()+"] at "+exceptionDetails);
		}
	}

	
	/** -------------------------------------------------------------------------
	 *
	 *runTheBonitaIndexDoGet
	 * 
	 */
	private void runTheBonitaIndexDoGet(HttpServletRequest request, HttpServletResponse response, PageResourceProvider pageResourceProvider, PageContext pageContext) {
				try {
						def String indexContent;
						pageResourceProvider.getResourceAsStream("index.html").withStream { InputStream s->
								indexContent = s.getText()
						}
						
						def String pageResource="pageResource?&page="+ request.getParameter("page")+"&location=";
						
					
						response.setCharacterEncoding("UTF-8");
						PrintWriter out = response.getWriter();
						out.print(indexContent);
						out.flush();
						out.close();
				} catch (Exception e) {
						logger.severe("during run Basic Custom page get " +e.toString());
				}
		}
		
		
}
