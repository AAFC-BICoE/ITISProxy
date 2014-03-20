package ca.gc.agr.mbb.itisproxy.wsclient;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.ArrayList;

@Provider
public class UserAgentFilter implements ClientRequestFilter{

    public void filter(ClientRequestContext requestContext){
	List<Object> userAgent = new ArrayList<Object>(1);
	userAgent.add(WSState.USER_AGENT);
	requestContext.getHeaders().put("User-Agent", userAgent);
    }

}


