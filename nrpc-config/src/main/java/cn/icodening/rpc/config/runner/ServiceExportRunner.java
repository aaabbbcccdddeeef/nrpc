package cn.icodening.rpc.config.runner;

import cn.icodening.rpc.common.Protocol;
import cn.icodening.rpc.common.model.NrpcService;
import cn.icodening.rpc.config.*;
import cn.icodening.rpc.core.LocalCache;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.boot.AbstractBootAdapter;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.transport.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 服务暴露
 *
 * @author icodening
 * @date 2021.03.14
 */
public class ServiceExportRunner extends AbstractBootAdapter implements NrpcRunner {

    @Override
    @SuppressWarnings("unchecked")
    protected void doStart() {
        NrpcBootstrap instance = NrpcBootstrap.getInstance();
        List<ServiceConfig> serviceConfigs = instance.getServiceConfigs();
        if (serviceConfigs.isEmpty()) {
            return;
        }
        String localIp = System.getProperty("local.ip");
        ApplicationConfig applicationConfig = NrpcBootstrap.getInstance().getApplicationConfig();
        List<ProtocolConfig> protocolConfigs = applicationConfig.getProtocolConfigs();
        if (protocolConfigs == null || protocolConfigs.isEmpty()) {
            ProtocolConfig protocolConfig = new ProtocolConfig();
            Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension();
            protocolConfig.setPort(protocol.defaultPort());
            protocolConfig.setName(protocol.getProtocolName());
            protocolConfigs = new ArrayList<>(Collections.singleton(protocolConfig));
        }
        LocalCache<String, Server> serverPointCache = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("server");
        //FIXME 暂时按照应用级别的协议配置优先
        for (ProtocolConfig config : protocolConfigs) {
            String protocolName = config.getName();
            Map<String, String> params = config.getParameters();
            URL url = new URL(protocolName, localIp, config.getPort());
            url.setParameters(params);
            Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(protocolName);
            Server export = protocol.export(url);
            export.initialize();
            export.start();
        }

        LocalCache<String, NrpcService> serviceConfigCache = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("service");
        for (ServiceConfig serviceConfig : serviceConfigs) {
            if (serviceConfig.getProtocolConfigs() == null || serviceConfig.getProtocolConfigs().isEmpty()) {
                serviceConfig.setProtocolConfigs(protocolConfigs);
            }
            NrpcService nrpcService = new NrpcService();
            nrpcService.setServiceInterface(serviceConfig.getServiceInterface());
            nrpcService.setName(serviceConfig.getName());
            nrpcService.setRef(serviceConfig.getReference());
            nrpcService.setVersion(serviceConfig.getVersion());
            //FIXME protocolMap -> protocol: serviceMap
            //FIXME serviceMap -> serviceName: serviceConfig
            serviceConfigCache.set(serviceConfig.getName(), nrpcService);
        }
    }

    @Override
    public int getPriority() {
        return SERVICE_EXPORT_PRIORITY;
    }
}