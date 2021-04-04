package cn.icodening.nrpc.example;

import cn.icodening.nrpc.example.api.IEchoService;
import cn.icodening.nrpc.example.api.IHelloService;
import cn.icodening.rpc.config.*;

import java.util.ArrayList;

/**
 * @author icodening
 * @date 2021.03.21
 */
public class NrpcProvider {

    public static void main(String[] args) {
        ServiceConfig serviceConfig1 = new ServiceConfig();
        serviceConfig1.setServiceInterface(IHelloService.class);
        serviceConfig1.setReference(new HelloService());
        serviceConfig1.setName(IHelloService.class.getName());

        ServiceConfig serviceConfig2 = new ServiceConfig();
        serviceConfig2.setServiceInterface(IEchoService.class);
        serviceConfig2.setReference(new EchoService());
        serviceConfig2.setName(IEchoService.class.getName());

        ApplicationConfig applicationConfig = new ApplicationConfig("nrpc-provider");
        ArrayList<ProtocolConfig> protocolConfigs = new ArrayList<>();
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("lightning");
        Integer port = Integer.valueOf(System.getProperty("port", "9090"));
        protocolConfig.setPort(port);
        protocolConfigs.add(protocolConfig);
        applicationConfig.setProtocolConfigs(protocolConfigs);
        NrpcBootstrap.getInstance()
                .application(applicationConfig)
                .registry(new RegistryConfig("nacos://127.0.0.1:8848"))
                .service(serviceConfig2)
                .service(serviceConfig1)
                .start();
    }
}