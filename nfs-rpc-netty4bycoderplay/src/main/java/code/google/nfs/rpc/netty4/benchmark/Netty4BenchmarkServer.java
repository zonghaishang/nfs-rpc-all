package code.google.nfs.rpc.netty4.benchmark;

/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.text.SimpleDateFormat;
import java.util.Date;

import code.google.nfs.rpc.benchmark.AbstractBenchmarkServer;
import code.google.nfs.rpc.benchmark.BenchmarkTestServiceImpl;
import code.google.nfs.rpc.benchmark.KryoUtils;
import code.google.nfs.rpc.benchmark.PB;
import code.google.nfs.rpc.benchmark.PBBenchmarkTestServiceImpl;
import code.google.nfs.rpc.benchmark.RequestObject;
import code.google.nfs.rpc.benchmark.RequestObjectSerializer;
import code.google.nfs.rpc.benchmark.ResponseObject;
import code.google.nfs.rpc.benchmark.ResponseObjectSerializer;
import code.google.nfs.rpc.netty4.server.Netty4Server;
import code.google.nfs.rpc.protocol.PBDecoder;
import code.google.nfs.rpc.protocol.RPCProtocol;
import code.google.nfs.rpc.protocol.SimpleProcessorProtocol;
import code.google.nfs.rpc.server.Server;
import code.google.nfs.rpc.server.ServerProcessor;

import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.google.protobuf.ByteString;


/**
 * Netty4 RPC Benchmark Server
 * 
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class Netty4BenchmarkServer extends AbstractBenchmarkServer {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	private int concurrency = 1;

	public void run(String[] args) throws Exception {
		if (args == null || args.length != 3) {
			throw new IllegalArgumentException(
					"must give three args: listenPort | maxThreads | responseSize");
		}
		int listenPort = Integer.parseInt(args[0]);
		concurrency = Integer.parseInt(args[1]);
		final int responseSize = Integer.parseInt(args[2]);
		System.out.println(dateFormat.format(new Date())
				+ " ready to start server,listenPort is: " + listenPort
				+ ",threads num is:" + concurrency + ",responseSize is:"
				+ responseSize + " bytes");

		Server server = getServer();
		server.registerProcessor(SimpleProcessorProtocol.TYPE,
				RequestObject.class.getName(), new ServerProcessor() {
					public Object handle(Object request) throws Exception {
						return new ResponseObject(responseSize);
					}
				});
		// for pb codec
		PBDecoder.addMessage(PB.RequestObject.class.getName(),
				PB.RequestObject.getDefaultInstance());
		PBDecoder.addMessage(PB.ResponseObject.class.getName(),
				PB.ResponseObject.getDefaultInstance());
		server.registerProcessor(SimpleProcessorProtocol.TYPE,
				PB.RequestObject.class.getName(), new ServerProcessor() {
					public Object handle(Object request) throws Exception {
						PB.ResponseObject.Builder builder = PB.ResponseObject
								.newBuilder();
						builder.setBytesObject(ByteString
								.copyFrom(new byte[responseSize]));
						return builder.build();
					}
				});
		server.registerProcessor(RPCProtocol.TYPE, "testservice",
				new BenchmarkTestServiceImpl(responseSize));
		server.registerProcessor(RPCProtocol.TYPE, "testservicepb",
				new PBBenchmarkTestServiceImpl(responseSize));
		// kryo codec
		KryoUtils.registerClass(byte[].class,
				new DefaultArraySerializers.ByteArraySerializer(), 0);
		KryoUtils.registerClass(RequestObject.class,
				new RequestObjectSerializer(), 1);
		KryoUtils.registerClass(ResponseObject.class,
				new ResponseObjectSerializer(), 2);

		server.start(listenPort, null);
	}
	
	public static void main(String[] args) throws Exception {
		new Netty4BenchmarkServer().run(args);
	}

	public Server getServer() {
		return new Netty4Server(concurrency);
	}

}
