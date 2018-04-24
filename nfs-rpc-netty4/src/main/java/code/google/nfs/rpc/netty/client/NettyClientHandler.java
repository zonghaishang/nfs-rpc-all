package code.google.nfs.rpc.netty.client;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import code.google.nfs.rpc.ResponseWrapper;
/**
 * Netty Client Handler
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
	
	private static final Log LOGGER = LogFactory.getLog(NettyClientHandler.class);
	
	private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();
	
	private NettyClientFactory factory;
	
	private String key;
	
	private NettyClient client;
	
	public NettyClientHandler(NettyClientFactory factory,String key){
		this.factory = factory;
		this.key = key;
	}
	
	public void setClient(NettyClient client){
		this.client = client;
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg instanceof List){
			@SuppressWarnings("unchecked")
			List<ResponseWrapper> responses = (List<ResponseWrapper>)msg;
			if(isDebugEnabled){
				// for performance trace
				LOGGER.debug("receive response list from server: "+ctx.channel().remoteAddress()+",list size is:"+responses.size());
			}
			client.putResponses(responses);
		}
		else if(msg instanceof ResponseWrapper){
			ResponseWrapper response = (ResponseWrapper)msg;
			if(isDebugEnabled){
				// for performance trace
				LOGGER.debug("receive response list from server: "+ctx.channel().remoteAddress()+",request is:"+response.getRequestId());
			}
			client.putResponse(response);
		}
		else{
			LOGGER.error("receive message error,only support List || ResponseWrapper");
			throw new Exception("receive message error,only support List || ResponseWrapper");
		}
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
			throws Exception {
		if(!(e.getCause() instanceof IOException)){
			// only log
			LOGGER.error("catch some exception not IOException",e.getCause());
		}
	}
	
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		LOGGER.warn("connection closed: "+ctx.channel().remoteAddress());
		factory.removeClient(key,client);
	}
	
}
