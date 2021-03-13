package cn.icodening.rpc.transport.netty4;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.transport.Client;
import cn.icodening.rpc.transport.NrpcChannelHandler;
import cn.icodening.rpc.transport.Server;
import cn.icodening.rpc.transport.TransportFactory;
import cn.icodening.rpc.transport.netty4.client.Netty4Client;
import cn.icodening.rpc.transport.netty4.server.Netty4Server;

/**
 * @author icodening
 * @date 2021.03.10
 */
public class Netty4TransportFactory implements TransportFactory {
    @Override
    public Client createClient(URL url, NrpcChannelHandler nrpcChannelHandler) {
        return new Netty4Client(url, nrpcChannelHandler);
    }

    @Override
    public Server createServer(URL url, NrpcChannelHandler nrpcChannelHandler) {
        return new Netty4Server(url, nrpcChannelHandler);
    }
}
