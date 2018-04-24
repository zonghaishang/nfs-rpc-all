package code.google.nfs.rpc.netty.benchmark;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import code.google.nfs.rpc.benchmark.AbstractBenchmarkServer;
import code.google.nfs.rpc.netty.server.NettyServer;
import code.google.nfs.rpc.server.Server;

/**
 * Netty RPC Benchmark Server
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class NettyBenchmarkServer extends AbstractBenchmarkServer {

	public static void main(String[] args) throws Exception{
		new NettyBenchmarkServer().run(args);
	}
	
	public Server getServer() {
		return new NettyServer();
	}

}
